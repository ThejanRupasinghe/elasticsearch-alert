import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        /** Taking Date in the format logstash-2017.07.12 **/
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);
        System.out.println(formattedDate);


        String queryString = "";
        for (int i=0; i< Configuration.INSTANCE.getMatchList().size()-1;i++){
            queryString+=Configuration.INSTANCE.getMatchList().get(i);
            queryString+=" OR ";
        }
        queryString+=Configuration.INSTANCE.getMatchList().get(Configuration.INSTANCE.getMatchList().size()-1);
        System.out.println(queryString);


        QueryBuilder qb = queryStringQuery(queryString).defaultField("message");

        QueryBuilder qbr = rangeQuery("timestamp").from("2017-07-04T11:09:26.100Z").to("2017-07-04T11:09:46.937Z");

        SearchResponse response = client.prepareSearch("logstash-" + formattedDate)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)                // Query
                .setFrom(0).setSize(10).setExplain(false)
                .get();

        System.out.println(response.toString());
        System.out.println(response.getHits().getHits().length);

//        QueryBuilders.rangeQuery("age").from(12).to(18);

        String log_message = response.getHits().getHits()[0].getSource().get("message").toString();
        System.out.println(log_message);
//        for (SearchHit hit : response.getHits().getHits()) {
//            System.out.println(hit.getSource().get("@timestamp").toString());
//        }

        return log_message;

    }
}
