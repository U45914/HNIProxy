package textmessaging;

import javax.ws.rs.client.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URLEncoder;
import javax.json.*;

public class GeoCodeAddress 
{
private String blockAddress;
private String address;
private String city;
private String zipCode;
private String state;
private double longitude;
private double latitude;
private JsonObject locationchoices;

private static final String GOOGLE_MAP_API_KEY="AIzaSyBCmt3RMn46CIpxUx20hmlpPbx6ws-lbkI";


//empty constructor
public GeoCodeAddress(){
	
}
	/**
 * @return the blockAddress
 */
public String getBlockAddress() {
	return blockAddress;
}


/**
 * @param blockAddress the blockAddress to set
 */
public void setBlockAddress(String newblockAddress) {
	blockAddress = newblockAddress;
}


/**
 * @return the address
 */
public String getAddress() {
	return address;
}


/**
 * @param address the address to set
 */
public void setAddress(String newaddress) {
	address = newaddress;
}


/**
 * @return the city
 */
public String getCity() {
	return city;
}


/**
 * @param city the city to set
 */
public void setCity(String newcity) {
	city = newcity;
}


/**
 * @return the zipCode
 */
public String getZipCode() {
	return zipCode;
}


/**
 * @param zipCode the zipCode to set
 */
public void setZipCode(String newZipCode) {
	zipCode = newZipCode;
}


/**
 * @return the state
 */
public String getState() {
	return state;
}


/**
 * @param state the state to set
 */
public void setState(String newState) {
	state = newState;
}


/**
 * @return the longitude
 */
public double getLongitude() {
	return longitude;
}


/**
 * @param longitude the longitude to set
 */
public void setLongitude(double newLongitude) {
	longitude = newLongitude;
}


/**
 * @return the latitude
 */
public double getLatitude() {
	return latitude;
}


/**
 * @param latitude the latitude to set
 */
public void setLatitude(double newLatitude) {
	latitude = newLatitude;
}
public boolean ConvertBlockAddress(String newBlockAddress)
{  
	boolean returnValue=false ;
	try
	{
	 //USE Google GEOCode API for resolving current address
	 Client geoClient=ClientBuilder.newClient();
	 String targetURI="https://maps.googleapis.com/maps/api/geocode/json?address=" +
			 	       URLEncoder.encode(newBlockAddress,"UTF-8") + "&key=" + GOOGLE_MAP_API_KEY;
	 
	 WebTarget geoTarget=geoClient.target(targetURI);
	 //TODO - Need to Fix Geocoding API Call
     Response geoResponse = geoTarget.request(MediaType.APPLICATION_JSON).get();
     if(geoResponse.getStatus()==200)
	     {
    	 StringReader stringReader = new StringReader(geoTarget.request(MediaType.APPLICATION_JSON).get(String.class));
         JsonReader jsonReader = Json.createReader(stringReader);
         JsonObject jsonData=jsonReader.readObject();
         blockAddress=jsonData.getString("geometry");
         JsonArray jsonDataArray=jsonData.getJsonArray("geometry");
         
	     blockAddress=newBlockAddress;
	     latitude=36.3631837;
	     longitude=-94.181901019708;
	     returnValue=true;
	     }
	     else
	     {
	    	 blockAddress=newBlockAddress;
	    	 latitude=36.3631837;
	    	 longitude=-94.181901019708;
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

    public JsonObject getJSONLocations() {
    	return locationchoices;
    
    }

 	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
   
}
