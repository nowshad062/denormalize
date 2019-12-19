package com.pvamu.den.dal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.User;

@Repository
public class UserDALImpl {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private MongoTemplate mongoTemplate;
	//@Autowired
	//private UserRepository userRepo ;
	
	private List<Long> usedId = new ArrayList<Long>();// This is used for generating target list
	private List<Long> usedIdOverlap = new ArrayList<Long>(); //This is used for generating the overlap list
/*
	public List<User> getAllUsersById(List<Long> userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").in(userId));
		return mongoTemplate.find(query, User.class);
	}*/
	public List<User> getAllUsersById(List<Long> userIds){
		List<User> tmplist = new ArrayList<User>();
		for(int i = 0; i<userIds.size(); i++)
			tmplist.add(getUserById(userIds.get(i)));
		return tmplist;
	}
	public User getUserById(Long userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}
	
	public void addNewUser(List<User> user) {
		user.forEach(u -> {
			SaveUser(u);	
		});
	}
	
	public void SaveUser(User u) {
		mongoTemplate.save(u);	
	}

	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, User.class);
	}
	
	public User getRandomRecord() {
		try {
		Query query = new Query();
		long randomNumber = (long)((Math.random() * (2421057 - 100001+1))+100001) ;
		
		/*Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		long randomNumber = rand.nextLong();*/
		//randomNumber = 1203757;
		//randomNumber = 694121;
		//100001 seems the smallest user id in the original data set
		//for a number between 2421057 and 100001, there may not be a node corresponding to it.
		// find the largest id: db.getCollection('user_profile').find().sort({'_id':-1}).limit(1)
		// find the smallest id: db.getCollection('user_profile').find().sort({'_id':1}).limit(1)
		if(usedId.contains(randomNumber)) {// checking individual nodes to see whether they are good
			//enough to be the 1st node in the target set
			return getRandomRecord();
		}
		usedId.add(randomNumber); // ### NL
		query.addCriteria(Criteria.where("_id").is(randomNumber) );
		 
		List<User> userResponse = mongoTemplate.find(query, User.class);
		
		if(userResponse.isEmpty()) {// the generated id doesn't exist in the db.
			return getRandomRecord();
		}
		
		LOG.info("user profile {}.",userResponse.get(0).toString());
		
		return userResponse.get(0);
			
		}catch (Exception e) {
			getRandomRecord();
		}
		return null;
	}
	public void resetUsedId() {
		this.usedId = new ArrayList<Long>();
	}
	
	public User getRandomFromTarget(List<Long> targetUsersIds) {
		// TODO Auto-generated method stub
		try {
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());
			Query query = new Query();
			int randomindex = rand.nextInt(targetUsersIds.size());
			//System.out.println("UserDALImpl.java:"+targetUsersIds.get(randomindex));
			if(usedIdOverlap.contains(targetUsersIds.get(randomindex))) {
				return getRandomFromTarget(targetUsersIds);
			}
			usedIdOverlap.add(targetUsersIds.get(randomindex));
			query.addCriteria(Criteria.where("_id").is(targetUsersIds.get(randomindex)));
			List<User> userResponse = mongoTemplate.find(query, User.class);
			
			if(userResponse.isEmpty()) {
				return getRandomFromTarget(targetUsersIds);
			}
			
			LOG.info("user profile {}.",userResponse.get(0).toString());
			
			return userResponse.get(0);
				
		}catch (Exception e) {
			getRandomFromTarget(targetUsersIds);
			//System.out.println(e.getMessage());
		}
		return null;
	}
}



