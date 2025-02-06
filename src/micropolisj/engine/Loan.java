package micropolisj.engine;

public class Loan
{
    private final Micropolis city;

    // 'week' when loan was taken (cityTime)
    int withdrawTime;
    // value of loan, added to player funds
    int initAmt;
    // amount of loan left to be paid off (starts at initAmt)
    public int unpaidBalance;
    // interest that will be applied to loan
    double interest;
    // period (in years) in which loan must be paid off
    int payPeriod;
    // years of automatic payments left until loan paid off (starts at payPeriod)
    public int yearsLeft;
    // maximum amount to be paid off in yearly installments (if not paid off early)
    public int maxDue;
    // yearly installments to be paid on loan
    public int yearlyCost;
    // portion of yearly payments that actually deduct from unpaidBalance
    int yearlyDeduct;

    public Loan(Micropolis city, int withdrawTime, int initAmt, int unpaidBalance, double interest, int payPeriod, int yearsLeft, int maxDue, int yearlyCost, int yearlyDeduct)
    {
        this.city = city;
        this.withdrawTime = withdrawTime;
        this.initAmt = initAmt;
        this.unpaidBalance = unpaidBalance;
        this.interest = interest;
        this.payPeriod = payPeriod;
        this.yearsLeft = yearsLeft;
        this.maxDue = maxDue;
        this.yearlyCost = yearlyCost;
        this.yearlyDeduct = yearlyDeduct;
    }

    public void payCost()
    {
        city.spend(yearlyCost);
        unpaidBalance -= yearlyDeduct;
        yearsLeft -= 1;
    }

    public void payFull()
    {
        city.spend(unpaidBalance);
        unpaidBalance = 0;
    }
}
