package CoffeeVendingMachine;

import CoffeeVendingMachine.entity.CoffeeMachine;
import CoffeeVendingMachine.enums.CoffeeType;

public class Main {
    public static void main(String[] args) {
        CoffeeMachine machine=new CoffeeMachine();
        machine.showMenu();
        machine.showInventory();
        System.out.println("------------------");

        machine.inserCoin(2.50);
        machine.selectCoffee(CoffeeType.CAPPUCCINO);
        machine.showMenu();
        machine.showInventory();

        machine.inserCoin(2.50);
        machine.selectCoffee(CoffeeType.ESPRESSO); 

         machine.inserCoin(1.00);
        machine.cancel();  // refunds $1.00

    }
}
