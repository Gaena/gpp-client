package az.millikart.gppclient.client;

import az.millikart.wsdl.EmptyRequest;
import az.millikart.wsdl.GetPaymentDataResponse;
import az.millikart.wsdl.MessageHeader;
import az.millikart.wsdl.ObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Slf4j
@Component
public class GppClient extends WebServiceGatewaySupport {

    private final String wsdlUrl;
    private final String wsdlPublicUrl;
    private final HttpsUrlConnectionMessageSender messageSender;

    public GppClient(@Value("${wsdl.url}") String wsdlUrl,
                     @Qualifier("marshaller") Jaxb2Marshaller marshaller,
                     @Value("${wsdl.public_url}") String wsdlPublicUrl,
                     @Qualifier("messageSender") HttpsUrlConnectionMessageSender messageSender) {
        this.wsdlUrl = wsdlUrl;
        this.wsdlPublicUrl = wsdlPublicUrl;
        this.messageSender = messageSender;
        super.setMarshaller(marshaller);
        super.setUnmarshaller(marshaller);
        super.setDefaultUri(wsdlPublicUrl);
        super.setMessageSender(messageSender);
    }

    public GetPaymentDataResponse getResponse() {

        ObjectFactory factory = new ObjectFactory();

        EmptyRequest emptyRequest = factory.createEmptyRequest();
        JAXBElement<EmptyRequest> request = factory.createGetPaymentData(emptyRequest);

        System.out.println(wsdlUrl);
        GetPaymentDataResponse getPaymentDataResponse = (GetPaymentDataResponse) getWebServiceTemplate()
                .marshalSendAndReceive(
                        request,
                        webServiceMessage -> {
                            try {
                                SoapHeader soapHeader = ((SoapMessage) webServiceMessage).getSoapHeader();
                                MessageHeader messageHeader = factory.createMessageHeader();
                                messageHeader.setReceiverID(888888);
                                messageHeader.setUserID(910000);
                                messageHeader.setTransactionID("88888820210805190624620000588");
                                JAXBElement<MessageHeader> header = factory.createMessageHeader(messageHeader);
                                JAXBContext context = JAXBContext.newInstance(MessageHeader.class);
                                Marshaller marshaller = context.createMarshaller();
                                marshaller.marshal(header, soapHeader.getResult());
                            } catch (JAXBException e) {
                                log.info("error during marshalling of the SOAP headers : {}", e.getMessage());
                            }
                        }
                );
        log.info("Response : {}", getPaymentDataResponse);
        return getPaymentDataResponse;
    }
}
