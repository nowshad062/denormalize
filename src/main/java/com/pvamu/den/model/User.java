package com.pvamu.den.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_profile")
public class User {
	@Id
	@Indexed
	private long _id;
	private int dob;
	private int gender ;
	private long parentId;
	private int xAxis;
	private int yAxis;
	private Date creationDate = new Date();
	
	public long getUserId() {
		return _id;
	}
	public void setUserId(long userId) {
		this._id = userId;
	}
	public int getDob() {
		return dob;
	}
	public void setDob(int dob) {
		this.dob = dob;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public int getxAxis() {
		return xAxis;
	}
	public void setxAxis(int xAxis) {
		this.xAxis = xAxis;
	}
	public int getyAxis() {
		return yAxis;
	}
	public void setyAxis(int yAxis) {
		this.yAxis = yAxis;
	}
	@Override
	public String toString() {
		return "User [userId=" + _id + ", dob=" + dob + ", gender=" + gender + ", parentId=" + parentId + ", xAxis="
				+ xAxis + ", yAxis=" + yAxis + ", creationDate=" + creationDate + "]";
	}
}
