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
	JLabel selectionAmtLbl = new JLabel(formatFunds(0));
	JLabel selectionCostLbl = new JLabel(formatFunds(0));
	JLabel selectionMaxLbl = new JLabel(formatFunds(0));

    static ResourceBundle strings = MainWindow.strings;

	private void applyChange()
	{
		double interest = engine.loansManager.getInterest();
		int payPeriod = engine.loansManager.getPayPeriod();
		int selectionAmt = ((Number) selectionSlider.getValue()).intValue();
		int selectionMax = (int)Math.round(selectionAmt * (1 + interest));
		int selectionCost = (int)Math.round(selectionMax / payPeriod);

		selectionAmtLbl.setText(formatFunds(selectionAmt));
		selectionCostLbl.setText(formatFunds(selectionCost));
		selectionMaxLbl.setText(formatFunds(selectionMax));
	}

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
		// FIXME: verify/adjust dimensions
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

		JLabel infoInterestHdr = new JLabel(strings.getString("loansdlg.new_interest"));
		JLabel infoInterestLbl = new JLabel(interest);
		JLabel infoPeriodHdr = new JLabel(strings.getString("loansdlg.new_period"));
		JLabel infoPeriodLbl = new JLabel(payPeriod);
		infoPanel.add(infoInterestHdr);
		infoPanel.add(infoInterestLbl);
		infoPanel.add(new JLabel(" | "));
		infoPanel.add(infoPeriodHdr);
		infoPanel.add(infoPeriodLbl);

		// LOAN SELECTION
		JPanel selectionPanel = new JPanel();
		newLoanPane.add(selectionPanel);
		selectionPanel.setLayout(new GridLayout(0, 4));

		// ROW 1 (headers)
		// FIXME: column spacing too wide
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
		selectionSlider.setMaximum(engine.loansManager.getMaxLoan());

		ChangeListener change = new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				applyChange();
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

		JButton withdrawButton = new JButton(strings.getString("loansdlg.new_withdraw_btn"));
		withdrawButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				onWithdrawClicked();
			}});
		withdrawPanel.add(withdrawButton);

		return newLoanPane;
	}

	private JComponent makeActiveLoansPane()
	{
		JPanel activeLoansPane = new JPanel();
		activeLoansPane.setLayout(new BoxLayout(activeLoansPane, BoxLayout.PAGE_AXIS));
		// FIXME: dimensions
		activeLoansPane.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));


		return activeLoansPane;
	}

	private JComponent makeFinalizePane()
	{
		JPanel finalizePane = new JPanel();
		finalizePane.setLayout(new BoxLayout(finalizePane, BoxLayout.PAGE_AXIS));
		// FIXME: dimensions
		finalizePane.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

		// "pay now" amount

		// "insufficient funds" message

		// "continue" and "cancel" buttons

		return finalizePane;
	}

    private void onWithdrawClicked()
	{
		int initAmt = ((Number) selectionSlider.getValue()).intValue();

		engine.loansManager.startLoan(initAmt);
		selectionSlider.setValue(0);
		// FIXME: update active loans display
    }

	// private void onContinueClicked()
	// {
	// 	System.out.println("continue");
    // }

	// private void onCancelClicked()
	// {
	// 	System.out.println("cancel");
    // }

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
