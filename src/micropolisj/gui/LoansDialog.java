package micropolisj.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import micropolisj.engine.*;
import static micropolisj.gui.MainWindow.formatFunds;

public class LoansDialog extends JDialog
{
    Micropolis engine;

	JSlider selectionSlider;
	JLabel selectionAmtVal = new JLabel(formatFunds(0));
	JLabel selectionCostVal = new JLabel(formatFunds(0));
	JLabel selectionMaxVal = new JLabel(formatFunds(0));

	JLabel totalUnpaidVal;
	JLabel totalCostVal;

	JLabel payVal;
	JLabel insuffText;

	int totalUnpaid;
	int totalCost;
	int totalPayNow = 0;

    static ResourceBundle strings = MainWindow.strings;

    public LoansDialog(Window owner, Micropolis engine)
    {
        super(owner);
		setTitle(strings.getString("loansdlg.title"));

		this.engine = engine;

        // draw dialog box
        Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		add(mainBox, BorderLayout.CENTER);

        // add dialog sections (panes), separated by horizontal lines
		mainBox.add(makeNewLoanPane());
		mainBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        mainBox.add(makeActiveLoansPane());
		mainBox.add(new JSeparator(SwingConstants.HORIZONTAL));
		mainBox.add(makeFinalizePane());

        // dialog boilerplate
        setAutoRequestFocus_compat(false);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

	private JComponent makeNewLoanPane()
	{
		JPanel newLoanPane = new JPanel();
		newLoanPane.setLayout(new BoxLayout(newLoanPane, BoxLayout.PAGE_AXIS));
		newLoanPane.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

		// SECTION TITLE
		JPanel titlePanel = new JPanel();
		newLoanPane.add(titlePanel);

		JLabel titleText = new JLabel(strings.getString("loansdlg.new_title"));
		titleText.setFont(titleText.getFont().deriveFont(Font.BOLD));
		titlePanel.add(titleText);

		// INTEREST & PAY PERIOD
		JPanel infoPanel = new JPanel();
		newLoanPane.add(infoPanel);

		String interest = (int)Math.floor(engine.loansManager.getInterest() * 100) + "%";
		String payPeriod = engine.loansManager.getPayPeriod() + " years";

		JLabel infoInterestLbl = new JLabel(strings.getString("loansdlg.new_interest"));
		JLabel infoInterestVal = new JLabel(interest);
		JLabel infoPeriodLbl = new JLabel(strings.getString("loansdlg.new_period"));
		JLabel infoPeriodVal = new JLabel(payPeriod);
		infoPanel.add(infoInterestLbl);
		infoPanel.add(infoInterestVal);
		infoPanel.add(new JLabel(" | "));
		infoPanel.add(infoPeriodLbl);
		infoPanel.add(infoPeriodVal);

		// LOAN SELECTION
		JPanel selectionPanel = new JPanel();
		newLoanPane.add(selectionPanel);
		selectionPanel.setLayout(new GridLayout(0, 4));

		// ROW 1 (headers)
		JLabel selectionAmtHdr = new JLabel(strings.getString("loansdlg.new_amt_hdr"));
		JLabel selectionCostHdr = new JLabel(strings.getString("loansdlg.new_cost_hdr"));
		JLabel selectionMaxHdr = new JLabel(strings.getString("loansdlg.new_max_hdr"));
		selectionCostHdr.setHorizontalAlignment(SwingConstants.CENTER);
		selectionAmtHdr.setHorizontalAlignment(SwingConstants.CENTER);
		selectionMaxHdr.setHorizontalAlignment(SwingConstants.CENTER);
		selectionPanel.add(new JLabel(" "));
		selectionPanel.add(selectionAmtHdr);
		selectionPanel.add(selectionCostHdr);
		selectionPanel.add(selectionMaxHdr);

		// ROW 2 (slider & values)
		// new loan amount slider
		selectionSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
		selectionSlider.setPreferredSize(new Dimension(120, selectionSlider.getPreferredSize().height));
		selectionSlider.setMaximum(engine.loansManager.getMaxLoan());
		selectionSlider.setMinorTickSpacing(50);
		selectionSlider.setSnapToTicks(true);

		ChangeListener change = new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				onSliderMoved();
			}
		};

		selectionSlider.addChangeListener(change);
		selectionPanel.add(selectionSlider);

