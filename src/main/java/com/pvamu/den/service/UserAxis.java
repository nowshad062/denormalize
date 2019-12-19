package com.pvamu.den.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.UserDALImpl;
import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.User;
import com.pvamu.den.model.UserRelation;

@Service
public class UserAxis {
	
	@Autowired
	private  UserRelationDALImpl userRelationDALImpl;
	
	@Autowired
	private  UserDALImpl userDALImpl;

	int index = 0;
	
	public List<Long> findConnectedUsers(User user , int expectedsize) {
		  
		   List<Long> nodesqueue = new LinkedList<>();
		   List<Long> targets = new LinkedList<>();
		   nodesqueue.add(user.getUserId());
		   
		   Long curuser;
		   int curindex = 0;
		   boolean flag = false;
		   // Use Breath First Search to find nodes and put them in the queue, nodesqueue.
		   while(curindex < nodesqueue.size() && nodesqueue.size() < expectedsize)
		   {
			   curuser = nodesqueue.get(curindex);
			   targets.add(curuser);
			   curindex++;
			   //Since the relationship data in the database is directed. e.g., 1, 2 doesn't mean 2, 1
			   // Instead of converting the entire data set to undirected, only when we generate the target set, 
			   // we convert the edges we need to undirected.
			    
			   List<UserRelation> oldchildUsers = userRelationDALImpl.getUsersByParentId(curuser);
			   //System.out.println("# of children: "+oldchildUsers.size());
			   for (UserRelation userRelation : oldchildUsers)
			   {
				  
				  /* if(userRelation.getChild()== curuser)//self connection should be deleted.
				   {
					   userRelationDALImpl.removeRelationship(curuser, curuser);
					   continue;
				   }*/
				   addUnidirectionRelation(userRelation.getChild(), curuser);
				  // System.out.println("T--"+userRelation.getChild());
					   if(!nodesqueue.contains(userRelation.getChild())){
						  User childUser =  userDALImpl.getUserById(userRelation.getChild());
						   if( childUser != null) {
							   nodesqueue.add(childUser.getUserId());
							   if(expectedsize <= nodesqueue.size())
							   {   flag = true;
								   break;
							   }
						   }else {
					//		   System.out.println("cleaning data");
							   userRelationDALImpl.removeChild(userRelation.getChild());//why do we need it? There may be data missing:
							   //a node exists in a relation but not in the profile file, then delete the node relation
						   }
						}
				  // System.out.println("test1");
			   }
			   if(flag == false)
		   {
			   List<UserRelation> parentUsers = userRelationDALImpl.getUsersByChildId(curuser);
			   //System.out.println("# of parents: "+parentUsers.size());
			   for (UserRelation userRelation : parentUsers)
			   {

				 /*  if(userRelation.getParent() == curuser)//self connection should be deleted.
				   {
					   userRelationDALImpl.removeRelationship(curuser, curuser);
					   continue;
				   }*/
			       addUnidirectionRelation(curuser, userRelation.getParent()); 
			       //System.out.println("T--"+userRelation.getParent());
				   if(!nodesqueue.contains(userRelation.getParent())){
					  User parentUser =  userDALImpl.getUserById(userRelation.getParent());
					   if( parentUser != null) {
						   nodesqueue.add(parentUser.getUserId());
						   if(expectedsize <= nodesqueue.size())
						   {   flag = true;
							   break;
						   }
					   }else {
						   //System.out.println("cleaning data");
						   userRelationDALImpl.removeChild(userRelation.getParent());//why do we need it? There may be data missing:
						   
						   //a node exists in a relation but not in the profile file, then delete the node relation
					   }
					}
			    }
			}
			   if(flag)
				   break;
		   }		   
		  
		   if(targets.size() < expectedsize && flag == true)
		   {
			   int indexrecord = targets.size();
			   while(curindex<nodesqueue.size())
			   {
				    targets.add(nodesqueue.get(curindex));
					curindex++;				
			   }
			   indexrecord = 0;
			   Long curusertmp;
			   while(indexrecord<targets.size()-1)
			   {
				   curusertmp = targets.get(indexrecord);
				   //System.out.println(curusertmp);
				   for(int i = indexrecord+1; i< targets.size(); i++)
				   {
					   if(userRelationDALImpl.isRelatedUniDirection(curusertmp, targets.get(i)))
					   {
						   //System.out.println(curusertmp+":"+targets.get(i));
						   addUnidirectionRelation(targets.get(i), curusertmp);
					   }
					   else if(userRelationDALImpl.isRelatedUniDirection(targets.get(i), curusertmp))
					   {
						   //System.out.println(targets.get(i)+":"+curusertmp);
						   addUnidirectionRelation(curusertmp, targets.get(i));
					   }
				   }
				   indexrecord++;
			   }
			   /*
			   Long curusertmp;
			   while(indexrecord<targets.size()-1)
			   {
				   curusertmp = targets.get(indexrecord);
				   System.out.println(curusertmp);
				   for(int i = indexrecord+1; i< targets.size(); i++)
				   {
					   if(userRelationDALImpl.isRelatedUniDirection(curusertmp, targets.get(i)))
					   {
						   System.out.println(curusertmp+":"+targets.get(i));
						   addUnidirectionRelation(targets.get(i), curusertmp);
					   }
					   else if(userRelationDALImpl.isRelatedUniDirection(targets.get(i), curusertmp))
					   {
						   System.out.println(targets.get(i)+":"+curusertmp);
						   addUnidirectionRelation(curusertmp, targets.get(i));
					   }
				   }
				   indexrecord++;
			   }*/
		   }
		   return targets;
	}

