package com.baitengsoft.bigdata.nwa.algm2;

import java.util.*;

/**
 * 描述一个由字符节点和连线组成的文本网络。
 *
 * @author xupeng 2018-6-12
 */
class Graph {

  private String page;
  private Map<Character, Node> nodeMap; // 文本网络字符节点集合：<字符，字符节点>
  private List<Line> lines; // 文本网络连线集合

  private static final double S1 = 0.04; // 表示连线有效取值的限值
  private static final double S2 = 0.05734; // 表示连线连词取值的限值
  private static final double S3 = 0.1745; // 表示连线差值判别的限值

  private int maxInFrequency = 0; // 文本网络中接入频率（接入点的接入次数）的最大值
  private int maxOutFrequency = 0; // 文本网络中接出频率（接出点的接出次数）的最大值
  private int maxInPointCount = 0; // 文本网络中字符节点拥有接入点的最大值
  private int maxOutPointCount = 0; // 文本网络中字符节点拥有接出点的最大值

  // 词组及边界集合：<词组，边界数组>
  // 边界数组每两个数为一组，分别词组在文本中的左边界和右边界索引
  private Map<String, ArrayList<Integer>> words;
  private Map<String, String> bounds; // 词组边界集合：<边界值，词组>
  private Set<String> wordSet; // 有序词组集合。

  /**
   * 初始化文本网络。
   */
  Graph () {
    nodeMap = new HashMap<>();
    lines = new ArrayList<>();
    words = new HashMap<>();
    bounds = new HashMap<>();
    wordSet = new TreeSet<>();
  }

  /**
   * 清空文本网络，以便重新启动新词发现过程。
   */
  void clear() {
    page = "";
    nodeMap.clear();
    lines.clear();
    words.clear();
    bounds.clear();
    wordSet.clear();
    maxInFrequency = 0;
    maxOutFrequency = 0;
    maxInPointCount = 0;
    maxOutPointCount = 0;
  }

  /**
   * 获取文本中的新发现词组。
   * @param page 文本。
   * @return 返回词组。
   */
  List<String> getNewWords(String page) {
    this.page = page;

    // 1、清洗文本
    cleanPage();
    // 2、构建文本网络
    buildStringToGraph();
    // 3、计算权重
    computeWeight();
    // 4、发现词组
    discoverWords3();
    // 5、清洗词组
    cleanWords();

    return new ArrayList<>(wordSet);
  }

  List<Line> getLines() {
    return lines;
  }

  /**
   * 清洗文本，消除空白、换行符对新词发现的影响。
   */
  private void cleanPage() {
    page = page.replaceAll("[' '|'　'|'\\r'|'\\n']", "");
  }

  /**
   * 从文本内容构建文本网络。
   * <li>nodeMap 存储文本网络中所有的字符节点（不重复）。</li>
   * <li>lines 存储文本网络中所有连线（保留从 A 节点到 B 节点的多条连线）。</li>
   */
  private void buildStringToGraph() {
    int pageCharIndex = 0;
    int lineIndexCounter = 1;
    int preCharIndex = 0;
    Node preNode = null;

    // 遍历文本内容的每个字符以构建文本网络
    while (pageCharIndex < page.length()) {
      char c = page.charAt(pageCharIndex);

      // 获取或创建字符节点
      Node curNode;
      if ((curNode = nodeMap.get(c)) == null) {
        curNode = new Node(c);
        nodeMap.put(c, curNode);
      }

      // 存在前续字符节点，则创建一条从前续字符节点连接到当前字符节点的连接
      if (preNode != null) {
        // 仅当当前字符节点和前续字符节点都是汉字字符时才创建连线
        if (curNode.isChinese && preNode.isChinese) {

          Line line = new Line(lineIndexCounter++, preCharIndex, preNode, pageCharIndex, curNode);
          lines.add(line);

          // 计算文本网络的最大接入频率和最大接出频率
          if (line.inPoint.inCount > maxInFrequency)
            maxInFrequency = line.inPoint.inCount;
          if (line.outPoint.outCount > maxOutFrequency)
            maxOutFrequency = line.outPoint.outCount;

          // 计算文本网络中字符节点接入点和接出点数量的最大值
          if (line.leftNode.outPointMap.size() > maxOutPointCount)
            maxOutPointCount = line.leftNode.outPointMap.size();
          if (line.rightNode.inPointMap.size() > maxInPointCount)
            maxInPointCount = line.rightNode.inPointMap.size();

        } else {
          // 未创建的连线占用连线序号，可用于判断词组边界
          lineIndexCounter++;
        }
      }

      // 准备处理下一个字符
      preCharIndex = pageCharIndex;
      preNode = curNode;
      pageCharIndex++;
    }
  }