		// new loan values
		selectionAmtVal.setHorizontalAlignment(SwingConstants.CENTER);
		selectionCostVal.setHorizontalAlignment(SwingConstants.CENTER);
		selectionMaxVal.setHorizontalAlignment(SwingConstants.CENTER);
		selectionPanel.add(selectionAmtVal);
		selectionPanel.add(selectionCostVal);
		selectionPanel.add(selectionMaxVal);

		// WITHDRAW BUTTON
		JPanel withdrawPanel = new JPanel();
		newLoanPane.add(withdrawPanel);

		JButton withdrawBtn = new JButton(strings.getString("loansdlg.new_withdraw_btn"));
		withdrawBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				onWithdrawClicked();
			}});
		withdrawPanel.add(withdrawBtn);

		return newLoanPane;
	}

	private JComponent makeActiveLoansPane()
	{
		JPanel activeLoansPane = new JPanel();
		activeLoansPane.setLayout(new BoxLayout(activeLoansPane, BoxLayout.PAGE_AXIS));
		activeLoansPane.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

		// SECTION TITLE
		JPanel titlePanel = new JPanel();
		activeLoansPane.add(titlePanel);

		JLabel titleText = new JLabel(strings.getString("loansdlg.active_title"));
		titleText.setFont(titleText.getFont().deriveFont(Font.BOLD));
		titlePanel.add(titleText);

		// ACTIVE LOANS LIST (table format)
		JPanel listPanel = new JPanel();
		activeLoansPane.add(listPanel);
		listPanel.setLayout(new GridLayout(0, 5));

		// loan property headers
		JLabel loanNumHdr = new JLabel(strings.getString("loansdlg.active_num_hdr"));
		JLabel loanUnpaidHdr = new JLabel(strings.getString("loansdlg.active_balance_hdr"));
		JLabel loanCostHdr = new JLabel(strings.getString("loansdlg.active_cost_hdr"));
		JLabel loanTimeHdr = new JLabel(strings.getString("loansdlg.active_time_hdr"));
		JLabel loanPayHdr = new JLabel(strings.getString("loansdlg.active_pay_hdr"));
		loanNumHdr.setHorizontalAlignment(SwingConstants.CENTER);
		loanUnpaidHdr.setHorizontalAlignment(SwingConstants.CENTER);
		loanCostHdr.setHorizontalAlignment(SwingConstants.CENTER);
		loanTimeHdr.setHorizontalAlignment(SwingConstants.CENTER);
		loanPayHdr.setHorizontalAlignment(SwingConstants.CENTER);
		listPanel.add(loanNumHdr);
		listPanel.add(loanUnpaidHdr);
		listPanel.add(loanCostHdr);
		listPanel.add(loanTimeHdr);
		listPanel.add(loanPayHdr);
		// FIXME: more vertical spacing between headers and each row of list

		// property values
		int loanNum = 0;
		for (Loan loan : engine.loansManager.activeLoans) {
			loanNum++;

			JLabel loanNumVal = new JLabel(String.valueOf(loanNum));
			JLabel loanUnpaidVal = new JLabel(formatFunds(loan.unpaidBalance));
			JLabel loanCostVal = new JLabel(formatFunds(loan.yearlyCost));
			JLabel loanTimeVal = new JLabel(String.valueOf(loan.yearsLeft));
			JCheckBox payNowCBox = new JCheckBox();

			payNowCBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					loan.isChecked = ((JCheckBox) ev.getSource()).isSelected();
					onCBoxClicked();
				}});

			loanNumVal.setHorizontalAlignment(SwingConstants.CENTER);
			loanUnpaidVal.setHorizontalAlignment(SwingConstants.CENTER);
			loanCostVal.setHorizontalAlignment(SwingConstants.CENTER);
			loanTimeVal.setHorizontalAlignment(SwingConstants.CENTER);
			payNowCBox.setHorizontalAlignment(SwingConstants.CENTER);
			listPanel.add(loanNumVal);
			listPanel.add(loanUnpaidVal);
			listPanel.add(loanCostVal);
			listPanel.add(loanTimeVal);
			listPanel.add(payNowCBox);
		}

		// totals
		totalUnpaid = engine.loansManager.getTotalUnpaid();
		totalCost = engine.loansManager.getTotalCost();

		JLabel totalLbl = new JLabel("Total");
		totalUnpaidVal = new JLabel(formatFunds(totalUnpaid));
		totalCostVal = new JLabel(formatFunds(totalCost));
		totalLbl.setFont(totalLbl.getFont().deriveFont(Font.ITALIC));
		totalUnpaidVal.setFont(totalUnpaidVal.getFont().deriveFont(Font.ITALIC));
		totalCostVal.setFont(totalCostVal.getFont().deriveFont(Font.ITALIC));
		totalLbl.setHorizontalAlignment(SwingConstants.CENTER);
		totalUnpaidVal.setHorizontalAlignment(SwingConstants.CENTER);
		totalCostVal.setHorizontalAlignment(SwingConstants.CENTER);
		listPanel.add(totalLbl);
		listPanel.add(totalUnpaidVal);
		listPanel.add(totalCostVal);

		return activeLoansPane;
	}

	private JComponent makeFinalizePane()
	{
		JPanel finalizePane = new JPanel();
		finalizePane.setLayout(new BoxLayout(finalizePane, BoxLayout.PAGE_AXIS));

		// "PAY NOW"
		JPanel payPanel = new JPanel();
		finalizePane.add(payPanel);

		JLabel payLbl = new JLabel(strings.getString("loansdlg.finalize_amt"));
		payVal = new JLabel(formatFunds(totalPayNow));
		payPanel.add(payLbl);
		payPanel.add(payVal);

		// "INSUFFICENT FUNDS"
		JPanel insuffPanel = new JPanel();
		finalizePane.add(insuffPanel);

		insuffText = new JLabel(strings.getString("loansdlg.finalize_insufficient"));
		insuffText.setFont(insuffText.getFont().deriveFont(Font.ITALIC));
		insuffText.setForeground(Color.red);
		insuffText.setVisible(false);

		insuffPanel.add(insuffText);

		// "CONTINUE" AND "CANCEL"
		JPanel buttonsPanel = new JPanel();
		finalizePane.add(buttonsPanel);

		JButton continueBtn = new JButton(strings.getString("loansdlg.finalize_continue_btn"));
		JButton cancelBtn = new JButton(strings.getString("loansdlg.finalize_cancel_btn"));
		continueBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				onContinueClicked();
			}});
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				onCancelClicked();
			}});
		buttonsPanel.add(continueBtn);
		buttonsPanel.add(cancelBtn);

		return finalizePane;
	}

	private void onSliderMoved()
	{
		double interest = engine.loansManager.getInterest();
		int payPeriod = engine.loansManager.getPayPeriod();
		int selectionAmt = ((Number) selectionSlider.getValue()).intValue();
		int selectionMax = (int)Math.round(selectionAmt * (1 + interest));
		int selectionCost = (int)Math.round(selectionMax / payPeriod);

		selectionAmtVal.setText(formatFunds(selectionAmt));
		selectionCostVal.setText(formatFunds(selectionCost));
		selectionMaxVal.setText(formatFunds(selectionMax));
	}

	private void onCBoxClicked()
	{
		totalPayNow = 0;

		for (Loan loan : engine.loansManager.activeLoans) {
			if (loan.isChecked) {
				totalPayNow += loan.unpaidBalance;
			}
		}

		payVal.setText(formatFunds(totalPayNow));
	}

    private void onWithdrawClicked()
	{
		int initAmt = ((Number) selectionSlider.getValue()).intValue();

		if (initAmt > 0 && engine.loansManager.activeLoans.size() < 3) {
			engine.loansManager.startLoan(initAmt);
			selectionSlider.setValue(0);
			// TODO: update active loans display here

			// update totals
			totalUnpaid = engine.loansManager.getTotalUnpaid();
			totalCost = engine.loansManager.getTotalCost();
			totalUnpaidVal.setText(formatFunds(totalUnpaid));
			totalCostVal.setText(formatFunds(totalCost));
		}
    }

	private void onContinueClicked()
	{
		// if sufficient funds, apply "pay now" amt and close window
		if (engine.budget.totalFunds >= totalPayNow) {
			Iterator<Loan> iterator = engine.loansManager.activeLoans.iterator();

			while (iterator.hasNext()) {
				Loan loan = iterator.next();

				if (loan.isChecked) {
					loan.payFull();
					iterator.remove();
				}
			}

			dispose();
		}
		// else display "insufficient funds"
		else {
			insuffText.setVisible(true);
			pack();
		}
    }

	private void onCancelClicked()
	{
		dispose();
    }

	private void setAutoRequestFocus_compat(boolean v)
	{
		try
		{
			if (super.getClass().getMethod("setAutoRequestFocus", boolean.class) != null) {
				super.setAutoRequestFocus(v);
			}
		}
		catch (NoSuchMethodException e) {
		}
	}
}
