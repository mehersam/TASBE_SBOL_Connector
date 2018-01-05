package SBOL_TASBE_Connector.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.Connector;
import edu.utah.ece.async.sboldesigner.sbol.editor.dialog.RegistryInputDialog;
import edu.utah.ece.async.sboldesigner.sbol.SBOLUtils.Types;
import edu.utah.ece.async.sboldesigner.sbol.SBOLUtils.Types.*;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	/**
	 * GUI for creating an instance of the Connector and login to SYB
	 * 
	 * @author Meher Samineni
	 */
	private String backendUrl;
	private Connector connect = null;
	private String tasbe_loc = "";
	private String CM_scr_loc = "";
	private String input_col = "";

	private JFrame loginFrame = null;
	private final JButton loginButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	// private final JTextField uriPrefix = new
	// JTextField("https://choe.utah.edu");
	private final JTextField username = new JTextField("mehersam251@gmail.com");
	private JTextField user = new JTextField("mehersam");
	private final JPasswordField password = new JPasswordField("S@ipav12");
	private JTextField input_document = new JTextField(
			"https://synbiohub.utah.edu/user/mehersam/TASBE_Tutorial_Example_Controls/TASBE_Tutorial_Example_Controls_collection/1");
	private JButton tasbe_button = new JButton("Browse");
	private JButton cm_script_button = new JButton("Browse");
	private JButton download_Collection_button = new JButton("Download Collection"); 
	
	//private JFrame 
	private JButton preferences_button = new JButton("Preferences");
	
	public LoginDialog(String backendUrl) {
		this.backendUrl = backendUrl;

		cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		cancelButton.addActionListener(this);

		loginButton.addActionListener(this);
		getRootPane().setDefaultButton(loginButton);

		tasbe_button.addActionListener(this);
		cm_script_button.addActionListener(this);
		download_Collection_button.addActionListener(this);
		
		loginFrame = new JFrame();
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setTitle("SynBioHub Login");
		loginFrame.setSize(500, 500);
		loginFrame.setLocationRelativeTo(null);

		FormBuilder builder = new FormBuilder();
		// builder.add("Prefix", uriPrefix);
		//builder.add("User", user);
		//builder.add("Username", username);
		//builder.add("Password", password);
		//builder.add("FCS Collection", input_document);
		builder.add("Download FCS Collection", download_Collection_button);
		builder.add("Preferences", preferences_button);
		//builder.add("Color Model Location", cm_script_button);
		//builder.add("TASBE Library Location", tasbe_button);
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

	private void set_input_col(String _input_col) {
		this.input_col = _input_col;
	}

	public String get_input_col() {
		return this.input_col;
	}

	public void set_CM(String cm_scr) {
		this.CM_scr_loc = cm_scr;
	}

	public String get_CM() {
		return this.CM_scr_loc;
	}

	public String getTasbeLoc() {
		return this.tasbe_loc;
	}
	
	private void downloadSynBioHub()
	{
		RegistryInputDialog registryInputDialog = new RegistryInputDialog(this, null, RegistryInputDialog.ALL_PARTS,
				Types.All_types, null, new SBOLDocument());
		//registryInputDialog.allowCollectionSelection();
		SBOLDocument selection = registryInputDialog.getInput();
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			loginFrame.setVisible(false);
			System.exit(1);
			return;
		}
		if(e.getSource() == download_Collection_button)
		{
			downloadSynBioHub(); 
		}
		if (e.getSource() == loginButton) {
			try {
				loginButton.setVisible(false);
				downloadSynBioHub(); 
				connect = new Connector(backendUrl);
				connect.login(username.getText(), new String(password.getPassword()));
				connect.setUser(user.getText());
				System.out.println(connect == null);
				if (input_document.getText() != "") {
					set_input_col(input_document.getText());
					try {
						new ConnectorController(connect, this);
					} catch (EngineException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					} catch (SynBioHubException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					} catch (SBOLValidationException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					} catch (URISyntaxException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					} catch (InterruptedException e1) {
						JOptionPane.showMessageDialog(loginFrame, e1.getMessage());
						System.exit(1);
					}
				} else if (input_document.getText() == "") {
					JOptionPane.showMessageDialog(loginFrame, "Input FCS Collection URI cannot be empty");
				} else {
					JOptionPane.showMessageDialog(loginFrame, "Please enter a valid URI");
				}
				loginFrame.setVisible(false);

				return;
			}

			catch (SynBioHubException e1) {

				JOptionPane.showMessageDialog(this, "Login failed" + e1.getMessage());
				connect = null;
				e1.printStackTrace();
				System.exit(1);
			}
			setVisible(false);
			return;
		}

		if (e.getSource() == tasbe_button) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
			;
			int returnVal = chooser.showOpenDialog(this.getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				tasbe_loc = chooser.getSelectedFile().getAbsolutePath();
			}
		}

		if (e.getSource() == cm_script_button) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(this.getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				CM_scr_loc = chooser.getSelectedFile().getAbsolutePath();
			}
		}

	}

}
