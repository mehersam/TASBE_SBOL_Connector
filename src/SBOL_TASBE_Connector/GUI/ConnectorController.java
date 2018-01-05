package SBOL_TASBE_Connector.GUI;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
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

		String input_fcs_col = login.get_input_col();
		String tasbeURI = "https://tasbe.org/";

		SBOLDocument col_doc = syb_connector.get_input_col(new URI(input_fcs_col));
		// get the toplevel collection of input fcs files
		Collection input_files = col_doc.getCollection(new URI(input_fcs_col));
		URI bead = null;
		URI blank = null;
		URI EYFP = null;
		URI mKate = null;
		URI EBFP2 = null;

		for (TopLevel tl : input_files.getMembers()) {
			URI temp = null;
			for (Annotation a : tl.getAnnotations()) {
				if (a.getQName().getNamespaceURI().equals(tasbeURI) && a.getQName().getLocalPart().equals("fcs")) {
					temp = a.getURIValue();
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

		String displayId = input_files.getDisplayId();
		String version = input_files.getVersion();
		String color_model_loc = login.get_CM();
		String tasbe_loc = login.getTasbeLoc();
		String user = syb_connector.getUser();

		// download the files and save them in the Tasbe dir
		syb_connector.download_Files(tasbe_loc, color_model_loc, bead, blank, EYFP, mKate, EBFP2);

		// create the activity
		syb_connector.create_Activity(displayId + "_activity", displayId + "_color_model", displayId + "_agent",
				displayId + "_usage", version);

		// need to open a file explorer to choose where the tasbe package is
		// go do matlab work because files should be there.

		// JOptionPane.showMessageDialog(browse, "Beginning Matlab work");
		//
		// System.out.println("Beginning Matlab work");
		// syb_connector.matlab_work(tasbe_loc + "\\code");
		//
		// JOptionPane.showMessageDialog(browse, "Finished Matlab work");
		// System.out.println("Finished Matlab work");

		String plot_dir = tasbe_loc + "\\code\\plots";

		File file_base = new File(plot_dir);

		Set<File> cm_files = new HashSet<File>();

		if (file_base.listFiles() != null) {
			for (File file : file_base.listFiles()) {
				cm_files.add(file);
			}

			syb_connector.assemble_CM(cm_files);
			syb_connector.assemble_collections(cm_files, cm_files, "https://dummy.org", tasbeURI);

			JOptionPane.showMessageDialog(login, "Assembling CM files");
			System.out.println("Assembling CM files");

			syb_connector.assemble_CM(cm_files, "https://dummy.org", tasbeURI);

			JOptionPane.showMessageDialog(login, "Submitting CM Collection");
			System.out.println("Submitting CM Collection");

			// InputStream colormodel =
			// ConnectorController.class.getResourceAsStream("ColorModelOutput.zip");

			syb_connector.submit("TASBE_Output", version, "TASBE_Output", "TASBE_Output", "ColorModelOutput.zip");

			JOptionPane.showMessageDialog(login, "Finished Submitting CM Collection");
			System.out.println("Finished Submitting CM Collection");

		} else {
			System.out.println("Errors occurred in making the color model. Please check error file");
		}

		JOptionPane.showMessageDialog(login, "Building Final Doc");
		System.out.println("Building Final Doc");
		SBOLDocument finalDoc = syb_connector.get_Built_Doc();

		SynBioHubFrontend frontend = syb_connector.getFrontend();

		// the output collection should have a URIs for the attachments
		URI output_col = new URI(
				"https://synbiohub.utah.edu/user/" + user + "/TASBE_Output/TASBE_Output_collection/" + version);

		try {
			// retrieve uploaded TASBE CM output collection
			SBOLDocument retrieved_output = frontend.getSBOL(output_col);
			Collection retrieved_output_col = retrieved_output.getCollection(output_col);

			// make a new collection that with references to the URIs of the members from TASBE CM output collection
			Collection final_cm_col = finalDoc.createCollection("TASBE_Color_Model_Output");
			
			for (TopLevel m : retrieved_output_col.getMembers()) {
				finalDoc.createCopy(m);
				final_cm_col.addMember(m.getIdentity());
			}

			// this collection has a wasGeneratedBy which references the activity built
			final_cm_col.addWasGeneratedBy(syb_connector.getActivity().getIdentity());
			
		} catch (SynBioHubException e) {
			e.printStackTrace();
			System.exit(1);
		} 

		JOptionPane.showMessageDialog(login, "Submitting Final Doc!");

		System.out.println("Submitting Final Doc");
		// upload final document
		
		
		try {
			SBOLWriter.write(finalDoc, "Final_TASBE.xml");
		} catch (SBOLConversionException e) {
			e.printStackTrace();
		}
		syb_connector.submit("TASBE_SBOL_Final_Doc", version, "TASBE_SBOL_Final_Doc", "TASBE_SBOL_Final_Doc", finalDoc);

		URI planURI = new URI("https://synbiohub.utah.edu/user/" + user + "/TASBE_SBOL_Final_Doc/"
				+ syb_connector.getPlan().getDisplayId() + "/" + version);
		
		frontend.attachFile(planURI, tasbe_loc + "/color_model.m");

		JOptionPane.showMessageDialog(login, "Complete!");
		System.exit(0);

	}


}
