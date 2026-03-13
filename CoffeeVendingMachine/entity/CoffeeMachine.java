package CoffeeVendingMachine.entity;

import java.util.HashMap;
import java.util.Map;

import CoffeeVendingMachine.enums.CoffeeType;
import CoffeeVendingMachine.enums.Ingrediants;



public class CoffeeMachine {
    private Map<CoffeeType ,CoffeeReciepe> menu=new HashMap<>();
    private Inventory inventory=new Inventory();
    private PaymentService service=new PaymentService();
    private MachineState state=MachineState.IDLE;


    private enum MachineState { IDLE, PROCESSING, OUT_OF_SERVICE }

    public CoffeeMachine(){
        loadMenu();
    }

    private void loadMenu(){
        menu.put(CoffeeType.ESPRESSO, new CoffeeReciepe(
            CoffeeType.ESPRESSO,
            Map.of(Ingrediants.WATER, 50, Ingrediants.COFFEE_BEANS, 20),
            1.50
        ));
        menu.put(CoffeeType.CAPPUCCINO, new CoffeeReciepe(
            CoffeeType.CAPPUCCINO,
            Map.of(Ingrediants.WATER, 50, Ingrediants.MILK, 100, Ingrediants.COFFEE_BEANS, 20),
            2.50
        ));
        menu.put(CoffeeType.LATTE, new CoffeeReciepe(
            CoffeeType.LATTE,
            Map.of(Ingrediants.WATER, 50, Ingrediants.MILK, 150, Ingrediants.COFFEE_BEANS, 20, Ingrediants.SUGAR, 10),
            3.00
        ));
        menu.put(CoffeeType.BLACK_COFFEE, new CoffeeReciepe(
            CoffeeType.BLACK_COFFEE,
            Map.of(Ingrediants.WATER, 200, Ingrediants.COFFEE_BEANS, 15),
            1.00
        ));
    }

    public void inserCoin(double amt){
        if(state==MachineState.OUT_OF_SERVICE){
            throw new IllegalStateException("Machine is out of service");
        }
        service.inserCoin(amt);
    }

    public void selectCoffee(CoffeeType type){
        if(state!=MachineState.IDLE){
            throw new IllegalStateException("Machine is busy");
        }
        CoffeeReciepe recipe=menu.get(type);

        if (recipe == null)
            throw new IllegalArgumentException("Coffee not available: " + type);

        if (!service.hasSufficientBalance(recipe.getPrice())) {
            System.out.println("Insufficient balance. Need " + recipe.getPrice()
                + ", have " + service.getBalance());
            return;
        }

        if(!inventory.hasEnough(recipe.getIngrediants())){
            System.out.println("Insufficient ingredients for " + type);
            return;
        }
        state=MachineState.PROCESSING;
        brew(recipe);
    }

    private void brew(CoffeeReciepe recipe){
        System.out.println("Brewing " + recipe.getType() + "...");
        inventory.deduct(recipe.getIngrediants());
        double change=service.collectPayment(recipe.getPrice());
        System.out.println(" Enjoy your " + recipe.getType() + "!");
        if (change > 0) System.out.println(" Change returned: $" + change);
        state=MachineState.IDLE;
    }

    public void cancel(){
        double amt=service.refund();
        if (amt > 0) System.out.println("Refunded: $" + amt);
        state = MachineState.IDLE;
    }

    public void showMenu(){
        System.out.println("\n===== MENU =====");
        menu.forEach((type, recipe) ->
            System.out.printf("%-15s $%.2f%n", type, recipe.getPrice()));
    }

    public void showInventory() {
        System.out.println("\n=== INVENTORY ===");
        inventory.getStock().forEach((ing, qty) ->
            System.out.printf("%-15s %d%n", ing, qty));
    }
    
}
