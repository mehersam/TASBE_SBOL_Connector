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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class PreferencesDialogOLD extends JDialog implements ActionListener {
 
	private EnvInfo env = null;
	private String tasbe_loc = "";
	private String CM_scr_loc = "";

	private JFrame pref_frame = null;
	private JButton tasbe_button = new JButton("Browse");
	private JButton cm_script_button = new JButton("Browse");

	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");

	private JTextField tasbeLocText = null; 
	private JTextField CMLocText = null; 

	public PreferencesDialogOLD(EnvInfo _env) {
		if(_env != null)
			env = _env; 
		else
			env = new EnvInfo();
		
		tasbeLocText = new JTextField(env.getTASBELocation()); 
		CMLocText = new JTextField(env.getCMScript()); 
		
		tasbe_button.addActionListener(this);
		cm_script_button.addActionListener(this);

		cancelButton.addActionListener(this);

		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		pref_frame = new JFrame();
		pref_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pref_frame.setTitle("Preferences");
		pref_frame.setSize(500,500); 
		pref_frame.setLocationRelativeTo(null);

		FormBuilder preferences_builder = new FormBuilder();
		preferences_builder.add("Color Model Location", CMLocText, cm_script_button);
		preferences_builder.add("TASBE Library Location", tasbeLocText, tasbe_button);
		JPanel prefPanel = preferences_builder.build();
		prefPanel.setAlignmentX(LEFT_ALIGNMENT);
		prefPanel.setSize(500, 500);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalStrut(100));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		Container preferencesPane = getContentPane();
		preferencesPane.add(prefPanel, BorderLayout.CENTER);
		preferencesPane.add(buttonPane, BorderLayout.PAGE_END);
		((JComponent) preferencesPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pref_frame.add(preferencesPane);
		pref_frame.pack();
		pref_frame.setVisible(true);
	}

	public EnvInfo getEnv() {
		return this.env;
	}

	public String getCMLoc() {
		return env.getCMScript();
	}

	public String getTasbeLoc() {
		return env.getTASBELocation();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (env.getCMScript() != null && env.getTASBELocation() != null) {
				pref_frame.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "No TASBE Library Location was chosen or previously saved.");
				new PreferencesDialogOLD(env); 
				pref_frame.setVisible(false);
			}
		}

		if (e.getSource() == cancelButton) {
			if (env.getCMScript() != null && env.getTASBELocation() != null) {
				cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
				pref_frame.setVisible(false);
			} else{
				JOptionPane.showMessageDialog(this, "No Color Model Script was chosen or previous saved.");
				new PreferencesDialogOLD(env); 
				pref_frame.setVisible(false);
			}
		}
		if (e.getSource() == tasbe_button) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(this.getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				tasbe_loc = chooser.getSelectedFile().getAbsolutePath();
				if (env.getTASBELocation() == null) {
					env.setTASBELocation(tasbe_loc);
				}
				else if(tasbe_loc != "")
				{
					env.setTASBELocation(tasbe_loc);
				}
			}
			tasbeLocText.setText(env.getTASBELocation()); 
		}
		
		if (e.getSource() == cm_script_button) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(this.getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				CM_scr_loc = chooser.getSelectedFile().getAbsolutePath();
				if (env.getCMScript() == null) {
					env.setCMScript(CM_scr_loc);
				}
				else if(CM_scr_loc != "")
				{
					env.setCMScript(CM_scr_loc);
				}
			}
			CMLocText.setText(env.getCMScript()); 
		}
	}
}