  /**
   * 计算当前文本网络中字符节点和连线的权重。
   */
  private void computeWeight() {
    // 计算字符节点接入点和接出点的权重，以及字符节点的最大接入权重和最大接出权重
    for (Node node : nodeMap.values()) {
      double maxNodeInWeight = 0.0;
      double maxNodeOutWeight = 0.0;

      // 处理 node 接入点的权重
      int nodeInPointCount = node.inPointMap.size();
      int nodeLeftLineCount = node.leftLines.size();
      for (Node.InPoint inPoint : node.inPointMap.values()) {
        int thisInFrequency = inPoint.inCount;

        inPoint.weight = ((double)thisInFrequency / (double)nodeLeftLineCount) *
                (Math.log(thisInFrequency) / Math.log(maxInFrequency)) /
                ((double)nodeInPointCount / (double)maxInPointCount + 1);

        if (inPoint.weight > maxNodeInWeight)
          maxNodeInWeight = inPoint.weight;
      }

      // 处理 node 接出点的权重
      int nodeOutPointCount = node.outPointMap.size();
      int nodeRightLineCount = node.rightLines.size();
      for (Node.OutPoint outPoint : node.outPointMap.values()) {
        int thisOutFrequency = outPoint.outCount;

        outPoint.weight = ((double)thisOutFrequency / (double)nodeRightLineCount) *
                (Math.log(thisOutFrequency) / Math.log(maxOutFrequency)) /
                ((double)nodeOutPointCount / (double)maxOutPointCount + 1);

        if (outPoint.weight > maxNodeOutWeight)
          maxNodeOutWeight = outPoint.weight;
      }
    }

    // 计算连线的权重
    for (Line line : lines) {
      line.weight = Math.min(line.inPoint.weight, line.outPoint.weight);
    }
  }

