package hello;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;



public class Node {

	private String name;

	private int id;

	

	@JacksonXmlProperty(localName = "class")

    private String classs;

	

	 public String getName() {

        return name;

    }



    public void setName(String name) {

        this.name = name;

    }

    public int getId() {

        return id;

    }



    public void setId(int id) {

        this.id = id;

    }

	

    @Override

    public String toString() {

        return "Node{" +

                "name='" + name + '\'' +

                ", id=" + id +

                '}';

    }



}
