package me.escoffier.demo;


import com.beust.jcommander.Parameter;

import java.net.URL;

public class Args {

    @Parameter(names = {"-u", "-url"}, description = "Root url of the shopping backend service", required = true)
    URL url;

    @Parameter(names = {"-product", "-p"}, description = "Name of the product")
    String product;

    @Parameter(names = {"-quantity", "-q"}, description = "Quantity of the product")
    int quantity = 1;

    @Parameter(names = {"-a", "-action"}, description = "Action to execute among 'get', 'add', 'remove', 'populate'")
    String action = "get";

    @Parameter(names = "--help", help = true)
    boolean help;

}
