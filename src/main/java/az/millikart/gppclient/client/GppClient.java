package az.millikart.gppclient.client;


import az.millikart.gppclient.model.PaymentDataResponse;
import az.millikart.wsdl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapFaultMessageResolver;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import java.io.IOException;

@Slf4j
@Component
public class GppClient extends WebServiceGatewaySupport {

    private final Jaxb2Marshaller marshaller;
    private HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender;


    public GppClient(@Qualifier("marshaller") Jaxb2Marshaller marshaller,
                     HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender) {
        this.marshaller = marshaller;
        this.httpsUrlConnectionMessageSender = httpsUrlConnectionMessageSender;
        super.setMarshaller(marshaller);
        super.setUnmarshaller(marshaller);
        super.setDefaultUri("wsdlPublicUrl");
    }

    public PaymentDataResponse getResponse() {

        ObjectFactory factory = new ObjectFactory();
        EmptyRequest emptyRequest = factory.createEmptyRequest();
        JAXBElement<EmptyRequest> request = factory.createGetPaymentData(emptyRequest);

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMessageSender(httpsUrlConnectionMessageSender);
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        FaultMessageResolver faultMessageResolver = new SoapFaultMessageResolver();
        webServiceTemplate.setFaultMessageResolver(faultMessageResolver);
        GetPaymentDataResponse getPaymentDataResponse = new GetPaymentDataResponse();
        PaymentDataResponse response = PaymentDataResponse.builder().build();
        try {
            getPaymentDataResponse = (GetPaymentDataResponse) webServiceTemplate
                    .marshalSendAndReceive(
                            "https://testservices.gpp.az/PacPmtProc/GPPWebPacWS",
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
        } catch (SoapFaultClientException e) {
            SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
            SoapFaultDetailElement detailElementChild = soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            log.info(e.getWebServiceMessage().getPayloadResult().toString());
            try {
                JAXBElement<ErrorType> unmarshal = (JAXBElement<ErrorType>) getWebServiceTemplate()
                        .getUnmarshaller().unmarshal(detailSource);
                log.info(unmarshal.getValue().toString());
                response.setErrorType(unmarshal.getValue());
            } catch (IOException e1) {
                throw new IllegalArgumentException("cannot unmarshal SOAP fault detail object: " + soapFaultDetail.getSource());
            }
        }
        response.setGetPaymentDataResponse(getPaymentDataResponse);
        return response;
    }
}
