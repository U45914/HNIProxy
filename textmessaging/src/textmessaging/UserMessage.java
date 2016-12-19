package textmessaging;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.*;
import java.io.StringReader;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/usermessage")
public class UserMessage  {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(UserMessage.class);

	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String RespondToMessageHTML(@QueryParam("auth_key") String authkey,@QueryParam("phonenumber") String PhoneNumber,@QueryParam("sessionid") String Sessionid,@QueryParam("usertext") String UserMessage ,@QueryParam("testmode") String TestMode) {
        Workflow CallWorkflow = new Workflow();
        WebRedirect WebRedirectHNI=new WebRedirect();
        String ReturnMessage =new String();
        //Converted to Pass Through on 11/21	        
 	   
      //  ReturnMessage=(CallWorkflow.ExecuteCallWorkflow(Sessionid,PhoneNumber,UserMessage,TestMode));
        LOGGER.info(authkey,Sessionid,PhoneNumber,UserMessage,TestMode);        
        ReturnMessage=WebRedirectHNI.sendHNITextMessage(authkey,Sessionid,PhoneNumber,UserMessage,TestMode);
        LOGGER.info(ReturnMessage);
        
        //return ReturnMessage;
	    return "<html> " +  ReturnMessage + "</html>";
	  }
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		  public String RespondToMessage(@QueryParam("auth_key") String authkey,@QueryParam("phonenumber") String PhoneNumber,@QueryParam("sessionid") String Sessionid,@QueryParam("usertext") String UserMessage,@QueryParam("testmode") String TestMode ) {
	        Workflow CallWorkflow = new Workflow();
	        WebRedirect WebRedirectHNI=new WebRedirect();
	        String ReturnMessage =new String();
	      //Converted to Pass Through on 11/21	        
	      //  ReturnMessage=(CallWorkflow.ExecuteCallWorkflow(Sessionid,PhoneNumber,UserMessage,TestMode));
	        LOGGER.info(authkey,Sessionid,PhoneNumber,UserMessage,TestMode);
	        ReturnMessage=WebRedirectHNI.sendHNITextMessage(authkey,Sessionid,PhoneNumber,UserMessage,TestMode);
	        LOGGER.info(ReturnMessage);
	        return ReturnMessage;
	     
	  	}
		  
	  
	
}
