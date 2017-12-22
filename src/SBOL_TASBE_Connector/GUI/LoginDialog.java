package SBOL_TASBE_Connector.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.synbiohub.frontend.SynBioHubException;

import SBOL_TASBE_Connector.Connector;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	/**
	 * GUI for creating an instance of the Connector and login to SYB
	 * @author Meher Samineni
	 */
	private String backendUrl;
	private Connector connect = null; 
	private BrowseDialog browse = null; 
	private JFrame loginFrame = null;
	private final JButton loginButton = new JButton("Login");
	private final JButton cancelButton = new JButton("Cancel");
	private final JTextField uriPrefix = new JTextField("https://choe.utah.edu");
	private final JTextField username = new JTextField("");
	private JTextField user = new JTextField(""); 
	private final JPasswordField password = new JPasswordField("");

	public LoginDialog(String backendUrl) {
		this.backendUrl = backendUrl;

		cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		cancelButton.addActionListener(this);

		loginButton.addActionListener(this);
		getRootPane().setDefaultButton(loginButton);

		loginFrame = new JFrame();
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setTitle("SynBioHub Login");
		loginFrame.setSize(500, 500);
		loginFrame.setLocationRelativeTo(null);

		FormBuilder builder = new FormBuilder();
		builder.add("Prefix", uriPrefix);
		builder.add("User", user);
		builder.add("Username", username);
		builder.add("Password", password);
		JPanel mainPanel = builder.build();
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalStrut(100));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(loginButton);

		JLabel infoLabel = new JLabel(
				"Login to SynBioHub account.  This enables you to upload parts and access your private constructs.");

		Container contentPane = getContentPane();
		contentPane.add(infoLabel, BorderLayout.PAGE_START);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);
		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		loginFrame.add(contentPane);

		loginFrame.pack();
		loginFrame.setVisible(true);

	}

	public Connector getConnector() {
		return connect;
	}
	
	public BrowseDialog getBrowseDialog()
	{
		return this.browse; 
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			loginFrame.setVisible(false);
			return;
		}

		if (e.getSource() == loginButton) {
			try {
				if(uriPrefix.getText() != ""){
				connect = new Connector(backendUrl, uriPrefix.getText());
				connect.login(username.getText(), new String(password.getPassword()));
				connect.setUser(user.getText());
				loginFrame.setVisible(false);
				if(loginFrame.isVisible() == false)
					browse = new BrowseDialog(connect);   
				return;
				}
			} catch (SynBioHubException e1) {

				JOptionPane.showMessageDialog(this, "Login failed" + e1.getMessage());
				connect = null;
				e1.printStackTrace();
			}
			setVisible(false);
			return;
		}
	}

}
