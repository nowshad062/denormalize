package com.pvamu.den.filehandle;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.UserDALImpl;
import com.pvamu.den.dal.UserRepository;
import com.pvamu.den.model.User;

@Service
public class ReadUserProfile {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private  UserDALImpl userDAL;
	@Autowired
	private  UserRepository userRepository;

	
	public void userProfileRead() {
		userRepository.deleteAll();
		LOG.info("User profile cout  : {}.", userDAL.collectionCount());
		String fileName = "user_profile.txt";
		List<User> users = new ArrayList<>();
		LOG.info("Saving user .");
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

			int index = 0;
			//br returns as stream and convert it into a List
			br.lines().forEach(name -> {
				
				users.add(newUser(name));
			//	index++;
				
				if(users.size()==1000) {
					
					 userDAL.addNewUser(users);				
					users.clear();
				}
				
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 userDAL.addNewUser(users);
	

	}
	
	
	private User newUser(String name) {
		
		User user = new User();
		user.setUserId(userIdGenerate(name));
		user.setDob(dobGenerate(name));
		user.setGender(genderGenerate(name));
	
		
		return user;
	}
	
	private long  userIdGenerate(String name) {
		try {
			
			return Long.parseLong(name.substring(0, 7).trim());
		}catch (Exception e) {
			
			return 0;
		}	
		
	}
	
	private int dobGenerate(String name) {
		try {
			return Integer.parseInt(name.substring(7, 12).trim());
		}catch (Exception e) {
			
			LocalDate currentDate = LocalDate.now();
			return currentDate.getYear();
		}
	}
	
	private int genderGenerate(String name) {
		try {
			return Integer.parseInt(name.substring(12, 14).trim());
		}catch (Exception e) {
			
			return 0;
		}
	}
	

}
