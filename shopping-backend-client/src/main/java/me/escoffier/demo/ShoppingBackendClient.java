package me.escoffier.demo;

import com.beust.jcommander.JCommander;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;

public class ShoppingBackendClient {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        Args cli = new Args();
        JCommander commander = JCommander.newBuilder()
            .addObject(cli)
            .build();
        commander.parse(args);

        if (cli.help) {
            commander.usage();
            return;
        }

        WebClientOptions options = new WebClientOptions();
        String host = cli.url.getHost();
        int port = cli.url.getPort();
        if (port <= 0) {
            port = 80;
        }
        options.setDefaultHost(host);
        options.setDefaultPort(port);
        WebClient client = WebClient.create(vertx, options);

        switch (cli.action.toLowerCase()) {
            case "remove": remove(vertx, client, cli); break;
            case "add": add(vertx, client, cli); break;
            case "populate": populate(vertx, client); break;
            case "get" : list(vertx, client); break;
            default: System.out.println("Unknown action " + cli.action + ", supported actions are list, remove, add " +
                "and populate");
        }
    }

    private static void populate(Vertx vertx, WebClient client) {
        JsonObject product1 = new JsonObject().put("name", "coffee").put("quantity", 2);
        JsonObject product2 = new JsonObject().put("name", "bacon").put("quantity", 1);
        JsonObject product3 = new JsonObject().put("name", "eggs").put("quantity", 3);

        client.post("/shopping")
            .rxSendJsonObject(product1)
            .flatMap(x -> client.post("/shopping").rxSendJsonObject(product2))
            .flatMap(x -> client.post("/shopping").rxSendJsonObject(product3))
            .flatMap(x -> client.get("/shopping").as(BodyCodec.jsonObject()).rxSend())
            .doAfterTerminate(vertx::close)
            .subscribe(resp -> dump("GET /shopping", resp), ShoppingBackendClient::dump);
    }

    private static void list(Vertx vertx, WebClient client) {
        client.get("/shopping")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .doAfterTerminate(vertx::close)
            .subscribe(
                resp -> dump("GET /shopping", resp),
                ShoppingBackendClient::dump
            );
    }

    private static void add(Vertx vertx, WebClient client, Args args) {
        if (args.product == null  || args.product.isEmpty()) {
            System.out.println("Missing product name (-product)");
            return;
        }
        JsonObject body = new JsonObject().put("name", args.product).put("quantity", args.quantity);
        client.post("/shopping")
            .as(BodyCodec.jsonObject())
            .rxSendJsonObject(body)
            .doAfterTerminate(vertx::close)
            .subscribe(
                resp -> dump("GET /shopping", resp),
                ShoppingBackendClient::dump
            );
    }

    private static void remove(Vertx vertx, WebClient client, Args args) {
        if (args.product == null  || args.product.isEmpty()) {
            System.out.println("Missing product name (-product)");
            return;
        }
        client.delete("/shopping/" + args.product)
            .as(BodyCodec.jsonObject())
            .rxSend()
            .doAfterTerminate(vertx::close)
            .subscribe(
                resp -> dump("DELETE /shopping/" + args.product, resp),
                ShoppingBackendClient::dump
            );
    }

    private static void dump(String line, HttpResponse<JsonObject> resp) {
        System.out.println(line);
        resp.headers().names().forEach(header -> System.out.println(header + " : " + resp.getHeader(header)));
        System.out.println("---------------");
        System.out.println(resp.body().encodePrettily());
        System.out.println("---------------");
    }

    private static void dump(Throwable err) {
        System.out.println("Unable to call the shopping-backend service");
        err.printStackTrace();
    }

}
