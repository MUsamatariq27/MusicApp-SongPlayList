package com.csc301.profilemicroservice;

import org.neo4j.driver.v1.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import org.springframework.stereotype.Repository;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.neo4j.driver.v1.Transaction;

@Repository
public class ProfileDriverImpl implements ProfileDriver {

	Driver driver = ProfileMicroserviceApplication.driver;
	
	OkHttpClient client = new OkHttpClient();

	public static void InitProfileDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.userName)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.password)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT nProfile.userName IS UNIQUE";
				trans.run(queryStr);

				trans.success();
			}
			session.close();
		}
	}
	
	@Override
	public DbQueryStatus createUserProfile(String userName, String fullName, String password) {
	  
	  DbQueryStatus status;
	     try (Session session = ProfileMicroserviceApplication.driver.session()) {
           try (Transaction trans = session.beginTransaction()) {
             
            
             String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
             String query = String.format(template, userName);
             
             //System.out.println(query); 
             StatementResult node = trans.run(query);
             
             
             
             if (node.hasNext()) 
             {
               trans.success();
               session.close();
               
               status = new DbQueryStatus("profile already exists", 
                   DbQueryExecResult.QUERY_ERROR_GENERIC);
               return status;
               
               
             }
             
              template = "CREATE ( (:profile {userName: '%s', fullName: '%s', password: '%s'})-"
                  + "[:created]->"
                  + "(:playlist{plName: '%s-favorites'}) )";
              
              query = String.format(template, userName, fullName, password, userName);
              
              trans.run(query);
             
              status = new DbQueryStatus("profile creation success", 
                  DbQueryExecResult.QUERY_OK);
             
               trans.success();
           }
           session.close();
           return status;
           
       }
	  
	}

	@Override
	public DbQueryStatus followFriend(String userName, String frndUserName) {
	  
	  if(userName.equals(frndUserName)) 
	  {
	    DbQueryStatus status = new DbQueryStatus("user can't follow himself", 
            DbQueryExecResult.QUERY_ERROR_GENERIC);
        return status;
	    
	  }
	  
	  DbQueryStatus status;
      try (Session session = ProfileMicroserviceApplication.driver.session()) {
        try (Transaction trans = session.beginTransaction()) {
          
         
          String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
          String query = String.format(template, userName);
          
          String template1 = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
          String query1 = String.format(template1, frndUserName);
          
          String template2 = "MATCH (p1:profile{userName: '%s'})," +
              "(p2:profile{userName: '%s'}) MATCH ((p1)-[r:follows]->(p2))" 
          + "Return r";
              
          String query2 = String.format(template2, userName, frndUserName);
          
          
          //System.out.println(query); 
          StatementResult node = trans.run(query);
          StatementResult node2 = trans.run(query1);
          
          StatementResult node3 = trans.run(query2);
          
          
          
          
          
          if (!node.hasNext()) 
          {
            trans.success();
            session.close();
            
            status = new DbQueryStatus("userName not found", 
                DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
            return status; 
          }
          else if (!node2.hasNext()) 
          {
            
            trans.success();
            session.close();
            
            status = new DbQueryStatus("firendsUsername not found", 
                DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
            return status;
          }
          else if (node3.hasNext()) 
          {
            
            trans.success();
            session.close();
            
            status = new DbQueryStatus("friend is already followed", 
                DbQueryExecResult.QUERY_ERROR_GENERIC);
            return status;
          }
          
          
          
           template = "MATCH (a:profile),(b:profile)" +
               "WHERE a.userName = '%s' AND b.userName= '%s' " +
               "CREATE (a)-[r:follows]->(b)";
           
           query = String.format(template, userName, frndUserName );
           
           trans.run(query);
          
           status = new DbQueryStatus("follow friends successful", 
               DbQueryExecResult.QUERY_OK);
          
            trans.success();
        }
        session.close();
        return status;
        
    }
	  
	  
	  
	  
	  
	}

	@Override
	public DbQueryStatus unfollowFriend(String userName, String frndUserName) {

  
	  
	  
	  DbQueryStatus status;
      try (Session session = ProfileMicroserviceApplication.driver.session()) {
        try (Transaction trans = session.beginTransaction()) {
          
         
          String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
          String query = String.format(template, userName);
          
          String template1 = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
          String query1 = String.format(template1, frndUserName);
          
          String template2 = "MATCH (p1:profile{userName: '%s'})," +
              "(p2:profile{userName: '%s'}) MATCH ((p1)-[r:follows]->(p2))" 
          + "Return r";
              
          String query2 = String.format(template2, userName, frndUserName);
          
          
          //System.out.println(query); 
          StatementResult node = trans.run(query);
          StatementResult node2 = trans.run(query1);
          
          StatementResult node3 = trans.run(query2);
          
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
            
            trans.success();
            session.close();
            
            status = new DbQueryStatus("firendsUsername not found", 
                DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
            return status;
          }
          else if (node3.hasNext()) 
          {
            
            
            template = "MATCH (:profile{userName: '%s'})-[r:follows]->"
                + "(:profile{userName: '%s'}) "
                + "DELETE (r)";
            
            query = String.format(template, userName, frndUserName);
            
            
            trans.run(query);
            
            status = new DbQueryStatus("No longer friends", 
                DbQueryExecResult.QUERY_OK);
            trans.success();
            session.close();
            return status;
          }
          
          
           status = new DbQueryStatus("Was already unfollowed", 
               DbQueryExecResult.QUERY_OK);
          
            trans.success();
        }
        session.close();
        return status;
      }
	  
	  
		
		
	}

	@Override
	public DbQueryStatus getAllSongFriendsLike(String userName) {

      DbQueryStatus status;
      try (Session session = ProfileMicroserviceApplication.driver.session()) {
        try (Transaction trans = session.beginTransaction()) {
          
         
          String template = "MATCH (n:profile {userName:'%s'}) RETURN(n)";
          String query = String.format(template, userName);
         
          //System.out.println(query); 
          StatementResult node = trans.run(query);

          if (!node.hasNext()) 
          {
            trans.success();
            session.close();
            
            status = new DbQueryStatus("userName not found", 
                DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
            return status; 
          }
          else
          {
          template = "MATCH (p1:profile{userName: '%s'})," +
                "(fr:profile) MATCH ((p1)-[r:follows]->(fr))" 
                + "Return fr.userName";
                
            query = String.format(template, userName);
           
            //System.out.println(query); 
            StatementResult node1 = trans.run(query);
            
            Map <String, List> data = new HashMap<String, List>();
            
            if (node1.hasNext()) 
            {
              
              while (node1.hasNext()) 
              {
                String friend = node1.next().values().toString();
                friend = friend.substring(2, friend.length()-2);
                //System.out.println(friend);
                
                
                template = "MATCH (p1:playlist{plName: '%s'})," +
                    "(s:song) MATCH ((p1)-[r:includes]->(s))" 
                    + "Return s.songId";
                
                String playlistname =  friend + "-favorites";
                query = String.format(template, playlistname);
               
                //System.out.println(query); 
                StatementResult node2 = trans.run(query);
                
                List <String> songNames = new ArrayList<String>();
                
                if (node2.hasNext()) 
                {
                  //System.out.println("getting in");
                  while (node2.hasNext()) 
                  {
                    String songId = node2.next().values().toString();
                    songId = songId.substring(2, songId.length()-2);
                    //System.out.println(songId);
                    Request request = new Request.Builder().url("http://localhost:3001/getSongTitleById/" 
                        + songId).method("GET", null).build();
                    
                       Call call = client.newCall(request);
                       Response responseFromAddMs = null;

                       String addServiceBody = "{}" ;
                       
                       try
                       {
                         responseFromAddMs = call.execute();
                         addServiceBody = responseFromAddMs.body().string();
                         //System.out.println(addServiceBody);
                         int index_data = addServiceBody.indexOf("data");
                         int index_status =  addServiceBody.indexOf("status")-3;
                         
                         String songName = addServiceBody.substring(index_data+7, index_status );
                         //System.out.println(songName);
                         
                         songNames.add(songName);
                         
                       }
                       catch(Exception e) {break;}
                    
                  }
                  
                  
                }
                
                data.put(friend, songNames);
                //System.out.println(data.toString());
                

                  
                
              }
              
              
              
            }
            
            status = new DbQueryStatus("No Friends", 
                DbQueryExecResult.QUERY_OK);
            status.setData(data);
            
            
            
            
          }

            trans.success();
        }
        session.close();
        return status;
      }	  
	  

	}
}
