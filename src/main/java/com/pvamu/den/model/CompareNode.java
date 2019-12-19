package com.pvamu.den.model;

import java.util.Random;

public class CompareNode {
	
	private long targetNode;
	private long auxNode;
	private long pTargetNode;
	private long pAuxNode;
	
	private boolean visted = false;
	private boolean matched = false;
	
	private double similartity ;
	
	
	public CompareNode(long targetNode, long auxNode , long pTargetNode , long pAuxNode) {
		super();
		this.targetNode = targetNode;
		this.auxNode = auxNode;
		this.pAuxNode = pAuxNode;
		this.pTargetNode = pTargetNode;
		
	}
	
	public long getTargetNode() {
		return targetNode;
	}

	public long getAuxNode() {
		return auxNode;
	}

	public boolean isVisted() {
		return visted;
	}

	public void setVisted(boolean visted) {
		this.visted = visted;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public double getSimilartity() {
		return similartity;
	}

	public void setSimilartity(double similartity) {
		this.similartity = similartity;
	}

	public long getpTargetNode() {
		return pTargetNode;
	}

	public void setpTargetNode(long pTargetNode) {
		this.pTargetNode = pTargetNode;
	}

	public void setTargetNode(long targetNode) {
		this.targetNode = targetNode;
	}

	public long getpAuxNode() {
		return pAuxNode;
	}

	public void setpAuxNode(long pAuxNode) {
		this.pAuxNode = pAuxNode;
	}

	public void setAuxNode(long auxNode) {
		this.auxNode = auxNode;
	}

//	public void setSimilartity() {
//		
//		Random rand = new Random();
//		int randomNum = rand.nextInt(10);
//		this.similartity = randomNum;
//	}

}
