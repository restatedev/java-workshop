package my.example.p5;

import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;

public class AppMain {
    public static void main(String[] args) {
        RestateHttpEndpointBuilder.builder()
                .bind(new CheckoutService())
                .bind(new TicketService())
                .buildAndListen();
    }
}
