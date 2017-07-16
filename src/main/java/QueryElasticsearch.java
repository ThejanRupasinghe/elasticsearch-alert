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
 * Created by thejan on 7/14/17.
 */
public enum QueryElasticsearch {

    INSTANCE;

    Settings settings;
    TransportClient client;

    public boolean configure () {

        settings = Settings.builder().put("cluster.name",Configuration.INSTANCE.getElasticsearchClusterName()).build();

        client = new PreBuiltTransportClient(settings);
        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Configuration.INSTANCE.getElasticsearchHost()),9300));
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String query() {

        Date date = new Date();

        /** Taking Date in the format logstash-2017.07.12 **/
        DateFormat dateFormatLogstash = new SimpleDateFormat("yyyy.MM.dd");
        String dateForLogstashIndex = dateFormatLogstash.format(date);

        DateFormat dateFormatQuery = new SimpleDateFormat("yyyy-MM-dd");
        String dateForQuery = dateFormatQuery.format(date);

        DateFormat dateFormatQueryTime = new SimpleDateFormat("HH:mm:ss");
        dateFormatQueryTime.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        String timeForQuery = dateFormatQueryTime.format(date);

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, AlertMain.TIMEPERIOD/1000);
        String timeForQueryNext = dateFormatQueryTime.format(calendar.getTime());

        String queryString = "";
        for (int i=0; i< Configuration.INSTANCE.getMatchList().size()-1;i++){
            queryString+=Configuration.INSTANCE.getMatchList().get(i);
            queryString+=" OR ";
        }
        queryString+=Configuration.INSTANCE.getMatchList().get(Configuration.INSTANCE.getMatchList().size()-1);
        System.out.println(queryString);


        QueryBuilder qb = queryStringQuery(queryString).defaultField("message");

        String timeStampToQuery = dateForQuery + "T" + timeForQuery + ".000Z";
        String timeStampToQueryNext = dateForQuery + "T" + timeForQueryNext + ".000Z";
        System.out.println(timeStampToQuery);
        System.out.println(timeStampToQueryNext);

        QueryBuilder qbr = rangeQuery("@timestamp").from(timeStampToQuery).to(timeStampToQueryNext);

        String log_message = null;

        try {

            SearchResponse response = client.prepareSearch("logstash-" + dateForLogstashIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(qb)
                    .setPostFilter(qbr)// Query
                    .setFrom(0).setSize(10).setExplain(false)
                    .get();

            System.out.println(response.toString());
            System.out.println(response.getHits().getHits().length);

            log_message = response.getHits().getHits()[0].getSource().get("message").toString();

        } catch (NoNodeAvailableException e) {

            e.printStackTrace();
            System.out.println("No Available Nodes to connect. Please give correct configurations and run Elasticsearch.");
        } catch (IndexNotFoundException e) {
//            e.printStackTrace();
            System.out.println("No such index");
        }




//        QueryBuilders.rangeQuery("age").from(12).to(18);

        System.out.println(log_message);
//        for (SearchHit hit : response.getHits().getHits()) {
//            System.out.println(hit.getSource().get("@timestamp").toString());
//        }

        return log_message;

    }
}
