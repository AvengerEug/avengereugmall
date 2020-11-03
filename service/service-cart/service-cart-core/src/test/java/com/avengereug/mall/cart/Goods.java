package com.avengereug.mall.cart;

import java.io.Serializable;
import java.math.BigDecimal;

public class Goods implements Serializable {
    int id;
    String name;
    BigDecimal price;
    int count;

    public Goods() {
    }

    public Goods(int id, String name, BigDecimal price, int count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}