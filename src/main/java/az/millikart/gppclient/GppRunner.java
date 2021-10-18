package az.millikart.gppclient;

import az.millikart.gppclient.client.GppClient;
import az.millikart.gppclient.model.PaymentDataResponse;
import az.millikart.wsdl.GetPaymentDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GppRunner implements CommandLineRunner {

    private final GppClient client;

    public GppRunner(GppClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {

        PaymentDataResponse response = client.getResponse();
        log.info("Response: " + response);

    }
}
