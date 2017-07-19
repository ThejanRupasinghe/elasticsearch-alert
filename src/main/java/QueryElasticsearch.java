/**
 * Created by thejan on 7/14/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * This class configure the elasticsearch client and query from elasticsearch. Singleton class.
 */
public enum QueryElasticsearch {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(QueryElasticsearch.class);

    // Elasticsearch settings object
    Settings settings;

    // Defines elasticsearch Transport Client as client
    TransportClient client;


    /**
     * This builds the elasticsearch settings and configures it
     *
     * @return true on successful configuring , false on exceptions
     */
    public boolean configure () {

        settings = Settings.builder().put("cluster.name",Configuration.INSTANCE.getElasticsearchClusterName()).build();

        client = new PreBuiltTransportClient(settings);

        try {

            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Configuration.INSTANCE.getElasticsearchHost()),9300));
            return true;

        } catch ( UnknownHostException e ) {

            logger.error("Unknown Elasticsearch Host");
            return false;

        }

    }


    /**
     * This method queries Elasticsearch for the logstash-current_date index
     *
     * @return ArrayList of matching log messages to the executed query
     */
    public ArrayList<String> query() {

        // ArrayList of queried log messages
        ArrayList<String> logMessages = new ArrayList<String>();

        // Current date
        Date date = new Date();

        // Converts current date in the format logstash-2017.07.12
        DateFormat dateFormatLogstash = new SimpleDateFormat("yyyy.MM.dd");
        String dateForLogstashIndex = dateFormatLogstash.format(date);

        // Converts current date in the format in the @timestamp
        DateFormat dateFormatQuery = new SimpleDateFormat("yyyy-MM-dd");
        String dateForQuery = dateFormatQuery.format(date);

        /** Calculating Time Range of the filter in the query **/

        // "to time" of the time range
        DateFormat dateFormatQueryTime = new SimpleDateFormat("HH:mm:ss.SSS");
        dateFormatQueryTime.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));    // Logstash works with UTC
        String timeForQuery = dateFormatQueryTime.format(date);

        // "from time" of the time range
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        // "from time" is ( current time - polling period - time for previous querying and mail sending )
        calendar.add(Calendar.MILLISECOND, -(AlertMain.getTimePeriod()+AlertMain.getPreviousExecutionTime()));
        String timeForQueryPrevious = dateFormatQueryTime.format(calendar.getTime());

        // Builds query string , connected with OR
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i< Configuration.INSTANCE.getMatchList().size()-1;i++){
            buffer.append(Configuration.INSTANCE.getMatchList().get(i));
            buffer.append("OR");
        }

        String queryString = buffer.toString();
        queryString+=Configuration.INSTANCE.getMatchList().get(Configuration.INSTANCE.getMatchList().size()-1);
        logger.info("Query String : " + queryString);

        // Query Builder with query string and default searching field as "message"
        QueryBuilder queryBuilder = queryStringQuery(queryString).defaultField("message");

        // Builds query pattern like @timestamp
        String timeStampToQuery = dateForQuery + "T" + timeForQuery + "Z";
        String timeStampToQueryPrevious = dateForQuery + "T" + timeForQueryPrevious + "Z";

        logger.info("Query Range of @timestamp : from " + timeStampToQueryPrevious + " to " + timeStampToQuery + " in UTC");

        // Range Query Builder with @timestamp range to only take the log messages that appeared between two queries
        QueryBuilder rangeQueryBuilder = rangeQuery("@timestamp").from(timeStampToQueryPrevious).to(timeStampToQuery);


        try {

            // Queries to Elasticsearch and takes responses
            SearchResponse response = client.prepareSearch("logstash-" + dateForLogstashIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder)
                    .setPostFilter(rangeQueryBuilder)
                    .setFrom(0).setSize(10).setExplain(false)
                    .get();

            logger.info(response.getHits().getHits().length + " results found");

            String responsePrint = "";
            for (SearchHit hit : response.getHits().getHits()){
                logMessages.add(hit.getSource().get("message").toString());
            }

            for (int i=0; i<logMessages.size();i++) {
                responsePrint += (i+1) + ") " + logMessages.get(i) + "\n";
            }

            // Only if there are responses
            if(response.getHits().getHits().length != 0) {
                logger.info(responsePrint);
            }

        } catch (NoNodeAvailableException e) {  // If no Elasticsearch instance is running

//            e.printStackTrace();
            logger.error("No available Elasticsearch Nodes to connect. Please give correct configurations and run Elasticsearch.");

            // Returns null to terminate program, if no node
            return null;

        } catch (IndexNotFoundException e) {    // logstash index cannot be found

//            e.printStackTrace();
            logger.error("logstash-" + dateForLogstashIndex +" index not found. Querying again in " + (AlertMain.getTimePeriod()/1000) + " seconds to find index.");

        }


        // Contains responses found. Empty if any error occurred.
        return logMessages;

    }
}
