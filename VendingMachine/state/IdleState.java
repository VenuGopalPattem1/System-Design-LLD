package VendingMachine.state;

import VendingMachine.entity.Product;
import VendingMachine.entity.VendingMachine;
import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;

public class IdleState implements VendingMachineState{
    private VendingMachine machine;
    public IdleState(VendingMachine machine){
        this.machine=machine;
    }
    @Override
    public void selectProduct(Product p) {
        if(!machine.getInventory().isAvailable(p)){
            System.out.println("Sorry, " + p.getName() + " is out of stock.");
            return;
        }
        machine.setSelectedProduct(p);
        machine.setCurrentState(machine.getReadyState());
        System.out.println("Selected: " + p + " | Insert Rs" + (int) p.getPrice());
    }

    @Override
    public void insertCoin(Coins c) {
        System.out.println("Select a product first.");
    }

    @Override
    public void insertNote(Notes n) {
        System.out.println("Select a product first.");
    }

    @Override
    public void dispenseProduct() {
        System.out.println("Select a product first.");
    }

    @Override
    public void returnChange() {
        System.out.println("No change to return."); 
    }
}
