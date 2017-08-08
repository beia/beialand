package hello;

import org.springframework.context.ApplicationContext;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;



public class Node1 {

	

	@JacksonXmlProperty(isAttribute = true)

	private String name;

	

	@JacksonXmlProperty(isAttribute = true)

	private String id;

	

	@JacksonXmlProperty(isAttribute = true, localName = "class")

    private String classs;

	

	@JacksonXmlProperty(localName = "nodes")

	private Node[] nodes;

	

	public Node[] getNodes() {

		return nodes;

	}



	public void setNodes(Node[] nodes) {

		this.nodes = nodes;

	}

	



	public String getName() {

		return name;

	}



	public void setName(String name) {

		this.name = name;

	}



	public String getId() {

		return id;

	}



	public void setId(String id) {

		this.id = id;

	}



}