  /**
   * 扫描文本网络（连线）并发现词组。
   */
  private void discoverWords() {
    if (lines.size() == 0)
      return;

    Line preLine = null;
    int scanLeftBound = 0, scanRightBound = 1; // 词组发现过程中控制扫描左右边界

    for (Line line : lines) {

      // 如果连线的权重小于 0.04，或者连线与前续连续的序号间隔大于 1，则连线对连接词组无价值
      if (line.weight < 0.04 || (preLine != null && line.index - preLine.index > 1)) {

        // 如果连线与前续连线相隔大于 1，且前续连线权重大于 0.045，则提交截止前续连线的词组
        if (preLine != null && line.index - preLine.index > 1 && preLine.weight >= 0.045) {
          preLine.rightNode.isRightBound = true;
          addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                  scanLeftBound, scanRightBound);
        }
        // 如果连线与前续连线相隔等于 1，且当前连线权重小于前续连线权重超过 0.045，则提交截止前续连线的词组
        else if (preLine != null && line.index - preLine.index == 1 && line.weight - preLine.weight < /*-0.1745*/-0.045) {
          line.leftNode.isRightBound = true;
          addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                  scanLeftBound, scanRightBound);
        }

        // 重置当前词组扫描
        // 对于首条连线直接重置词组扫描（preLine == null）
        scanLeftBound = line.leftCharIndex;
        scanRightBound = line.rightCharIndex;
      }
      // 如果连线的权重大于等于 0.045，则连线对连接词组有价值：
      else if (line.weight >= /*0.05734*/0.045) {

        // 如果连线与前续连线相隔等于 1，且当前连线权重大于前续连线权重超过 0.045，且前续连线权重小于 0.04，则确定词组左边界
        if (preLine != null && line.index - preLine.index == 1 && preLine.weight < 0.04 && line.weight - preLine.weight > /*0.1745*/0.045) {
          scanLeftBound = line.leftCharIndex;
          line.leftNode.isLeftBound = true;
        }
        // 如果连线与前续连线相隔等于 1，且当前连线权重小于前续连线权重超过 0.045，则确定词组右边界
        // 此时如提交词组，可以获得最细粒度词组
        else if ((preLine != null && line.index - preLine.index == 1 && line.weight - preLine.weight < /*-0.1745*/-0.045)) {
          line.leftNode.isRightBound = true;
          // !!!!!注释以下语句可以获取最大长度词组
          //addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
          //        scanLeftBound, scanRightBound);
        }
        // 如果连线与前续连线相隔大于 1，则确定词组左边界
        else if (preLine != null && line.index - preLine.index > 1) {
          scanLeftBound = line.leftCharIndex;
          line.leftNode.isLeftBound = true;
        }
        // 如果连线左字符节点为左边界，则确定词组左边界
        else if (line.leftNode.isLeftBound) {
          scanLeftBound = line.leftCharIndex;
        }
        // 如果连线左字符节点为右边界，此时如提交词组，可以获得最细粒度词组
        else if (line.leftNode.isRightBound) {
          // !!!!!注释以下语句可以获取最大长度词组
          //addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
          //        scanLeftBound, scanRightBound);
        }

        // 扩展词组右边界
        scanRightBound = line.rightCharIndex;
      }

