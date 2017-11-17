package SBOL_TASBE_Connector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

public class Connector_Tester {

	public static void main(String[] args) throws URISyntaxException, SBOLValidationException, SynBioHubException, IOException, SBOLConversionException {
		
		String prefix = "https://synbiohub.utah.edu";
		String email = "mehersam251@gmail.com";
		String pass = "S@ipav12";
		
		String id = "TASBE_Tutorial_Example_Controls";
		String version = "1";
		String name = "Example_Controls";
		String description = "Example controls containing .fcs files"; 
		
		String topLevel = "https://synbiohub.utah.edu/user/mehersam/" + id + "/" + id + "_collection" + "/" + version; 
		URI TP_collection = URI.create(topLevel); 
		String prURI = "https://sbols.org/"; 
		
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(prefix);
		document.setComplete(true);
		document.setCreateDefaults(true);
		
		//build an SBOLDocument with fcs input files
		Set<GenericTopLevel> fcs_files = new HashSet<GenericTopLevel>(); 
		
		GenericTopLevel fcs_1 = document.createGenericTopLevel(
				"fcs_1",
				"",
				new QName("https://sbols.org/", "fcs_1", "pr")); 
		fcs_1.createAnnotation(new QName(prURI, "fcs_1","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/07-29-11_A_006_P3.fcs")); 
		
		GenericTopLevel fcs_2 = document.createGenericTopLevel(
				"fcs_2",
				"",
				new QName("https://sbols.org/", "fcs_2", "pr")); 
		fcs_2.createAnnotation(new QName(prURI, "fcs_2","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/07-29-11_B_006_P3.fcs")); 
		
		GenericTopLevel fcs_3 = document.createGenericTopLevel(
				"fcs_3",
				"",
				new QName("https://sbols.org/", "fcs_3", "pr")); 
		fcs_3.createAnnotation(new QName(prURI, "fcs_3","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/raw/develop/example_controls/07-29-11_C_006_P3.fcs")); 
		
		fcs_files.add(fcs_1); 
		fcs_files.add(fcs_2);
		fcs_files.add(fcs_3); 
		
		//Collection c = document.createCollection("Input_fcs"); 
		//for(GenericTopLevel gtl : fcs_files)
		//c.addMember(gtl.getIdentity()); 
		
		document.write("Tester.xml");
		SynBioHubFrontend fb = new SynBioHubFrontend(prefix, prefix);
		fb.login(email, pass);
		fb.submit(id, version, name, description, "", "", "1", document);
		
		SBOLDocument retrieved_doc = new SBOLDocument();
		System.out.println(topLevel); 
		retrieved_doc = fb.getSBOL(new URI(topLevel));
		retrieved_doc.setDefaultURIprefix(prefix);
		retrieved_doc.setComplete(true);
		retrieved_doc.setCreateDefaults(true);
		
		Collection ret_c = retrieved_doc.getCollection("Input_fcs", "1"); 
		for(GenericTopLevel gtl : retrieved_doc.getGenericTopLevels())
		{
			System.out.println(gtl.getDisplayId());
			for(Annotation a : gtl.getAnnotations())
				System.out.println(a.getURIValue()); 
		}
			
	}

}
