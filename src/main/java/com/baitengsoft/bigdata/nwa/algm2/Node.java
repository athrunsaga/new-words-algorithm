package com.baitengsoft.bigdata.nwa.algm2;

import java.util.*;

/**
 * 表示文本网络中的一个字符节点。
 *
 * @author xupeng 2018-6-12
 */
class Node {

  /**
   * 描述从当前字符节点向另一字符节点发出连线的接出点（同一目标字符节点只有一个接出点）。
   */
  class OutPoint {

    Node toNode; // 目标字符节点
    int outCount; // 向目标字符节点接出的次数
    double weight; // 接出点权重

    OutPoint(Node toNode) {
      this.toNode = toNode;
      outCount = 1;
    }
  }

  /**
   * 描述从另一字符节点向当前字符节点进入连线的接入点（同一来源字符节点只有一个接入点）。
   */
  class InPoint {

    Node fromNode; // 来源字符节点
    int inCount; // 从来源字符节点接入的次数
    double weight; // 接入点权重

    InPoint(Node fromNode) {
      this.fromNode = fromNode;
      inCount = 1;
    }
  }

  char c; // 当前节点的字符
  boolean isChinese; // 是否中文字符

  Map<Integer, Line> leftLines; // 左侧对接的连线集合：<连线序号，左侧连线对象>
  Map<Integer, Line> rightLines; // 右侧对接的连接集合：<连线序号，右侧连线对象>

  Map<Character, OutPoint> outPointMap; // 接出点集合
  Map<Character, InPoint> inPointMap; // 接入点集合

  boolean isLeftBound = false; // 在发现词组阶段，是否作为词组左边界
  boolean isRightBound = false; // 在发现词组阶段，是否作为词组右边界

  private static Set<Character> stopWords = new HashSet<>();

  static {
    // TODO:目前方案仅处理常用单字停止词，结合现有分词组件提高精度
    // 以下这些字原则上是不会与其它字组成词组的
    stopWords.addAll(Arrays.asList('也', '了', '个', '着', '的', '吗', '为', '只', '在', '以',
      '是', '么', '得', '而', '将', '等', '且', '每', '其', '之', '乎', '尔', '若', '它', '他',
      '你', '我', '谁', '种'));
  }

  /**
   * 初始化字符节点。
   * @param c 节点字符。
   */
  Node(char c) {
    this.c = c;
    isChinese = isChineseByBlock(c) && !isStopWord(c); // 中文字符还要排除已知的停止词
    leftLines = new HashMap<>();
    rightLines = new HashMap<>();
    outPointMap = new HashMap<>();
    inPointMap = new HashMap<>();
  }

  /**
   * 连接右侧对接的连线。
   * @param rightLine 右侧连线。
   * @return 返回连接字符节点的接出点。
   */
  OutPoint connectRightLine(Line rightLine) {
    // 获取或创建接出点
    OutPoint outPoint;
    if ((outPoint = outPointMap.get(rightLine.rightNode.c)) != null) {
      outPoint.outCount++;
    } else {
      outPoint = new OutPoint(rightLine.rightNode);
      outPointMap.put(rightLine.rightNode.c, outPoint);
    }

    rightLines.put(rightLine.index, rightLine);
    return outPoint;
  }

  /**
   * 连接左侧对接的连线。
   * @param leftLine 左侧连线。
   * @return 返回连接字符节点的接入点。
   */
  InPoint connectLeftLine(Line leftLine) {
    // 获取或创建接入点
    InPoint inPoint;
    if ((inPoint = inPointMap.get(leftLine.leftNode.c)) != null) {
      inPoint.inCount++;
    } else {
      inPoint = new InPoint(leftLine.leftNode);
      inPointMap.put(leftLine.leftNode.c, inPoint);
    }

    leftLines.put(leftLine.index, leftLine);
    return inPoint;
  }

  /**
   * 检查字符 c 是否中文字符。
   * @param c 字符。
   * @return 中文字符返回 true。
   */
  private static boolean isChineseByBlock(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
      || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
      || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
      || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C//jdk1.7
      || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D//jdk1.7
      || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
      || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
  }

  /**
   * 检查字符 c 是否停止字符。
   * @param c 字符。
   * @return 停止字符返回 true。
   */
  private static boolean isStopWord(char c) {
    return stopWords.contains(c);
  }
}
