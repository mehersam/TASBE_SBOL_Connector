package SBOL_TASBE_Connector;

import java.io.File;
import com.mathworks.engine.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;
import net.sf.json.JSONObject;

public class TASBE_Collections {

	public static void main(String[] args) throws URISyntaxException, IOException, SynBioHubException,
			SBOLValidationException, EngineException, InterruptedException {

		if (args[0] == null) {
			System.out.println("Please give a settings file as input.");
			System.exit(1);
		}
		String config_file_name = args[0];
		File f = new File(TASBE_Collections.class.getResource(config_file_name).toURI());
		InputStream is = new FileInputStream(f);
		String jsonTxt = IOUtils.toString(is, "UTF-8");
		JSONObject json = JSONObject.fromString(jsonTxt);

		String prefix = json.getString("prefix");
		//String pr = json.getString("docPrefix");
		String email = json.getString("email");
		String pass = json.getString("pass");
		String version = json.getString("version");
		String input_col = json.getString("input_col");
		String agent = json.getString("agent");
		String color_model = json.getString("color_model");
		//String batch_analysis = json.getString("batch_analysis");

		boolean complete = json.getBoolean("complete");
		boolean create_defaults = json.getBoolean("create_defaults");

		// this is the synbiohub instance
		Connector syb_connector = new Connector(prefix, email, pass, complete, create_defaults);

		// get the SBOLDocument with
		SBOLDocument col_doc = syb_connector.get_input_col(new URI(input_col));

		// get the toplevel collection of input fcs files
		Collection input_files = col_doc.getCollection(new URI(input_col)); 
		
		URI bead = null;
		URI blank = null;
		URI EYFP = null;
		URI mKate = null;
		URI EBFP2 = null;
		String tasbeURI = "https://tasbe.org/";

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
						if (a.getStringValue().equals("bead")) {
							bead = temp;
						}
						if (a.getStringValue().equals("blank")) {
							blank = temp;
						}
						if (a.getStringValue().equals("EYFP")) {
							EYFP = temp;
						}
						if (a.getStringValue().equals("mKate")) {
							mKate = temp;
						}
						if (a.getStringValue().equals("EBFP2")) {
							EBFP2 = temp;
						}
						break;
					}
				}
			}
		}
		String displayId = input_files.getDisplayId();

		// create the activity and download the files and save them in the
		// matlab dir
		syb_connector.create_Activity(displayId + "_activity", displayId + "_color_model", color_model, agent,
				displayId + "_agent", displayId + "_usage", version, bead, blank, EYFP, mKate, EBFP2);

		// go do matlab work because files should be there.
		String plot_dir = syb_connector.matlab_work("C:\\Users\\Meher\\Documents\\MATLAB\\TASBEFlowAnalytics\\code");
		// plot_dir = plot_dir + "\\";
		File file_base = new File(plot_dir);

		Set<File> cm_files = new HashSet<File>();
		
		if (file_base.listFiles() != null) {
			for (File file : file_base.listFiles()) {
				cm_files.add(file);
			}
			
			SBOLDocument document = syb_connector.assemble_collections(cm_files, cm_files, "https://dummy.org", tasbeURI);
			syb_connector.submit("TASBE_Output", "1", "TASBE_Output", "Output from tasbe", document);
		} 
		
		else 
		{
			System.out.println("Errors occurred in making the color model. Please check error file");
		}
	}
}
