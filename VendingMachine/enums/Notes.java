package VendingMachine.enums;

public enum Notes {
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100);

    private int val;
    Notes(int val){
        this.val=val;
    }

    public int getVal() {
        return val;
    }
}
