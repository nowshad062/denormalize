package com.pvamu.den.filehandle;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.dal.UserRelationRepository;
import com.pvamu.den.model.UserRelation;

@Service
public class ReadUserRelation {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private  UserRelationDALImpl UserRelationDALImpl;
	@Autowired
	private  UserRelationRepository userRepository;
	
	public void userRelationRead() {
		userRepository.deleteAll();
		LOG.info("User profile cout  : {}.", UserRelationDALImpl.collectionCount());
		String fileName = "user_sns.txt";
		List<UserRelation> userRelation = new ArrayList<>();
		LOG.info("Saving user profile.");
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

			//br returns as stream and convert it into a List
			br.lines().forEach(name -> {
				userRelation.add(newUser(name));
				if(userRelation.size()==1000) {
					
					UserRelationDALImpl.addAllRealtion(userRelation);				
					 userRelation.clear();
					
				}
				
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		UserRelationDALImpl.addAllRealtion(userRelation);
	

	}
	
	
	private UserRelation newUser(String name) {
		String[] column = name.split("	");
		UserRelation user = new UserRelation();
		user.setParent(userIdGenerate(column[0]));
		user.setChild(dobGenerate(column[1]));
	
		return user;
	}
	
	private long  userIdGenerate(String name) {
		try {
			return Long.parseLong(name.trim());
		}catch (Exception e) {
			return 0;
		}	
		
	}
	
	private int dobGenerate(String name) {
		try {
			return Integer.parseInt(name.trim());
		}catch (Exception e) {
			
			return 0;
		}
	}
	
	


}
