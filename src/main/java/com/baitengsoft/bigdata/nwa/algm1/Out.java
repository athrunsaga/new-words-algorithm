package com.baitengsoft.bigdata.nwa.algm1;

import java.util.ArrayList;
import java.util.List;

public class Out {

    public int index;
    public char next_text;
    public int frequency;
    public double weight;

    List<String> edge_ind_list = new ArrayList<>();

    public void setIndex(int index){ this.index = index;}

    public void setNext_text(char text){ this.next_text = text;}

    public void setFrequency(int fre){ this.frequency = fre;}

    public void setWeight(double weight){ this.weight = weight; }

    public void Add_edgeindList(int ind){
        String str = String.valueOf(ind);
        edge_ind_list.add(str);
    }

    public void fre_plus(){
        this.frequency++;
    }

    public Out(){

    }
}
