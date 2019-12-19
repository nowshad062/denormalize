package com.pvamu.den.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.TargerUserDALImpl;
import com.pvamu.den.dal.UserDALImpl;
import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.model.Anonymization;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.User;
import com.pvamu.den.model.UserRelation;

@Service
public class Target {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private  TargerUserDALImpl targetUserDALImpl;

	@Autowired
	private  UserRelationDALImpl userRelationDALImpl;

	@Autowired
	private  UserDALImpl userDALImpl;

	@Autowired
	private UserAxis userXAxis ;
	
	@Autowired
	private EgenVectorC egenVectorC;

	public void generate(int targetSize) {
		List<Long> targetslist = new LinkedList<Long>();
		targetUserDALImpl.clearAllRecord();	
		
		LOG.info("========== User collectionCount : {}.", targetUserDALImpl.collectionCount());

		User  initRecord = userDALImpl.getRandomRecord();//as the starting node in T set.
		//System.out.println("The id of the first node in Target Set:"+initRecord.getUserId());
		targetslist = userXAxis.findConnectedUsers(initRecord, targetSize);//find all connected nodes starting from initRecoard. 
		System.out.println("targetlist:");
		for(int i = 0; i< targetslist.size();i++)
			System.out.print(targetslist.get(i).toString()+";");
		if(targetslist.size() < targetSize) {
			 this.generate(targetSize);
			 return;
		}
		
		int index = 0;
		List<Double>  targetCentrality = egenVectorC.getNormalisedEigenVectors(targetslist);
		System.out.println("finished calculating egenVectorC");
		List<User>targetUsers = userDALImpl.getAllUsersById(targetslist);//Note the order returned will be different from the order of targetslist
		
		String neighbors; 
		for (User user : targetUsers)  //calculate the degree of each user in listUserId - Target set
		{  
			neighbors = "";
			TargetedUsers tUsers = new TargetedUsers();	
			neighbors=neighbors+String.valueOf(user.getUserId())+":"+index+":";
			List<UserRelation> relations = userRelationDALImpl.getUsersByParentId(user.getUserId());
			int degree = 0;
			for(UserRelation  ur: relations) {
				/*if(ur.getChild()==user.getUserId())
				{
					this.userRelationDALImpl.removeRelationship(ur.getChild(), user.getUserId());
					continue;
				}*/
				if(targetslist.contains(ur.getChild()) && ur.getChild()!=user.getUserId() ) {
				neighbors+=String.valueOf(ur.getChild())+"-";
				degree = degree+1;
				}
			}
			//System.out.println(neighbors);
			tUsers.setxAxis(degree);
			tUsers.setGender(user.getGender());
			tUsers.setDob(user.getDob());
			tUsers.setUserId(user.getUserId());
			tUsers.setCentrality(targetCentrality.get(index));
			tUsers.setAlterId(index);
			//System.out.println(tUsers.getAlterId()+":"+tUsers.getUserId());
			targetUserDALImpl.addTargetUsers(tUsers);
			index = index+1;
		} 
	
	}

	public void anonymize(Anonymization  anonymization) {
		
		for ( TargetedUsers user : targetUserDALImpl.getTargetUser()){ 
			user.setGender(genderRangerCalc(user.getGender(),anonymization.getGenderRetain()));   
			user.setMaxYear(user.getDob() +  genderdobCalc(anonymization.getYearRange()));
			user.setMinYear(user.getDob() -  genderdobCalc(anonymization.getYearRange()));
			user.setDob(Math.round((user.getMaxYear()+ user.getMinYear()) / 2));
			targetUserDALImpl.updateTargetUsers(user);				
		}	
	}

	public List<TargetedUsers> getAllRecords(){
		return targetUserDALImpl.getAllRecord();
	}
/*
 * possible% chance to retain the gender. 1 and 2 are used to represent female and male; 0 means unknown and it's cleaned 
 * when at the beginning of data processing.
 * */
	private int genderRangerCalc(int gender , int possible) {
		//int randomNum = (int)Math.random()*100;
		if (possible != 100)
		{
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());
			int randomNum = rand.nextInt(100);// generate an integer between 0 and 99
			if (randomNum >= possible) {
				gender = (gender == 1)? 2:1;
			}
		}
		return gender;
	}
	
	private int genderdobCalc(int range) {
		range = range/2 ;
		//int randomNumber = (int)(Math.random()*range+1) ;
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		return rand.nextInt(range+1);
	}
}
