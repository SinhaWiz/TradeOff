package src;

import java.time.LocalDateTime;

public abstract class Trade
{
    protected double quantity;
    protected Coin coin;
    protected LocalDateTime localDateTime;
    protected boolean isOpen;
    public Trade(Coin coin, double quantity)
    {
        this.coin = coin;
        this.quantity = quantity;
    }
}
