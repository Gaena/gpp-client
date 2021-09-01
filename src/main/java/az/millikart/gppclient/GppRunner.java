package az.millikart.gppclient;

import az.millikart.gppclient.client.GppClient;
import az.millikart.wsdl.GetPaymentDataResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GppRunner implements CommandLineRunner {

    private final GppClient client;

    public GppRunner(GppClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(Arrays.toString(args));

        GetPaymentDataResponse response = client.getResponse();
/*
        System.out.println(response.getReturn());*/

    }
}
