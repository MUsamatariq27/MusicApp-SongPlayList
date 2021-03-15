package com.csc301.profilemicroservice;


import java.io.IOException;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Repository;
import org.neo4j.driver.v1.Transaction;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



@Repository
public class PlaylistDriverImpl implements PlaylistDriver {

	Driver driver = ProfileMicroserviceApplication.driver;
	
	OkHttpClient client = new OkHttpClient();

	public static void InitPlaylistDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nPlaylist:playlist) ASSERT exists(nPlaylist.plName)";
				trans.run(queryStr);
				trans.success();
			}
			session.close();
		}
	}

	@Override
	public DbQueryStatus likeSong(String userName, String songId) {
	  
	   DbQueryStatus status;
	   
	   try 
	   {
	    
	    Request request = new Request.Builder().url("http://localhost:3001/getSongTitleById/" 
	        + songId).method("GET", null).build();
	    
	       Call call = client.newCall(request);
	       Response responseFromAddMs = null;

	        String addServiceBody = "{}";
	        
	        try {
	            responseFromAddMs = call.execute();
	            addServiceBody = responseFromAddMs.body().string();

	           // System.out.println(addServiceBody);
	            
	            if (!addServiceBody.contains("OK")) 
	            {
	              status = new DbQueryStatus("SongId not in the database!", 
	                  DbQueryExecResult.QUERY_ERROR_GENERIC);
	              return status;
	              
	            }
	            
	            
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        status = new DbQueryStatus("INTERNAL SERVER ERROR", 
	             DbQueryExecResult.QUERY_ERROR_GENERIC);
	         //return status;

	     
	   }
	   catch(Exception e) 
	   {
	     status = new DbQueryStatus("INTERNAL SERVER ERROR", 
             DbQueryExecResult.QUERY_ERROR_GENERIC);
	     //return status;
	     
	   }
	   
	   try (Session session = ProfileMicroserviceApplication.driver.session()) {
	        try (Transaction trans = session.beginTransaction()) {
	          
	         
	          String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
	          String query = String.format(template, userName); 
	          StatementResult node = trans.run(query);
	          
	          String playlistname =  userName + "-favorites";
	          
	          //System.out.println(playlistname);
	          
	          String template2 = "MATCH (p1:playlist{plName: '%s'})," +
	              "(s1:song{songId: '%s'}) MATCH ((p1)-[r:includes]->(s1))" 
	          + "Return r";
	              
	          String query2 = String.format(template2, playlistname, songId);
	          StatementResult node2 = trans.run(query2);

	          
	          if (!node.hasNext()) 
	          {
	            trans.success();
	            session.close();
	            
	            status = new DbQueryStatus("userName not found", 
	                DbQueryExecResult.QUERY_ERROR_GENERIC);
	            return status; 
	          }
	          else if (node2.hasNext()) 
	          {
	            
	            status = new DbQueryStatus("the song is included in the playlist", 
                    DbQueryExecResult.QUERY_OK);
                return status;
	            
	          }
	          
	          template = "MATCH (a:playlist{plName: '%s'}) " +
	               "CREATE (a)-[r:includes]->(s1:song{songId: '%s'}) Return s1";
	          
	          query = String.format(template, playlistname ,songId);
	          
	          trans.run(query);
	          
	          HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:3001/updateSongFavouritesCount/" + 
	          songId).newBuilder();
              urlBuilder.addQueryParameter("shouldDecrement", "false");

              String url = urlBuilder.build().toString();
              
              //System.out.println(url);
              
              RequestBody body = RequestBody.create(null, new byte[0]);

              Request request = new Request.Builder()
                      .url(url)
                      .method("PUT", body)
                      .build();

              Call call = client.newCall(request);
              Response responseFromMulMs = null;

              String addServiceBody = "{}";

              try {
                  responseFromMulMs = call.execute();
                  addServiceBody = responseFromMulMs.body().string();
                  
              } catch (IOException e) {
                  e.printStackTrace();
              }
	          
	          
	          
	          
	          
	           status = new DbQueryStatus("Ok", 
	               DbQueryExecResult.QUERY_OK);
	          
	            trans.success();
	        }
	        session.close();
	        return status;
	        
	    }
	
		
	}

	@Override
	public DbQueryStatus unlikeSong(String userName, String songId) {
	  
	    DbQueryStatus status;
	       
	    try 
	    {
	        
	      Request request = new Request.Builder().url("http://localhost:3001/getSongTitleById/" 
	      + songId).method("GET", null).build();
	        
	      Call call = client.newCall(request);
	      Response responseFromAddMs = null;

	      String addServiceBody = "{}";
	            
	      try {
	        
	        responseFromAddMs = call.execute();
	        addServiceBody = responseFromAddMs.body().string();
	        if (!addServiceBody.contains("OK")) 
            {
	          status = new DbQueryStatus("SongId not in the database!", 
	              DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
	          return status;
	                  
             }
	        
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        status = new DbQueryStatus("INTERNAL SERVER ERROR", 
                DbQueryExecResult.QUERY_ERROR_GENERIC);
            return status;
	        }
	         
	       }
	       catch(Exception e) 
	       {
	         status = new DbQueryStatus("INTERNAL SERVER ERROR", 
	             DbQueryExecResult.QUERY_ERROR_GENERIC);
	         //return status;
	         
	       }
	       
	       try (Session session = ProfileMicroserviceApplication.driver.session()) {
	            try (Transaction trans = session.beginTransaction()) {
	              
	             
	              String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
	              String query = String.format(template, userName); 
	              StatementResult node = trans.run(query);
	              
	              String playlistname =  userName + "-favorites";
	              
	              //System.out.println(playlistname);
	              
	              String template2 = "MATCH (p1:playlist{plName: '%s'})," +
	                  "(s1:song{songId: '%s'}) MATCH ((p1)-[r:includes]->(s1))" 
	              + "Return r";
	                  
	              String query2 = String.format(template2, playlistname, songId);
	              StatementResult node2 = trans.run(query2);

	              
	              if (!node.hasNext()) 
	              {
	                trans.success();
	                session.close();
	                
	                status = new DbQueryStatus("userName not found", 
	                    DbQueryExecResult.QUERY_ERROR_GENERIC);
	                return status; 
	              }
	              else if (!node2.hasNext()) 
	              {
	                
	                status = new DbQueryStatus("the song is not in the play list", 
	                    DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
	                return status;
	                
	              }
	              
	              template = "MATCH (:playlist{plName: '%s'})-[r:includes]->"
	                  + "(s:song{songId: '%s'}) "
	                  + "DELETE r, s";
	              
	              query = String.format(template, playlistname ,songId);
	
	              trans.run(query);
	              
	              HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:3001/updateSongFavouritesCount/" + 
	              songId).newBuilder();
	              urlBuilder.addQueryParameter("shouldDecrement", "true");

	              String url = urlBuilder.build().toString();
	              
	              //System.out.println(url);
	              
	              RequestBody body = RequestBody.create(null, new byte[0]);

	              Request request = new Request.Builder()
	                      .url(url)
	                      .method("PUT", body)
	                      .build();

	              Call call = client.newCall(request);
	              Response responseFromMulMs = null;

	              String addServiceBody = "{}";

	              try {
	                  responseFromMulMs = call.execute();
	                  addServiceBody = responseFromMulMs.body().string();
	                  
	              } catch (IOException e) {
	                  e.printStackTrace();
	              }
	              
	               status = new DbQueryStatus("Ok", 
	                   DbQueryExecResult.QUERY_OK);
	              
	                trans.success();
	            }
	            session.close();
	            return status;
	            
	        }
	  
	  
		
		
	}

	@Override
	public DbQueryStatus deleteSongFromDb(String songId) {
		
		return null;
	}
}
