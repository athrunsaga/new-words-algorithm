package com.baitengsoft.bigdata.nwa.algm1;

import com.baitengsoft.bigdata.nwa.BaseTests;
import com.baitengsoft.bigdata.nwa.algm1.Graph;
import org.junit.Test;

import java.util.List;

public class Algorithm1Tests extends BaseTests {

  @Test
  public void discoverNewWordsTest() throws Exception {
    String page = getPageFromResourceFile("page4.txt");
    Graph graph = new Graph();
    List<String> newWords = null;
    try {
      newWords = graph.createGraph(page);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (newWords != null)
      for (String word : newWords)
        System.out.println(word);
  }
}
