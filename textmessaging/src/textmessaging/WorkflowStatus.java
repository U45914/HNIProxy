package textmessaging;
import javax.json.*;

public class WorkflowStatus {

	private String returnString="";
	private boolean needUserInput=false;
	private boolean statechange=false;
	
	private JsonObject customerJSON= null;  // We will use the Null Value to determine if we save this JSONObject
	private JsonObject menuJSON= null; // We will use the Null Value to determine if we save this JSONObject
	private JsonObject locationJSON= null; // We will use the Null Value to determine if we save this JSONObject
	
	private int newState=0;
	
	private static final int STATUS_JSON_OBJECT_CUSTOMER=0;
	private static final int STATUS_JSON_OBJECT_MENU=0;
	private static final int STATUS_JSON_OBJECT_LOCATION=0;
	
	public boolean isStatechange() {
		return statechange;
	}

	public void setStatechange(boolean newstatechange) {
		statechange = newstatechange;
	}

	public int getNewState() {
		return newState;
	}

	public void setNewState(int newNewState) {
		newState = newNewState;
	}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getReturnString() {
		return returnString;
	}

	public void setReturnString(String newReturnString) {
		returnString = newReturnString;
	}

	public boolean isNeedUserInput() {
		return needUserInput;
	}

	public void setNeedUserInput(boolean newNeedUserInput) {
		needUserInput = newNeedUserInput;
	}
}
