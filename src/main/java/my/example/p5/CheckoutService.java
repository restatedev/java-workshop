package my.example.p5;

import dev.restate.sdk.Context;
import dev.restate.sdk.JsonSerdes;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.restate.sdk.common.TerminalException;
import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class CheckoutService {

  private static final Logger logger =
          LogManager.getLogger(my.example.p1.CheckoutService.class);

  @Handler
  public boolean checkout(Context ctx, String ticket) {
    final Deque<Runnable> compensations = new ArrayDeque<>();
    try {
      compensations.add(() -> TicketServiceClient.fromContext(ctx, ticket).unreserve().await());
      boolean reserved = TicketServiceClient.fromContext(ctx, ticket).reserve().await();
      if (!reserved) {
        return false;
      }

      String paymentId = ctx.random().nextUUID().toString();
      var awakeable = ctx.awakeable(JsonSerdes.BOOLEAN);
      compensations.add(() -> abortPayment(paymentId));
      ctx.run("payment", () -> payAsync(paymentId, 40, awakeable.id()));
      boolean paid = awakeable.await(Duration.ofMinutes(10));

      if(paid) {
        compensations.add(() -> TicketServiceClient.fromContext(ctx, ticket).unreserve().await());
        TicketServiceClient.fromContext(ctx, ticket).send().markAsSold();
      }

      return paid;
    } catch (TimeoutException | TerminalException e) {
      compensations.reversed().forEach(Runnable::run);
      throw new TerminalException(e.getMessage());
    }
  }

  private void payAsync(String paymentId, int amount, String durableFutureId){
    // call payment provider
    logger.info("Doing the payment for id " + paymentId +
            ", amount " + amount +
            " and durableFutureId " + durableFutureId);
  }

  private void abortPayment(String paymentId){
    // call payment provider
    logger.info("Aborting the payment for id " + paymentId);
  }
}
