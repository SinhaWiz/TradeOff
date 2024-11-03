package src;

public class LongTrade extends Trade{
    private double entryPrice;

    public LongTrade(Coin coin,double quantity)
    {
        super(coin,quantity);
        this.entryPrice = coin.getPrice();
    }
    @Override
    public void executeTrade() {
        if(isOpen())
        {
            System.out.println("LongTrade is open: "+"Executing longTrade for "+ quantity +" of" + coin.getName()+"at "+ entryPrice);
            // wallet update logic will be updated
        }
        else
        {
            System.out.println("The trade os not currently open");
        }

    }

    @Override
    public double calcGainLoss()
    {
        double currentPrice = coin.getPrice();
        return  (currentPrice - entryPrice)*quantity;
    }

    @Override
    public void closeTrade() {
        if(isOpen())
        {
            isOpen = false;
            System.out.println("Closing LongTrade for "+quantity+" of "+coin.getName());
            System.out.println("Total gain/loss: "+ calcGainLoss());
            //update Player Balance method will be updated
        }
        else
        {
            System.out.println("The trade is already closed");
        }

    }
}
