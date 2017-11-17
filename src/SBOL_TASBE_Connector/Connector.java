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

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.RemoteMatlabProxy;
import matlabcontrol.RemoteMatlabProxyFactory;

public class Connector {

	private SynBioHubFrontend hub;
	private SBOLDocument built_doc; 
	private SBOLDocument col_doc; 
	private Collection fcs_col; 
	private String prefix;
	
	public Connector(String _prefix, String email, String pass, boolean complete, boolean create_defaults) throws SynBioHubException
	{		
		prefix = _prefix; 
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
	public Collection get_input_col(URI _fcs_col) throws SynBioHubException, SBOLValidationException
	{	
		col_doc = hub.getSBOL(_fcs_col); //should be a doc with a collection of uris
		Collection retrieved_col = col_doc.getCollection(_fcs_col); 
		return retrieved_col; 
	} 
	
	public void create_Activity(String activity_name, String plan_Id, String batch_analysis, String color_model, String agent_prefix, String _agent, String _usage, String version) throws SBOLValidationException, IOException
	{
		Activity a = built_doc.createActivity(activity_name);
		a.createUsage(_usage, fcs_col.getIdentity());
	
		//assign a plan and an agent
		built_doc.createPlan(plan_Id); //uri to a script - take this run with Matlab
		execute_plan(batch_analysis, "batch_template.m");
		execute_plan(color_model, "color_model.m"); 
		built_doc.createAgent(agent_prefix, _agent, version); 
	}
	
	private void execute_plan(String fileURL, String fileName) throws IOException
	{
		//make a get request to get a file
		HttpDownloadUtility.downloadFile(fileURL, fileName);
	}
	
	
}
