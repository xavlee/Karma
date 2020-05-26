package edu.upenn.cis350.karma;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String item;
    private Long price;
    private Long quantity;


    public FoodItem(String item, Long price, Long quantity) {
        this.item = item;
        this.price = price;
        this.quantity = quantity;
    }


    public FoodItem(String item, Long price) {
        this.item = item;
        this.price = price;
    }


    public Long getPrice() {
        return price;
    }

    public String getItem() {
        return item;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
