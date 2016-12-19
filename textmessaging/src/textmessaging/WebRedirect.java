package textmessaging;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.*;
import java.io.StringReader;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebRedirect {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebRedirect.class);
    private String FormatJSONResponse(String returnCode,String responseBody){
   	 JsonObject jsonvalue;
   	 //If ReturnCodeis 200 (Non Error) then bring back json list otherwise bring back error json
   	 if(returnCode.equals("200"))
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

    public void SendSlackAlert(String msgAlert)
    {
    	try
    	{
    		String slackURI="https://hooks.slack.com/services/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"; //order-alerts channel on HNI SLACK
    		//String slackURI="https://hooks.slack.com/services/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"; //testing channel on HNI SLACK
    		Client slackClient=ClientBuilder.newClient();
    		WebTarget slackTarget=slackClient.target(slackURI);
    		String msgText="";
    		msgText="{\"text\"" + ":" + "\"" + msgAlert + "\"}" ;    		 
    		Response slackresponse=slackTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(msgText));
    		LOGGER.info("Slack Message Sent:"+msgAlert);
    	}
    	catch (Exception e)
    	{
    		LOGGER.error("Slack Message Sending Error:" + msgAlert);
    	}
    }
	public String sendHNITextMessage(String authkey, String sessionID, String phoneNumber,String userMessage, String testMode)
	{
		 String hniReturnCode="500";
		 String hniReturnMessage="";
		 
		 try {
			 Client hniClient=ClientBuilder.newClient();
			 //convert null to SPACE for UserMessage
	
			 //Build Target URI - phonenumber and sessiond id should never be null but all other values could be
			 
			 //String targetURI="http://hni-api-dev3.centralus.cloudapp.azure.com:8080/api/v1/usermessage?" + "sessionid=";
			 String targetURI="http://hni-api-prod.centralus.cloudapp.azure.com:8080/api/v1/usermessage?" + "sessionid=";
			 
			 
			    if (sessionID!=null){
			    	  targetURI=targetURI + URLEncoder.encode(sessionID,"UTF-8");
				   } 	   
			    
		         targetURI=targetURI+ "&phonenumber=" ;
			   if (phoneNumber!=null)
			   {
				   targetURI=targetURI + URLEncoder.encode(phoneNumber,"UTF-8");
			   }
			   targetURI=targetURI+ "&usertext="; 
	   		    if(userMessage!= null) {targetURI=targetURI  + URLEncoder.encode(userMessage,"UTF-8");}    
	   			
	   			if(authkey != null){targetURI=targetURI+ "&authkey=" +URLEncoder.encode(authkey,"UTF-8");} 
			    if(testMode != null) {targetURI=targetURI+"&testmode=" +URLEncoder.encode(testMode,"UTF-8");}   
			    
			   
			LOGGER.info("URI-"+targetURI);
		 	WebTarget hniTarget=hniClient.target(targetURI);
		 	String hniType;
		 	boolean normalUser=true;
		 	//Triggers for testing users to get around launch prompts
		 	//if(authkey!=null){if(authkey.equals("99")){normalUser=false;}}
		 	//if(phoneNumber!=null){
		 //		if(phoneNumber.matches("9727551458|8188461238|4793669759|8173089913|4796444343|4799576762|4797907615|6822173771|1111111111|4252339883|4794661905"))
		// 		{ normalUser=false;}
		// 	}
		 	
		// 	if (normalUser)
		// 	{
		//	 	 hniReturnCode="200";
		//	 	 hniReturnMessage="Sorry. The launch date for the Hunger Not Impossible Program has been delayed until 12/3/16. Please try again then. ";
		//	  }
		// 	else
		// 	{		 		
		 	switch(userMessage.toUpperCase())
		 	 {  		 		
		 		case "MEAL":
		 		case "ORDER":
		 			SendSlackAlert("A Meal order has been started.");		 			
		 			break;
		 		case "CONFIRM":
		 			SendSlackAlert("A Meal has been confirmed.");		 			
		 			break;
		 		
		 		default:		 			
				 	break;	
		 	}
		 	hniType=MediaType.TEXT_PLAIN;
			Response hniResponse=hniTarget.request(hniType).get();
		     
		    hniReturnCode=String.valueOf(hniResponse.getStatus());
		    
		    hniReturnMessage =hniResponse.readEntity(String.class);	
		    //Workaround for the 404 on register your phone error from meal workflow 
		    if(hniReturnCode.equals("404") && hniReturnMessage !=null) {hniReturnCode="200";}
			//StringReader hniStringReader = new StringReader(hniTarget.request(MediaType.TEXT_PLAIN).get(String.class));   
			//hniReturnMessage= IOUtils.toString(hniStringReader);
			LOGGER.info("ReturnStatus-"+ hniReturnCode);
			LOGGER.info("ReturnMessage-"+hniReturnMessage);
		 //	}
		 	
		 }	
	catch (Exception e)
	{
		LOGGER.error("WebRedirectError",e);
		hniReturnCode="500";
		hniReturnMessage="Unexpected Error "+e.toString();
	}
	
    return FormatJSONResponse(hniReturnCode,hniReturnMessage);
	
	}
}
