package textmessaging;
import java.sql.*;

public class AuthCode {
	private static final String DBFIELD_ACTIVATIONCODE="activation_code";
	private static final String DBFIELD_ACTIVATIONCUSTOMERID="customer_id";
	private static final String DBFIELD_ACTIVATIONMEALSREMAINING="mealsremaining";
   
	public long decodeAuthCode(long authCode )
	{   
		return (605673*(authCode -151647)+100000) % 999983 ;
 		
	}
	public String encodeAuthCode(long inputCode)
	{   long encodedCode;
		encodedCode=(305914*(inputCode-100000)+151647) % 999983;  		 
		return String.format("%06d", encodedCode);
	}
	public boolean isAuthCodeValid(Connection dbConnection,String authCode)
	{
		boolean returnValue=false;
		// Declare the JDBC objects.
		Statement statement = null;   
	    ResultSet resultSet = null;
	    
		String selectSql = "SELECT * from activation_codes Where " + DBFIELD_ACTIVATIONCODE + "='" + authCode +"'";  
        
		try{
			statement = dbConnection.createStatement();  
			resultSet = statement.executeQuery(selectSql);
			//the code is valid id the code is in the database and there is no customer assigned
			if(resultSet.next()) 
			{   
			    long activationCustomer=resultSet.getLong(DBFIELD_ACTIVATIONCUSTOMERID);
			    //null values should be automatically converted to zero 
			    if (activationCustomer==0)
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
