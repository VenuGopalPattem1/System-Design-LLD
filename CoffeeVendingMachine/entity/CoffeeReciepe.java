package CoffeeVendingMachine.entity;

import java.util.Map;

import CoffeeVendingMachine.enums.CoffeeType;
import CoffeeVendingMachine.enums.Ingrediants;

public class CoffeeReciepe {
    private CoffeeType type;
    private Map<Ingrediants,Integer> ingrediants;
    private double price;

    public CoffeeReciepe(CoffeeType type, Map<Ingrediants,Integer> ingrediants, double price){
        this.type=type;
        this.ingrediants=ingrediants;
        this.price=price;
    }

    public CoffeeType getType() {
        return type;
    }

    public Map<Ingrediants, Integer> getIngrediants() {
        return ingrediants;
    }

    public double getPrice() {
        return price;
    }

    
}