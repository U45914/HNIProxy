package textmessaging;

import java.sql.*;  
import java.time.*;
import javax.json.*;

public class SessionState {

    private String sessionID="" ;
	private int sessionStateValue=0; // Default value for session state is zero which is new call
    	
    private int userStep=0; // how many steps is the user on (if they repeat a step this will be different than sessionstate)
    private String userReturnMessage;
    private String userText; 
    private String statusCode="200";
    private String phoneNumber;
    private Customer sessionCustomer = new Customer();    
    private HNISQLDatabase hNISQLDB = new HNISQLDatabase();
    private Connection dbConnection;    
    
    //these objects are used to store lists for use in matching with user responses
    //menu, locations, and customer during registration
    
    private JsonObject customerdataJSON= null;  // We will use the Null Value to determine if we save this JSONObject
	private JsonObject menudataJSON= null; // We will use the Null Value to determine if we save this JSONObject
	private JsonObject locationdataJSON= null; // We will use the Null Value to determine if we save this JSONObject

	private static final int STATUS_JSON_OBJECT_CUSTOMER=0;
	private static final int STATUS_JSON_OBJECT_MENU=0;
	private static final int STATUS_JSON_OBJECT_LOCATION=0;

	private static final String DBFIELD_SESSIONSTATE_ID="id";
	private static final String DBFIELD_SESSIONSTATE_SESSIONID="sessionid";
	private static final String DBFIELD_SESSIONSTATE_SESSIONSTEP="sessionstep";
	private static final String DBFIELD_SESSIONSTATE_UPDATED="updated";
    
    private static final String DBFIELD_SESSIONSTATEDATA_ID="id";
	private static final String DBFIELD_SESSIONSTATEDATA_SESSIONID="sessionid";
	private static final String DBFIELD_SESSIONSTATEDATA_SESSIONSTEP="sessionstep";								
	private static final String DBFIELD_SESSIONSTATEDATA_USERSTEP="userstep";
	private static final String DBFIELD_SESSIONSTATEDATA_CUSTOMERID="customerid";
    private static final String DBFIELD_SESSIONSTATEDATA_PHONENUMBER="phonenumber";
	private static final String DBFIELD_SESSIONSTATEDATA_USERTEXT="userText";
	private static final String DBFIELD_SESSIONSTATEDATA_RESPONSETEXT="responseText";
	private static final String DBFIELD_SESSIONSTATEDATA_UPDATED="updated";
	
	private static final String DBFIELD_SESSIONSTATEDATAJSON_SESSIONID ="sessionid";
	private static final String DBFIELD_SESSIONSTATEDATAJSON_DATATYPEVALUE="datavaluetype";
	private static final String DBFIELD_SESSIONSTATEDATAJSON_jsondata="jsondata"; 
	   
    public Connection getConnection()
    {
    return hNISQLDB.GetConnection();
    }
   
    private boolean FillStateRow(ResultSet openResultSet){
       try {
    	     openResultSet.updateString(DBFIELD_SESSIONSTATE_SESSIONID,sessionID);
             openResultSet.updateInt(DBFIELD_SESSIONSTATE_SESSIONSTEP,sessionStateValue);             	  	                        
             openResultSet.updateDate(DBFIELD_SESSIONSTATE_UPDATED,currentDateTime());
             return true;
            }
         	catch (Exception e) {  
                e.printStackTrace(); 
                return false;                           
            	}         
    }
    
