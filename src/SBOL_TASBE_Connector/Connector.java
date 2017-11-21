package SBOL_TASBE_Connector;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

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
		col_doc = hub.getSBOL(_fcs_col); //this will return the sboldocument
		Collection retrieved_col = col_doc.getCollection(_fcs_col); 
		fcs_col = retrieved_col; 
		return retrieved_col; 
	} 
	
	public void create_Activity(String activity_name, String plan_Id, String color_model, String agent_prefix, String _agent, String _usage, String version, URI bead, URI blank, URI EYFP, URI mKate, URI EBFP2) throws SBOLValidationException, IOException
	{
		Activity a = built_doc.createActivity(activity_name);
		a.createUsage(_usage, fcs_col.getIdentity());
	
		//assign a plan and an agent
		built_doc.createPlan(plan_Id); //uri to a script - take this run with Matlab
		//execute_plan(batch_analysis, "batch_template.m");
		execute_plan(color_model, ""); 
		execute_plan(bead.toString(), ""); 
		execute_plan(blank.toString(), ""); 
		execute_plan(EYFP.toString(), ""); 
		execute_plan(mKate.toString(), ""); 
		execute_plan(EBFP2.toString(), ""); 

		built_doc.createAgent(agent_prefix, _agent, version); 
	}
	
	private void execute_plan(String fileURL, String fileName) throws IOException
	{
		//make a get request to get a file
		HttpDownloadUtility.downloadFile(fileURL, fileName);
	}
	
	public void assemble_collections(Set<File> cm, String prefix) throws SBOLValidationException
	{
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(prefix);
		document.setComplete(true);
		document.setCreateDefaults(true);
		
		for(File f : cm)
		{
			GenericTopLevel gtl = document.createGenericTopLevel(f.getName(), new QName(prefix, "CM_file", "pr")); 
			gtl.createAnnotation(new QName(prefix, "matlab_file","pr"), f.getName());
		}
	}

	
}
