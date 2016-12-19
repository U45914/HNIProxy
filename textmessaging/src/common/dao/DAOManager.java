package common.dao;

import java.sql.*;

import org.omg.CORBA.portable.ApplicationException;



public class DAOManager {
	 public static DAOManager instance=null;
	 
     public static DAOManager getInstance() {
     try 
     {
        if(instance==null)
        {
        	instance=new DAOManager();
        }
        return instance;
     }
     catch(Exception e)
 	 {
    	 e.printStackTrace();
    	 
 	  }
       return instance;
    }  

    

    public void close() throws SQLException {
        try
        {
            if(daocon!=null && daocon.isValid(0))
                daocon.close();            
        }
        catch(SQLException e) { throw e; }
    }

    
    private Connection daocon;
       
    public DAOManager(Connection newConnection) throws SQLException 
    {
        try
        {
        	if(newConnection.isValid(0))
        	{
        		daocon=newConnection;
        	}
        	else
        	{
        		daocon=null;
        	}
        }
        catch(SQLException sqle)
        {
        	throw sqle;
        }
        catch(Exception e)
        {
        	 e.printStackTrace();
               }
    }    
    //private constructor 
   private  DAOManager()
   {
	  
   }
 }
