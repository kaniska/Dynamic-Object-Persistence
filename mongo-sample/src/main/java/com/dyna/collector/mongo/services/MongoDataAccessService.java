package com.dyna.collector.mongo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoDbUtils;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository("mongoDataAccessService")
@Transactional
public class MongoDataAccessService {
	@Autowired
	MongoOperations mongoOperations;
	
	@Autowired
	MongoFactoryBean mongoFactoryBean;
	//protected static Logger logger = Logger.getLogger("MongoDataAccessService");
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * @param entityName
	 * @return
	 */
	public List<DBObject> getAll(String entityName) {
		logger.debug("Retrieving all Business Entities");
        DBCursor cursor = mongoOperations.getCollection(entityName).find();
         List<DBObject> entities = new ArrayList<DBObject>();
         for(Iterator<DBObject> i = cursor.iterator(); i.hasNext();) {
        	 Object obj = i.next();
        	if(obj != null)  entities.add((DBObject)obj);
         }
		return entities;
	}
	
	/**
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public DB getDB(String dbName) throws Exception{
		return MongoDbUtils.getDB(mongoFactoryBean.getObject(), dbName);
	}
	
	/**
	 * Retrieves a single person
	 */
	public DBObject get( String id ) {
		logger.debug("Retrieving an existing Entity");
		// Find an entry where id matches the id
		DBObject entity = mongoOperations.findById(id, DBObject.class);
    	
		return entity;
	}
	
	/**
	 * Adds a new Entity
	 */
	public Boolean add(DBObject entity, String collectionName) {
		logger.debug("Adding a new Entity");
		try {
			mongoOperations.save(entity, collectionName);
			return true;
			
		} catch (Exception e) {
			logger.error("An error has occurred while trying to add new Entity", e);
			return false;
		}
	}
	
	/**
	 * Deletes an existing Entity
	 */
	public Boolean delete(String id) {
		logger.debug("Deleting existing Entity");
		try {
			// Find an entry where id matches the id, then delete that entry
			DBObject entity = mongoOperations.findById(id, DBObject.class);
			mongoOperations.remove(entity);
			return true;
			
		} catch (Exception e) {
			logger.error("An error has occurred while trying to delete the Entity", e);
			return false;
		}
	}
	
	/**
	 * Edits an existing person
	 */
	public Boolean edit(String id, String columnName, String Value) {
		logger.debug("Editing existing Entity");
		try {
			// Find an entry where id matches the id
	        DBObject entity = mongoOperations.findById(id, DBObject.class);
	        // Assign new values
	        entity.put(columnName, Value);
	        // Save to db
	        mongoOperations.save(entity);
			return true;
		} catch (Exception e) {
			logger.error("An error has occurred while trying to edit existing Entity", e);
			return false;
		}
		
	}
	
	/**
	 * @param name
	 * @return
	 */
	public boolean collectionExists(String name) {
		return mongoOperations.collectionExists(name);
	}
	
	/**
	 * 
	 * @param name
	 */
	public void createCollection(String name) {
		mongoOperations.createCollection(name);
	}

}
