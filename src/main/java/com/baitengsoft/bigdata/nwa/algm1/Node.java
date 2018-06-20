package com.baitengsoft.bigdata.nwa.algm1;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public char text;

    public Edge pre_edge;
    public Edge next_edge;

    public int node_index;

    public List<String> ind_list = new ArrayList<>();

    public List<Edge> pre_edge_list = new ArrayList<>();
    public List<Edge> next_edge_list = new ArrayList<>();

    public List<In> in_list = new ArrayList<>();
    public List<Out> out_list = new ArrayList<>();

    public double in_weight;
    public double out_weight;

    public int flag;//1 左词边界；2 右词边界； default 词内字

    public void setFlag(int flag){
        this.flag = flag;
    }

    public void Add_node(char ch, Edge pre_edge){
        this.text = ch;
        this.pre_edge = pre_edge;

    }

    public void setNode_index(int index){
        this.node_index = index;
    }

    public void setIn_weight(double in_weight){
        this.in_weight = in_weight;
    }

    public void setOut_weight(double out_weight){
        this.out_weight = out_weight;
    }

    public void setNext_edge(Edge next_edge){
        this.next_edge = next_edge;
    }

    public void Pre_edge_list_Add(Edge edge){
        if(!this.pre_edge_list.contains(edge)) {
            this.pre_edge_list.add(edge);
        }
    }

    public void Next_edge_list_Add(Edge edge){
        if(!this.next_edge_list.contains(edge)) {
            this.next_edge_list.add(edge);
        }
    }

    public void Pre_edge_list_Remove(Edge edge){
        this.pre_edge_list.remove(edge);
    }

    public void Next_edge_list_Remove(Edge edge){
        this.next_edge_list.remove(edge);
    }

    public void Add_inList(In in){
        in_list.add(in);
    }

    public void Add_outList(Out out){
        out_list.add(out);
    }

    public void ind_list_Add(int ind){
        ind_list.add(String.valueOf(ind));
    }

    public Node(){

    }
}
