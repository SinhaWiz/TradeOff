package src;

public class DisplayString {
     static void mainMenuOptions(){
        System.out.println("1. Start New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Exit");
    }
     static void welcomeMessage(){
        System.out.println("\n|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$| Welcome to TradeOff |$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|\n");
        System.out.println("You are broke and borrowed $1,000,000 from Loan Sharks.");
        System.out.println("You have to pay them $3,000,000 within 10 days.");
        System.out.println("If you don't, the consequences will be beyond your imagination.");
        System.out.println("Because of this short deadline, you have opted for cryptocurrency trading to earn this money ASAP.\n");
        System.out.println("You have " + 160 + " turns to make your fortune.");
    }
    static void secondaryMenuOptions(int marketInsiderAttempts ,double balance){
        System.out.println("\n     |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| MENU |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$|");
        System.out.println("|+|+|+| 1.View Market         |+|+|+| 2.View Portfolio |+|+|+| 3.Close Position       |+|+|+| 4.Open Long Position             |+|+|+|");
        System.out.println("|+|+|+| 5.Open Short Position |+|+|+| 6.Skip Turn      |+|+|+| 7.Skip a Day           |+|+|+| 8.Consult Market Insider  (" + marketInsiderAttempts + "/3)  |+|+|+|");
        System.out.println("|+|+|+| 9.Statistics          |+|+|+| 10.Save Game     |+|+|+| 11.Load Game           |+|+|+| 12.Exit Game                     |+|+|+|");
        System.out.println("\nBalance: $" + String.format("%.2f", balance));
    }
    static void blackMarketWarning(double currentPrice){
        System.out.println("WARNING: Consulting the market insider is a dangerous and costly activity!");
        System.out.println("His consulting cost is $" + currentPrice);
        System.out.println("If you get caught, you will be sent to jail and 20 turns will be skipped.");
        System.out.println("Do you wish to proceed? (y/n)");
    }
    static void caughtInAction(double currentPrice){
        System.out.println("Oh no! You have been caught by the authorities!");
        System.out.println("You will be in jail for 20 turns. The market will continue to move during this time.");
        System.out.println("Or you can pay your bail: $" + currentPrice * 2);
        System.out.println("Press 0 to pay bail, Press 1 to go to jail");
    }
    static void winningMessage(){
        System.out.println("\n=== Congratulations! ===");
        System.out.println("You've done the impossible! Not only did you pay off the loan sharks, but you also made a massive profit.");
        System.out.println("The loan sharks are impressed by your skills and even offer you a partnership in their 'legitimate' business ventures.");
        System.out.println("You walk away with your head held high, knowing you've beaten the odds and secured your future.");
        System.out.println("But remember... the crypto world is unpredictable. Will you stay in the game, or cash out and live the good life?");
        System.out.println("The choice is yours.");
    }
    static void losingMessage(){
        System.out.println("\n=== Game Over! ===");
        System.out.println("The loan sharks have come to collect their dues... and you don't have enough.");
        System.out.println("They take everything you have, leaving you with nothing but the clothes on your back.");
        System.out.println("As they drag you away, you realize the true cost of gambling with borrowed money.");
        System.out.println("The crypto world is unforgiving, and now you're paying the ultimate price.");
        System.out.println("Better luck next time... if there is a next time.");
    }
    static void noLossNoProfitMessage(){
        System.out.println("Sure, you've managed to break even and perhaps made some more money on the side.");
        System.out.println("But did you take a good look at your balance again?");
        System.out.println("Thought so, that definitely doesn't say 3 million...");
        System.out.println("If you get lucky, maybe they'll let you off the hook...");
        System.out.println("If...");
    }
    static void coinTableString(){
        System.out.println("\n=== Available Coins ===");
        System.out.println();
        System.out.println("_____________________________");
        System.out.println("|    Coin        |   Ticker  |");
        System.out.println("|________________|___________|");
    }
    static void marketTableString(){
        System.out.println("\n=== Current Market Prices ===");
        System.out.println();
        System.out.println("__________________________________________________________");
        System.out.println("|    Coin        |   Ticker  |   Price       |  Change    |");
        System.out.println("|_________________________________________________________|");
    }

}
