package SBOL_TASBE_Connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import net.sf.json.JSONObject;

public class TASBE_Collections {

	public static void main(String[] args) throws URISyntaxException, IOException, SynBioHubException, SBOLValidationException {

		String config_file_name = args[0]; 
		System.out.println(config_file_name); 
		File f = new File(TASBE_Collections.class.getResource(config_file_name).toURI());
	    InputStream is = new FileInputStream(f);
	    String jsonTxt = IOUtils.toString(is, "UTF-8");
	    JSONObject json = JSONObject.fromString(jsonTxt);       

	     String prefix = json.getString("prefix" );
	     String email = json.getString( "email" );
	     String pass = json.getString( "pass" );
	     String version = json.getString( "version" );
	     String input_col = json.getString("input_col");
	     String agent = json.getString("agent"); 
	     String color_model = json.getString("color_model"); 
	     String batch_analysis = json.getString("batch_analysis");
	     
	     boolean complete = json.getBoolean("complete"); 
	     boolean create_defaults = json.getBoolean("create_defaults"); 
		 
	    Connector syb_connector = new Connector(prefix, email, pass, complete, create_defaults); 
		Collection input_files = syb_connector.get_input_col(new URI(input_col));  //get input_col
		
		for(Annotation a : input_files.getAnnotations())
		{
			String displayId = a.getQName().getLocalPart(); 
			syb_connector.create_Activity(displayId + "_activity", displayId + "_batch_analysis", batch_analysis, color_model, agent, displayId + "_agent", displayId + "_usage", version);
		}
				
	}

}
