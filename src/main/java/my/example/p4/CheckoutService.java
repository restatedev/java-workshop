package my.example.p4;

import dev.restate.sdk.Context;
import dev.restate.sdk.JsonSerdes;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CheckoutService {

  private static final Logger logger =
          LogManager.getLogger(my.example.p1.CheckoutService.class);

  @Handler
  public boolean checkout(Context ctx, String ticket) {

    boolean reserved = TicketServiceClient.fromContext(ctx, ticket).reserve().await();
    if (!reserved) {
      return false;
    }

    String paymentId = ctx.random().nextUUID().toString();

    var awakeable = ctx.awakeable(JsonSerdes.BOOLEAN);
    ctx.run("payment", () -> payAsync(paymentId, 40, awakeable.id()));
    boolean paid = awakeable.await();

    if(!paid) {
        TicketServiceClient.fromContext(ctx, ticket).send().unreserve();
    }
    return paid;
  }

  private void payAsync(String paymentId, int amount, String durableFutureId){
    // call payment provider
    logger.info("Doing the payment for id " + paymentId +
            ", amount " + amount +
            " and durableFutureId " + durableFutureId);
  }
}
