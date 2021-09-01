package az.millikart.gppclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class GppConfig {

    private final String wsdlPackage;

    public GppConfig(@Value("${wsdl.package}") String wsdlPackage) {
        this.wsdlPackage = wsdlPackage;
    }

    @Bean(name = "marshaller")
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(wsdlPackage);
        return marshaller;
    }
}
