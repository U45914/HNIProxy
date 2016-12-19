package textmessaging;
import javax.json.*;

import common.dao.DAOManager;

import java.sql.*;

public class Workflow {
	
	
	private static final int STATE_NEWCALL=0;
	private static final int STATE_GETADDRESS=1;		
	private static final int STATE_GETLOCATIONCHOICES=2;	
	private static final int STATE_PROCESSLOCATIONCHOICE=3;
	private static final int STATE_ORDERMOREMEALS=4;
	private static final int STATE_ORDERPLACINGORDER=5;
	private static final int STATE_ORDERCONFIRM=6;
	
	private static final int STATE_STARTREGISTRATION=100;
	private static final int STATE_REGISTER_GETFIRSTNAME=101;
	private static final int STATE_REGISTER_GETLASTNAME=102;
	private static final int STATE_REGISTER_GETEMAIL=103;
	private static final int STATE_REGISTER_PROCESSEMAIL=104;
	private static final int STATE_REGISTER_CONFIRMEMAIL=105;
	private static final int STATE_REGISTER_GETAUTHCODE=106;
	private static final int STATE_REGISTER_PROCESSAUTHCODE=107;	
	private static final int STATE_REGISTER_USERREGISTRATIONCOMPLETE=108;
	private static final int STATE_REGISTER_ADDITIONALMEMBERS=109;
	private static final int STATE_REGISTER_ALREADYREGISTERED=110;
	
	private DAOManager daoManager;
	private static final int STATE_NEWREGISTER=100;
    
