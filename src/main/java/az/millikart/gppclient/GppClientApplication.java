package az.millikart.gppclient;

import az.millikart.gppclient.client.GppClient;
import az.millikart.wsdl.GetPaymentDataResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GppClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GppClientApplication.class, args);
    }

}
