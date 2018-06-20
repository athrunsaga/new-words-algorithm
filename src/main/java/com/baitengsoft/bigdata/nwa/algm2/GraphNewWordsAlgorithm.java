package com.baitengsoft.bigdata.nwa.algm2;

import java.util.List;

/**
 * 通过文本网络发现新词的算法。
 *
 * @author xupeng 2018-6-12
 */
public final class GraphNewWordsAlgorithm {

  // 存储线程独立的 Graph 对象
  private static final ThreadLocal<Graph> threadGraphObject = new ThreadLocal<Graph>() {

    @Override
    protected Graph initialValue() {
      return new Graph();
    }
  };

  /**
   * 从文本 page 中发现并返回新词列表。
   * @param page 文本。
   * @return 返回新词列表。
   */
  public static List<String> getNewWords(String page) {
    if (page == null)
      throw new NullPointerException("page");

    Graph threadGraph = threadGraphObject.get();
    threadGraph.clear();
    return threadGraph.getNewWords(page);
  }
}
