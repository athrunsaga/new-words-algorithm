package com.baitengsoft.bigdata.nwa.algm2;

import com.baitengsoft.bigdata.nwa.BaseTests;
import com.baitengsoft.bigdata.nwa.algm2.GraphNewWordsAlgorithm;
import org.junit.Test;

import java.util.List;

public class Algorithm2Tests extends BaseTests {

  @Test
  public void discoverNewWordsTest() {
    String page = getPageFromResourceFile("page4.txt");
    List<String> newWords = GraphNewWordsAlgorithm.getNewWords(page);

    for (String word : newWords)
      System.out.println(word);
  }

  @Test
  public void showGraphWeight() {
    String page = getPageFromResourceFile("page1.txt");
    Graph graph = new Graph();
    graph.getNewWords(page);

    List<Line> lines = graph.getLines();
    lines.forEach(
      l -> System.out.println(
        String.format("%3d  %c %c  %f", l.index, l.leftNode.c, l.rightNode.c, l.weight)));
  }
}
