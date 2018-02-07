package SBOL_TASBE_Connector.GUI;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;

import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.Connector;

public class ConnectorController {

	private Connector syb_connector = null;
	private LoginDialog login = null;

	public ConnectorController(Connector _connect, LoginDialog loginDialog) throws SynBioHubException,
			SBOLValidationException, URISyntaxException, IOException, EngineException, InterruptedException {
		
		syb_connector = _connect;
		login = loginDialog;

		String tasbeURI = "https://tasbe.org/";

		SBOLDocument col_doc = syb_connector.get_FCS_Doc();
		Collection fcs = null; 
		
		// get the toplevel collection of input fcs files
		URI bead = null;
		URI blank = null;
		URI EYFP = null;
		URI mKate = null;
		URI EBFP2 = null;
		
		for(Collection input_files : col_doc.getCollections())
			for (TopLevel tl : input_files.getMembers()) {
			URI temp = null;
			for (Annotation a : tl.getAnnotations()) {
				if (a.getQName().getNamespaceURI().equals(tasbeURI) && a.getQName().getLocalPart().equals("fcs")) {
					temp = a.getURIValue();
					fcs = input_files; 
					break;
				}
			}
			if (temp != null) {
				for (Annotation a : tl.getAnnotations()) {
					if (a.getQName().getNamespaceURI().equals(tasbeURI)
							&& a.getQName().getLocalPart().equals("file_type")) {
						if (a.getStringValue().contains("bead")) {
							bead = temp;
						}
						if (a.getStringValue().contains("blank")) {
							blank = temp;
						}
						if (a.getStringValue().contains("EYFP")) {
							EYFP = temp;
						}
						if (a.getStringValue().contains("mKate")) {
							mKate = temp;
						}
						if (a.getStringValue().contains("EBFP2")) {
							EBFP2 = temp;
						}
						break;
					}
				}
			}
		}

		syb_connector.set_FCS_col(fcs);
		String displayId = "TASBE";
		String version = "1.0";
		String color_model_loc = login.getCMLoc();
		String tasbe_loc = login.getTASBELoc();

		System.out.println("Downloading Files");
		// download the files and save them in the Tasbe dir
		syb_connector.download_Files(tasbe_loc, color_model_loc, bead, blank, EYFP, mKate, EBFP2);

		// create the activity
		syb_connector.create_Activity(displayId + "_activity", displayId + "_color_model", displayId + "_agent",
				displayId + "_usage", version, new File(login.getCMLoc()).getName());

		// need to open a file explorer to choose where the tasbe package is
		// go do matlab work because files should be there.

		// JOptionPane.showMessageDialog(browse, "Beginning Matlab work");
		//
		//System.out.println("Beginning Matlab work");
		//syb_connector.matlab_work(tasbe_loc + "\\code");
		
		// JOptionPane.showMessageDialog(browse, "Finished Matlab work");
		//System.out.println("Finished Matlab work");

		String plot_dir = tasbe_loc + "\\code\\plots";

		SBOLDocument finalDoc = syb_connector.get_Built_Doc();
		finalDoc.setComplete(false);
		Collection resultCM = finalDoc.createCollection("Final_Tasbe_CM_Output"); 
		
		File file_base = new File(plot_dir);

		Set<File> cm_files = new HashSet<File>();
		if (file_base.listFiles() != null) {
			for (File file : file_base.listFiles()) {
				cm_files.add(file);
				resultCM.addMember(new URI("file:" + file.getName()));
			}
		} 
		
		// this collection has a wasGeneratedBy which references the built activity
		resultCM.addWasGeneratedBy(syb_connector.getActivity().getIdentity());
		
		//TODO: adding script file
		//zip doc and files
		System.out.println("Assembling Color Model Files");
		syb_connector.assemble_CM(finalDoc, cm_files, color_model_loc);
		
		/*
		 * For DEBUGGING
		 * try {
			SBOLWriter.write(finalDoc, "Final_TASBE.xml");
		} catch (SBOLConversionException e) {
			e.printStackTrace();
		}*/
		
		System.out.println("Uploading Final Document");	
		//upload Final Document and results
		login.upload(null, new File("ColorModelOutput.zip"));
		
		//URI planURI = new URI("https://synbiohub.utah.edu/user/" + "mehersam" + "/TASBE_SBOL_Final_Doc/"
		//		+ syb_connector.getPlan().getDisplayId() + "/" + version);

		//frontend.attachFile(planURI, tasbe_loc + "/color_model.m");

		JOptionPane.showMessageDialog(login, "Complete!");
		login = new LoginDialog("https://synbiohub.utah.edu/");	
		//System.exit(0);

	}
}
