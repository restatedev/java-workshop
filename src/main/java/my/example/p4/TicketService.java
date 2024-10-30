package my.example.p4;

import dev.restate.sdk.Context;
import dev.restate.sdk.JsonSerdes;
import dev.restate.sdk.ObjectContext;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.restate.sdk.annotation.VirtualObject;
import dev.restate.sdk.common.StateKey;
import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;

@VirtualObject
public class TicketService {

    private static final StateKey<String> STATUS =
            StateKey.of("status", JsonSerdes.STRING);

    @Handler
    public boolean reserve(ObjectContext ctx) {
        String status = ctx.get(STATUS).orElse("Available");
        if(!status.equals("Available")){
            return false;
        }

        ctx.set(STATUS, "Reserved");
        return true;
    }

    @Handler
    public void unreserve(ObjectContext ctx) {
        String status = ctx.get(STATUS).orElse("Available");
        if(!status.equals("Reserved")){
            return;
        }
        ctx.set(STATUS, "Available");
    }
}
