package com.pvamu.den.model;


import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "log_de_anonymization")
public class LogReport {
	
	@Id
	@Indexed
	private UUID id = UUID.randomUUID() ; 
	private Date timeStamp;
	private int noSeeds;
	private int configuration;
	private int totalMatched;
	private float accuracy;
	private boolean preSeed = false ;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getNoSeeds() {
		return noSeeds;
	}
	public void setNoSeeds(int noSeeds) {
		this.noSeeds = noSeeds;
	}
	public int getConfiguration() {
		return configuration;
	}
	public void setConfiguration(int configuration) {
		this.configuration = configuration;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public boolean isPreSeed() {
		return preSeed;
	}
	public void setPreSeed(boolean preSeed) {
		this.preSeed = preSeed;
	}
	public int getTotalMatched() {
		return totalMatched;
	}
	public void setTotalMatched(int totalMatched) {
		this.totalMatched = totalMatched;
	}

}
