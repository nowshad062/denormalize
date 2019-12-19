package com.pvamu.den.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.AuxliaryUserDALImpl;
import com.pvamu.den.dal.LogReportDALImpl;
import com.pvamu.den.dal.OverLpaUserDALImpl;
import com.pvamu.den.dal.TargerUserDALImpl;
import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.model.Analysis;
import com.pvamu.den.model.AuxliaryUser;
import com.pvamu.den.model.CompareNode;
import com.pvamu.den.model.Data_;
import com.pvamu.den.model.DeAnonymizationResponse;
import com.pvamu.den.model.Edge;
import com.pvamu.den.model.Elements;
import com.pvamu.den.model.LogReport;
import com.pvamu.den.model.OverLapuser;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.UserRelation;



@Service
public class DenormalationNodes {
	
	@Autowired
	private  TargerUserDALImpl targerUserDALImpl;
	
	@Autowired
	private  AuxliaryUserDALImpl auxliaryUserDALImpl;
	
	@Autowired
	private  OverLpaUserDALImpl overLpaUserDALImpl;
	
	@Autowired
	private  UserRelationDALImpl userRelationDALImpl;
	
	@Autowired
	private  LogReportDALImpl logReportDALImpl;
	
	
	@Autowired
	private  CosineSimilarity cosineSimilarity;
	
	@Autowired
	private  ConstructGraph constructGraph;
	
	Map<Long , TargetedUsers> mapTargetUsers = new LinkedHashMap<Long, TargetedUsers>();
	Map<Long , AuxliaryUser> mapAuxUsers = new LinkedHashMap<Long, AuxliaryUser>();
	Map<String, CompareNode> mapCompare = new LinkedHashMap<>();
	
	public DeAnonymizationResponse generate(int noSeed , int config , boolean keepPrev ) {
		
		clearData();
		//Prepare Data 
		List<Long> initialseeds =  overlapProcess(noSeed, keepPrev);
		if(keepPrev)
			noSeed = initialseeds.size();
		targetProcess();
		auxProcess();
		      
		// initial seed for comparison 
		for(Long userid : initialseeds) {
				String id = ""+userid+""+userid; 
				CompareNode compareNode = new CompareNode(userid, userid ,mapTargetUsers.get(userid).getAlterId() , mapAuxUsers.get(userid).getUserId() );
				compareNode.setSimilartity(1);
				compareNode.setMatched(true);
				mapCompare.put(id, compareNode);
		}
		
		getchildNodes(config) ;

		int accuracy = 0 ;
		for (String key : mapCompare.keySet()){
			if ( mapCompare.get(key).isMatched()) {
				accuracy = accuracy+1;
			}
		}
		List<OverLapuser> overlapUsers = overLpaUserDALImpl.getAllRecord();
		// The definition of accuracy: among non-seeds in the overlap, the percentage of nodes accurately matched excluding the seeds.
		float averageAcc = 0;
		if( (overlapUsers.size() - noSeed) > 0) { 
			averageAcc = ((float)(accuracy-noSeed))/(overlapUsers.size() - noSeed);
		}
		//System.out.println(accuracy);
		//System.out.println(noSeed);
		//System.out.println(overlapUsers.size());
		//System.out.println(averageAcc);
		
		LogReport logrport = new LogReport();
		
		logrport.setNoSeeds(noSeed);
		logrport.setConfiguration(config);
		logrport.setPreSeed(keepPrev);
		logrport.setAccuracy(averageAcc);
		logrport.setTotalMatched(mapCompare.size());
		
		logrport.setTimeStamp(new Date());
		
		logReportDALImpl.addLog(logrport);
		Elements elements = constructGraph.graphCreate(mapCompare , mapTargetUsers , mapAuxUsers);
				
		Analysis analysis = new Analysis();
		analysis.setNoAccuracy(accuracy);
		analysis.setNoMatched(mapCompare.size());
		analysis.setNoofA(mapAuxUsers.size());
		analysis.setNoofT(mapTargetUsers.size());
		
		DeAnonymizationResponse response = new DeAnonymizationResponse();
		//System.out.println("before responding:"+elements.getEdges().size());
		
		/*final String tlineColor = "#8EE6EC";
		final String tlineStyle = "solid";
		Edge edget = new Edge();
		Data_ data_ = new Data_();
		data_.setEnzyme("test");
		data_.setId("1-4");
		data_.setSource("1");
		data_.setTarget("4");
		data_.setFaveColor(tlineColor);
		data_.setLine_style(tlineStyle);// solid, dotted, or dashed.
		edget.setData(data_);
		List<Edge> t=new ArrayList<Edge>();
		t.add(edget);
		elements.setEdges(t);*/
		response.setElements(elements);
		response.setAnalysis(analysis);
		clearData() ;	
		return response;
	}
	
