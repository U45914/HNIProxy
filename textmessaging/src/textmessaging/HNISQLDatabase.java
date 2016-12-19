package textmessaging;
import java.sql.*;  

public class HNISQLDatabase {

	private  Connection dbconnection = null;
	
	            // Connect to your database.  
	            // Replace server name, username, and password with your credentials  
	            public boolean ConnectDB() {  
	                String connectionString =	                		
	                    "jdbc:sqlserver://hni.database.windows.net:1433;"  
	                    + "database=HNI;"  
	                    + "user=HungerAdmin;"  
	                    + "password=HNI!server73;"  
	                    + "encrypt=true;"  
	                    + "trustServerCertificate=false;"  
	                    + "hostNameInCertificate=*.database.windows.net;"  
	                    + "loginTimeout=30;"; 
	               
	                
	                              
	                try {  
	                	//Driver specification should not be needed in JDBC 4.0
	                	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	                	dbconnection = DriverManager.getConnection(connectionString);  
	      
	                    return true;
	                }  
	                catch (Exception e) {  
	                    e.printStackTrace();
	                    return false;
	                }  
	                
	            } 
	public Connection GetConnection()
	{ 
		// if dbconnection is empty try to connect again
		
		if(dbconnection==null)
		{
			boolean IsConnected=ConnectDB();
		}
	   return dbconnection;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
			
	}

}
