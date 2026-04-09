package aviation.bot.service.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, String> {

  @Override
  public String handleRequest(APIGatewayV2HTTPEvent input, Context context) {
    return "";
  }
}
