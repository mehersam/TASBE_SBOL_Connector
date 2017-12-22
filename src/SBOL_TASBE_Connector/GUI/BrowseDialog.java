package SBOL_TASBE_Connector.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.Connector;

@SuppressWarnings("serial")
public class BrowseDialog extends JDialog implements ActionListener {
 

	private Connector connect = null; 
	private String tasbe_loc = ""; 
	private String CM_scr_loc = "";
	private JFrame inputDocFrame = null;
	
	//https://synbiohub.utah.edu/user/mehersam/TASBE_Tutorial_Example_Controls/TASBE_Tutorial_Example_Controls_collection/1
	private JTextField input_document = new JTextField("");
	//private JTextField version = new JTextField("1");
	private JTextField agent = new JTextField("https://github.com/TASBE/TASBEFlowAnalytics/releases/tag/2.0.2");
	private JTextField color_model = new JTextField("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/master/template_colormodel/make_color_model.m");
	private JButton tasbe_button = new JButton("Browse"); 
	private JButton cm_script_button = new JButton("Browse"); 
	private JButton okButton = new JButton("OK");
	private String input_col = "";
	
	public BrowseDialog(Connector _connector) {
		connect = _connector; 
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		tasbe_button.addActionListener(this);
		getRootPane().setDefaultButton(tasbe_button);
		cm_script_button.addActionListener(this);
		getRootPane().setDefaultButton(cm_script_button);
		
		inputDocFrame = new JFrame();
		inputDocFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputDocFrame.setTitle("Input FCS Collection");
		inputDocFrame.setSize(700, 700);
		inputDocFrame.setLocationRelativeTo(null);

		FormBuilder builder = new FormBuilder();
		builder.add("FCS Collection", input_document);
		builder.add("Color Model Location", color_model);
		builder.add("Agent", agent);
		builder.add("TASBE Library Location", tasbe_button);
		//builder.add("Color Model Location", color_model);
		JPanel mainPanel = builder.build();
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalStrut(100));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(okButton);

		Container contentPane = getContentPane();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);
		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		inputDocFrame.add(contentPane);
		inputDocFrame.pack();
		inputDocFrame.setVisible(true);

	}

	private void set_input_col(String _input_col) {
		this.input_col = _input_col;
	}

	public String get_input_col() {
		return this.input_col;
	}
	
	public void set_CM(String cm_scr)
	{
		this.CM_scr_loc = cm_scr; 
	}
	
	
	public String get_CM() {
		return this.CM_scr_loc;
	}
	
//	public String getVersion() {
//		return this.version.getText();
//	}
	
	public String getAgent() {
		return this.agent.getText();
	}
	
	public String getTasbeLoc()
	{
		return this.tasbe_loc; 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tasbe_button)
		{
			JFileChooser chooser = new JFileChooser(); 
			chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);; 
			int returnVal = chooser.showOpenDialog(this.getParent()); 
			if (returnVal == JFileChooser.APPROVE_OPTION) {
		         tasbe_loc = chooser.getSelectedFile().getAbsolutePath(); 
			}
		}
		
		if(e.getSource() == cm_script_button)
		{
			JFileChooser chooser = new JFileChooser(); 
			int returnVal = chooser.showOpenDialog(this.getParent()); 
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				CM_scr_loc = chooser.getSelectedFile().getAbsolutePath();
			}
		}
		if (e.getSource() == okButton) {
			if(color_model.getText() != "")
			{
				set_CM(color_model.getText()); 
			}
			if (input_document.getText() != "") {
				set_input_col(input_document.getText());
				inputDocFrame.setVisible(false);
				if(inputDocFrame.isVisible() == false)
					try {
						new ConnectorController(connect, this);
					} catch (EngineException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SynBioHubException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SBOLValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}   
				return;
			}
			else if(input_document.getText() == ""){
				JOptionPane.showMessageDialog(inputDocFrame, "Input FCS Collection URI cannot be empty");
			}
			else 
			{
				JOptionPane.showMessageDialog(inputDocFrame, "Please enter a valid URI");
			}

		}
		
	}

}