    private boolean DBSaveSessionState(String userText, String responseText)
	{
		boolean RecordSaved = false;
		if(hNISQLDB.ConnectDB())
		{   // Connected to Database
		    Statement statement = null;   
	        ResultSet resultSet = null;  
            
		  try {  
			  
  		     //Get Database Connection
		    dbConnection=hNISQLDB.GetConnection();
	        // Declare the JDBC objects.  
	        // Create and execute a SELECT SQL statement.  
            String selectSessionSql = "SELECT * from SessionState Where "+  DBFIELD_SESSIONSTATE_SESSIONID
            	   + "='" + sessionID +"'";  
            statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
            resultSet = statement.executeQuery(selectSessionSql);
            //Update Session State Row
            if(resultSet.next())
	          { if(FillStateRow(resultSet))
	          	{
	            	resultSet.updateRow();
	            	RecordSaved=true;
            
              	}
	          }
	          else
	          { //Add New Record
	            resultSet.moveToInsertRow();
	            if(FillStateRow(resultSet))
	          	{
	            	resultSet.insertRow();
	            	RecordSaved=true;            
              	}
	          
	          }
	                     
             resultSet.close();
             statement.close();       
             //Now that session state is save we need to fill session statedata with more information
             //session statedata becomes an effective user activity log as well
             //sessionstatedata should always be an insert 
             String selectSessionDataSQL = "SELECT * from SessionStateData Where " + DBFIELD_SESSIONSTATEDATA_SESSIONID
             	    + "='" + sessionID +"' and " + DBFIELD_SESSIONSTATEDATA_USERSTEP + "=" + userStep;  
             statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
             resultSet = statement.executeQuery(selectSessionDataSQL);
             //Insert New record
 	           resultSet.moveToInsertRow();
 	           resultSet.updateString(DBFIELD_SESSIONSTATEDATA_SESSIONID,sessionID);
 	           resultSet.updateInt(DBFIELD_SESSIONSTATEDATA_USERSTEP,userStep);
 	           resultSet.updateString(DBFIELD_SESSIONSTATEDATA_USERTEXT,userText);
 	           resultSet.updateString(DBFIELD_SESSIONSTATEDATA_PHONENUMBER,phoneNumber); 	           
 	           resultSet.updateString(DBFIELD_SESSIONSTATEDATA_RESPONSETEXT, responseText);
 	           resultSet.updateDate("updated",currentDateTime());
 	           //Save Customer ID if it is valid
 	           if(sessionCustomer.isCustomerFoundInDB())
 	           		{ 	           
 	           		resultSet.updateLong(DBFIELD_SESSIONSTATEDATA_CUSTOMERID, sessionCustomer.getID());
 	           		}
 	           resultSet.insertRow(); 	            
 	           RecordSaved=true;      
               		          
 	           resultSet.close();
 	           statement.close();       
            
    		  }
    	catch (Exception e) {  
                e.printStackTrace();
                            
            	}  
        finally {  
                // Close the connections after the data has been handled.  
                if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}  
                if (statement != null) try { statement.close(); } catch(Exception e) {}  	              
           		}
        } // IF HNISQLDB.ConnectDB
	    return RecordSaved;
	} 
	 
    
    public void DBFillSessionState(String newSessionID, String newPhoneNumber, int defaultState) 
    {
        
    	//Looks up Session Info from database based on session ID		
		//Dummy Value
		if(hNISQLDB.ConnectDB())
		{   // Connected to Database
		    Statement statement = null;   
	        ResultSet resultSet = null;  
            
		  try {  
  		     //Get Database Connection
		    dbConnection=hNISQLDB.GetConnection();
	        // Declare the JDBC objects.  a
	        // Create and execute a SELECT SQL statement.  
            String selectSql = "SELECT * from SessionState LEFT JOIN SessionStateData ON "
                   + "SessionStateData." + DBFIELD_SESSIONSTATEDATA_SESSIONID + "=SessionState." 
                   + DBFIELD_SESSIONSTATE_SESSIONID + " Where SessionState."+ DBFIELD_SESSIONSTATE_SESSIONID + "='" + newSessionID +"'";  
            statement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);  
            resultSet = statement.executeQuery(selectSql);  
            if(resultSet.next())
	            {	             
	             sessionStateValue=resultSet.getInt("SessionStep");
	             userStep=resultSet.getInt("userStep");
	             //Use Long wrapper class to handle null values
	             Long customerID=resultSet.getLong("customerid");
	             
	             //Handle if customer id is missed first time during session 
	             if(customerID==null || Long.valueOf(customerID)==0)
	             {
	            	boolean foundCustomer=sessionCustomer.FillCustomerbyPhone(dbConnection, newPhoneNumber);
	             	if(foundCustomer)
	             	{
	             		customerID=sessionCustomer.getID();	             	
	             	}
	             	else
	             	{	
	             		customerID=Long.valueOf(0);    	
	             	}
	             } 
        	    sessionCustomer.FillCustomerbyID(dbConnection,customerID);
	            }
           		else // If No Records found then we need to create the session
	            {	            
				 startNewSession(newPhoneNumber,defaultState);
	            }
	            
	            //Values for session Id and Phone number are always set to passed in values
	            sessionID=newSessionID;	             
	            phoneNumber=newPhoneNumber;
	            
	            resultSet.close();
	            statement.close();
	                              
    		  }
    	catch (Exception e) {  
                e.printStackTrace();
                sessionStateValue = -1;
				userStep = -1;            
            	}  
        finally {  
                // Close the connections after the data has been handled.  
                if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}  
                if (statement != null) try { statement.close(); } catch(Exception e) {}  	              
           		}
        } // IF HNISQLDB.ConnectDB
      else  
    	{
		//No Database Connection - Return negative one values
		sessionStateValue = -1;
		userStep = -1;
		statusCode="500";
		userReturnMessage="HNI System Error: An error has occurred in the system. Please retry later.";
	    }
	 }
	private void startNewSession(String NewPhoneNumber,int defaultState)
	{
		phoneNumber=NewPhoneNumber;
		//If it's a new session we need to load the customer information
		
    	//Looks up Customer Info from database based on session ID		
		//Dummy Value
		Statement statement = null;   
	    ResultSet resultSet = null;  
	
		if(dbConnection==null)
		{   // Connected to Database			
		    dbConnection=hNISQLDB.GetConnection();
		}
		
		 try
		  {
			// Get Customer Info  
  		    boolean foundCustomer=sessionCustomer.FillCustomerbyPhone(dbConnection, phoneNumber);
  		    sessionStateValue = defaultState;
			userStep = 1; // New Session 1st Step of User Conversation
  		    }  		    	            
    		  
    	catch (Exception e) 
    	       {  
                e.printStackTrace();
                sessionStateValue = -1;
				userStep = -1;				
            	} 
				
	}
	
	
	public int getState() {
		return sessionStateValue;
	}
    
	public void setState(int newSessionState) {
		//Set States local value  only - must be saved to database
		sessionStateValue = newSessionState;

	}
	public int getUserStep(){
		return userStep;
	}
	public void setUserStep(int newUserStep){
			userStep=newUserStep;
	}
	public boolean SaveState(String userText, String responseText){		
		return DBSaveSessionState(userText,responseText);
	}	 	
	public String getUserReturnMessage(){
		return userReturnMessage;
	}
	public void setUserReturnMessage(String newMessage){
		userReturnMessage=newMessage;
	}
	public void setUserText(String newText){
		userText=newText;
	}
	public String getUserText(){
		return userText;
	}
	public void setStatusCode(String newStatusCode){
		statusCode=newStatusCode;
	}
	public String getStatusCode(){
		return statusCode;
	}	
	
	public String getCustomerNickName(){
		 String nickName=sessionCustomer.getNickname();
		 if(nickName != null) {
		 	return nickName;
		 }
		 else
		 {
		 	return "";
		 }		  
	 }
   public boolean isValidCustomer()
   {
   		//customer id is greater than zero for all valid customers
   	    return sessionCustomer.isCustomerFoundInDB();   	 	
   }	
   	
   
   public Customer getSessionCustomer()
   {
      return sessionCustomer;
   }
   
	private java.sql.Date currentDateTime()
	{
	 LocalDateTime ldt=LocalDateTime.now();	             	             
     java.sql.Date updatedDate= new java.sql.Date(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());	  	                        
     return updatedDate;
	}
