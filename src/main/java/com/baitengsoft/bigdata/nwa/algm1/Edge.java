package com.baitengsoft.bigdata.nwa.algm1;

public class Edge {

    public int index;

    public Node pre_node;
    public Node next_node;

    public double weight;

    public void set_index(int index){this.index = index;}

    public void setWeight(double weight){ this.weight = weight;}

    public void  setPre_node(Node pre_node){this.pre_node = pre_node;}

    public void  setNext_node(Node next_node){this.next_node = next_node;}

    public Edge(){

    }
}
