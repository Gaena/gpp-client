package az.millikart.gppclient.model;

import az.millikart.wsdl.ErrorType;
import az.millikart.wsdl.GetPaymentDataResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDataResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GetPaymentDataResponse getPaymentDataResponse;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorType errorType;
}
