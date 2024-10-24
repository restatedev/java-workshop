package my.example.p3;

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
          LogManager.getLogger(my.example.p1.CheckoutService.class);

  @Handler
  public boolean checkout(Context ctx, String ticket) {

    boolean reserved = TicketServiceClient.fromContext(ctx).reserve(ticket).await();
    if (!reserved) {
      return false;
    }

    String paymentId = ctx.random().nextUUID().toString();

    var durableFuture = ctx.awakeable(JsonSerdes.BOOLEAN);
    ctx.run("payment", () -> payAsync(paymentId, 40, durableFuture.id()));
    boolean paid = durableFuture.await();

    return paid;
  }

  private void payAsync(String paymentId, int amount, String durableFutureId){
    // call payment provider
    logger.info("Doing the payment for id " + paymentId +
            ", amount " + amount +
            " and durableFutureId " + durableFutureId);
  }

  public static void main(String[] args) {
    RestateHttpEndpointBuilder.builder()
            .bind(new CheckoutService())
            .buildAndListen();
  }
}