private void DBSaveSessionJSONData(Connection dbActiveConnection,int dataValueType,String jsonValueasaString)
	{
	      // Declare the JDBC objects.
	       Statement statement = null;   
           ResultSet resultSet = null;  
	
            
		  try { 
		 
	          
	           // Create and execute a SELECT SQL statement.  
               String selectSessionSql = "SELECT * from SessionStateDataJSON Where "+  DBFIELD_SESSIONSTATEDATAJSON_SESSIONID
            	   + "='" + sessionID +"' AND " + DBFIELD_SESSIONSTATEDATAJSON_DATATYPEVALUE + "=" + dataValueType;  
            statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
            resultSet = statement.executeQuery(selectSessionSql);
            //Update data Row if it exists
             
            boolean newRow=(resultSet.next());
            if(newRow)
            {
            resultSet.moveToInsertRow();
            }                        
            resultSet.updateString(DBFIELD_SESSIONSTATEDATAJSON_SESSIONID,sessionID);
 	        resultSet.updateInt(DBFIELD_SESSIONSTATEDATAJSON_DATATYPEVALUE,dataValueType);
 	        resultSet.updateString(DBFIELD_SESSIONSTATEDATAJSON_jsondata,jsonValueasaString);	                    
            if(newRow)
            {
            	resultSet.insertRow();
	        
            }
            else
            {
                resultSet.updateRow();     	
            }       
	                             
             resultSet.close();
             statement.close();       
    		  }
    	catch (Exception e) {  
                e.printStackTrace();
                            
            	}  
        finally {  
                // Close the connections after the data has been handled.  
                if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}  
                if (statement != null) try { statement.close(); } catch(Exception e) {}  	              
           		}
        }
	    
	
}