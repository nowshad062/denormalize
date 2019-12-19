package com.pvamu.den.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_relation")
public class UserRelation {
	
	@Id
	private String id;
	@Indexed
	private long parent;
	@Indexed
	private long child;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getParent() {
		return parent;
	}
	public void setParent(long parent) {
		this.parent = parent;
	}
	public long getChild() {
		return child;
	}
	public void setChild(long child) {
		this.child = child;
	}
	

}
