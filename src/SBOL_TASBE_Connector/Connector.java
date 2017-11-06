package SBOL_TASBE_Connector;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Agent;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.Plan;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

public class Connector {

	private SynBioHubFrontend hub;
	private SBOLDocument built_doc; 
	private SBOLDocument col_doc; 
	private Collection fcs_col; 
	private File script;
	
	public Connector(String prefix, String email, String pass, boolean complete, boolean create_defaults) throws SynBioHubException
	{
		hub = new SynBioHubFrontend(prefix, prefix);
		hub.login(email, pass);
		
		built_doc = new SBOLDocument();
		built_doc.setDefaultURIprefix(prefix);
		built_doc.setComplete(complete);
		built_doc.setCreateDefaults(create_defaults);
		
	}
	
	public void submit(String id, String version, String name, String desc, SBOLDocument doc) throws SynBioHubException
	{
		hub.submit(id, version, name, desc, "", "", "1", doc);

	}
	
	//retrieve the collection of fcs files
	public void get_Collection(URI _fcs_col, String col_dispId, String col_version) throws SynBioHubException, SBOLValidationException
	{
		col_doc = hub.getSBOL(_fcs_col); //should be a doc with a collection of uris	
		Collection c = col_doc.getCollection(col_dispId, col_version); //list of fcs files. 
		
	}
	
	public void create_Activity(String activity_name, String plan_prefix, String agent_prefix,  String version, String _plan, String _usage, String _agent ) throws SBOLValidationException, IOException
	{
		Activity a = built_doc.createActivity(activity_name);
		a.createUsage(_usage, fcs_col.getIdentity());
	
		//assign a plan and an agent
		built_doc.createPlan(plan_prefix, _plan, version); //uri to a script - take this run with Matlab
		retrieve_execute_plan(plan_prefix); 
		built_doc.createAgent(agent_prefix, _agent, version); 
	}
	
	public Collection retrieve_execute_plan(String fileURL) throws IOException
	{
		//make a get request to get a file
		
		HttpDownloadUtility.downloadFile(fileURL, "");

		//call matlab with the file
		
		return null; 
	}
	
	
	
	
	
}
