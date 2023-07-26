package au.edu.sydney.soft3202.task1;

import java.util.ArrayList;

public class Customer {
    String name;
    ShoppingBasket basket;

    public Customer(String name){
        this.name = name;
        this.basket = new ShoppingBasket();
    }

    public ShoppingBasket getBasket(){
        return basket;
    }

    public String getName(){
        return name;
    }

}
