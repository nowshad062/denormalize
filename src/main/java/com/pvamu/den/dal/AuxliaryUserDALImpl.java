package com.pvamu.den.dal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.AuxliaryUser;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.User;

@Repository
public class AuxliaryUserDALImpl {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void addAuxliaryUsers(List<AuxliaryUser> user) {
		mongoTemplate.insertAll(user);
	}
	
	public void saveAuxliaryUsers(AuxliaryUser user) {
		mongoTemplate.save(user);	
    }
	
	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, AuxliaryUser.class);
	}
	
	public void clearAllRecord() {
		mongoTemplate.dropCollection(AuxliaryUser.class);
	}
	
	public List<AuxliaryUser> getAllRecord() {
		return mongoTemplate.findAll(AuxliaryUser.class);
	}
	
	public List<AuxliaryUser> getAllUsersByIds(List<Long> userIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").in(userIds));
		List<User> users =mongoTemplate.find(query,User.class);
		List<AuxliaryUser> response = new ArrayList<>();
		users.forEach(user -> {
			AuxliaryUser tUsers = new AuxliaryUser();
			tUsers.setGender(user.getGender());
			tUsers.setDob(user.getDob());
			tUsers.setUserId(user.getUserId());
			response.add(tUsers);
		});
		return response;
	}

}


