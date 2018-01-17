package SBOL_TASBE_Connector.GUI;

import java.util.prefs.Preferences;

import edu.utah.ece.async.sboldesigner.versioning.Infos;
import edu.utah.ece.async.sboldesigner.versioning.PersonInfo;

public enum TASBEPreferences {
	INSTANCE;

	private PersonInfo userInfo = null;

	public PersonInfo getUserInfo() {
		if (userInfo == null) {
			Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");
			String name = prefs.get("name", "");
			String email = prefs.get("email", "");
			String uri = prefs.get("uri", "http://www.dummy.org");
			userInfo = Infos.forPerson(uri, name, email);
		}

		return userInfo;
	}

	public void saveUserInfo(PersonInfo userInfo) {
		this.userInfo = userInfo;

		Preferences prefs = Preferences.userNodeForPackage(TASBEPreferences.class).node("user");

		try {
			if (userInfo == null) {
				prefs.removeNode();
			} else {
				prefs.put("uri", userInfo.getURI().toString());
				prefs.put("name", userInfo.getName());
				if (userInfo.getEmail() != null) {
					prefs.put("email", userInfo.getEmail().toString());
				} else {
					prefs.put("email", "");
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