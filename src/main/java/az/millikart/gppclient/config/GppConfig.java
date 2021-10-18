package az.millikart.gppclient.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
@Slf4j
public class GppConfig {

    private final String wsdlPackage;

    private final Resource trustStore;
    private final String trustStorePassword;

    private Resource keyStore;
    private String keyStorePassword;

    public GppConfig(@Value("${wsdl.package}") String wsdlPackage,
                     @Value("${ssl.trust-store}") Resource trustStore,
                     @Value("${ssl.trust-store-password}") String trustStorePassword,
                     @Value("${ssl.key-store}") Resource keyStore,
                     @Value("${ssl.key-store-password}") String keyStorePassword) {
        this.wsdlPackage = wsdlPackage;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }

    @Bean(name = "marshaller")
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(wsdlPackage);
        return marshaller;
    }

    @Bean
    KeyManagerFactory keyManagerFactory() throws NoSuchAlgorithmException, CertificateException,
            KeyStoreException, IOException, UnrecoverableKeyException {

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keyStore.getInputStream(), keyStorePassword.toCharArray());
        log.info("Loaded keystore: " + keyStore.getURI());
        try {
            keyStore.getInputStream().close();
        } catch (IOException e) {
            e.getMessage();
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(ks, keyStorePassword.toCharArray());

        return keyManagerFactory;
    }

    @Bean
    TrustManagerFactory trustManagerFactory() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(trustStore.getInputStream(), trustStorePassword.toCharArray());
        log.info("Loaded trustStore: " + trustStore.getURI());
        try {
            trustStore.getInputStream().close();
        } catch (IOException e) {
            e.getMessage();
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ts);

        return trustManagerFactory;
    }

    @Bean
    HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender() throws UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
        messageSender.setKeyManagers(keyManagerFactory().getKeyManagers());
        messageSender.setTrustManagers(trustManagerFactory().getTrustManagers());

        // otherwise: java.security.cert.CertificateException: No name matching localhost found
        messageSender.setHostnameVerifier((hostname, sslSession) -> true);

        return messageSender;
    }


}
