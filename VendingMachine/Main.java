package VendingMachine;

import VendingMachine.entity.Product;
import VendingMachine.entity.VendingMachine;
import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;

public class Main {
    public static void main(String[] args) {
        VendingMachine vm=VendingMachine.getInstance();

        Product coke  = new Product("Coca Cola", 25);
        Product chips = new Product("Lays",      30);
        Product water = new Product("Water",     15);
        Product juice = new Product("Juice",     40);

        vm.getInventory().addProduct(coke, 3);
        vm.getInventory().addProduct(chips, 2);
        vm.getInventory().addProduct(water, 5);
        vm.getInventory().addProduct(juice, 0); // out of stock

        vm.getInventory().display();

        System.out.println("\n--- Case 1: Buy Coca Cola with coins ---");
        vm.selectProduct(coke);
        vm.insertCoin(Coins.TEN);
        vm.insertCoin(Coins.TEN);
        vm.insertCoin(Coins.FIVE);   // ₹25 exact → dispense

        // Case 2: Overpay with note → get change
        System.out.println("\n--- Case 2: Buy Lays, overpay with ₹50 ---");
        vm.selectProduct(chips);
        vm.insertNote(Notes.FIFTY);  // paid ₹50, price ₹30 → ₹20 change

        // Case 3: Out of stock
        System.out.println("\n--- Case 3: Out of stock ---");
        vm.selectProduct(juice);  


        // Case 4: Cancel mid-transaction
        System.out.println("\n--- Case 4: Cancel after inserting money ---");
        vm.selectProduct(coke);
        vm.insertNote(Notes.TWENTY);
        vm.returnChange();          // refund ₹20


         // Case 5: Insert money before selecting
        System.out.println("\n--- Case 5: Insert before selecting ---");
        vm.insertCoin(Coins.FIVE);

        vm.getInventory().display();

    }
}
