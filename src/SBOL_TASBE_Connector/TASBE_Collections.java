package SBOL_TASBE_Connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;
import net.sf.json.JSONObject;

public class TASBE_Collections {

	public static void main(String[] args) throws URISyntaxException, IOException, SynBioHubException, SBOLValidationException {

		if(args[0] == null){
			System.out.println("Please give a settings file as input.");
			System.exit(1);
		}
		String config_file_name = args[0]; 
		System.out.println(config_file_name); 
		File f = new File(TASBE_Collections.class.getResource(config_file_name).toURI());
	    InputStream is = new FileInputStream(f);
	    String jsonTxt = IOUtils.toString(is, "UTF-8");
	    JSONObject json = JSONObject.fromString(jsonTxt);       

	    String prefix = json.getString("prefix" );
	    String pr = json.getString("docPrefix"); 
	    String email = json.getString( "email" );
	    String pass = json.getString( "pass" );
	    String version = json.getString( "version" );
	    String input_col = json.getString("input_col");
	    String agent = json.getString("agent"); 
	    String color_model = json.getString("color_model"); 
	    String batch_analysis = json.getString("batch_analysis");
	     
	    boolean complete = json.getBoolean("complete"); 
	    boolean create_defaults = json.getBoolean("create_defaults"); 
		 
	     //this is the synbiohub instance
	    Connector syb_connector = new Connector(prefix, email, pass, complete, create_defaults); 
		
	    //get the toplevel collection of input fcs files
	    Collection input_files = syb_connector.get_input_col(new URI(input_col));  //get input_col
		
	    URI bead = null; 
	 		URI blank = null; 
	 		URI EYFP = null; 
	 		URI mKate = null; 
	 		URI EBFP2 = null; 
	 		
	 	    for(TopLevel tl : input_files.getMembers())
	 	  		{	    	
	 	    	for(Annotation a : tl.getAnnotations()) 
	 	  	    	{
	 	    		if(a.getQName().getLocalPart().equals("fcs")){
	 	    			if(a.getStringValue().equals("bead"))
	 	    				bead = a.getURIValue(); 
	 	    			if(a.getStringValue().equals("blank"))
	 	    				blank = a.getURIValue(); 
	 	    			if(a.getStringValue().equals("color_EYFP"))
	 	    				EYFP = a.getURIValue(); 
	 	    			if(a.getStringValue().equals("color_mKate"))
	 	    				mKate = a.getURIValue(); 
	 	    			if(a.getStringValue().equals("color_EBFP2"))
	 	    				EBFP2 = a.getURIValue(); 
	 	    		}
	 	  	    }	
	 	  	}	
	    String displayId = input_files.getDisplayId(); 
	    System.out.println(color_model); 
	    syb_connector.create_Activity(displayId + "_activity", displayId + "_color_model", color_model, agent, displayId + "_agent", displayId + "_usage", version, bead, blank, EYFP, mKate, EBFP2);
		    
	 	
	}
}
