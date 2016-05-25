package com.kevin.shoppinglist;

import java.io.Serializable;

/**
 * Created by kevinwetzel on 20.05.16.
 */
public class Product implements Serializable{
private final static  String TAG = Product.class.getSimpleName();
    
    private  long id;
    private  String productName;
    private  int quantity;

    public Product(long id, String name, int quantity) {
        this.id = id;
        this.productName = name;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        String output = quantity + " X " + productName;
        return output;
    }
}
