package az.millikart.gppclient.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;
import sun.net.www.http.HttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Configuration
public class GppConfig {

    private final String wsdlPackage;

    private final Resource trustStore;

    private final String trustStorePassword;

    public GppConfig(@Value("${wsdl.package}") String wsdlPackage,
                     @Value("${ssl.trust-store}") Resource trustStore,
                     @Value("${ssl.trust-store-password}") String trustStorePassword) {
        this.wsdlPackage = wsdlPackage;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
    }

    @Bean(name = "marshaller")
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(wsdlPackage);
        return marshaller;
    }

    @Bean(name = "messageSender")
    public HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender() throws Exception {
        HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender =
                new HttpsUrlConnectionMessageSender();
        httpsUrlConnectionMessageSender.setTrustManagers(trustManagersFactoryBean().getObject());
        // allows the client to skip host name verification as otherwise following error is thrown:
        // java.security.cert.CertificateException: No name matching localhost found
        httpsUrlConnectionMessageSender.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return "localhost".equals(hostname);
            }
        });

        return httpsUrlConnectionMessageSender;
    }

    @Bean
    public KeyStoreFactoryBean trustStore() {
        KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();

        keyStoreFactoryBean.setLocation(trustStore);
        keyStoreFactoryBean.setPassword(trustStorePassword);

        return keyStoreFactoryBean;
    }

    @Bean
    public TrustManagersFactoryBean trustManagersFactoryBean() throws NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
        trustManagersFactoryBean.setKeyStore(trustStore().getObject());

        return trustManagersFactoryBean;
    }
}
