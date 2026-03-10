package VendingMachine.state;

import VendingMachine.entity.Product;
import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;

public interface VendingMachineState {
    void selectProduct(Product p);
    void insertCoin(Coins c);
    void insertNote(Notes n);
    void dispenseProduct();
    void returnChange();
}
