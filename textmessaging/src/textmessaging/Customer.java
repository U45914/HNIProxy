package textmessaging;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Customer {
    
	private long id;
	private String firstname="";
	private String lastname="";
	private String nickname="";
	private String phonenumber="";
	private String email="";
	private int maxorders=0;
	private boolean customerinDB=false;

	private static final String DBFIELD_CUSTOMER_PHONENUMBER="phonenumber";
	private static final String DBFIELD_CUSTOMER_FIRSTNAME="firstname";
	private static final String DBFIELD_CUSTOMER_LASTNAME="lastname";
	private static final String DBFIELD_CUSTOMER_ID="id";
	private static final String DBFIELD_CUSTOMER_EMAIL="emailaddress";
	private static final String DBFIELD_CUSTOMER_FAMILYMEMBERS="familymembers";
	
	private static final String DBFIELD_ACTIVATION_CUSTOMERID="customer_id";
	private static final String DBFIELD_ACTIVATION_MEALSREMAINING="mealsremaining";
    
	private AuthCode customerAuthCode =new AuthCode();
	
	private static final int LIMIT_MealsPerDay=1;  // TODO : Pull this from SystemLimits Table
	
	public boolean FillCustomerbyPhone(Connection dbConnection,String inputPhoneNumber)
	{
		 boolean returnValue=false;
		
		// Declare the JDBC objects.
		Statement statement = null;   
	    ResultSet resultSet = null;
	    
	    //Set PhoneNumber
	    phonenumber=inputPhoneNumber;
	    try
	    {
             
	    // Create and execute a SELECT SQL statement.  
         String selectSql = "SELECT * from Customers Where " +DBFIELD_CUSTOMER_PHONENUMBER + "='" + inputPhoneNumber +"'";  
         statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
         resultSet = statement.executeQuery(selectSql);
         //If there are no rows then CustomerinDB will become false
         customerinDB=(resultSet.next());
         if(customerinDB)
         {
        	 customerinDB=true;
        	 FillCustomerDataRow(resultSet);                         
         }
         resultSet.close();
         statement.close();
		returnValue=true;
	    }
	    catch (Exception e)
	    {  
            e.printStackTrace();
            returnValue=false;
        }  	    
	    return returnValue;
	}
	
	private void FillCustomerDataRow(ResultSet customersResultSet)
	{ //All of the value set are class level variables
	try
	{
	   	 firstname=customersResultSet.getString(DBFIELD_CUSTOMER_FIRSTNAME);
    	 nickname=firstname; // Assume firstname is NickName until we have this as another option/field
    	 lastname=customersResultSet.getString(DBFIELD_CUSTOMER_LASTNAME);
    	 email=customersResultSet.getString(DBFIELD_CUSTOMER_EMAIL);
    	 id=customersResultSet.getLong(DBFIELD_CUSTOMER_ID);
    	 phonenumber=customersResultSet.getString(DBFIELD_CUSTOMER_PHONENUMBER);
    	 maxorders=customersResultSet.getInt(DBFIELD_CUSTOMER_FAMILYMEMBERS);
	}
    catch (Exception e)
    {  
        e.printStackTrace();
	}
	}
	
	public boolean FillCustomerbyID(Connection dbConnection,long customerID)
	{
		// Declare the JDBC objects.
		Statement statement = null;   
	    ResultSet resultSet = null;
	    boolean returnValue=false;
	    
	    //Set PhoneNumber
	    id=customerID;
	    try
	    {
             
	    // Create and execute a SELECT SQL statement.  
         String selectSql = "SELECT * from Customers Where " + DBFIELD_CUSTOMER_ID +"=" + customerID;  
         statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
         resultSet = statement.executeQuery(selectSql);
         //If there are no rows then CustomerinDB will become false
         customerinDB=(resultSet.next());
         if(customerinDB)
         {
        	 customerinDB=true;
        	 FillCustomerDataRow(resultSet);                         
         }
         resultSet.close();
         statement.close();        
		 returnValue=true;
	    }
	    catch (Exception e)
	    {  
            e.printStackTrace();
            returnValue=false;
        }  	    
	    return returnValue;
	}
	public long getID() {
		return id;
	}
	public void setID(long newid) {
		id = newid;
	}
	public String getFirstname() {
		if(firstname !=null)
		{
			return firstname;
		}
		else
		{
			return "";
		}
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		if(lastname !=null)
		{
			return lastname;
		}
		else
		{
			return "";
		}
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getNickname() {
		if(nickname !=null)
		{
			return nickname;
		}
		else
		{
			return "";
		}
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhonenumber() {
		if(phonenumber !=null)
		{
			return phonenumber;
		}
		else
		{
			return "";
		}
		
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getEmail() {
		if(email !=null)
		{
			return email;
		}
		else
		{
			return "";
		}
		
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isCustomerFoundInDB()
	{
	       return customerinDB;	
	}
	 public boolean hasAuthorizationsLeft(Connection dbConnection)
	   {
	    	Statement statement = null;   
		    ResultSet resultSet = null;
		    boolean returnValue=false;
		    int mealsLeft=0;
		    //Set PhoneNumber
		    if(customerinDB){
		    	
		    
			    try
			    {
		             
			    // Create and execute a SELECT SQL statement.  
		         String selectSql = "select sum(" + DBFIELD_ACTIVATION_MEALSREMAINING + ")"
		        		             + " as MealsLeft From activation_codes Where " 
		        		             + DBFIELD_ACTIVATION_CUSTOMERID + "=" + id;  
		         statement = dbConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);  
		         resultSet = statement.executeQuery(selectSql);
		         resultSet.next();
		         mealsLeft=resultSet.getInt("MealsLeft");
		         if(mealsLeft>0)
		         {
		        	 returnValue=true;
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
		    }
		    else
		    {
		    	returnValue=false;
		    }
	     return returnValue;
	   }
	 public boolean hasMealsLeftToday()
		{
			//TODO
			return true;
		}
	 public boolean save()
	 {
		 boolean returnValue=false;
		 return returnValue;
	 }
	 
	 public boolean isAuthCodeValid(Connection dbConnection,String authCode)
	 {
		 return customerAuthCode.isAuthCodeValid(dbConnection,authCode);
	 }
	}

