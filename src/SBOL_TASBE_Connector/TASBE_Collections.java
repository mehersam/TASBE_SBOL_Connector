package SBOL_TASBE_Connector;

import java.io.IOException;
import java.net.URISyntaxException;

import org.sbolstandard.core2.SBOLValidationException;
import org.synbiohub.frontend.SynBioHubException;

import com.mathworks.engine.EngineException;

import SBOL_TASBE_Connector.GUI.LoginDialog;

public class TASBE_Collections {

	public static void main(String[] args) throws URISyntaxException, IOException, SynBioHubException,
			SBOLValidationException, EngineException, InterruptedException {

		new LoginDialog("https://synbiohub.utah.edu/");	
		return; 
	}
}
