package SBOL_TASBE_Connector;

import java.io.File;
import com.mathworks.engine.*;

import SBOL_TASBE_Connector.GUI.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;

import org.apache.commons.io.IOUtils;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.TopLevel;
import org.synbiohub.frontend.SynBioHubException;

public class TASBE_Collections {

	public static void main(String[] args) throws URISyntaxException, IOException, SynBioHubException,
			SBOLValidationException, EngineException, InterruptedException {

		new LoginDialog("https://synbiohub.utah.edu/");	
		return; 
	}
}
