package com.pvamu.den.dal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.UserRelation;

@Repository
public interface UserRelationRepository extends MongoRepository<UserRelation, String> {

}
