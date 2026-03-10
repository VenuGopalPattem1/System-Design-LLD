package VendingMachine.entity;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Product,Integer> hm=new HashMap<>();

    public void addProduct(Product p, int qty){
        hm.put(p,hm.getOrDefault(p,0)+qty);
    }

    public boolean isAvailable(Product p){
        return hm.getOrDefault(p,0)>0;
    }

    public void dispense(Product p) {
        if (!isAvailable(p)) throw new IllegalStateException(p.getName() + " out of stock!");
        hm.put(p, hm.get(p) - 1);
    }

    public void display() {
        System.out.println("\n--- INVENTORY ---");
        hm.forEach((p, qty) ->
            System.out.println("  " + p + "  Qty: " + qty));
    }
}
