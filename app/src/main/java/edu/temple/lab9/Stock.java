package edu.temple.lab9;

public class Stock {
    String symb;

    String url;
    String charturl;

    Double price;
    Double change;

    String name;
    Double opening;



    public Stock(String symb) {
        this.symb = symb;
        url = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symb;
        charturl = "https://macc.io/lab/cis3515/?symbol=" + symb;

        price = 0.0;
        change = 0.0;
        name = "";
        opening = 0.0;

    }

    @Override
    public String toString() {
        return symb + " | " + name + " | " + price + " | " + change + " | " + opening;
    }
}
