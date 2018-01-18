
package SBOL_TASBE_Connector.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openrdf.model.URI;

import com.google.common.base.Strings;

import SBOL_TASBE_Connector.GUI.PreferencesDialog.PreferencesTab;
import edu.utah.ece.async.sboldesigner.sbol.editor.Images;
import edu.utah.ece.async.sboldesigner.sbol.editor.Registries;
import edu.utah.ece.async.sboldesigner.sbol.editor.Registry;
import edu.utah.ece.async.sboldesigner.swing.FormBuilder;

public enum EnvInfoTab implements PreferencesTab {
	INSTANCE;

	private String tasbe_loc = "";
	private String CM_scr_loc = "";

	private JButton tasbe_button = new JButton("Browse");
	private JButton cm_script_button = new JButton("Browse");

	private JTextField tasbeLocText = null;
	private JTextField CMLocText = null;

	@Override
	public String getCM() {
		// TODO Auto-generated method stub
		return CM_scr_loc;
	}

	@Override
	public String getTASBE() {
		return tasbe_loc;
	}

	@Override
	public String getTitle() {
		return "User";
	}

	@Override
	public String getDescription() {
		return "User information added to designs";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(Images.getActionImage("user.png"));
	}

	@Override
	public Component getComponent() {
		EnvInfo env = TASBEPreferences.INSTANCE.getEnvInfo();
		FormBuilder builder = new FormBuilder();
		CMLocText = builder.addTextField("", env == null ? null : env.getCMLoc());
		tasbeLocText = builder.addTextField("", env == null || env.getTASBELoc() == null ? null : env.getTASBELoc());
		CM_scr_loc = CMLocText.getText();
		tasbe_loc = tasbeLocText.getText(); 
		builder.add("Color Model Script", CMLocText, cm_script_button);
		builder.add("TASBE Library Location", tasbeLocText, tasbe_button);

		cm_script_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					CM_scr_loc = chooser.getSelectedFile().getAbsolutePath();
					CMLocText.setText(CM_scr_loc);
				}
			}

		});
		
		tasbe_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					tasbe_loc = chooser.getSelectedFile().getAbsolutePath();
					tasbeLocText.setText(tasbe_loc);
				}
				
			}
		});
		
		
		if(CM_scr_loc != "" && tasbe_loc != "")
		{
			env = Infos.forEnv(CM_scr_loc, tasbe_loc);
			TASBEPreferences.INSTANCE.saveUserInfo(env);
			
		}
		
		JPanel formPanel = builder.build();

		JButton deleteInfo = new JButton("Delete env info");
		deleteInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EnvInfo userInfo = Infos.forEnv(CMLocText.getText(), tasbeLocText.getText());
				TASBEPreferences.INSTANCE.saveUserInfo(userInfo);
				CMLocText.setText(null);
				tasbeLocText.setText(null);
			}
		});
		deleteInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		deleteInfo.setEnabled(env != null);

		Box buttonPanel = Box.createHorizontalBox();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(deleteInfo);

		JPanel p = new JPanel(new BorderLayout());
		p.add(formPanel, BorderLayout.NORTH);
		p.add(buttonPanel, BorderLayout.SOUTH);

		return p;
	}

	@Override
	public void save() {
		boolean noCM = Strings.isNullOrEmpty(CMLocText.getText());
		boolean noTASBE = Strings.isNullOrEmpty(tasbeLocText.getText());

		String envCM = noCM ? "" : CMLocText.getText();
		String envTASBE = noTASBE ? "" : tasbeLocText.getText();
		EnvInfo info = Infos.forEnv(envCM, envTASBE);
		TASBEPreferences.INSTANCE.saveUserInfo(info);
	}

	@Override
	public boolean requiresRestart() {
		return false;
	}

}