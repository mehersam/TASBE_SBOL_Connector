package SBOL_TASBE_Connector.GUI;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.Connector;

public class ConnectorController {
	
	private Connector syb_connector = null; 
	private BrowseDialog browse = null; 
	public ConnectorController(Connector _connect, BrowseDialog _browse) throws SynBioHubException, SBOLValidationException, URISyntaxException, IOException, EngineException, InterruptedException
	{
		syb_connector = _connect; 
		browse = _browse;
		
		String input_fcs_col = browse.get_input_col();
		String tasbeURI = "https://tasbe.org/";

		SBOLDocument col_doc = syb_connector.get_input_col(new URI(input_fcs_col), tasbeURI);
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
		String color_model_loc = browse.get_CM();
		String agent = browse.getAgent(); 
		String tasbe_loc = browse.getTasbeLoc();
		String user = syb_connector.getUser(); 
		// create the activity and download the files and save them in the Tasbe dir
		syb_connector.create_Activity(tasbe_loc, displayId + "_activity", displayId + "_color_model", color_model_loc, agent,
				displayId + "_agent", displayId + "_usage", version, bead, blank, EYFP, mKate, EBFP2);

		// need to open a file explorer to choose where the tasbe package is
		//go do matlab work because files should be there.
		
		System.out.println("Beginning Matlab work"); 
		syb_connector.matlab_work(tasbe_loc + "\\code");
		System.out.println("Finished Matlab work"); 

		String plot_dir = tasbe_loc + "\\code\\plots";
		
		File file_base = new File(plot_dir);

		Set<File> cm_files = new HashSet<File>();

		if (file_base.listFiles() != null) {
			for (File file : file_base.listFiles()) {
				cm_files.add(file);
			}

			syb_connector.assemble_CM(cm_files);
			syb_connector.assemble_collections(cm_files, cm_files, "https://dummy.org", tasbeURI);
			System.out.println("Assembling CM files"); 

			syb_connector.assemble_CM(cm_files, "https://dummy.org", tasbeURI); 
			System.out.println("Submitting CM Collection"); 
			syb_connector.submit("TASBE_Output", version, "ColorModelOutput.zip");
			System.out.println("Finished Submitting CM Collection"); 

					} else {
			System.out.println("Errors occurred in making the color model. Please check error file");
		}

		System.out.println("Building Final Doc"); 
		SBOLDocument finalDoc = syb_connector.get_Built_Doc(); 
		
		//add a reference of the input collection to the built doc
		//add reference to final document to cm collection
		GenericTopLevel fcs_gtl = finalDoc.createGenericTopLevel("Input FCS Collection", new QName(tasbeURI, "m_attachment", "tasbe")); 
		fcs_gtl.createAnnotation(new QName(tasbeURI, "Input_FCS","FCS"), input_files.getIdentity());
		
		//add reference to final document to cm collection
		String retrieve_CM_Col = "https://synbiohub.utah.edu/user/"; 
		SBOLDocument cm_doc = syb_connector.get_Component(new URI(retrieve_CM_Col + user + "/TASBE_Output/TASBE_Output_collection/" + version)); 
		Collection cm_col = cm_doc.getCollection(new URI(retrieve_CM_Col + user + "/TASBE_Output/TASBE_Output_collection/" + version)); 
		 
		GenericTopLevel gtl = finalDoc.createGenericTopLevel("CM Output Reference", new QName(tasbeURI, "m_attachment", "tasbe")); 
		gtl.createAnnotation(new QName(tasbeURI, "CM_output","CM"), cm_col.getIdentity());
		
		System.out.println("Submitting Final Doc"); 
		//upload final document
		syb_connector.submit("TASBE_SBOL_Final_Doc", version,  finalDoc);
		return; 
	
	}
	
	

}
