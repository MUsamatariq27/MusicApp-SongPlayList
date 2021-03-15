package com.csc301.songmicroservice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import okhttp3.RequestBody;

import java.util.Map;

public class Utils {

	public static RequestBody emptyRequestBody = RequestBody.create(null, "");
	
	// Used to determine path that was called from within each REST route, you don't need to modify this
	public static String getUrl(HttpServletRequest req) {
		String requestUrl = req.getRequestURL().toString();
		String queryString = req.getQueryString();

		if (queryString != null) {
			requestUrl += "?" + queryString;
		}
		return requestUrl;
	}
	
	// Sets the response status and data for a response from the server. You will not always be able to use this function
	public static Map<String, Object> setResponseStatus(Map<String, Object> response, DbQueryExecResult dbQueryExecResult, Object data) {	
		switch (dbQueryExecResult) {
		case QUERY_OK:
			response.put("status", HttpStatus.OK);
			if (data != null) {
				response.put("data", data);
			}
			break;
		case QUERY_ERROR_NOT_FOUND:
			response.put("status", HttpStatus.NOT_FOUND);
			break;
		case QUERY_ERROR_GENERIC:
			response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			break;
		}
		
		return response;
	}
	
	
    public static Map<String, Object> setResponseStatus2(Map<String, Object> response, 
        DbQueryExecResult dbQueryExecResult) {   
         switch (dbQueryExecResult) {
         case QUERY_OK:
             response.put("status", HttpStatus.OK);
             break;
         case QUERY_ERROR_NOT_FOUND:
             response.put("status", "Id NOT_FOUND");
             break;
         case QUERY_ERROR_GENERIC:
           response.put("status", HttpStatus.INTERNAL_SERVER_ERROR); 
             break;
         }
         
         return response;
     }
	
	
	   public static Map<String, Object> setResponseStatus3(Map<String, Object> response, DbQueryExecResult dbQueryExecResult,
	       String message) {   
	        switch (dbQueryExecResult) {
	        case QUERY_OK:
	            response.put("status", HttpStatus.OK);
	            break;
	        case QUERY_ERROR_NOT_FOUND:
	            response.put("status", "Id NOT_FOUND");
	            break;
	        case QUERY_ERROR_GENERIC:
	            if (message.equals("Object Found, But Likes Can't be negative")) 
	            {
	              response.put("status", "Object Found, But Likes Can't be negative");
	            }
	            else {response.put("status", HttpStatus.INTERNAL_SERVER_ERROR); }
	            break;
	        }
	        
	        return response;
	    }
}