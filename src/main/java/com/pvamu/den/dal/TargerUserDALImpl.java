package com.pvamu.den.dal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.User;

@Repository
public class TargerUserDALImpl {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<TargetedUsers> getAllUsersByIds(List<Long> userIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").in(userIds));
		List<User> users =mongoTemplate.find(query, User.class);
		List<TargetedUsers> response = new ArrayList<>();
		users.forEach(user -> {
			TargetedUsers tUsers = new TargetedUsers();
			tUsers.setGender(user.getGender());
			tUsers.setDob(user.getDob());
			tUsers.setUserId(user.getUserId());
			response.add(tUsers);
		});
		return response;
	}

	public TargetedUsers getbyId(long userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").in(userId));
		return	mongoTemplate.findOne(query, TargetedUsers.class);
	}
	
	public void addTargetUsers(TargetedUsers user) {
			mongoTemplate.save(user);	
	}
	
	public void updateTargetUsers(TargetedUsers user) {
		Query query = new Query();
	    query.addCriteria(new Criteria("_id").is(user.getUserId()));
	    Update update = new Update();
	    update.set("dob", user.getDob());
	    update.set("minYear", user.getMinYear());
	    update.set("maxYear", user.getMaxYear());
	    update.set("centrality", user.getCentrality());
	    update.set("gender", user.getGender());
		mongoTemplate.upsert(query, update, TargetedUsers.class);	
}
	public void clearAllRecord() {
		mongoTemplate.dropCollection(TargetedUsers.class);
	}
	
	public List<TargetedUsers> getTargetUser() {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").exists(true));
		return mongoTemplate.find(query, TargetedUsers.class );
	}
	
	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, TargetedUsers.class);
	}
	
	public List<TargetedUsers> getAllRecord() {
		return mongoTemplate.findAll(TargetedUsers.class);
	}
	
	
	
}
