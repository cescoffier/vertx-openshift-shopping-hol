package me.escoffier.demo;

import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.reactivex.Observable;
import io.reactivex.Single;

public class MyShoppingList extends AbstractVerticle {

    WebClient shopping, pricer;
	private CircuitBreaker circuit;

    @Override
    public void start() {   
    	circuit = CircuitBreaker.create("circuit-breaker", vertx,
                new CircuitBreakerOptions()
                    .setFallbackOnFailure(true)
                    .setMaxFailures(3)
                    .setResetTimeout(5000)
                    .setTimeout(1000)
            );
        Router router = Router.router(vertx);
        router.route("/health").handler(rc -> rc.response().end("OK"));
        router.route("/").handler(this::getShoppingList);
                    
    }

    private void getShoppingList(RoutingContext rc) {
        HttpServerResponse serverResponse =
            rc.response().setChunked(true);

       /*
         +--> Retrieve shopping list
           +
           +-->  for each item, call the pricer, concurrently
                    +
                    |
                    +-->  For each completed evaluation (line),
                          write it to the HTTP response
         */

         /*
                               shopping          pricer
                               backend
                 +                +                +
                 |                |                |
                 +--------------> |                |
                 |                |                |
                 |                |                |
                 +-------------------------------> |
                 |                |                |
                 +-------------------------------> |
        write <--|                |                |
                 +-------------------------------> |
        write <--|                +                +
                 |
        write <--|
                 |
          end <--|
         */
              
    }

}
