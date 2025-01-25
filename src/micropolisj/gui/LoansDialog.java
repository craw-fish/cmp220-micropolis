package micropolisj.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import micropolisj.engine.*;
import static micropolisj.gui.MainWindow.formatFunds;
import static micropolisj.gui.MainWindow.formatGameDate;

public class LoansDialog extends JDialog
{
    Micropolis engine;

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


        // *add dialog sections (panes), separated by horizontal lines

        // mainBox.add(makeEXAMPLE1Pane());

		// JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		// mainBox.add(sep);

        // mainBox.add(makeEXAMPLE2Pane());

		// JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		// mainBox.add(sep1);


        // *add buttons at bottom of dialog

        // JPanel buttonPane = new JPanel();
        // add(buttonPane, BorderLayout.SOUTH);

        // JButton EXAMPLE1Btn = new JButton(strings.getString("loansdlg.EXAMPLE1"));
		// EXAMPLE1Btn.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent ev) {
		// 		onEXAMPLE1Clicked();
		// 	}});
		// buttonPane.add(EXAMPLE1Btn);

        // JButton EXAMPLE2Btn = new JButton(strings.getString("loansdlg.EXAMPLE2"));
		// EXAMPLE2Btn.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent ev) {
		// 		onEXAMPLE2Clicked();
		// 	}});
		// buttonPane.add(EXAMPLE2Btn);



        // boilerplate dialog stuff
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

    // private void onEXAMPLE1Clicked()
	// {

    // }

    // private void onEXAMPLE2Clicked()
	// {

    // }
}
