package SBOL_TASBE_Connector.GUI;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.openrdf.model.URI;

import com.google.common.base.Preconditions;


public class Infos {	

	public static EnvInfo forEnv(String cm_loc, String tasbe_loc) {
		return new ImmutableEnvInfo(cm_loc, tasbe_loc);
	}

	public static ActionInfo forAction(EnvInfo env, String msg) {
		return forAction(env, msg, GregorianCalendar.getInstance());
	}
	
	public static ActionInfo forAction(EnvInfo env, String msg, Calendar time) {
		return new ImmutableActionInfo(env, msg, time);
	}
	
	private static class ImmutableEnvInfo implements EnvInfo {
		private final String cm_loc;
		private final String tasbe_loc;

		public ImmutableEnvInfo(String cm_loc, String tasbe_loc) {
			Preconditions.checkNotNull(cm_loc, "Person URI cannot be null");
			Preconditions.checkNotNull(tasbe_loc, "Person URI cannot be null");
			this.cm_loc = cm_loc;
			this.tasbe_loc = tasbe_loc;
		}

		
		@Override
		public String getCMLoc() {
			return cm_loc;
		}

		@Override
		public String getTASBELoc() {
			return tasbe_loc;
		}

		@Override
	    public String toString() {
			StringBuilder sb = new StringBuilder();
			if(cm_loc != null)
				sb.append(cm_loc);
			if (tasbe_loc != null) {
				sb.append(tasbe_loc);
			}
		    return sb.toString();
	    }	
	}
	
	private static class ImmutableActionInfo implements ActionInfo {
		private final EnvInfo env;
		private final String message;
		private final Calendar time;

		public ImmutableActionInfo(EnvInfo user, String message, Calendar time) {
			this.env = user;
			this.message = message;
			this.time = time;
		}

		@Override
	    public String toString() {
		    return "ActionInfo [message=" + message + ", time=" + time + ", user=" + env + "]";
	    }

		@Override
		public EnvInfo getEnv() {
			// TODO Auto-generated method stub
			return env;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return message;
		}

		@Override
		public Calendar getDate() {
			// TODO Auto-generated method stub
			return time;
		}	
	}

}
