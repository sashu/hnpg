package com.hnpg.service.db;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.hnpg.service.constants.Constants;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;

@SuppressWarnings("unchecked")
public class HnPGDataServices {

	static final String host = "ec2-54-86-108-177.compute-1.amazonaws.com";
	static final int port = 27017;

	public boolean save(BasicDBObject object, String document) throws UnknownHostException {
		getMongoOps().insert(object, document);
		return true;
	}

	public boolean update(BasicDBObject object, String document) throws UnknownHostException {
		stringToObjectId(object);
		getMongoOps().save(object, document);
		return true;
	}

	public List<BasicDBObject> fetchAll(String document) throws UnknownHostException {
		List<BasicDBObject> list = getMongoOps().findAll(BasicDBObject.class, document);
		objectToStringId(list);
		return list;
	}

	public BasicDBObject fetchOne(String id, String document) throws UnknownHostException {
		BasicDBObject item = getMongoOps().findById(id, BasicDBObject.class, document);
		objectToStringId(item);
		return item;
	}

	private MongoOperations getMongoOps() throws UnknownHostException {
		MongoClientURI uri = new MongoClientURI("mongodb://" + host + ":" + port + "/hnpg");
		MongoOperations mongoOps = new MongoTemplate(new SimpleMongoDbFactory(uri));
		return mongoOps;
	}

	private static void objectToStringId(Object item) {
		if (item instanceof BasicDBObject) {
			BasicDBObject object = (BasicDBObject) item;
			String id = object.getString("_id");
			object.put("_id", id);
		}
		if (item instanceof List) {
			List<BasicDBObject> list = (List<BasicDBObject>) item;
			for (BasicDBObject basicDBObject : list) {
				String id = basicDBObject.getString("_id");
				basicDBObject.put("_id", id);
			}
		}
	}

	private static void stringToObjectId(Object item) {
		if (item instanceof BasicDBObject) {
			BasicDBObject object = (BasicDBObject) item;
			String id = object.getString("_id");
			object.put("_id", new ObjectId(id));
		}
		if (item instanceof List) {
			List<BasicDBObject> list = (List<BasicDBObject>) item;
			for (BasicDBObject basicDBObject : list) {
				String id = basicDBObject.getString("_id");
				basicDBObject.put("_id", new ObjectId(id));
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		HnPGDataServices service = new HnPGDataServices();
		BasicDBObject object = new BasicDBObject();
		object.append("NAME", "Ashu3");
		List<BasicDBObject> list = service.fetchAll(Constants.Documents.LOCATION);
		objectToStringId(list);
		for (BasicDBObject o : list) {
			stringToObjectId(o);
			o.append("NewVal", new Date().getTime());
			service.update(o, Constants.Documents.LOCATION);
		}
	}
}
