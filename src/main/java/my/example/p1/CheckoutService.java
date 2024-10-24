package my.example.p1;

import dev.restate.sdk.Context;
import dev.restate.sdk.JsonSerdes;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class CheckoutService {

  private static final Logger logger =
          LogManager.getLogger(CheckoutService.class);

  @Handler
  public boolean checkout(Context ctx, String ticket) {

    String paymentId = ctx.random().nextUUID().toString();
    ctx.run("payment", JsonSerdes.BOOLEAN, () -> pay(paymentId, 40));

    logger.info("Generated UUID: " + paymentId);

    throw new RuntimeException("This is an example exception");
  }

  private boolean pay(String paymentId, int amount){
    // call payment provider
    logger.info("Doing the payment for id " + paymentId + " and amount " + amount);
    return true;
  }

  public static void main(String[] args) {
    RestateHttpEndpointBuilder.builder()
            .bind(new CheckoutService())
            .buildAndListen();
  }
}
