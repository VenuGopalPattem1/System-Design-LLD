package CoffeeVendingMachine.entity;

public class PaymentService {
    private double balance=0;

    public void inserCoin(double amt){
        balance+=amt;
        System.out.println("Inserted amount : "+amt+" | Balance "+balance);
    }

    public boolean hasSufficientBalance(double price){
        return balance>=price;
    }

    public double collectPayment(double amt){
        double d=balance-amt;
        balance=0;
        return d;
    }

    public double refund(){
        double d=balance;
        balance=0;
        return d;
    }
    public double getBalance() {
        return balance;
    }
}
