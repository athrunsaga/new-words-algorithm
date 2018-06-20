package com.baitengsoft.bigdata.nwa.algm1;

public class Data {
    public Data(){

    }

    public String str;
    public int[] num;

    public void setStr(String str){
        this.str = str;
    }

    public void setNum(int[] num){
        this.num = num;
    }

    public void changeNum0(int s){
        this.num[0] = s;
    }

    public void changeNum1(int e){
        this.num[1] = e;
    }
}