	public List<Long> findOverlapUsers(User initOverlap, int overlapsize, List<Long> targetUsersIds) {
		// The read way to implement overlap
		// TODO Auto-generated method stub
		List<Long> queueoverlap = new LinkedList<>();
		List<Long> overlapUsersIds = new LinkedList<>();
		queueoverlap.add(initOverlap.getUserId());
		Long curuser;
		
		//find the overlap set
		int curindex = 0;
		boolean flag = false;
		while(curindex < queueoverlap.size() && overlapsize > queueoverlap.size())
		 	{
			   curuser = queueoverlap.get(curindex);
			   overlapUsersIds.add(curuser);
			   
			   List<UserRelation> childUsers = userRelationDALImpl.getUsersByParentId(curuser);
			   
			   for (UserRelation userRelation : childUsers) {
				   if(targetUsersIds.contains(userRelation.getChild())&&!queueoverlap.contains(userRelation.getChild())){
					  User childUser =  userDALImpl.getUserById(userRelation.getChild());
					   if( childUser != null) {
						   queueoverlap.add(childUser.getUserId());
						   if(overlapsize <= queueoverlap.size())
						   {   
							   flag = true;
							   break; 
						   }
					   }
				   }
			   }
			   curindex++;
			   if(flag)
				   break;
		   }

			while(overlapUsersIds.size()<overlapsize && curindex < queueoverlap.size())
			{
				overlapUsersIds.add(queueoverlap.get(curindex));
				curindex++;
			}
		return overlapUsersIds;
		/*Option 2. take the first overlapsize nodes from the target list as overlap. 
		List<Long> overlapUsersIds = new LinkedList<>();
		for (int i = 0; i< overlapsize; i++)
			overlapUsersIds.add(targetUsersIds.get(i));
		return overlapUsersIds;*/
			
	}
	public List<Long> findAuxiUsers(int auxiliarySize, List<Long> targetUsersIds, List<Long> overlaplist) {
	    //  The real way to implement auxiliary set
			List<Long> auxiUsersIds = new LinkedList<>();
			List<Long> queueauxi = new LinkedList<>();
			if(auxiliarySize == overlaplist.size())
			{
				return overlaplist;
			}
			
			for(int i = 0; i<overlaplist.size(); i++)//copy overlap nodes to auxiliary set
				{
					queueauxi.add(overlaplist.get(i));
					//auxiUsersIds.add(overlaplist.get(i));
				}
			Long curuser;
			int curindex = 0;
			boolean flag = false;
			while(curindex < queueauxi.size() && auxiliarySize > queueauxi.size())
			{
				   curuser = queueauxi.get(curindex);
				   curindex++;
				   if(!auxiUsersIds.contains(curuser))
					   auxiUsersIds.add(curuser);
			   
				   List<UserRelation> oldchildUsers = userRelationDALImpl.getUsersByParentId(curuser);
				   for (UserRelation userRelation : oldchildUsers)
				   {
					  /* if(userRelation.getChild()== curuser)//self connection should be deleted.
					   {
						   userRelationDALImpl.removeRelationship(curuser, curuser);
						   continue;
					   }*/
					   addUnidirectionRelation(userRelation.getChild(), curuser);
					   if(!targetUsersIds.contains(userRelation.getChild())&&!queueauxi.contains(userRelation.getChild())){
						  User childUser =  userDALImpl.getUserById(userRelation.getChild());
						   if( childUser != null) {
							   queueauxi.add(childUser.getUserId());
							   if(auxiliarySize <= queueauxi.size())
							   {
								   flag = true;
								   break;
							   }
						   }
						}
				   }
				   if(flag == false)
				   {
					   List<UserRelation> parentUsers = userRelationDALImpl.getUsersByChildId(curuser);
					   for (UserRelation userRelation : parentUsers)
					   {
						   /*if(userRelation.getParent()== curuser)//self connection should be deleted.
						   {
							   userRelationDALImpl.removeRelationship(curuser, curuser);
							   continue;
						   }*/
						   addUnidirectionRelation(curuser, userRelation.getParent());
						   if(!targetUsersIds.contains(userRelation.getChild())&&!queueauxi.contains(userRelation.getChild())){
							  User childUser =  userDALImpl.getUserById(userRelation.getChild());
					    	   if( childUser != null) {
								   queueauxi.add(childUser.getUserId());
								   if(auxiliarySize <= queueauxi.size())
								   {
									   flag = true;
									   break;
								   		}	
								   }
							}
					   }
				   }
				   if(flag == true)
					   break;
			}
			
			if(auxiUsersIds.size()<auxiliarySize && flag == true) {
			int indexrecord = auxiUsersIds.size();
			while(curindex < queueauxi.size())
			{
				auxiUsersIds.add(queueauxi.get(curindex));
				curindex++;	
				}
			
			/*
			for(int i = 0; i< overlaplist.size(); i++)
			{
				for(int j = overlaplist.size(); j < auxiUsersIds.size();j++)
				{
					
					if(userRelationDALImpl.isRelatedUniDirection(auxiUsersIds.get(i), auxiUsersIds.get(j)))
						   addUnidirectionRelation(auxiUsersIds.get(j), auxiUsersIds.get(i));
					   else if(userRelationDALImpl.isRelatedUniDirection(auxiUsersIds.get(j), auxiUsersIds.get(i)))
						   addUnidirectionRelation(auxiUsersIds.get(i), auxiUsersIds.get(j));
				}
			}*/
			
			 Long curusertmp;
			 //indexrecord = overlaplist.size();
			 indexrecord = 0;
			   while(indexrecord<auxiUsersIds.size()-1)
			   {
				   curusertmp = auxiUsersIds.get(indexrecord);
				   //System.out.println(curusertmp);
				   for(int i = indexrecord+1; i< auxiUsersIds.size(); i++)
				   {
					   //System.out.println(i+":"+auxiUsersIds.get(i));
					   if(userRelationDALImpl.isRelatedUniDirection(curusertmp, auxiUsersIds.get(i)))
						   addUnidirectionRelation(auxiUsersIds.get(i), curusertmp);
					   else if(userRelationDALImpl.isRelatedUniDirection(auxiUsersIds.get(i), curusertmp))
						   addUnidirectionRelation(curusertmp, auxiUsersIds.get(i));
				   }
				   indexrecord++;
			   }
			
			}
			
			return auxiUsersIds;
		}
	/*
	public List<Long> findAuxiUsers(int auxiliarySize, List<Long> targetUsersIds, List<Long> overlaplist) {
    //  The real way to implement auxiliary set
		List<Long> auxiUsersIds = new LinkedList<>();
		List<Long> queueauxi = new LinkedList<>();
		for(int i = 0; i<overlaplist.size(); i++)//copy overlap nodes to auxiliary set
			queueauxi.add(overlaplist.get(i));
		Long curuser;
		int curindex = 0;
		boolean flag = false;
		while(curindex < queueauxi.size() && auxiliarySize > queueauxi.size())
		   {
			   curuser = queueauxi.get(curindex);
			   if(!auxiUsersIds.contains(curuser))
				   auxiUsersIds.add(curuser);
			  
			   
			   List<UserRelation> oldchildUsers = userRelationDALImpl.getUsersByParentId(curuser);//???
			   for (UserRelation userRelation : oldchildUsers)
				   addUnidirectionRelation(userRelation.getChild(), curuser);

			   List<UserRelation> parentUsers = userRelationDALImpl.getUsersByChildId(curuser);
			   for (UserRelation userRelation : parentUsers)
				   addUnidirectionRelation(curuser, userRelation.getParent());
			   
			   List<UserRelation> childUsers = userRelationDALImpl.getUsersByParentId(curuser);
			   
			   for (UserRelation userRelation : childUsers) {
				   if(!targetUsersIds.contains(userRelation.getChild())&&!queueauxi.contains(userRelation.getChild())){
					  User childUser =  userDALImpl.getUserById(userRelation.getChild());
					   if( childUser != null) {
						   queueauxi.add(childUser.getUserId());
						   if(auxiliarySize <= queueauxi.size())
						   {
							   flag = true;
							   break;
						   }
					   }
					}
			   }
			   curindex++;
			   if (flag)
				   break;
		   }
				while(auxiUsersIds.size()<auxiliarySize && curindex < queueauxi.size())
				{
					auxiUsersIds.add(queueauxi.get(curindex));
					curindex++;
				}
		   return auxiUsersIds;
	}*/
	private void addUnidirectionRelation(long parentId , long childId) {
		
		if(!userRelationDALImpl.isRelatedUniDirection(parentId, childId)) {
			UserRelation ur = new UserRelation();
			ur.setId(UUID.randomUUID().toString());
			ur.setChild(childId);
			ur.setParent(parentId);
			userRelationDALImpl.save(ur);
			//index++;
		}
	}
}
