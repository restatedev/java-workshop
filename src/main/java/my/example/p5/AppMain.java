package my.example.p5;

import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;
import my.example.p3.CheckoutService;
import my.example.p3.TicketService;

public class AppMain {
    public static void main(String[] args) {
        RestateHttpEndpointBuilder.builder()
                .bind(new CheckoutService())
                .bind(new TicketService())
                .buildAndListen();
    }
}
