package com.pvamu.den.dal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;
import com.pvamu.den.model.UserRelation;



@Repository
public class UserRelationDALImpl {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public UserRelation addAllRealtion(List<UserRelation> userRelation) {
		mongoTemplate.insertAll(userRelation);
		return null;
	}
	
	public UserRelation save(UserRelation userRelation) {
		mongoTemplate.save(userRelation);
		return null;
	}
	
	public List<UserRelation> getUsersByParentId(long userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parent").is(userId));
		return mongoTemplate.find(query, UserRelation.class);
		
	}
	
	public List<UserRelation> getUsersByChildId(long userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("child").is(userId));
		return mongoTemplate.find(query, UserRelation.class);
		
	}
	
	public double isRelatedAnyDirection(long parentId ,  long childId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parent").is(parentId).and("child").is(childId));
		
		
		boolean parentChild = mongoTemplate.exists(query, UserRelation.class);
		
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("child").is(parentId).and("parent").is(childId));
		
		boolean childParent = mongoTemplate.exists(query1, UserRelation.class);
		double result = parentChild|childParent ? 1 : 0;;
		return result;
		
	}
	
	public boolean isRelatedUniDirection(long parentId ,  long childId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parent").is(parentId).and("child").is(childId));
		return mongoTemplate.exists(query, UserRelation.class);
	}
	
	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, UserRelation.class);
	}
	public void removeRelationship(long parentId, long childId)
	{
		
		Query query = new Query();
		query.addCriteria(Criteria.where("parent").is(parentId).and("child").is(childId));
		 WriteResult rep = mongoTemplate.remove(query, UserRelation.class);
	}
	public void removeChild(long childId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("child").is(childId));
		 WriteResult rep = mongoTemplate.remove(query, UserRelation.class);
		 LOG.info("removed user ID  {}  , number of records {}",childId , rep.getN());
	}
}
