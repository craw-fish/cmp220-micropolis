package micropolisj.engine;
import java.util.ArrayList;

public class LoansManager {
    private final Micropolis city;

    public ArrayList<Loan> activeLoans = new ArrayList<Loan>();

    public LoansManager(Micropolis city)
    {
        this.city = city;
    }

    public int getMaxLoan()
    {
        int cityPop = city.getCityPopulation();
        // magnitude of square root function
        // higher value -> faster maxLoan growth relative to cityPop
        double yscale = 25.0;
        // maxLoan value when cityPop = 0
        double yintercept = 1000.0;
        // follows formula: y = k * sqrt(x + c)
        // where x = cityPop, k = yscale, c = computed value for y(0) = yintercept
        int maxLoan = (int)Math.floor(
            yscale * Math.sqrt(
                cityPop + Math.pow(yintercept / yscale, 2)
            )
        );

        return maxLoan;
    }

    public void startLoan(int initAmt)
    {
        if (activeLoans.size() < 3) {
            int withdrawTime = city.cityTime;
            int unpaidBalance = initAmt;
            double interest = getInterest();
            int payPeriod = getPayPeriod();
            int yearsLeft = payPeriod;
            int maxDue = (int)Math.round(initAmt * (1 + interest));
            int yearlyCost = (int)Math.round(maxDue / payPeriod);
            int yearlyDeduct = (int)Math.round(initAmt / payPeriod);

            // FIXME: this contributes neg amt to "Capital Expenditures" in budget - OK?
            city.spend(-initAmt);

            Loan loan = new Loan(city, withdrawTime, initAmt, unpaidBalance, interest, payPeriod, yearsLeft, maxDue, yearlyCost, yearlyDeduct);
            activeLoans.add(loan);
        }

        // else do nothing, forget this loan
    }

    public double getInterest()
    {
        switch (city.gameLevel) {
            case 0: return 0.05;
            case 1: return 0.12;
            case 2: return 0.20;
            default:
                throw new Error("unexpected game level: " + city.gameLevel);
        }
    }

    public int getPayPeriod()
    {
        switch (city.gameLevel) {
            case 0: return 15;
            case 1: return 8;
            case 2: return 6;
            default:
                throw new Error("unexpected game level: " + city.gameLevel);
        }
    }

    // called weekly
    public void payLoansYearly()
    {
        for (Loan loan : activeLoans) {
            int loanElapsed = city.cityTime - loan.withdrawTime;
            // every 48 weeks (1 year) since loan start, pay yearly cost
            if (loanElapsed % 48 == 0) {
                loan.payCost();
                loan.yearsLeft -= 1;
                if (loan.yearsLeft == 0) {
                    activeLoans.remove(loan);
                }
            }
        }
    }
}
