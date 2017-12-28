package SBOL_TASBE_Connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Agent;
import org.sbolstandard.core2.Association;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.Plan;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

public class Connector {

	private SynBioHubFrontend hub;
	private SBOLDocument built_doc; 
	private SBOLDocument col_doc; 
	private Collection fcs_col; 
	private String prefix;
	private Activity cm_act = null; 
	private Plan plan = null; 
	private String user = ""; 
	public Connector(String backendUrl, String _prefix) throws SynBioHubException
	{		
		prefix = _prefix; 
		hub = new SynBioHubFrontend(backendUrl, backendUrl);
		
		built_doc = new SBOLDocument();
		built_doc.setDefaultURIprefix(prefix);
		built_doc.setComplete(true);
		built_doc.setCreateDefaults(true);
	}
	
	public SynBioHubFrontend getFrontend()
	{
		
		return this.hub;
		
	}
	public void setUser(String _user)
	{
		this.user = _user; 
	}
	
	public String getUser()
	{
		return this.user; 
	}
	public Plan getPlan()
	{
		return this.plan; 
	}
	
	public Activity getActivity()
	{
		return this.cm_act; 
	}
	
	public void login(String email, String pass) 
	{
		try {
			hub.login(email, pass);
		} catch (SynBioHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public void submit(String id, String version, String name, String desc, SBOLDocument doc) 
	{
		try {
			hub.createCollection(id, version, name, desc, "",true, doc);
		} catch (SynBioHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void submit(String id, String version, String name, String desc, String file)
	{
		//add to an empty collection
		try {
			hub.createCollection(id, version, name, desc, "", true, file);
		} catch (SynBioHubException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//retrieve the collection of fcs files
	public SBOLDocument get_input_col(URI _fcs_col, String prefix)
	{	
		try {
			col_doc = hub.getSBOL(_fcs_col);
		} catch (SynBioHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} //this will return the sboldocument
		fcs_col = col_doc.getCollection(_fcs_col); 
		
		return col_doc; 
	} 
	public SBOLDocument get_Component(URI output_col) 
	{
		SBOLDocument found_doc = null; 
		try {
			found_doc = hub.getSBOL(output_col);
		} catch (SynBioHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return found_doc; 
	}
	
	public SBOLDocument get_Built_Doc()
	{
		return this.built_doc; 
	}
	
	public void download_Files(String savedir, String color_model, URI bead, URI blank, URI EYFP, URI mKate, URI EBFP2)
	{
		try {
			HttpDownloadUtility.downloadFile(color_model, savedir , "color_model.m");
		//	HttpDownloadUtility.downloadFile(color_model, "" , "color_model.m");
			HttpDownloadUtility.downloadFile(bead.toString(),savedir , "2012-03-12_Beads_P3.fcs");
			HttpDownloadUtility.downloadFile(blank.toString(),savedir, "2012-03-12_blank_P3.fcs");
			HttpDownloadUtility.downloadFile(EYFP.toString(), savedir, "2012-03-12_EYFP_P3.fcs");
			HttpDownloadUtility.downloadFile(mKate.toString(), savedir, "2012-03-12_mkate_P3.fcs");
			HttpDownloadUtility.downloadFile(EBFP2.toString(), savedir, "2012-03-12_ebfp2_P3.fcs");
		} catch (IOException e) {
			System.out.println("File could not be downloaded"); 
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	public void create_Activity(String activity_name, String plan_Id,  String agent_prefix, String _agent, String _usage, String version)
	{
		String tasbeURI = "https://synbiohub.utah.edu/user/mehersam/SBOL_Software/TASBEFlowAnalytics/1.0"; 
		try {
			//create the CM activity
			cm_act = built_doc.createActivity(activity_name, version);
			
			cm_act.createUsage(_usage, fcs_col.getIdentity()); //input collection
			
			//create the CM plan
			plan = built_doc.createPlan(plan_Id, version);
			
			//create the CM association
			Association tasbe = cm_act.createAssociation(activity_name + "_association", new URI(tasbeURI)); 
			//tasbe.addRole(); //test role sbol onotology?

			//set the plan to the association
			tasbe.setPlan(plan.getIdentity());
			
			
		} catch (SBOLValidationException|URISyntaxException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 

	}
	
	public void assemble_CM(Set<File> color_model)
	{
		 //zip up the set of files
		try
		{
			byte[] buffer = new byte[1024]; 
			
			FileOutputStream fos = new FileOutputStream("ColorModelOutput.zip"); 
			ZipOutputStream zos = new ZipOutputStream(fos); 
			
			for(File cm_file : color_model)
			{
				FileInputStream fis = new FileInputStream(cm_file); 
				
				zos.putNextEntry(new ZipEntry(cm_file.getName()));
				
				int length; 
				
				while((length = fis.read(buffer)) > 0)
				{
					zos.write(buffer, 0, length);
				}
				
				zos.closeEntry(); 
				fis.close();
			}
			zos.close();
		}
		catch(IOException e)
		{
			System.out.println("Error in creating the zip file: " + e); 
		}
	}
	
	public SBOLDocument assemble_CM(Set<File> cm, String doc_prefix, String file_prefix) 
	{
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(doc_prefix);
		document.setComplete(true);
		document.setCreateDefaults(true);
		
		Collection cm_col = null;
		try {
			cm_col = document.createCollection("color_model");
		} catch (SBOLValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} 
		//cm_col.addWasGeneratedBy(cm_act.getIdentity()); 
		//cm_col.addWasDerivedFrom(fcs_col.getIdentity()); 
		for(File f : cm)
		{
			String filename = f.getName().replace("-", "_");
			filename = filename.replace(".", "_");
			filename = filename.concat("_copy");
			GenericTopLevel gtl = null;
			try {
				gtl = document.createGenericTopLevel(filename, new QName(file_prefix, "m_attachment", "tasbe"));
			} catch (SBOLValidationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				System.exit(1);
			} 
			try {
				gtl.createAnnotation(new QName(file_prefix, "color_model","CM"), f.getName());
			} catch (SBOLValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			try {
				cm_col.addMember(gtl.getIdentity());
			} catch (SBOLValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			} 
		}	
		
		return document; 
	}
		
	
	public SBOLDocument assemble_collections(Set<File> cm, Set<File> batch_analysis, String doc_prefix, String file_prefix) throws SBOLValidationException, URISyntaxException, SynBioHubException
	{
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix(doc_prefix);
		document.setComplete(true);
		document.setCreateDefaults(true);
				
		Collection BA = document.createCollection("batch_analysis");
		Collection cm_col = document.createCollection("color_model"); 
		BA.addMember(cm_col.getIdentity());//add CM as a subcollection of BA 
		for(File f : batch_analysis)
		{
			String filename = f.getName().replace("-", "_");
			filename = filename.replace(".", "_");
			GenericTopLevel gtl = document.createGenericTopLevel(filename, new QName(file_prefix, "m_attachment", "tasbe")); 
			gtl.createAnnotation(new QName(file_prefix, "batch_analysis","BA"), f.getName());
			BA.addMember(gtl.getIdentity()); 
		}
		
		for(File f : cm)
		{
			String filename = f.getName().replace("-", "_");
			filename = filename.replace(".", "_");
			filename = filename.concat("_copy");
			GenericTopLevel gtl = document.createGenericTopLevel(filename, new QName(file_prefix, "m_attachment", "tasbe")); 
			gtl.createAnnotation(new QName(file_prefix, "color_model","CM"), f.getName());
			cm_col.addMember(gtl.getIdentity()); 
		}	
		
		return document; 
	}
	
	public void getErrors(StringWriter writer, String saveDir) throws IOException
	{
		String filename = "ErrorsFromTasbe.txt";  
		if(saveDir != "")
		{
			filename = saveDir + filename; 
		}
		File file = new File(filename); 
		FileWriter fw =  new FileWriter(file);
		fw.write(writer.toString());
		fw.close();
	}
	
	public void matlab_work(String pathToTASBE)  
	{
		MatlabEngine eng = null;
		try {
			eng = MatlabEngine.startMatlab();
		} catch (EngineException | IllegalArgumentException | IllegalStateException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
        StringWriter writer = new StringWriter();
		String matlab_dir = pathToTASBE + "\\plots";
		//String plot_dir = ""; 
		try{
			eng.eval("addpath('" + pathToTASBE + "'" + ")");
			eng.eval("cd " + pathToTASBE);
			eng.eval("cd ../");
			eng.eval("mkdir('" + "example_controls\\')"); 
			//eng.eval("mkdir('" + pathToTASBE + "\\example_controls\\')"); 
			eng.eval("movefile *.fcs example_controls\\"); //move all the fcs files in TASBEdir to examplecontrols
			eng.eval("cd " + pathToTASBE);
			eng.eval("make_color_model", writer, null); //run makecolor model
			eng.eval("plots = dir( '" + matlab_dir + "')");
			eng.eval("plot_dir = plots.folder");
			//plot_dir = eng.getVariable("plot_dir");
			
		}
		catch(Exception e)
		{
		}
        try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		try {
			eng.close();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		writer.flush(); 
		try {
			getErrors(writer, "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} 
	}
}
