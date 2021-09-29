package az.millikart.gppclient;

import az.millikart.gppclient.client.GppClient;
import az.millikart.wsdl.GetPaymentDataResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GppRunner implements CommandLineRunner {

    private final GppClient client;

    public GppRunner(GppClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {

        GetPaymentDataResponse response = client.getResponse();

    }
}
