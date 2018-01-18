package SBOL_TASBE_Connector.GUI;

import java.util.prefs.Preferences;


public enum TASBEPreferences {
	INSTANCE;

	private EnvInfo envInfo = null;

	public EnvInfo getEnvInfo() {
		if (envInfo == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");
			String cm_loc = prefs.get("cm", "");
			String tasbe_loc = prefs.get("tasbe", "");
			envInfo = Infos.forEnv(cm_loc, tasbe_loc);
		}

		return envInfo;
	}

	public void saveUserInfo(EnvInfo envInfo) {
		this.envInfo = envInfo;

		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");

		try {
			if (envInfo == null) {
				prefs.removeNode();
			} else {
				if (envInfo.getCMLoc() != null) {
					prefs.put("cm", envInfo.getCMLoc());
				} 
				else {
					prefs.put("cm", "");
				}
				if (envInfo.getTASBELoc() != null) {
					prefs.put("tasbe", envInfo.getTASBELoc());
				} 
				else {
					prefs.put("tasbe", "");
				}
			}

			prefs.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getValidate() {
		return false;
	}

	private Boolean enableBranching = null;
	private Boolean enableVersioning = null;

	public boolean isBranchingEnabled() {
		if (enableBranching == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("versioning");
			enableBranching = prefs.getBoolean("enableBranching", false);
		}

		return enableBranching;
	}

	public void setBranchingEnabled(boolean enableBranching) {
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("versioning");
		prefs.putBoolean("enableBranching", enableBranching);
	}

	public boolean isVersioningEnabled() {
		if (enableVersioning == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("versioning");
			// versioning is no longer supported
			enableVersioning = prefs.getBoolean("enable", false);
		}

		return enableVersioning;
	}

	public void setVersioningEnabled(boolean enableVersioning) {
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("versioning");
		prefs.putBoolean("enable", enableVersioning);
	}

	private Integer seqBehavior = null;

	/**
	 * askUser is 0, overwrite is 1, and keep is 2
	 */
	public Integer getSeqBehavior() {
		if (seqBehavior == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("settings");
			seqBehavior = prefs.getInt("seqBehavior", 2);
		}
		return seqBehavior;
	}

	/**
	 * askUser is 0, overwrite is 1, and keep is 2
	 */
	public void setSeqBehavior(int seqBehavior) {
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("settings");
		prefs.putInt("seqBehavior", seqBehavior);
		this.seqBehavior = seqBehavior;
	}

	private Integer nameDisplayIdBehavior = null;

	/**
	 * show name is 0, show displayId is 1
	 */
	public Integer getNameDisplayIdBehavior() {
		if (nameDisplayIdBehavior == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("settings");
			nameDisplayIdBehavior = prefs.getInt("nameDisplayIdBehavior", 0);
		}
		return nameDisplayIdBehavior;
	}

	/**
	 * show name is 0, show displayId is 1
	 */
	public void setNameDisplayIdBehavior(int showNameOrDisplayId) {
		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("settings");
		prefs.putInt("nameDisplayIdBehavior", showNameOrDisplayId);
		this.nameDisplayIdBehavior = showNameOrDisplayId;
	}
}