	private int maxLocationChoices=1;	
	
	
	
   
    private WorkflowStatus executeStepNewCall(String userMessage,Customer callCustomer,Connection dbConnection){
    	WorkflowStatus stepStatus=new WorkflowStatus();
    	//Convert input keyword to all upper case
    	String keyWord =userMessage.toUpperCase();    	
    	switch (keyWord)
    	{
    	//suport multiple keywords for enrollment
    	case "SIGNUP":
    	case "ENROLL":
    	case "REGISTER":
    		stepStatus.setNewState(STATE_NEWREGISTER);		        		
    		break;
    	//Support multiple Keywords for ordering"
    	case "MEAL":
    	case "ORDER":		        		
    		if(callCustomer.isCustomerFoundInDB())
    		{
    			if(callCustomer.hasAuthorizationsLeft(dbConnection))
    			{
    				if(callCustomer.hasMealsLeftToday())
        			{
        			stepStatus.setNewState(STATE_GETADDRESS);
        			stepStatus.setNeedUserInput(false);
        			}
        			else // NO Meals Left Today
        			{
        			 stepStatus.setReturnString("We have received your request but currently "
        		      + "you have already used all your meals for this day.");
        		     stepStatus.setNeedUserInput(true);
        			}
    			}
    			else // No Authorizations left
    			{
    				stepStatus.setReturnString("We have received your request but there are no "
        			 + "available meals for your account. Please contact your local organization.");
    				stepStatus.setNeedUserInput(true); 	
    			}
    		}
    		else //Not a Valid Customer 
    		{ 
    			stepStatus.setReturnString("This phone number is not currently registered with HNI. Please register your authorization code by sending the keyword ENROLL.");
    			stepStatus.setNeedUserInput(true); 	
    		}
    		break;
    	case "GIVE":
    	case "DONATE":
    			stepStatus.setReturnString("Thank you for your interest in helping prove that solving hunger is not impossible.  You can give online at http://www.hungernotimpossible.com/give");
    			stepStatus.setNeedUserInput(true); 	 //Converstation is over
    		break;
    	default: // No valid keyword entered		        		
    			stepStatus.setReturnString("I'm sorry we didn't recognize that keyword.  Please try using one of these keywords MEAL, REGISTER, HELP, or STOP");
    			stepStatus.setNeedUserInput(true); 	
    		break;
    	}		 
    	return stepStatus;
    	
    }
    private WorkflowStatus executeStepGetAddress(String userMessage,String customerName){
    	WorkflowStatus stepStatus=new WorkflowStatus();

		// Build Personalized Welcome
		if( customerName == null || customerName.isEmpty())
		{
			stepStatus.setReturnString("Hi!");	            		             			
		} 
		else
		{
			stepStatus.setReturnString("Hi " + customerName +"!") ;           		
		}	            		
		stepStatus.setReturnString(stepStatus.getReturnString() + "Please send me your address and "
				 + "city so I can find the closest meals to you. For example: 111 S Main St, Bentonville");
		stepStatus.setNewState(STATE_GETLOCATIONCHOICES);	            		
		stepStatus.setNeedUserInput(true);
		return stepStatus;
    }
    
	
    private WorkflowStatus executeStepGetLocationChoices(String userMessage,String SessionID)
    {
    	
    	WorkflowStatus stepStatus=new WorkflowStatus();
		//User Input 
  		GeoCodeAddress userLocation= new GeoCodeAddress();
  		//if user has valid address will get choices and prompt user
  		//Otherwise will return to user to renter
  		//Check to see if user location is valid
  		if(userLocation.ConvertBlockAddress(userMessage)) 
  		{
      		//Valid User Address so get locations currently open near address
  			//only bring 1 page of information
      		String locationInfo = "";//DummyGetLocations(userLocation.getLatitude(),userLocation.getLongitude(),1,maxLocationChoices);
      		//Empty LocationInfo String means no locations found
      		//Moves to Next Workflow Step
      		if (locationInfo.intern() != "")
      		{
      			stepStatus.setReturnString("OK! Here's what I found. Reply with the number for the location /meal you prefer"
      		                               + " (for example: 2) or # to start over.");
      			stepStatus.setReturnString(stepStatus.getReturnString() + locationInfo);
      			stepStatus.setNeedUserInput(true);
      		}    	      		
      		else
      			// Could not find locations so need to have user retry 
      			//Does NOT Move to Next Workflow step
      		{   	      			
      			stepStatus.setReturnString("Sorry, can you try a different address? That one didn't seem to have any "
      					                  + "locations currently open nearby.");
      			stepStatus.setNeedUserInput(true);
      		}
  		}
  		else
  		{
  			//Invalid Address - Give User Error Message and don't update next session state
  			//Does NOT Move to Next Workflow step
  			stepStatus.setReturnString("Sorry, can you try a different address? That one didn't seem to work.");
  			stepStatus.setNeedUserInput(true);
  		}    	      		
  		stepStatus.setNeedUserInput(true);
  		return stepStatus;
    }
    private WorkflowStatus executeStepPlaceOrder(String sessionID)
    {
    	WorkflowStatus stepStatus=new WorkflowStatus();
    	stepStatus.setReturnString("Great I will place your order. Please give me a moment to place your order. I'll let know you as soon as I'm done");
    	stepStatus.setNewState(STATE_ORDERCONFIRM);
    	stepStatus.setNeedUserInput(true);

    	return stepStatus;	
    }
    private WorkflowStatus executeStepProcessLocationChoice(String userMessage)
    {        	
    	//TODO
    	WorkflowStatus stepStatus=new WorkflowStatus();
    	
    	return stepStatus;
    }
	
