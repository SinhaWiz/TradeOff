package src;

public class LongTrade extends Trade{
    private double entryPrice;
    private Player player;

    public LongTrade(Coin coin,double quantity)
    {
        super(coin,quantity);
        this.entryPrice = coin.getPrice();
    }
    @Override
    public void executeTrade() {
        if(isOpen())
        {
            double  totalCost=quantity*entryPrice;
            if(player.getBalance()>=totalCost)
            {
                player.updateBalance(-totalCost);
                player.addToPortfolio(coin,quantity);
                System.out.println("LongTrade is open: "+"Executing longTrade for "+ quantity +" of" + coin.getName()+"at "+ entryPrice);
            }
            else
            {
                System.out.println("Insufficient balance");
            }
            
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
            player.updateBalance(calcGainLoss());
        }
        else
        {
            System.out.println("The trade is already closed");
        }

    }
}
