package com.pvamu.den.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.UserRelationDALImpl;
import com.pvamu.den.model.AuxliaryUser;
import com.pvamu.den.model.CompareNode;
import com.pvamu.den.model.Data;
import com.pvamu.den.model.Data_;
import com.pvamu.den.model.Edge;
import com.pvamu.den.model.Elements;
import com.pvamu.den.model.Node;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.model.UserRelation;

@Service
public class ConstructGraph {

	@Autowired
	private UserRelationDALImpl userRelationDALImpl;

	public Elements graphCreate(Map<String, CompareNode> mapCompare, Map<Long, TargetedUsers> mapTargetUsers,Map<Long, AuxliaryUser> mapAuxUsers) {

		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();
		List<String> taEdge = new ArrayList<>();

		Elements ele = new Elements();
		
		final String tlineColor = "#8EE6EC";
		final String tlineStyle = "solid";
		
		//create target graph
		for (Long key : mapTargetUsers.keySet()) {
			//System.out.println("each target node:"+String.valueOf(key));
			addNode((long)mapTargetUsers.get(key).getAlterId(), nodes, "#46C0D8");
			List<UserRelation> target_relations = userRelationDALImpl.getUsersByParentId(key);
			for (UserRelation targetUser : target_relations) {
				if (mapTargetUsers.containsKey(targetUser.getChild())) {
					TargetedUsers tuchildEdge = mapTargetUsers.get(targetUser.getChild());
					//System.out.println(tuchildEdge.getAlterId());
					//System.out.println(mapTargetUsers.get(key).getAlterId());
					Edge eee = constrctEdge(mapTargetUsers.get(key).getAlterId(), tuchildEdge.getAlterId(),tlineColor, tlineStyle, "Target Relation", taEdge);
					/*if(key == 676858 && targetUser.getChild()==850923 ) {
						System.out.println(eee.getData().getLine_style());
						System.out.println(eee.getData().getSource());
						System.out.println(eee.getData().getTarget());
						System.out.println(eee.getData().getFaveColor());
					}*/
					if(eee!=null)
						edges.add(eee);
				}
			}	
			/*
			if(key == 676858)
			{	String tmp = "";
				for(int i = 0; i < edges.size(); i++)
					{
					//tmp = tmp + "="+edges.get(i).getData().getId();
				//System.out.println(tmp);
					System.out.println("***"+edges.get(i).getData().getSource());
					System.out.println(edges.get(i).getData().getTarget()+"***");
					}
			}*/
		}
		/*
		for(int i = 0; i< edges.size(); i++)
		{
			System.out.println("==="+edges.get(i).getData().getId());
		}*/
		//create auxiliary graph
		for (Long key : mapAuxUsers.keySet()) {
			addNode((long)mapAuxUsers.get(key).getUserId(), nodes, "#CD8E58");
			List<UserRelation> aux_relations = userRelationDALImpl.getUsersByParentId(key);
			for (UserRelation targetUser : aux_relations) {
				if (mapAuxUsers.containsKey(targetUser.getChild())) {
					AuxliaryUser tuchildEdge = mapAuxUsers.get(targetUser.getChild());
					Edge eeee = constrctEdge(tuchildEdge.getUserId(), key, "#C7891F", tlineStyle, "Auxiliary Relation", taEdge);
					if(eeee!=null)
						edges.add(eeee);
				}
			}
		}
		final String alineColor = "#888B88"; //F5A25A
		final String alineStyle = "dashed";

		for (String key : mapCompare.keySet()) {
			String enzyme = conectEnzyme(mapTargetUsers.get(mapCompare.get(key).getTargetNode()), mapAuxUsers.get(mapCompare.get(key).getAuxNode()));
			String lineColort= alineColor;
			if(mapCompare.get(key).isMatched()) {
				lineColort = "#FF0000";
			}
			Edge ee = constrctEdge(mapCompare.get(key).getAuxNode(), mapCompare.get(key).getpTargetNode(), lineColort, alineStyle, enzyme, taEdge);
			if(ee!=null)
				edges.add(ee);
		}
		//System.out.println(edges.size());
		/*for(int j=8; j<edges.size(); j++)
		{
			//if(edges.get(j).getData().getId().equals("10"))
			edges.get(j);
			System.out.println("test1");
			edges.get(j).getData();
			System.out.println("test2");
			edges.get(j).getData().getId();
			System.out.println("test3");
				System.out.println(j+":"+edges.get(j).getData().getId());
				
		}*/
		/*
		for(int i = 0; i< edges.size(); i++)
		{
			
			System.out.println("+++"+edges.get(i).getData().getId());
		}*/
		ele.setNodes(nodes);
		ele.setEdges(edges);
		/*List<Edge> tmpedges = new ArrayList<>();
		taEdge = new ArrayList<>();
		Edge eetmp = constrctEdge(2, 0, tlineColor, tlineStyle, "Target Relation", taEdge);
		if(eetmp!=null)
			tmpedges.add(eetmp);
		ele.setNodes(nodes);
		ele.setEdges(tmpedges);
		System.out.println(tmpedges.size()+"-=-=-=");*/
		return ele;
	}

	private Edge constrctEdge(long pUserId, long cUserId, String lineColor, String lineStyle, String enzyme,
			List<String> taEdge) {

		String ptu = Long.toString(pUserId);
		String tu = Long.toString(cUserId);
		String edgeId = ptu +"-" +tu;
		String rEdgeId = tu + "-"+ ptu;
		
		if (!taEdge.isEmpty()&&(taEdge.contains(edgeId)|| taEdge.contains(rEdgeId) || ptu.equals(tu))) {
			//System.out.println("NULL----");
			//System.out.println(edgeId);
			//System.out.println(rEdgeId);
			return null;
		}
		taEdge.add(edgeId);
		Edge edge = new Edge();
		Data_ data_ = new Data_();
		data_.setEnzyme(enzyme);
		data_.setId(edgeId);
		data_.setSource(ptu);
		data_.setTarget(tu);
		data_.setFaveColor(lineColor);
		data_.setLine_style(lineStyle);// solid, dotted, or dashed.
		edge.setData(data_);
		return edge;
	}

	private String conectEnzyme(TargetedUsers targetUser, AuxliaryUser auxUser) {

		StringBuilder value = new StringBuilder();
		value.append("ID #");

		value.append(targetUser.getUserId());
		value.append(" , "); 
		value.append(auxUser.getUserId());
		value.append("\n");
	
		value.append("YoB#");
		value.append(targetUser.getDob());
		value.append(" , ");
		value.append(auxUser.getDob());
		value.append("\n");

		value.append("Gender#");
		value.append(targetUser.getGender());
		value.append(" , ");
		value.append(auxUser.getGender());
		value.append("\n");
		
		
		value.append("Degree #");
		value.append(targetUser.getxAxis());
		value.append(" , ");
		value.append(auxUser.getxAxis());
		value.append("\n");

		value.append("Centrality #");
		value.append(targetUser.getCentrality());
		value.append(" , ");
		value.append(auxUser.getCentrality());
		value.append("\n");
		//value.append("</table>");
		return value.toString();
	}

	private void addNode(Long userId, List<Node> nodes, String color) {
		Data node_data = new Data();
		node_data.setId(Long.toString(userId));
		node_data.setColor(color);
		Node nod = new Node();
		nod.setData(node_data);
		nodes.add(nod);
	}
}
