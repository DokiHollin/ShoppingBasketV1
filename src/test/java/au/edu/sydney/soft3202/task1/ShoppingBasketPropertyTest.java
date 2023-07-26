package au.edu.sydney.soft3202.task1;

import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShoppingBasketPropertyTest {

    @Property
    void getValueEmptyPropertyTest() {
        ShoppingBasket sb = new ShoppingBasket();
        assertThat(sb.getValue()).isEqualTo(null);

    }

    @Property(tries = 50)
    void addingItemsReflectsCost(@ForAll @Positive int n) {
        ShoppingBasket basket = new ShoppingBasket();
        basket.addItem("apple", n);
        assertThat(basket.getValue()).isEqualTo(basket.values.get("apple") * n);
    }

    @Provide
    Arbitrary<ShoppingBasket> shoppingBaskets() {
        List<String> items = Arrays.asList("apple", "banana", "pear", "orange");
        Arbitrary<Integer> index = Arbitraries.integers().between(0, items.size() - 1);

        return Arbitraries.integers().between(1, 100).flatMap(numberOfItems -> {
            return index.map(itemIndex -> {
                String selectedItem = items.get(itemIndex);
                ShoppingBasket basket = new ShoppingBasket();
                double cost = Math.random() * 1000;
                basket.addItem(selectedItem, numberOfItems);
                return basket;
            });
        });
    }


    @Property(tries = 50)
    void valueShouldBeZeroAfterAllItemsRemoved(@ForAll("shoppingBaskets") ShoppingBasket basket) {
        Set<String> itemNames = basket.items.keySet();
        for(String each: itemNames){
            int count = basket.items.get(each);
            if(count != 0){
                basket.removeItem(each, count);
            }
        }

        assertThat(basket.getValue()).isNull();

    }



    @Test
    public void addItemEdgeTets(){
        ShoppingBasket basket = new ShoppingBasket();
        assertThrows(IllegalArgumentException.class,()->{
            basket.addItem("abc",1);
        });
        assertThrows(IllegalArgumentException.class,()->{
            basket.addItem("apple",-1);
        });
        assertThrows(IllegalArgumentException.class,()->{
            basket.addItem("apple",Integer.MAX_VALUE);
            basket.addItem("apple",1);
        });
    }

    @Test
    public void removeItemEdgeTets(){
        ShoppingBasket basket = new ShoppingBasket();
        assertThrows(IllegalArgumentException.class,()->{
            basket.removeItem(null,1);
        });
        assertThrows(IllegalArgumentException.class,()->{
            basket.removeItem("apple",-1);
        });
        assertFalse(basket.removeItem("abc",3));


    }



}
