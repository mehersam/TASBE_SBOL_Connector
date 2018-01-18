package SBOL_TASBE_Connector.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

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

import org.apache.commons.httpclient.URIException;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.Connector;
import edu.utah.ece.async.sboldesigner.sbol.SBOLUtils.Types;
import edu.utah.ece.async.sboldesigner.sbol.editor.SBOLDesign;
import edu.utah.ece.async.sboldesigner.sbol.editor.dialog.RegistryInputDialog;


@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	/**
	 * GUI for creating an instance of the Connector and login to SYB
	 * 
	 * @author Meher Samineni
	 */
	private String backendUrl;
	private Connector connect = null;
	private String input_col = "";
	private SBOLDocument selection = null; 
	private JFrame loginFrame = null;
	private final JButton loginButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");

	private JButton download_Collection_button = new JButton("Download Collection");
	private JButton preferences_button = new JButton("Preferences");
	
	private PreferencesDialog prefDialog = null; 
	private EnvInfo env = null; 
	
	public LoginDialog(String backendUrl) {
		this.backendUrl = backendUrl;

		cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		cancelButton.addActionListener(this);

		loginButton.addActionListener(this);
		getRootPane().setDefaultButton(loginButton);

		download_Collection_button.addActionListener(this);
		preferences_button.addActionListener(this);
		
		loginFrame = new JFrame();
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setTitle("SynBioHub Login");
		loginFrame.setSize(500, 500);
		loginFrame.setLocationRelativeTo(null);

		FormBuilder builder = new FormBuilder();
		builder.add("Download FCS Collection", download_Collection_button);
		builder.add("Preferences", preferences_button);
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

	public PreferencesDialog getPref()
	{
		return this.prefDialog; 
	}
	
	public String getCMLoc()
	{
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");
		return prefs.get("cm", "");
	}
	public String getTASBELoc()
	{
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");

		return prefs.get("tasbe", "");	
	}
	
	private void downloadSynBioHub() {
		RegistryInputDialog registryInputDialog = new RegistryInputDialog(this, null, RegistryInputDialog.ALL_PARTS,
				Types.All_types, null, new SBOLDocument());
		 registryInputDialog.allowCollectionSelection();
		 registryInputDialog.setObjectType("Collection");
		 selection = registryInputDialog.getInput();
	}
	
	public void upload(SBOLDocument doc, File file) 
	{
		try {
			SBOLDesign.uploadDesign(this, doc, file);
		} catch (URIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SynBioHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SBOLValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			loginFrame.setVisible(false);
			System.exit(1);
			return;
		}
		if (e.getSource() == download_Collection_button) {
			downloadSynBioHub();
		}
		if (e.getSource() == loginButton) {
			try {
				loginButton.setVisible(false);
				connect = new Connector(backendUrl);
				connect.set_FCS_Doc(selection);
				System.out.println(connect == null);
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

		if(e.getSource() == preferences_button)
		{
			PreferencesDialog.showPreferences(LoginDialog.this, EnvInfoTab.INSTANCE.getTitle());	
		}
		
		

	}

}
