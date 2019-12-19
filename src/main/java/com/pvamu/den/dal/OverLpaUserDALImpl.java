package com.pvamu.den.dal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.OverLapuser;


@Repository
public class OverLpaUserDALImpl {
	@Autowired
	private MongoTemplate mongoTemplate;

	
	public void addOverloapUsers(List<OverLapuser> user) {
		mongoTemplate.insertAll(user);
	}
	
	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, OverLapuser.class);
	}
	
	public void clearAllRecord() {
		mongoTemplate.dropCollection(OverLapuser.class);
		
	}
	
	public List<OverLapuser> getAllRecord() {
		
		return mongoTemplate.findAll(OverLapuser.class);
	}
	
	public void updateOverlapUserSeed(OverLapuser user , boolean selectSeed) {
		
		Query query = new Query();
	    query.addCriteria(new Criteria("_id").is(user.getUserId()));
	    
	    Update update = new Update();
	    update.set("selectSeed", selectSeed);
		
		mongoTemplate.upsert(query, update, OverLapuser.class);
	
}
}