    private WorkflowStatus mockupStepGetLocationChoices(String userMessage,String SessionID)
    {    	
     	WorkflowStatus stepStatus=new WorkflowStatus();
     	stepStatus.setReturnString("OK! Here's what I found. Reply with the number for the location /meal you prefer"
                  + " (for example: 2) or # to start over.");
     	stepStatus.setReturnString(stepStatus.getReturnString() + "1. Taco Bell (Beef Tacos) 818 Walnut Street, ) "
     			                   + "2. McDonalds (Chef Salad) 138 Dixieland Road, 3. Stake N Shake (Chicken Salad)"
     			                   + " 6934 28th Street:");
     	stepStatus.setNewState(STATE_PROCESSLOCATIONCHOICE);
     	stepStatus.setNeedUserInput(true);
     	return stepStatus;
    }
    private WorkflowStatus mockUpStepProcessLocationChoice(String userMessage,String SessionID)
    {   	
    	WorkflowStatus stepStatus=new WorkflowStatus();
    	switch (userMessage)
    	{
    				//will need to save user selection
    	case "1":    			
    	case "2":
    	case "3":
    		    stepStatus.setNewState(STATE_ORDERMOREMEALS);  		    
    		    stepStatus.setNeedUserInput(false);
    			break;
    	case "#":
    			stepStatus.setNewState(STATE_GETADDRESS);
    			stepStatus.setNeedUserInput(false);
    			break;
    	default :    		
    			stepStatus.setReturnString("Sorry, I didn't get that. Please reply 1,2, or 3 to tell me which you want. Press # to start over.");
    			stepStatus.setNeedUserInput(true);
    	}  	
     	
     	return stepStatus;
    }
   
    
    private WorkflowStatus executeStepStartRegistration()
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	
       	stepStatus.setReturnString("Welcome to Hunger Not Impossible! Msg&data rates may apply."
       						       + "8 messages / transaction.  Reply HELP for Help, STOP to "
       						       + "cancel.  Any information you provide here will "
       			                   + "be kept private. Reply with PRIVACY to learn more."
       						       + "Let's get you registered. What's your first name?");       			                   
       	stepStatus.setNewState(STATE_REGISTER_GETFIRSTNAME);
       	stepStatus.setNeedUserInput(true);
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterGetFirstName(String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	if(userMessage==null || userMessage.equals(""))
       	{       		
       		stepStatus.setReturnString("We didn't get that. Please send your " 
				       + "first name again.");
				          			                   
       		stepStatus.setNewState(STATE_REGISTER_GETFIRSTNAME);
       		
       	}
       	else
       	{
       		stepStatus.setReturnString("Thanks " + userMessage + ". What's your "
			       + "last name?");			           			                   
       		stepStatus.setNewState(STATE_REGISTER_GETLASTNAME);
       	}
       	stepStatus.setNeedUserInput(true);
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterGetLastName(String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	if(userMessage==null || userMessage.equals(""))
       	{       		
       		stepStatus.setReturnString("We didn't get that. Please send your " 
				       + "last name again.");
				          			                   
       		stepStatus.setNewState(STATE_REGISTER_GETLASTNAME);
       		stepStatus.setNeedUserInput(true);
       	}
       	else
       	{     		         			                   
       		stepStatus.setNewState(STATE_REGISTER_GETEMAIL);
       		stepStatus.setNeedUserInput(false);
       	}       	
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterGetEmail()
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	stepStatus.setReturnString("Perfect! Lastly, I'd like to get your email address "
       					         + "to verify your account in case you text me from a new "
       			                 + "number. So what's your email address? Thanks");
       	stepStatus.setNewState(STATE_REGISTER_PROCESSEMAIL);       
       	stepStatus.setNeedUserInput(true);
    	return stepStatus;
    }
    private WorkflowStatus executeStepRegisterProcessEmail(String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	if(userMessage==null || userMessage.equals(""))
       	{       		
       		stepStatus.setReturnString("We didn't get that. Please send your " 
				       					+ "email address.");				          			                   
       		stepStatus.setNewState(STATE_REGISTER_PROCESSEMAIL);
       		stepStatus.setNeedUserInput(true);
       	}
       	else
       	{
       		stepStatus.setReturnString("Okay! I have " + userMessage + " as your email address. "
       					         + "Is that correct? Reply 1 for yes and 2 for no");       			                 
       	 	stepStatus.setNewState(STATE_REGISTER_CONFIRMEMAIL);       
       		stepStatus.setNeedUserInput(true);
       	}
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterGetAuthCode()
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	stepStatus.setReturnString("Please enter the 6 digit authorization code provied to you for this program?");       					         
       	stepStatus.setNewState(STATE_REGISTER_PROCESSAUTHCODE);       
       	stepStatus.setNeedUserInput(true);
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterProcessAuthCode(Connection dbConnection,Customer currentCustomer,String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	//TODO       					         
       	if(currentCustomer.isAuthCodeValid(dbConnection, userMessage))
       	{
       		stepStatus.setNewState(STATE_REGISTER_USERREGISTRATIONCOMPLETE);
       		stepStatus.setNeedUserInput(false);
       	}
       	else
       	{
       		stepStatus.setReturnString("The authorization code you entered (" + userMessage+") is not valid."
       						           +" Please resend a valid unused authorization code");
       		stepStatus.setNewState(STATE_REGISTER_PROCESSAUTHCODE);
       		stepStatus.setNeedUserInput(true);
       	}
    	return stepStatus;
    }
    
    
    private WorkflowStatus executeStepRegisterConfirmEmail(String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	switch (userMessage)
       	{ 
       		//pass through multiple options for 1 and 2
       		case "Y":       		
       		case "YES":
       		case "yes":
       		case "1":   
       			stepStatus.setNewState(STATE_REGISTER_GETAUTHCODE);
       			stepStatus.setNeedUserInput(false);
       			break;
       		default: // Invalid response or No will have them put their email back in
       			stepStatus.setNewState(STATE_REGISTER_GETEMAIL);
       			stepStatus.setNeedUserInput(false);
       	}
       	
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepUserRegisterationComplete()
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	stepStatus.setReturnString("Ok. You're all setup for yourself.  If you have additional family" 
       						       +" members to register please enter the additional authorization"
       							   +" codes now.  When you need a meal just text MEAL back to this number");       					         
       	stepStatus.setNewState(STATE_REGISTER_ADDITIONALMEMBERS);       
       	stepStatus.setNeedUserInput(true);
    	return stepStatus;
    }
    
    private WorkflowStatus executeStepRegisterProcessFamilyMembersAuthCode(Connection dbConnection,Customer currentCustomer,String userMessage)
    {        	
       	WorkflowStatus stepStatus=new WorkflowStatus();
       	//TODO       					         
       	if(currentCustomer.isAuthCodeValid(dbConnection, userMessage))
       	{
       		stepStatus.setReturnString("We have added that authorization code to your family account. Please"
       								   + " send any additional codes you need for your family .");
       		stepStatus.setNewState(STATE_REGISTER_ADDITIONALMEMBERS);
       		stepStatus.setNeedUserInput(true);
       	}
       	else
       	{
       		stepStatus.setReturnString("The authorization code you entered (" + userMessage+") is not valid."
       						           +" Please resend a valid unused authorization code");
       		stepStatus.setNewState(STATE_REGISTER_ADDITIONALMEMBERS);
       		stepStatus.setNeedUserInput(true);
       	}
    	return stepStatus;
    }
    
	public String ExecuteCallWorkflow(String sessionID, String phoneNumber,String userMessage, String testMode)
	{
		WorkflowStatus currentStatus=new WorkflowStatus();
		SessionState CallSessionState = new SessionState();
		currentStatus.setNeedUserInput(false);
		//Default message is an error message. Should never pass through to user
		currentStatus.setReturnString("Sorry I didn't understand that message. Please retry.");	
		int callState=0;
		
		try
		{			
			HNISQLDatabase hNISQLDB = new HNISQLDatabase();
			DAOManager hniDAOManager=new DAOManager(hNISQLDB.GetConnection());		    
		    CallSessionState.DBFillSessionState(sessionID,phoneNumber,STATE_NEWCALL);
		}
		catch (Exception e)
		{
			currentStatus.setReturnString("System Error - Sorry I didn't understand that message. Please retry.");
			e.printStackTrace();
			
		}
			
			
			
			
			//Execute call flow unless TestMode was specified
			if(testMode==null)
			{
			while (currentStatus.isNeedUserInput() == false)
			{
				//Increment User Step
				CallSessionState.setUserStep(CallSessionState.getUserStep()+1);
				callState=CallSessionState.getState();				
		        switch (callState)
		        {
		        case STATE_NEWCALL:
		        	currentStatus=executeStepNewCall(userMessage,CallSessionState.getSessionCustomer(),CallSessionState.getConnection());
		        	//Handle KeyWords or route call to ordering a meal by default		         
		        	break;
		        case STATE_GETADDRESS:
		        	String customerName=CallSessionState.getCustomerNickName();
		        	currentStatus=executeStepGetAddress(userMessage,customerName);      	
	                break;
		        case STATE_GETLOCATIONCHOICES:		        		
		        	    if(phoneNumber.equals("8188461238"))
		        		{		        	    
		        	       currentStatus=executeStepGetLocationChoices(userMessage,sessionID);
		        		}
		        	    else
		        	    {		        	    
		        	      currentStatus=mockupStepGetLocationChoices(userMessage,sessionID);
		        	    }
	        	        break;	
		        case STATE_PROCESSLOCATIONCHOICE: 
		        	if(phoneNumber.equals("8188461238"))
	        		{		        	    
	        	       //currentStatus=executeStepGetLocationChoices(userMessage,sessionID);
	        		}
	        	    else
	        	    {		        	    
	        	      currentStatus=mockUpStepProcessLocationChoice(userMessage,sessionID);
	        	    }
	                     break;        
		        case STATE_ORDERMOREMEALS:
		        	if(phoneNumber.equals("8188461238"))
		        	{
		        		//Processs more meals
		        	}
		        	else
		        	{
		        	   currentStatus.setNewState(STATE_ORDERPLACINGORDER);
		        	   currentStatus.setNeedUserInput(false);
		        	}
		        	break;
		        case STATE_ORDERPLACINGORDER:
		        	   currentStatus=executeStepPlaceOrder(sessionID);
		        	   break;
		        case STATE_STARTREGISTRATION:
		        	   currentStatus=executeStepStartRegistration();
		        	   break;
		        case STATE_REGISTER_GETFIRSTNAME:
		        	   currentStatus=executeStepRegisterGetFirstName(userMessage);
		        	   break;
		        case STATE_REGISTER_GETLASTNAME:
		        	   currentStatus=executeStepRegisterGetLastName(userMessage);
		        	   break;
		        case STATE_REGISTER_GETEMAIL:
		        	   currentStatus=executeStepRegisterGetEmail();
		        	   break;
		        case STATE_REGISTER_PROCESSEMAIL:
		        		currentStatus=executeStepRegisterProcessEmail(userMessage);
		        		break;
		        case STATE_REGISTER_CONFIRMEMAIL:
		        		currentStatus=executeStepRegisterConfirmEmail(userMessage);
		        		break;
		        case STATE_REGISTER_GETAUTHCODE:
	        			currentStatus=executeStepRegisterGetAuthCode();
	        			break;
		        case STATE_REGISTER_PROCESSAUTHCODE:
		        		currentStatus=executeStepRegisterProcessAuthCode(CallSessionState.getConnection(),
		        														 CallSessionState.getSessionCustomer(),userMessage);
		        		break;
		        case STATE_REGISTER_USERREGISTRATIONCOMPLETE:
		        		currentStatus=executeStepUserRegisterationComplete();
		        		break;
		        case STATE_REGISTER_ADDITIONALMEMBERS:
		        		currentStatus=executeStepRegisterProcessFamilyMembersAuthCode(CallSessionState.getConnection(),
		        																	  CallSessionState.getSessionCustomer(),userMessage);
		        		break;		      
		        default: 
	            		 currentStatus.setReturnString("An unexpected system error has occured. Please retry later.");
	            	     currentStatus.setNeedUserInput(false);
	                     break;
		        }
		    CallSessionState.setState(currentStatus.getNewState());
			}
			
			CallSessionState.setUserReturnMessage(currentStatus.getReturnString());
	        CallSessionState.SaveState(userMessage,currentStatus.getReturnString());
	        currentStatus.setReturnString(FormatJSONResponse(CallSessionState.getStatusCode(),CallSessionState.getUserReturnMessage()));
			}
			else
			{ // TestMode
				 switch (testMode)
			        {
			        case "API":       	
			        	currentStatus.setReturnString( FormatJSONResponse("400","Invalid API key"));
			        	break;
			        case "phone":       	
			        	currentStatus.setReturnString(FormatJSONResponse("400","Invalid phone"));
			        	break;
			        case "session":
			        	currentStatus.setReturnString(  FormatJSONResponse("400","Invalid session"));
			        	break;
			        default:		        	
			        	currentStatus.setReturnString( FormatJSONResponse("500","Something went wrong. Please try again later"));
			        	break;
			        }			
			}		
			return currentStatus.getReturnString();			
		}
	
	    private String FormatJSONResponse(String returnCode,String responseBody){
	    	 JsonObject jsonvalue;
	    	 //If ReturnCodeis 200 (Non Error) then bring back json list otherwise bring back error json
	    	 if(returnCode=="200")
	    	 {
	    	 jsonvalue = Json.createObjectBuilder()
	    		     .add("Status", returnCode)	    		     
	    		     .add("message", Json.createObjectBuilder()
	    		     .add("1",responseBody))    		         
	    		     .build();
	    	 }
	    	 else
	    	 { 
	    		 jsonvalue = Json.createObjectBuilder()
		    		     .add("Status", returnCode)
		    		     .add("error",responseBody)		    		         		         
		    		     .build();
	    	 }    
	    return jsonvalue.toString();
	    }
	 
	    public static void main(String[] args) {
	}
	    
	    
}
