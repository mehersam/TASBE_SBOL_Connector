package SBOL_TASBE_Connector;

import java.io.File;
import java.net.URI;

import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

public class Connector {

	private SynBioHubFrontend hub;
	private SBOLDocument doc; 
	private Collection fcs_col; 
	private File script;
	
	public Connector(String url, String prefix, String email, String pass, File read_file, String id, String version, String name,
			String desc, boolean complete, boolean create_defaults, URI _fcs_col, File _script_plan) throws SynBioHubException
	{
		hub = new SynBioHubFrontend(prefix, prefix);
		doc = hub.getSBOL(_fcs_col); //should be a doc with a collection of uris
		
	}
	
	public void create_Activities(String displayId) throws SBOLValidationException
	{
		Activity a = doc.createActivity(displayId);
		
		//assign a plan
		
		//assign an agent

	}
	
	
	
	
	
	
}
