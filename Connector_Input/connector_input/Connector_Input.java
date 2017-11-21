package connector_input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;
import net.sf.json.JSONObject;

public class Connector_Input {

	public static void main(String[] args) throws URISyntaxException, SBOLValidationException, SynBioHubException, IOException, SBOLConversionException {
		
		if(args[0] == null){
			System.out.println("Please give a settings file as input.");
			System.exit(1);
		}
		String config_file_name = args[0]; 
		System.out.println(config_file_name); 
		File f = new File(Connector_Input.class.getResource(config_file_name).toURI());
	    InputStream is = new FileInputStream(f);
	    String jsonTxt = IOUtils.toString(is, "UTF-8");
	    JSONObject json = JSONObject.fromString(jsonTxt);       
		

	     String prefix = json.getString("prefix" );
	     String email = json.getString( "email" );
	     String pass = json.getString( "pass" );
	     String version = json.getString("version");
	     String id = json.getString("id"); 
	     String name = json.getString("name"); 
	     String desc = json.getString("desc"); 

		String prURI = "https://dummy.org/"; 
		
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(prURI);
		document.setComplete(true);
		document.setCreateDefaults(true);
		
		//build an SBOLDocument with fcs input files
		Set<GenericTopLevel> fcs_files = new HashSet<GenericTopLevel>(); 
		
		GenericTopLevel bead_file = document.createGenericTopLevel(
				"bead",
				"",
				new QName("https://dummy.org/", "fcs", "pr")); 
		Annotation bead = bead_file.createAnnotation(new QName(prURI, "fcs","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/blob/master/example_controls/2012-03-12_Beads_P3.fcs")); 
		bead.setStringValue("bead");
		//bead_file.createAnnotation(new QName(prURI, "file_type","pr"), "bead"); 

		GenericTopLevel blank_file = document.createGenericTopLevel(
				"blank",
				"",
				new QName("https://dummy.org/", "fcs", "pr")); 
		Annotation blank = blank_file.createAnnotation(new QName(prURI, "fcs","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/blob/master/example_controls/2012-03-12_blank_P3.fcs")); 
		blank.setStringValue("blank");
		//blank_file.createAnnotation(new QName(prURI, "file_type","pr"), "blank"); 
		
		GenericTopLevel EYFP = document.createGenericTopLevel(
				"EYFP",
				"",
				new QName("https://dummy.org/", "fcs", "pr")); 
		Annotation eyfp = EYFP.createAnnotation(new QName(prURI, "fcs","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/blob/master/example_controls/07-29-11_EYFP_P3.fcs")); 
		eyfp.setStringValue("color_EYFP");
		//EYFP.createAnnotation(new QName(prURI, "file_type","pr"), "color_EYFP"); 

		GenericTopLevel mKate = document.createGenericTopLevel(
				"mKate",
				"",
				new QName("https://dummy.org/", "fcs", "pr")); 
		Annotation mkate = mKate.createAnnotation(new QName(prURI, "fcs","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/blob/master/example_controls/07-29-11_mkate_P3.fcs")); 
		mkate.setStringValue("color_mKate");
		//mKate.createAnnotation(new QName(prURI, "file_type","pr"), "color_mKate"); 
		
		GenericTopLevel EBFP2 = document.createGenericTopLevel(
				"EBFP2",
				"",
				new QName("https://dummy.org/", "fcs", "pr")); 
		Annotation ebfp2 = EBFP2.createAnnotation(new QName(prURI, "fcs","pr"),  new URI("https://github.com/jakebeal/TASBEFlowAnalytics-Tutorial/blob/master/example_controls/07-29-11_EBFP2_P3.fcs")); 
		ebfp2.setStringValue("color_EBFP2");
		//EBFP2.createAnnotation(new QName(prURI, "file_type","pr"), "color_EBFP2"); 
		
			
		fcs_files.add(bead_file); 
		fcs_files.add(blank_file);
		fcs_files.add(EYFP); 
		fcs_files.add(mKate); 
		fcs_files.add(EBFP2); 

		
		SynBioHubFrontend fb = new SynBioHubFrontend(prefix, prefix);
		fb.login(email, pass);
		fb.submit(id, version, name, desc, "", "", "1", document);
		
//		SBOLDocument retrieved_doc = new SBOLDocument();
//		System.out.println(topLevel); 
//		retrieved_doc = fb.getSBOL(new URI(input_col));
//		retrieved_doc.setDefaultURIprefix(prefix);
//		retrieved_doc.setComplete(true);
//		retrieved_doc.setCreateDefaults(true);
//		retrieved_doc.write("Tester.xml");
//
//		URI col = new URI(input_col); 
//		for(TopLevel tl : retrieved_doc.getCollection(col).getMembers()){
//			System.out.println(tl.getDisplayId());
//			System.out.println(tl.getAnnotation(new QName("https://dummy.org/", "fcs", "pr")).getURIValue()); 
//		}
		
			
	}

}
