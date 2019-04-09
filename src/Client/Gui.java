package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Font;
import javax.swing.ImageIcon;

public class Gui implements ActionListener{

	public JFrame frame;
	public JTextField textField;
	public JTextArea textArea;
	public JButton btnNewButton;
	private String in;
	private boolean isTextNotEmpty = false;
	public boolean isTextNotEmpty() {
		return isTextNotEmpty;
	}

	public void setTextNotEmpty(boolean isTextEmpty) {
		this.isTextNotEmpty = isTextEmpty;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setForeground(new Color(169, 169, 169));
		frame.setBackground(new Color(112, 128, 144));
		frame.setBounds(100, 100, 450, 363);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBackground(Color.WHITE);
		textArea.setEditable(false);
		textArea.setBounds(12, 13, 420, 229);
		frame.getContentPane().add(textArea);
		
		
		textField = new JTextField();
		textField.setBounds(12, 255, 420, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnNewButton = new JButton("Send Message");
		btnNewButton.setBackground(new Color(128, 128, 128));
		btnNewButton.setForeground(Color.BLUE);
		btnNewButton.addActionListener(this);
		btnNewButton.setActionCommand("send");
		btnNewButton.setBounds(148, 290, 147, 25);
		frame.getContentPane().add(btnNewButton);
	}
	
	public String getUserIn() {
		return in;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("send".equals(e.getActionCommand())) {
			in = textField.getText();
			isTextNotEmpty = true;
		}
	}
}
