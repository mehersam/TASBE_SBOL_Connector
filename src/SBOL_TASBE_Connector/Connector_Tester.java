package SBOL_TASBE_Connector;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

public class Connector_Tester {

	public static void main(String[] args) throws URISyntaxException, SBOLValidationException, SynBioHubException {
		
		String prefix = "https://synbiohub.utah.edu";
		String email = "mehersam251@gmail.com";
		String pass = "S@ipav12";
		
		String id = "TASBE_Tutorial_Example_Controls";
		String version = "1";
		String name = "Example_Controls";
		String description = "Example controls containing .fcs files"; 
		
		String topLevel = "https://synbiohub.utah.edu/user/mehersam/" + id + id + "_" + "/" + version; 
		URI TP_collection = URI.create(topLevel); 
		
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(prefix);
		document.setComplete(true);
		document.setCreateDefaults(true);
		
		//build an SBOLDocument with fcs input files
		Set<GenericTopLevel> fcs_files = new HashSet<GenericTopLevel>(); 
		
		fcs_files.add(document.createGenericTopLevel(
				"fcs_1",
				"",
				new QName("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/", "07-29-11_A_006_P3.fcs", "pr")));
	
		fcs_files.add(document.createGenericTopLevel(
				"fcs_2",
				"",
				new QName("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/", "07-29-11_B_006_P3.fcs", "pr")));
		
		fcs_files.add(document.createGenericTopLevel(
				"fcs_3",
				"",
				new QName("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/", "07-29-11_C_006_P3.fcs", "pr")));
		
		Collection c = document.createCollection("Input_fcs"); 
		for(GenericTopLevel gtl : fcs_files)
			c.addMember(gtl.getIdentity()); 

		SynBioHubFrontend fb = new SynBioHubFrontend(prefix, prefix);
		fb.login(email, pass);
		fb.submit(id, version, name, description, "", "", "1", document);
		
		//add it as a source?
		
		
			
		
		
	}

}
