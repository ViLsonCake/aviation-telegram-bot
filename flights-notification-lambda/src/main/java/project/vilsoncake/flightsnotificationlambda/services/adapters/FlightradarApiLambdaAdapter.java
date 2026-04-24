package project.vilsoncake.flightsnotificationlambda.services.adapters;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.flightsnotificationlambda.configurations.AwsConfig;
import project.vilsoncake.flightsnotificationlambda.models.AirportRequest;
import project.vilsoncake.flightsnotificationlambda.models.AirportResponse;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor(staticName = "create")
public class FlightradarApiLambdaAdapter {

  private final LambdaClient lambdaClient;
  private final AwsConfig awsConfig;
  private final ObjectMapper objectMapper;

  public Map<String, AirportResponse> getFilteredFlightsForAirports(List<AirportRequest> airports) {
    Map<String, List<AirportRequest>> payload = Map.of("airports", airports);
    String jsonPayload = objectMapper.writeValueAsString(payload);

    InvokeRequest invokeRequest = buildInvokeRequest(jsonPayload);
    InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

    String responseBody = invokeResponse.payload().asUtf8String();

    TypeReference<Map<String, AirportResponse>> typeReference =
        new TypeReference<Map<String, AirportResponse>>() {};
    return objectMapper.readValue(responseBody, typeReference);
  }

  private InvokeRequest buildInvokeRequest(String jsonPayload) {
    return InvokeRequest.builder()
        .functionName(awsConfig.getFlightradarApiLambdaName())
        .invocationType(InvocationType.REQUEST_RESPONSE)
        .payload(SdkBytes.fromUtf8String(jsonPayload))
        .build();
  }
}
