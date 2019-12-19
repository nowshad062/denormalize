package com.pvamu.den.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"weight",
	"source",
	"target",
	"enzyme",
	"faveColor",
	"line_style"
})
public class Data_ implements Serializable
{

	@JsonProperty("id")
	private String id;
	@JsonProperty("weight")
	private Integer weight;
	@JsonProperty("source")
	private String source;
	@JsonProperty("target")
	private String target;
	@JsonProperty("enzyme")
	private String enzyme;
	@JsonProperty("faveColor")
	private String faveColor;
	@JsonProperty("line_style")
	private String line_style;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = 6507459959261474383L;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("weight")
	public Integer getWeight() {
		return weight;
	}

	@JsonProperty("weight")
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@JsonProperty("source")
	public String getSource() {
		return source;
	}

	@JsonProperty("source")
	public void setSource(String source) {
		this.source = source;
	}

	@JsonProperty("target")
	public String getTarget() {
		return target;
	}

	@JsonProperty("target")
	public void setTarget(String target) {
		this.target = target;
	}

	@JsonProperty("enzyme")
	public String getEnzyme() {
		return enzyme;
	}

	@JsonProperty("enzyme")
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	
	
	
	@JsonProperty("faveColor")
	public String getFaveColor() {
		return faveColor;
	}
	
	@JsonProperty("faveColor")
	public void setFaveColor(String faveColor) {
		this.faveColor = faveColor;
	}
	
	@JsonProperty("line_style")
	public String getLine_style() {
		return line_style;
	}
	
	@JsonProperty("line_style")
	public void setLine_style(String line_style) {
		this.line_style = line_style;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new org.apache.commons.lang3.builder.ToStringBuilder(this).append("id", id).append("weight", weight).append("line_style", line_style).append("source", source).append("target", target).append("enzyme", enzyme).append("faveColor", faveColor).append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {//line_style
		return new HashCodeBuilder().append(id).append(line_style).append(enzyme).append(faveColor).append(weight).append(source).append(additionalProperties).append(target).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Data_) == false) {
			return false;
		}
		Data_ rhs = ((Data_) other);
		return new EqualsBuilder().append(id, rhs.id).append(line_style, rhs.line_style).append(faveColor, rhs.faveColor).append(enzyme, rhs.enzyme).append(weight, rhs.weight).append(source, rhs.source).append(additionalProperties, rhs.additionalProperties).append(target, rhs.target).isEquals();
	}

}
