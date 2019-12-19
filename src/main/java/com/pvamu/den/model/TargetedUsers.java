package com.pvamu.den.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "target_user_profile")
public class TargetedUsers extends User {
	private int alterId;
	
	private int minYear ;
	
	private int maxYear ;
	
	private boolean selectSeed;
	
	private boolean visted ;
	
	private double centrality;

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}

	public boolean isSelectSeed() {
		return selectSeed;
	}

	public void setSelectSeed(boolean selectSeed) {
		this.selectSeed = selectSeed;
	}

	public boolean isVisted() {
		return visted;
	}

	public void setVisted(boolean visted) {
		this.visted = visted;
	}

	public double getCentrality() {
		return centrality;
	}

	public void setCentrality(double centrality) {
		this.centrality = centrality;
	}

	public int getAlterId() {
		return alterId;
	}

	public void setAlterId(int alterId) {
		this.alterId = alterId;
	}
	
}
