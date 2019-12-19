package com.pvamu.den.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.AuxliaryUserDALImpl;
import com.pvamu.den.dal.OverLpaUserDALImpl;
import com.pvamu.den.dal.TargerUserDALImpl;
import com.pvamu.den.dal.UserDALImpl;
import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.model.Anonymization;
import com.pvamu.den.model.AuxliaryUser;
import com.pvamu.den.model.OverLapuser;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.User;
import com.pvamu.den.model.UserRelation;


@Service
public class Auxiliary {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private  TargerUserDALImpl targetUserDALImpl;
	
	@Autowired
	private  AuxliaryUserDALImpl auxiliaryUserDALImpl;
	
	@Autowired
	private  OverLpaUserDALImpl overLapUserDALImpl;
	
	@Autowired
	private  UserDALImpl userDALImpl;
	
	@Autowired
	private  UserRelationDALImpl userRelationDALImpl;

	@Autowired
	private EgenVectorC egenVectorC;
	
	@Autowired
	private UserAxis userXAxis ;
	
	public void generate(int auxiliarySize, double overlap) {
		
		auxiliaryUserDALImpl.clearAllRecord();
		overLapUserDALImpl.clearAllRecord();	
		List<TargetedUsers> targetUsers = targetUserDALImpl.getTargetUser();
		
		LOG.info("User profile cout  : {}.", auxiliaryUserDALImpl.collectionCount());
		
		int overlapsize = (int) ((targetUsers.size() * overlap)/100);
		
		List<Long> targetUsersIds = new ArrayList<>();
		for( TargetedUsers user : targetUsers) {
			targetUsersIds.add(user.getUserId());
		}
		
		//find a node from the target set to generate overlap
		User  initOverlap= userDALImpl.getRandomFromTarget(targetUsersIds);//????????????????
		//System.out.println("The initial Overlap Node: "+initOverlap.getUserId());
		//select nodes from target set as overlap nodes
		List<Long> overlaptargetUserIds  = userXAxis.findOverlapUsers(initOverlap, overlapsize, targetUsersIds);
		if(overlapsize > overlaptargetUserIds.size())
			//if didn't get enough overlap nodes, reselect the initial node from the target set and regenerate
			//the overlap set. There must be at least one possible set of overlap nodes which begins with the 
			//first node in the target set.
		{
			generate(auxiliarySize, overlap);
			return;
		}
		List<TargetedUsers> overlaptargetUsers = targetUserDALImpl.getAllUsersByIds(overlaptargetUserIds);
		List<OverLapuser> overlapUsers = new ArrayList<>();
		//System.out.println("Overlap Users:");
		overlaptargetUsers.forEach(user -> {
			OverLapuser tUsers = new OverLapuser();
			tUsers.setGender(user.getGender());
			tUsers.setDob(user.getDob());
			tUsers.setUserId(user.getUserId());
			overlapUsers.add(tUsers);
			//System.out.print(tUsers.getUserId()+":");
		});
		overLapUserDALImpl.addOverloapUsers(overlapUsers);	
		
		//generate auxiliary set
		List<Long> auxiliaryUsersIds = userXAxis.findAuxiUsers(auxiliarySize, targetUsersIds, overlaptargetUserIds);
		if(auxiliaryUsersIds.size()< auxiliarySize)
		{
			generate(auxiliarySize, overlap);
			return;
		}
		
		List<User> auxiUserUsers = this.userDALImpl.getAllUsersById(auxiliaryUsersIds);
		List<AuxliaryUser> auxiUsers = new ArrayList<>();
		List<Double>  auxiCentrality = egenVectorC.getNormalisedEigenVectors(auxiliaryUsersIds);
		int index = 0;
		for (User user: auxiUserUsers)  
	       {   
			AuxliaryUser tUsers = new AuxliaryUser();
			List<UserRelation> relations = userRelationDALImpl.getUsersByParentId(user.getUserId());
			
			int degree = 0;
			for(UserRelation  ur: relations) {
				/*if(ur.getChild()==user.getUserId())
				{
					this.userRelationDALImpl.removeRelationship(ur.getChild(), user.getUserId());
					continue;
				}*/
				//may have self loop
				
				if(auxiliaryUsersIds.contains(ur.getChild()) && ur.getChild()!=user.getUserId()) {
						//System.out.println(user.getUserId()+"---"+ur.getChild());
					degree = degree+1;
				}
			}
			
			tUsers.setxAxis(degree);
			tUsers.setGender(user.getGender());
			tUsers.setDob(user.getDob());
			tUsers.setUserId(user.getUserId());
			tUsers.setCentrality(auxiCentrality.get(index));
			auxiUsers.add(tUsers);
			index = index + 1;
	       } 
			
		auxiliaryUserDALImpl.addAuxliaryUsers(auxiUsers); 
	}
/*
	public void getAlias(long id) {
		List<AuxliaryUser>  tUsers = auxiliaryUserDALImpl.getAllRecord();
		List<Long> onlyId = new ArrayList<>(); 
			
		for(AuxliaryUser tlong : tUsers) {
			onlyId.add(tlong.getUserId());
		}
		targetUserDALImpl.getbyId(id);
		List<UserRelation> pR = userRelationDALImpl.getUsersByParentId(id);
		
		List<UserRelation> cR = userRelationDALImpl.getUsersByChildId(id);
		
		for(UserRelation tuserid : pR) {
			if(onlyId.contains(tuserid.getChild()) ) {
				System.out.println(tuserid.getChild());
			}
		}
		System.out.println("============");
		for(UserRelation tuserid : cR) {
			if(onlyId.contains(tuserid.getParent()) ) {
				System.out.println(tuserid.getParent());
			}
		}
	}
	*/
}
