package com.pvamu.den.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"nodes",
	"edges"
})
public class Elements implements Serializable
{

	@JsonProperty("nodes")
	private List<Node> nodes = null;
	@JsonProperty("edges")
	private List<Edge> edges = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -7981534677664437533L;

	@JsonProperty("nodes")
	public List<Node> getNodes() {
		return nodes;
	}

	@JsonProperty("nodes")
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	@JsonProperty("edges")
	public List<Edge> getEdges() {
		return edges;
	}

	@JsonProperty("edges")
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
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
		return new ToStringBuilder(this).append("nodes", nodes).append("edges", edges).append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(edges).append(additionalProperties).append(nodes).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Elements) == false) {
			return false;
		}
		Elements rhs = ((Elements) other);
		return new EqualsBuilder().append(edges, rhs.edges).append(additionalProperties, rhs.additionalProperties).append(nodes, rhs.nodes).isEquals();
	}

}