      preLine = line;
    }
  }

  private void discoverWords3() {
    if (lines.size() == 0)
      return;

    Line preLine = null;
    int scanLeftBound = 0, scanRightBound = 0; // 词组发现过程中控制扫描左右边界

    for (Line line : lines) {
      if (preLine != null) {

        if (line.index - preLine.index == 1) { // 处理两条连线紧邻的情况
                                               // 这种情况下，需要结合两条连线的权重取值范围和权重差值
          int flag = 0; // 设置判别决策数，方便进行规则的组合判断

          // 识别两条连线权重的取值模式
          if (preLine.weight < S1 && line.weight < S1)
            flag |= 1;
          else if (preLine.weight < S1 && line.weight >= S1)
            flag |= 2;
          else if (preLine.weight >= S1 && line.weight < S1)
            flag |= 4;
          else if (preLine.weight >= S1 && line.weight >= S1)
            if (line.weight >= S2) // 连词规则
              flag |= 8;

          // 识别两条连线权重的差值模式
          double diff = line.weight - preLine.weight;
          if (diff >= S3)
            flag |= 16;
          else if (diff <= -S3)
            flag |= 32;

          switch (flag) {
            case 1: // 重置
              scanLeftBound = scanRightBound = line.rightCharIndex;
              break;
            case 2:
            case 18: // 确定词组左边界为line的左字符节点
              scanLeftBound = line.leftCharIndex;
              scanRightBound = line.rightCharIndex;
              break;
            case 0:
            case 4:
            case 16:
            //case 32:
            case 36: // 确定词组右边界为preLine的右字符节点，同时完成词组
              addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                scanLeftBound, scanRightBound);
              scanLeftBound = scanRightBound = line.rightCharIndex;
              break;
            case 8:
            case 24: // 连词，词组右边界向后扩展一个字符节点
              scanRightBound = line.rightCharIndex;
              break;
            case 32:
            case 40: // 确定词组右边界为preLine的右字符节点，提交一次词组，但继续当前词组的发现
              addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                scanLeftBound, scanRightBound);
              scanRightBound = line.rightCharIndex;
              break;
            default: // 这里不应该出现其它状态
              throw new IllegalStateException();
          }

        } else { // 处理两条连线分隔的情况

          // 先检查前续连线是否需要完成词组
          if (preLine.weight >= S1) {
            addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
              scanLeftBound, scanRightBound);
          }

          // 再检查当前连线权重重新确定词组左边界
          if (line.weight >= S1) {
            scanLeftBound = line.leftCharIndex;
            scanRightBound = line.rightCharIndex;
          } else {
            scanLeftBound = scanRightBound = line.rightCharIndex;
          }
        }

      } else {
        // 处理第一条连线，根据连线权重确定词组左边界
        if (line.weight >= S1) {
          scanLeftBound = line.leftCharIndex;
          scanRightBound = line.rightCharIndex;
        } else {
          scanLeftBound = scanRightBound = line.rightCharIndex;
        }
      }

      preLine = line;
    }
  }

  private void discoverWords2() {
    if (lines.size() == 0)
      return;

    Line preLine = null;
    int scanLeftBound = 0, scanRightBound = 1; // 词组发现过程中控制扫描左右边界

    for (Line line : lines) {

      if (preLine != null) {

        if (line.index - preLine.index == 1) {

          if (line.weight >= S2 && preLine.weight < S1) {
            line.leftNode.isLeftBound = true;
            scanLeftBound = line.leftCharIndex;
            scanRightBound = line.rightCharIndex;
            preLine = line;
            continue;

          } else if (line.weight < S1 && preLine.weight >= S2) {
            addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                    scanLeftBound, scanRightBound);
            line.leftNode.isRightBound = true;
            scanLeftBound = line.leftCharIndex;
            scanRightBound = line.rightCharIndex;
            preLine = line;
            continue;

          } else if (line.weight - preLine.weight >= S3) {
            line.leftNode.isLeftBound = true;
            scanLeftBound = line.leftCharIndex;
            scanRightBound = line.rightCharIndex;
            preLine = line;
            continue;

          } else if (preLine.weight - line.weight >= S3) {
            //addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
            //        scanLeftBound, scanRightBound);
            //line.leftNode.isRightBound = true;

            if (line.weight >= S2) {
              scanRightBound = line.rightCharIndex;
            } else if (line.weight < S1) {
              addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                      scanLeftBound, scanRightBound);
              line.leftNode.isRightBound = true;
            }
            else {
              scanLeftBound = line.leftCharIndex;
              scanRightBound = line.rightCharIndex;
            }
            preLine = line;
            continue;

          } else if (line.weight >= S2 && preLine.weight >= S2) {
            scanRightBound = line.rightCharIndex;
            preLine = line;
            continue;

          }

        } else {

          if (preLine.weight >= S2) {
            addNewWord(page.substring(scanLeftBound, scanRightBound + 1),
                    scanLeftBound, scanRightBound);
            preLine.rightNode.isRightBound = true;
          }
        }
      }

      preLine = line;
      scanLeftBound = line.leftCharIndex;
      scanRightBound = line.rightCharIndex;
    }
  }

  /**
   * 将发现的词组添加到内部集合，仅供 {@link #discoverWords()} 方法调用。
   * @param word 词组文本。
   * @param leftBound 词组在 page 中的左边界。
   * @param rightBound 词组在 page 中的右边界。
   */
  private void addNewWord(String word, int leftBound, int rightBound) {
    // 只有一个字符不作为词组
    if (leftBound == rightBound)
      return;

    ArrayList<Integer> wordBoundsArray = words.computeIfAbsent(word, k -> new ArrayList<>());
    wordBoundsArray.add(leftBound);
    wordBoundsArray.add(rightBound);
    bounds.put(leftBound + "," + rightBound, word);
    wordSet.add(word);
  }

  /**
   * 清洗发现的词组。
   */
  private void cleanWords() {

  }
}
