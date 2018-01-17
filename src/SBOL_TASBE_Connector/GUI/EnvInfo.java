package SBOL_TASBE_Connector.GUI;

import java.io.File;

public class EnvInfo {

	private String colorModelScript; 
	private String tasbeLocation; 
	
	public EnvInfo()
	{
		colorModelScript = null; 
		tasbeLocation = null; 
	}
	
	public void setCMScript(String _colorModelScript)
	{
		this.colorModelScript = _colorModelScript; 
	}
	
	public void setTASBELocation(String _tasbeLocation)
	{
		this.tasbeLocation = _tasbeLocation; 
	}
	
	public String getCMScript()
	{
		return this.colorModelScript; 
	}
	
	public String getTASBELocation()
	{
		return this.tasbeLocation; 
	}
}
