package my.example.p3;

import dev.restate.sdk.Context;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;

@Service
public class TicketService {

    @Handler
    public boolean reserve(Context ctx, String ticket) {
        System.out.println("Reserving ticket: " + ticket);
        return true;
    }
}
