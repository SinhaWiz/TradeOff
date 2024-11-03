package src;
public class Coin {
    private double price;
    private String name;

    public Coin(String name, double price) {
        this.name = name;
        this.price = price;
    }


    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
