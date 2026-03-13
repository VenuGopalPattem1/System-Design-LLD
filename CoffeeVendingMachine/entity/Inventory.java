package CoffeeVendingMachine.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import CoffeeVendingMachine.enums.Ingrediants;

public class Inventory {
    Map<Ingrediants,Integer> stock=new HashMap<>();
    
    public Inventory() {
        // pre-load default stock
        stock.put(Ingrediants.WATER,        1000);
        stock.put(Ingrediants.MILK,         1000);
        stock.put(Ingrediants.COFFEE_BEANS, 500);
        stock.put(Ingrediants.SUGAR,        300);
    }

    public boolean hasEnough(Map<Ingrediants,Integer> req){
        return req.entrySet().stream()
               .allMatch(e->stock.getOrDefault(e.getKey(),0)>=e.getValue());
    }

    public void deduct(Map<Ingrediants,Integer> req){
        req.forEach((a,b)->stock.merge(a, -b, Integer::sum));
    }

    public void refill(Ingrediants in,Integer val){
        stock.merge(in, val, Integer::sum);
    }

    public Map<Ingrediants,Integer> getStock(){
        return Collections.unmodifiableMap(stock);
    }
}
