package com.baitengsoft.bigdata.nwa;

import com.baitengsoft.bigdata.nwa.algm1.Graph;
import com.baitengsoft.bigdata.nwa.algm2.GraphNewWordsAlgorithm;
import org.junit.Test;

import java.util.*;

public class CompareTests extends BaseTests {

  @Test
  public void compareDiscoverNewWordsTest() {
    String page = getPageFromResourceFile("page1.txt");

    // 原始算法
    Graph graph = new Graph();
    List<String> algm1Words = null;
    try {
      algm1Words = graph.createGraph(page);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (algm1Words == null) {
      System.out.println("原始算法错误");
      return;
    }

    // 优化算法
    List<String> algm2Words = GraphNewWordsAlgorithm.getNewWords(page);

    // 比较两个算法发现新词情况
    Set<String> algm1WordsSet = new HashSet<>(algm1Words);
    Set<String> bothWordsSet = new TreeSet<>();
    List<String> onlyAlgm2Words = new ArrayList<>();
    for (String word : algm2Words) {
      if (algm1WordsSet.contains(word))
        bothWordsSet.add(word);
      else
        onlyAlgm2Words.add(word);
    }

    List<String> onlyAlgm1Words = new ArrayList<>();
    for (String word : algm1Words) {
      if (!bothWordsSet.contains(word))
        onlyAlgm1Words.add(word);
    }

    // 显示对比结果
    System.out.println("Both discovered by algm1 and algm2:");
    bothWordsSet.forEach(System.out::println);

    System.out.println("Only discovered by algm1");
    onlyAlgm1Words.forEach(System.out::println);

    System.out.println("Only discovered by algm2");
    onlyAlgm2Words.forEach(System.out::println);
  }
}
