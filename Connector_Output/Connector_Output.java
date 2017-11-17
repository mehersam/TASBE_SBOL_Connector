import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

import net.sf.json.JSONObject;

public class Connector_Output {

	public static void main(String[] args) throws SBOLValidationException, URISyntaxException, IOException, SynBioHubException {
		// TODO Auto-generated method stub

		File batch_files  = new File(Connector_Output.class.getResource(args[0]).toURI());
		File cm_files  = new File(Connector_Output.class.getResource(args[1]).toURI());
		File settings  = new File(Connector_Output.class.getResource(args[1]).toURI());
		
		InputStream is = new FileInputStream(settings);
		String jsonTxt = IOUtils.toString(is, "UTF-8");
		JSONObject json = JSONObject.fromString(jsonTxt);       

		String prefix = json.getString("prefix" );
		String version = json.getString("version"); 
		String email = json.getString( "email" );
		String pass = json.getString( "pass" );
		String collection_name = json.getString("collection_name"); 
		boolean complete = json.getBoolean("complete"); 
	    boolean create_defaults = json.getBoolean("create_defaults"); 
	    
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(prefix);
		document.setComplete(complete);
		document.setCreateDefaults(create_defaults);
		
		Collection batch_analysis_col = document.createCollection(collection_name); 
		Collection cm_col = document.createCollection(collection_name + "color_model"); 
		batch_analysis_col.addMember(cm_col.getIdentity());//add CM as a subcollection of BA 
		
		//add all of the batch analysis files as attachments
		for(File f : batch_files.listFiles())
		{
			GenericTopLevel gtl = document.createGenericTopLevel(f.getName(), new QName("https://sbols.org/", "file", "pr")); 
			gtl.createAnnotation(new QName(prefix, "fcs_1","pr"), f.getName());			
		}
		
		for(File f : cm_files.listFiles())
		{
			GenericTopLevel gtl = document.createGenericTopLevel(f.getName(), new QName("https://sbols.org/", "file", "pr")); 
			gtl.createAnnotation(new QName(prefix, "fcs_1","pr"), f.getName());
		}
		
		//submit files
		SynBioHubFrontend fb = new SynBioHubFrontend(prefix, prefix); 
		fb.login(email, pass);
		
	}

}
