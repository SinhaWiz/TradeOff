package src;

public class Leverage {
    private double multiplier;
    public Leverage(double multiplier)
    {
        if(multiplier <= 1)
        {
        throw new IllegalArgumentException("Multiplier must be greater than 0");
        }
        this.multiplier = multiplier;
    }

    public double getMultiplier()
    {
        return multiplier;
    }
    public double applyLeverage(double amount)
    {
        return amount * multiplier;
    }
    public double CalculateBorrowedAmount(double amount)
    {
        return amount * (multiplier-1);
    }
}
