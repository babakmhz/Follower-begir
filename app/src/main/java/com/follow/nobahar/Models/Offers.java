package com.follow.nobahar.Models;

public class Offers {

    int id;
    int price;
    int type;
    int count;
    int price_discount;
    String Rsa;

    public Offers() {
    }

    public Offers(int id, int price, int type, int count, int price_discount, String rsa) {
        this.id = id;
        this.price = price;
        this.type = type;
        this.count = count;
        this.price_discount = price_discount;
        Rsa = rsa;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice_discount() {
        return price_discount;
    }

    public void setPrice_discount(int price_discount) {
        this.price_discount = price_discount;
    }

    public String getRsa() {
        return Rsa;
    }

    public void setRsa(String rsa) {
        Rsa = rsa;
    }
}
