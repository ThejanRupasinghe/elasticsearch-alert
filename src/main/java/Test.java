import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by thejan on 7/12/17.
 */
public class Test {

    public static void main(String[] args) {

        Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();

        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


//        client.close();

    }
}
