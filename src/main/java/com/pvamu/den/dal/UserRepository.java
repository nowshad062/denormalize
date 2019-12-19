package com.pvamu.den.dal;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.pvamu.den.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	@Query("{'userId' : {$ne : null}}")
	Stream<User> findAllByCustomQueryAndStream();
	
	@Query("[ { $sample: {size: 1} }]")
	List<User> findRandomRecoard();

}
