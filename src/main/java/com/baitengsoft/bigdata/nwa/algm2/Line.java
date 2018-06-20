package com.baitengsoft.bigdata.nwa.algm2;

/**
 * 表示文本网络中连接字符节点的一条连线。
 *
 * @author xupeng 2018-6-12
 */
class Line {

  int index; // 根据文本顺序的连线序号
  int leftCharIndex; // 左侧连接的字符在文本中的序号
  int rightCharIndex; // 右侧连接的字符在文本中的序号
  Node leftNode; // 左侧连接的字符节点
  Node rightNode; // 右侧连接的字符节点
  Node.OutPoint outPoint; // 左侧连接的字符节点的接出点
  Node.InPoint inPoint; // 右侧连接的字符节点的接入点
  double weight; // 连线权重

  /**
   * 初始化连线。
   * @param index 连线序号。
   * @param leftCharIndex 左侧字符序号。
   * @param leftNode 左侧字符节点。
   * @param rightCharIndex 右侧字符序号。
   * @param rightNode 右侧字符节点。
   */
  Line(int index, int leftCharIndex, Node leftNode, int rightCharIndex, Node rightNode) {
    this.index = index;
    this.leftCharIndex = leftCharIndex;
    this.leftNode = leftNode;
    this.rightCharIndex = rightCharIndex;
    this.rightNode = rightNode;

    outPoint =leftNode.connectRightLine(this);
    inPoint = rightNode.connectLeftLine(this);
  }
}
