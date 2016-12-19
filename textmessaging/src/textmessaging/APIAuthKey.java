package textmessaging;
import java.sql.*;


public class APIAuthKey {
private boolean authorized=false;
private static final String DBFIELD_APIKEY_APIKEY="authkey";
private static final String DBFIELD_APIKEY_ORGANIZATION="organization";
private static final String DBFIELD_APIKEY_AUTHORIZED="authorized";


	public boolean isKeyValid(Connection dbConnection,String apicode)
	{
		boolean returnValue=false;
		// Declare the JDBC objects.
		Statement statement = null;   
	    ResultSet resultSet = null;
	    
		String selectSql = "SELECT * from  APIAuthKey Where " + DBFIELD_APIKEY_APIKEY + "='" + apicode +"'";  
        
		try{
			statement = dbConnection.createStatement();  
			resultSet = statement.executeQuery(selectSql);
			//the code is valid id the code is in the database and there is no customer assigned
			if(resultSet.next()) 
			{   
			    //null values should be automatically converted to zero 
			    if (resultSet.getBoolean(DBFIELD_APIKEY_APIKEY))
			    {
			    	returnValue=true;
			    }
			    else
			    { 
			    	returnValue=false;
			    }
			}
			else
			{
				returnValue=false;
			}
		}
		 catch (Exception e)
	    {  
            e.printStackTrace();
            returnValue=false;
        }  	    
		return returnValue;
}
}
