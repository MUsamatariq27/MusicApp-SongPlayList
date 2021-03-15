package com.csc301.songmicroservice;

import static com.mongodb.client.model.Filters.eq;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;

@Repository
public class SongDalImpl implements SongDal {

	private final MongoTemplate db;

	@Autowired
	public SongDalImpl(MongoTemplate mongoTemplate) {
		this.db = mongoTemplate;
	}

	@Override
	public DbQueryStatus addSong(Song songToAdd) {
		// TODO Auto-generated method stub
	  
	  try {
	    
	    songToAdd.setId(new ObjectId());
        
        Document doc =  new Document("_id", songToAdd._id);
        doc.append("songName", songToAdd.getSongName() );
        doc.append("songArtistFullName", songToAdd.getSongArtistFullName());
        doc.append("songAlbum", songToAdd.getSongAlbum());
        doc.append("songAmountFavourites", songToAdd.getSongAmountFavourites());
        
        this.db.getDb().getCollection("songs").insertOne(doc);
        
        DbQueryStatus status = new DbQueryStatus("OK", DbQueryExecResult.QUERY_OK);
        status.setData(songToAdd.getJsonRepresentation());
        return status;
        
      }
      catch(Exception e) 
	  { 
        
        DbQueryStatus status = new DbQueryStatus("INTERNAL_SERVER_ERROR!", 
            DbQueryExecResult.QUERY_ERROR_GENERIC);
        
        return status;
	  }
	}

	@Override
	public DbQueryStatus findSongById(String songId) 
	{
		// TODO Auto-generated method stub
      try 
      {
        int check =0;
        //songId = songId.substring(1, songId.length()-1);
        
        try{  new ObjectId(songId) ; check =1;}
        catch(Exception e) {check =404;}
       
        if (check == 1) 
        {
          
          Document doc = this.db.getCollection("songs").find(eq("_id", new ObjectId(songId) ))
              .first();
          
          if (doc == null) {check = 404;}
          
          if (check == 1) 
          {
            DbQueryStatus status = new DbQueryStatus("Object Found!", DbQueryExecResult.QUERY_OK);
            
            Map<String, String> data = new HashMap<String, String>();
            
            data.put("id", doc.get("_id").toString());
            data.put("songName", doc.getString("songName"));
            data.put("songArtistFullName",  doc.getString("songArtistFullName"));
            data.put("songAlbum", doc.getString("songArtistFullName") );
            data.put("songAmountFavourites", String.valueOf(doc.get("songAmountFavourites")));
            
            
            
            status.setData(data);
            return status;
          }
          
        }
        else {;}
        
        DbQueryStatus status = new DbQueryStatus("Id not found in the database", 
            DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
        return status;

      }
      catch(Exception e) 
      { 
        
        DbQueryStatus status = new DbQueryStatus("INTERNAL SERVER ERROR!", 
            DbQueryExecResult.QUERY_ERROR_GENERIC);
        
        return status;
      }
   
	  
	  
		
	}

	@Override
	public DbQueryStatus getSongTitleById(String songId) {
		// TODO Auto-generated method stub
	     try 
	      {
	        int check =0;
	        //songId = songId.substring(1, songId.length()-1);
	        
	        try{  new ObjectId(songId) ; check =1;}
	        catch(Exception e) {check =404;}
	       
	        if (check == 1) 
	        {
	          
	          Document doc = this.db.getCollection("songs").find(eq("_id", new ObjectId(songId) ))
	              .first();
	          
	          if (doc == null) {check = 404;}
	          
	          if (check == 1) 
	          {
	            DbQueryStatus status = new DbQueryStatus("Object Found!", DbQueryExecResult.QUERY_OK);
	            
	            String data = new String (doc.getString("songName"));
	            
	            
	            status.setData((Object)data);
	            return status;
	          }
	          
	        }
	        else {;}
	        
	        DbQueryStatus status = new DbQueryStatus("Id not found in the database", 
	            DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
	        return status;

	      }
	      catch(Exception e) 
	      { 
	        
	        DbQueryStatus status = new DbQueryStatus("INTERNAL SERVER ERROR!", 
	            DbQueryExecResult.QUERY_ERROR_GENERIC);
	        
	        return status;
	      }
	  
	  
		
	}

	@Override
	public DbQueryStatus deleteSongById(String songId) {
		// TODO Auto-generated method stub
	  
	  try 
      {
        int check =0;
        //songId = songId.substring(1, songId.length()-1);
        
        try{  new ObjectId(songId) ; check =1;}
        catch(Exception e) {check =404;}
       
        if (check == 1) 
        {
          
          Document doc = this.db.getCollection("songs").find(eq("_id", new ObjectId(songId) ))
              .first();
          
          if (doc == null) {check = 404;}
          
          if (check == 1) 
          {
            
            this.db.getDb().getCollection("songs").deleteOne(doc);
            
            DbQueryStatus status = new DbQueryStatus("Object Found! Deleted Succesfully", 
                DbQueryExecResult.QUERY_OK);
            
            return status;
          }
          
        }
        else {;}
        
        DbQueryStatus status = new DbQueryStatus("Id not found in the database", 
            DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
        return status;

      }
      catch(Exception e) 
      { 
        
        DbQueryStatus status = new DbQueryStatus("INTERNAL SERVER ERROR!", 
            DbQueryExecResult.QUERY_ERROR_GENERIC);
        
        return status;
      }
	  
	  
		
	}

	@Override
	public DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement) {
		// TODO Auto-generated method stub
	  
	   try 
	      {
	        int check =0;
	        //songId = songId.substring(1, songId.length()-1);
	        
	        try{  new ObjectId(songId) ; check =1;}
	        catch(Exception e) {check =404;}
	       
	        if (check == 1) 
	        {
	          
	          Document doc = this.db.getCollection("songs").find(eq("_id", new ObjectId(songId) ))
	              .first();
	          
	          if (doc == null) {check = 404;}
	          
	          if (check == 1) 
	          {
	            long num = (long) doc.get("songAmountFavourites");
	            if ( num == 0 && shouldDecrement == true) 
	            {
	              DbQueryStatus status = new DbQueryStatus("Object Found, But Likes Can't be negative", 
	                    DbQueryExecResult.QUERY_ERROR_GENERIC);
	                
	                return status;
	            }
	            Document doc1 = new Document("_id", new ObjectId(songId));
	            
	            BasicDBObject newDocument;
	            
	            if (shouldDecrement == false) {
	              newDocument = 
	              new BasicDBObject().append("$inc", 
  	              new BasicDBObject().append("songAmountFavourites", 1));
  	            
	            }
	            
	            else {
	              newDocument = 
	                  new BasicDBObject().append("$inc", 
	                  new BasicDBObject().append("songAmountFavourites", -1));
	              
	            }
	            
	            this.db.getDb().getCollection("songs").findOneAndUpdate(doc1, 
	                  newDocument);
	            
	            DbQueryStatus status = new DbQueryStatus("Object Found! Updated Succesfully", 
	                DbQueryExecResult.QUERY_OK);
	            
	            return status;
	          }
	          
	       
	          
	        }
	        else {;}
	        
	        DbQueryStatus status = new DbQueryStatus("Id not found in the database", 
	            DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
	        return status;

	      }
	      catch(Exception e) 
	      { 
	        
	        DbQueryStatus status = new DbQueryStatus("INTERNAL SERVER ERROR!", 
	            DbQueryExecResult.QUERY_ERROR_GENERIC);
	        
	        return status;
	      }
	}
}