package VendingMachine.enums;

public enum Coins {
    ONE(1),
    TWO(2),
    FIVE(5),
    TEN(10);

    private int val;
    Coins(int val){
        this.val=val;
    }
    public int getVal() {
        return val;
    }
}