	private void getchildNodes(int config) {
		String lastKey = "";
		for (Map.Entry<String, CompareNode> entry : mapCompare.entrySet()) {
				lastKey = entry.getKey();
        }
		if( mapCompare.get(lastKey).isVisted()) {
			return;
		}
		Map<String, CompareNode> childmapCompare = new LinkedHashMap<>();
		//mapCompare has all matched pairs although there are incorrectly matched pairs.
		for (String key : mapCompare.keySet()){
			CompareNode node = mapCompare.get(key);
			if(node.isVisted()) {
				continue;
			}
			node.setVisted(true);
			//System.out.println("compare:"+node.getTargetNode()+"-"+node.getAuxNode());
			List<UserRelation> target_relations = userRelationDALImpl.getUsersByParentId(node.getTargetNode());
			List<UserRelation> aux_relations = userRelationDALImpl.getUsersByParentId(node.getAuxNode());
			 for(UserRelation targetUser : target_relations) {				 
				 if( mapTargetUsers.containsKey(targetUser.getChild())) {
					 // mapTargetUsers has all target nodes selected from the original data set.
					 for(UserRelation auxUser : aux_relations) {
						 if( mapAuxUsers.containsKey(auxUser.getChild())) {
							// mapAuxUsers has all Auxiliary nodes selected from the original data set.
							 	//String reverseid = ""+auxUser.getChild()+""+targetUser.getChild(); 
							 	String id = ""+targetUser.getChild()+""+auxUser.getChild(); 	
							 	// check whether the two neighbors are already paired.
							 	//System.out.println("check child pair"+id);
							 	boolean nodePairedAlready = false ;						 	
							 	for (String checkKey : mapCompare.keySet()){							
									CompareNode checkNode = mapCompare.get(checkKey);									
									if( (targetUser.getChild() == checkNode.getTargetNode())|| (auxUser.getChild() == checkNode.getAuxNode() )){
										nodePairedAlready = true;
										//System.out.println("already paired!");
										break;
									}									
								}
							 	//if(childmapCompare.containsKey(id)||mapCompare.containsKey(reverseid)||nodePairedAlready) {
							 	if(nodePairedAlready) {
							 		continue;
							 	}							 								 	
							 	CompareNode compareNode = new CompareNode(targetUser.getChild(), auxUser.getChild() , mapTargetUsers.get(targetUser.getChild()).getAlterId() , node.getAuxNode() );
							 	compareNode.setSimilartity(cosineSimilarity.findcosineSimilarity(mapTargetUsers.get(targetUser.getChild()) ,mapAuxUsers.get(auxUser.getChild()), config));
							 	//System.out.println("Similarit value:"+compareNode.getSimilartity());
							 	if(compareNode.getAuxNode()== compareNode.getTargetNode()) {
							 		//System.out.println("truely matched!");
							 		compareNode.setMatched(true);
								}
							    // childmapCompare has the intermediate results of mapping
							 	childmapCompare.put(id, compareNode); 	
						 }
					 }
				 } 
			 }							
		}				
		Map<String, CompareNode> result = new LinkedHashMap<>();
		if(!childmapCompare.isEmpty())
		{
			highvalue(childmapCompare, result);		
			mapCompare.putAll(result);		
		}
		getchildNodes(config);	
	}
	
	private void highvalue(Map<String, CompareNode> childmapCompare , Map<String, CompareNode> result ) {
		/*if(childmapCompare.isEmpty()) {
			return;
		}*/		
		Map<String, CompareNode> childmapCompare1 = new LinkedHashMap<>(childmapCompare);
		//System.out.println("childmapcompare1 before finding largest value!");
		double highVlue = 0;
		CompareNode last_node = null;
		//find the pair with the highest similarity value
		for (String childKey : childmapCompare.keySet()){
			//System.out.println(childKey);
			if( highVlue < childmapCompare.get(childKey).getSimilartity()) {
				highVlue = childmapCompare.get(childKey).getSimilartity();
				last_node =childmapCompare.get(childKey);
			}
		}
		if(last_node != null) {
			String id = ""+last_node.getTargetNode()+""+last_node.getAuxNode(); 
			//System.out.println("last node pair:"+id+"-value:"+highVlue);
			result.put(id , last_node);
			for (String childKey : childmapCompare.keySet()){
				if(childmapCompare.get(childKey).getTargetNode() == last_node.getTargetNode()) {
					childmapCompare1.remove(childKey);				
				}
				if(childmapCompare.get(childKey).getAuxNode() == last_node.getAuxNode()) {
								//if(childmapCompare.get(childKey).getTargetNode() != last_node.getTargetNode()) {
						childmapCompare1.remove(childKey);
					//}
				}			
			}// remove the mappings with the two ending nodes which are matched already.
			/*System.out.println("after finding highest value");
			for (String childKey : result.keySet()){
				System.out.println(childKey);
			}*/
			highvalue(childmapCompare1 , result);
	    }		
		else
			return;
	}

	private void targetProcess() {
		List<TargetedUsers> targetUsers = targerUserDALImpl.getAllRecord();
		for(TargetedUsers t:targetUsers) {			
			mapTargetUsers.put(t.getUserId(), t);
		}		
	}
	
	private List<Long> overlapProcess(int noSeed , boolean keepPrev ) {
		List<Long> selectedseed = new LinkedList<>();
		List<OverLapuser> overlapUsers = overLpaUserDALImpl.getAllRecord();
		
		if (keepPrev) {// if keep previous seeds, copy them to the selectedseed list.
			for(int x = 0 ; x < overlapUsers.size(); x++) {
			if( overlapUsers.get(x).isSelectSeed()) {
				selectedseed.add(overlapUsers.get(x).getUserId());
				}
			}
		}
		else {
			List<Integer>  selectedIndex = new ArrayList<>();
			// remove the seed flag set previously.
			for(int x = 0; x < overlapUsers.size(); x++) {
				overLpaUserDALImpl.updateOverlapUserSeed(overlapUsers.get(x), false);
				}
			Random randn = new Random();
			randn.setSeed(System.currentTimeMillis());
			for(int x = 0 ; x < noSeed ;x++) {
				boolean flag = true ;
				while(flag) {
					int luckyIndex = randn.nextInt(overlapUsers.size());
					if(!selectedIndex.contains(luckyIndex)) {
						selectedIndex.add(luckyIndex);
						flag = false;
						selectedseed.add(overlapUsers.get(luckyIndex).getUserId());
						overLpaUserDALImpl.updateOverlapUserSeed(overlapUsers.get(luckyIndex), true);
						}
				}
			}		
		}
		return selectedseed;
	}
	
	private void auxProcess() {
		List<AuxliaryUser> auxUsers = auxliaryUserDALImpl.getAllRecord();
		for(AuxliaryUser a:auxUsers) {							
			mapAuxUsers.put(a.getUserId(), a);
		}
	}
	
	private void clearData() {
		mapTargetUsers.clear();
		mapAuxUsers.clear();
		mapCompare.clear();
	}

	@Override
	protected void finalize() throws Throwable {
		clearData();
		super.finalize();
	}
}

