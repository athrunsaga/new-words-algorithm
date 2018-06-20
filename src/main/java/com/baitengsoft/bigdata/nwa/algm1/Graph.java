package com.baitengsoft.bigdata.nwa.algm1;

//import org.ansj.domain.Result;
//import org.ansj.domain.Term;
//import org.ansj.splitWord.analysis.ToAnalysis;
//import org.nlpcn.commons.lang.tire.GetWord;
//import org.nlpcn.commons.lang.tire.domain.Forest;
//import org.nlpcn.commons.lang.tire.library.Library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Graph {

    List<Node> cleanNode_list = new ArrayList<>();
    List<Edge> cleanEage_list = new ArrayList<>();
    List<String> wordList = new ArrayList<>();
    List<String> next_char_list = new ArrayList<>();
    List<String> wordList_sort = new ArrayList<>();

//    private static Map<String, Forest> CACHE_FOREST = new HashMap<>();


    //static String page;

    public List<String> createGraph(String page) throws Exception {

        int Node_num;
        int Edge_num;
        List<Edge> edge_list = new ArrayList<>();
        List<Node> node_list = new ArrayList<>();


        //扫描每个字 建立文档图
        int i = 0;
        int edge_num = 1;
        int node_num = 0;
        boolean stop = false;
        boolean stop_word = false;
        boolean stopped = false;
        while (i < page.length()) {
            char ch = page.charAt(i);
            //System.out.println(ch);
            if (edge_num == 36) {
                boolean s = true;
            }
            Node node = new Node();
            Edge pre_edge = new Edge();
            //若此字符是#或者换行符，或者前一个字符是#或者换行符，则此节点无入边
            if (i == 0 || page.charAt(i - 1) == '#' || ch == '#' || page.charAt(i - 1) == '\n' || ch == '\n') {
                pre_edge = null;
                if (edge_num > 1) {
                    stop = true;
                }


                if (stop && !stopped) {
                    edge_num++;
                    stopped = true;
                }

                if (i > 1 && ((ch == '#') || ch == '\n') && (page.charAt(i - 1) != '#' && page.charAt(i - 1) != '\n') && (page.charAt(i - 2) == '#' || page.charAt(i - 2) == '\n')) {
                    edge_num++;
                }
            }


            //当有了第一个节点后， 或到了文档最后一个字， 或者该节点无入边
            if ((node_list.size() > 0 || i == page.length() - 1) && pre_edge != null) {
                //Edge pre_edge = new Edge();
                pre_edge.set_index(edge_num++);
                stopped = false;
                pre_edge.setNext_node(node);//此时生成的边的下一节点是此时生成的节点
                pre_edge.setPre_node(node_list.get(node_num - 1));//上一节点是节点列表在上一次循环中最新保存好的节点

                edge_list.add(pre_edge);
                if (edge_num > 1) {
                    //下一节点的入边生成好之后，再赋值给上一节点的出边
                    node_list.get(node_num - 1).setNext_edge(pre_edge);
                    //即，下一节点无入边，则上一节点无出边
                }
            }

            node.Add_node(ch, pre_edge);
            if (node.text != '#' && node.text != '\n') {
                node_list.add(node);
                node.setNode_index(i);
                node_num++;
            }
            i++;
        }

        //输出每个边对象
        Iterator iterator = edge_list.iterator();
        while (iterator.hasNext()) {
            Edge edge = (Edge) iterator.next();
            //System.out.println("第" + edge.index + "条边：" + " " + "前一节点=" + "[" + edge.pre_node.text + "]" + " " + "后一节点=" + "[" + edge.next_node.text + "]");
        }


        cleanGraph(node_list);
        cleanEdge();


        Classification_for_NodeEdgeList();

        set_weightOfEdegs();
        set_weightOfNode();

        List<int[]> word_num_list = inputResult();
        List<String> wordList_Fianl = cleanResult(word_num_list, page);

//        wordList_Fianl = cleanExist("library/default.dic", wordList_Fianl);
//        wordList_Fianl = cleanExist("library/Copy of keyWords.txt", wordList_Fianl);

        return wordList_Fianl;
    }


//    List<String> cleanExist(String f, List<String> wordList_Fianl) throws Exception {
//        // 先从内存中拿
//        Forest forest = CACHE_FOREST.get(f);
//        // 设置缓存
//        if(forest == null){
//            FileInputStream fin = new FileInputStream(f);
//            InputStreamReader reader = new InputStreamReader(fin);
//            forest = Library.makeForest(new BufferedReader(reader));
//            CACHE_FOREST.put(f, forest);
//        }
//
//        //输出最终结果
//        List<String> fianl = new ArrayList<>();
//        fianl.addAll(wordList_Fianl);
//        for (String str : wordList_Fianl) {
//            GetWord udg = forest.getWord(str);
//            String temp = null;
//            List<String> alltemp = new ArrayList<>();
//            while ((temp = udg.getAllWords()) != null) {
//                alltemp.add(temp);
//            }
//            if (alltemp.contains(str)) {
//                //在词库里有的
//                //System.out.println(str);
//                fianl.remove(str);
//            }
//        }
//        return fianl;
//    }


    List<String> cleanResult(List<int[]> word_num_list0, String page) throws Exception {

        List<String> wordlist1 = new ArrayList<>();
        List<String> wordlist2 = new ArrayList<>();
        List<int[]> word_num_list1 = new ArrayList<>();

        int i0 = 0;
        while ((i0 < wordList.size())) {
            String str = wordList.get(i0);
            String next_char = next_char_list.get(i0);
            int[] num = word_num_list0.get(i0);
            if (str.length() >= 2 &&
                    (!next_char.equals("给") && !next_char.equals("了") && !next_char.equals("到") && !next_char.equals("有") && !next_char.equals("在") && !next_char.equals("设")
                            && !next_char.equals("于"))) {
                wordlist1.add(str);
                word_num_list1.add(num);
            }
            i0++;
        }

        List<int[]> word_num_list = new ArrayList<>();

        int i = 0;
        int j, put_j;
        j = i + 1;
        put_j = j;
        boolean have = false;
        boolean stop = false;

        while (i < word_num_list1.size()) {

            String str = wordlist1.get(i);
            if (str.equals("突变功率增量与")) {
                boolean r = true;
            }
            int fre = 0;
            fre = Collections.frequency(wordlist1, str);

            //System.out.println(str +":"+ fre);
            if (j < word_num_list1.size() && (word_num_list1.get(j)[0] == (word_num_list1.get(i))[0])) {
                String strj = wordlist1.get(j);
                if (word_num_list1.get(j)[1] != word_num_list1.get(i)[1] && !stop) {
                    stop = true;
                    if (word_num_list1.get(j)[1] > word_num_list1.get(i)[1]) {
                        put_j = j;
                    } else {
                        put_j = j + 1;
                    }
                }
                int frej = 0;
                frej = Collections.frequency(wordlist1, strj);


                j++;

                if (frej <= 2 || (double) frej / (double) fre < 0.03 || stop) {
                    put_j = put_j;
                    stop = true;
                } else {
                    put_j = j;
                }
                have = true;
                continue;
            } else {
                if (have) {

                    wordlist2.add(wordlist1.get(put_j - 1));
                    if ((word_num_list1.get(put_j - 1)[1] - word_num_list1.get(put_j - 1)[0] + 1) != wordlist1.get(put_j - 1).length()) {
                        word_num_list1.get(put_j - 1)[1] = word_num_list1.get(put_j - 1)[0] + wordlist1.get(put_j - 1).length() - 1;
                    }
                    word_num_list.add(word_num_list1.get(put_j - 1));

                    i = j;
                    j += 1;
                    have = false;
                    stop = false;
                } else {

                    wordlist2.add(wordlist1.get(i));
                    word_num_list.add(word_num_list1.get(i));

                    i++;
                    j = i + 1;
                    stop = false;
                }
            }

            if (j >= word_num_list1.size()) {
                break;
            }

        }

        List<String> wordlist3 = new ArrayList<>();
        List<int[]> word_num_list2 = new ArrayList<>();


        i = 0;
        j = i + 1;
        boolean scan = false;
        have = false;
        String str = "";
        int s = 0;
        while (i < wordlist2.size()) {
            if (!scan) {
                String str1 = wordlist2.get(i);
                str = "" + str1;
                s = word_num_list.get(i)[0];
                scan = true;
            }

            if (word_num_list.get(j - 1)[1] == (word_num_list.get(j)[0] - 1)) {
                String str2 = wordlist2.get(j);
                str = str + str2;

                j++;
                have = true;
            } else {
                if (have) {

                    wordlist3.add(str);
                    int[] temp_num = new int[2];
                    temp_num[0] = s;
                    temp_num[1] = word_num_list.get(j - 1)[1];
                    word_num_list2.add(temp_num);
                    i = j;
                    j += 1;
                    have = false;
                    scan = false;
                } else {
                    wordlist3.add(str);
                    word_num_list2.add(word_num_list.get(i));
                    i++;
                    j = i + 1;
                    scan = false;
                }

            }

            if (j >= wordlist2.size()) {
                break;
            }
        }

        i = 0;
        while (i < wordlist3.size()) {
            wordList_sort.add(wordlist3.get(i));
            i++;
        }

        List<String> wordlist3_1 = new ArrayList<>();
        wordlist3_1.addAll(wordlist3);

//        wordlist3 = comparison(word_num_list2, page);
        wordlist3 = cleanResult_1(wordlist3);


        List<String> wordlist4 = new ArrayList<>();
        for (String the_str : wordlist3) {
            wordlist4.add(the_str);
        }

        for (String the_str : wordlist3) {

            int fre = Collections.frequency(wordlist3_1, the_str);
            if (the_str.length() < 2 || (the_str.length() >= 9 && fre <= 3)) {
                wordlist4.remove(the_str);
            } else if (fre <= 1) {
                wordlist4.remove(the_str);
            }
        }

        List<String> wordlist_final = new ArrayList<>();
        for (String the_str : wordlist4) {
            if (!wordlist_final.contains(the_str)) {
                wordlist_final.add(the_str);
            }
        }

        return wordlist_final;

    }

    List<String> cleanResult_1(List<String> wordlist) {
        List<String> wordlist3 = new ArrayList<>();
        for (String str : wordlist) {

            Pattern pattern1 = Pattern.compile("..个");
            str = pattern1.matcher(str).replaceAll("#");

            Pattern pattern1_1 = Pattern.compile("..种");
            str = pattern1_1.matcher(str).replaceAll("#");

            Pattern pattern2 = Pattern.compile("第.");
            str = pattern2.matcher(str).replaceAll("#");
            Pattern pattern2_1 = Pattern.compile("若干.");
            str = pattern2_1.matcher(str).replaceAll("#");

            Pattern pattern3 = Pattern.compile(".个");
            str = pattern3.matcher(str).replaceAll("#");
            Pattern pattern3_1 = Pattern.compile(".种");
            str = pattern3_1.matcher(str).replaceAll("#");
            Pattern pattern3_2 = Pattern.compile(".次");
            str = pattern3_2.matcher(str).replaceAll("#");

            Pattern pattern4 = Pattern.compile("个");
            str = pattern4.matcher(str).replaceAll("#");
            Pattern pattern4_1 = Pattern.compile("种");
            str = pattern4_1.matcher(str).replaceAll("#");
            Pattern pattern4_2 = Pattern.compile("若干");
            str = pattern4_2.matcher(str).replaceAll("#");
            Pattern pattern4_3 = Pattern.compile("所述");
            str = pattern4_3.matcher(str).replaceAll("#");

            Pattern pattern5 = Pattern.compile("固定在");
            str = pattern5.matcher(str).replaceAll("#");
            Pattern pattern5_1 = Pattern.compile("安装在");
            str = pattern5_1.matcher(str).replaceAll("#");
            Pattern pattern5_2 = Pattern.compile("安装有");
            str = pattern5_2.matcher(str).replaceAll("#");
            Pattern pattern5_3 = Pattern.compile("是否有");
            str = pattern5_3.matcher(str).replaceAll("#");
            Pattern pattern5_4 = Pattern.compile("连接到");
            str = pattern5_4.matcher(str).replaceAll("#");
            Pattern pattern5_5 = Pattern.compile("插入到");
            str = pattern5_5.matcher(str).replaceAll("#");
            Pattern pattern5_6 = Pattern.compile("安装于");
            str = pattern5_6.matcher(str).replaceAll("#");

            if (str.contains("#")) {
                if (str.charAt(0) == '#' || str.charAt(str.length() - 1) == '#') {
                    if (!str.equals("#")) {
                        Pattern pattern = Pattern.compile("#");
                        str = pattern.matcher(str).replaceAll("");
                        wordlist3.add(str);
                    }
                }
            } else {
                wordlist3.add(str);
            }
        }
        return wordlist3;
    }

    List<int[]> inputResult() {

        List<int[]> word_ind_list = new ArrayList<>();

        int num = 0;
        String str = "";
        char ch1;
        Edge pre_e = new Edge();
        Edge next_e = new Edge();
        boolean stop = true;
        int stop_count = 0;
        boolean conti = false;

        for (Edge e : cleanEage_list) {
            if (e.index == 1) {
                pre_e = e;
                break;
            }
        }

        int[] temp_num = new int[2];
        int temp_num_ind = 0;

        int i = 1;
        int j = 0;

        for (Edge e : cleanEage_list) {
            i = e.index;

            if (j != (cleanEage_list.size() - 1)) {
                next_e = cleanEage_list.get(j + 1);
                j++;
                if ((next_e.index - e.index - 1) > 1) {
                    stop_count += (next_e.index - e.index - 2);
                }
            }

            if (e.weight < 0.04) {
                if (!(e.index == (pre_e.index + 1))) {
                    stop_count++;
                }

                pre_e = e;
                str = "";
                stop = true;
                temp_num_ind = 0;
                temp_num = new int[2];
                temp_num[0] = i + stop_count;
                continue;
            }


            if (i == 1) {
                e.pre_node.setFlag(1);
                temp_num[0] = i + stop_count - 1;
                //pre_char_list.add(String.valueOf('#'));
            }


            if (!(e.index == (next_e.index - 1))) {
                e.next_node.setFlag(2);
            } else {
                if ((next_e.weight - e.weight) > 0.1745) {
                    e.next_node.setFlag(1);
                }
                if ((next_e.weight - e.weight) < -0.1745) {
                    e.next_node.setFlag(2);
                }
            }


            if (i > 1) {
                if (!(e.index == (pre_e.index + 1))) {
                    // 结束一个词
                    if (str.length() >= 2 && !wordList.contains(str)) {
                        wordList.add(str);
                        next_char_list.add(String.valueOf('#'));
                        num = num + 1;
                        temp_num[1] = temp_num[0] + temp_num_ind;
                        word_ind_list.add(temp_num);
                    }
                    stop = true;
                    stop_count += 1;
                    e.pre_node.setFlag(1);
                    str = "";
                    temp_num_ind = 0;
                    temp_num = new int[2];
                }
            }


            if (e.pre_node.flag == 1 || stop) {
                // 开始新词
                if (!conti || stop) {
                    str = str + e.pre_node.text;
                    temp_num[0] = i + stop_count - 1;
                }

                stop = false;

                if (e.next_node.flag == 2) {

                    str = str + e.next_node.text;
                    temp_num_ind += 1;

                    if (str.length() >= 2) {
                        wordList.add(str);
                        if (next_e.index == (e.index + 1)) {
                            next_char_list.add(String.valueOf(next_e.next_node.text));
                        } else {
                            next_char_list.add(String.valueOf('#'));
                        }
                        num = num + 1;
                        temp_num[1] = temp_num[0] + temp_num_ind;
                        word_ind_list.add(temp_num);
                    }
                } else if (e.next_node.flag == 1) {
                    if (e.weight >= 0.05734) {

                        str = str + e.next_node.text;
                        temp_num_ind += 1;
                        conti = true;
                    } else {
                        str = "";
                        temp_num_ind = 0;
                        temp_num = new int[2];
                        temp_num[0] = i + stop_count;
                        conti = false;
                    }
                } else {
                    str = str + e.next_node.text;
                    temp_num_ind += 1;
                }
            } else if (e.pre_node.flag == 2) {

                if (str.equals("")) {
                    temp_num[0] = i + stop_count;
                }

                //char ch = wordList.get(num-1).charAt(wordList.get(num-1).length()-1);
                if (e.next_node.flag == 2) {

                    if (e.weight >= 0.05734) {
                        str = str + e.next_node.text;
                        temp_num_ind += 1;
                        conti = true;
                        if (str.length() >= 2) {
                            wordList.add(str);
                            if (next_e.index == (e.index + 1)) {
                                next_char_list.add(String.valueOf(next_e.next_node.text));
                            } else {
                                next_char_list.add(String.valueOf('#'));
                            }
                            num = num + 1;
                            temp_num[1] = temp_num[0] + temp_num_ind;
                            word_ind_list.add(temp_num);
                        }
                    } else {
                        str = "";
                        temp_num_ind = 0;
                        temp_num = new int[2];
                        temp_num[0] = i + stop_count;
                        conti = false;

                        //str = "";
                    }
                } else if (e.next_node.flag == 1) {

                    if (e.weight >= 0.05734) {
                        str = str + e.next_node.text;
                        temp_num_ind += 1;
                        conti = true;
                    } else {
                        str = "";
                        temp_num_ind = 0;
                        temp_num = new int[2];
                        temp_num[0] = i + stop_count;
                        conti = false;
                    }
                } else {
                    if (e.weight >= 0.05734) {
                        str = str + e.next_node.text;
                        temp_num_ind += 1;
                        conti = true;
                    } else {
                        str = "";
                        temp_num_ind = 0;
                        temp_num = new int[2];
                        temp_num[0] = i + stop_count;
                        conti = false;
                    }
                }
            } else {
                if (str.equals("")) {
                    temp_num[0] = i + stop_count;
                }

                if (e.next_node.flag == 2) {
                    str = str + e.next_node.text;
                    temp_num_ind += 1;
                    if (str.length() >= 2) {
                        wordList.add(str);
                        if (next_e.index == (e.index + 1)) {
                            next_char_list.add(String.valueOf(next_e.next_node.text));
                        } else {
                            next_char_list.add(String.valueOf('#'));
                        }
                        num = num + 1;
                        temp_num[1] = temp_num[0] + temp_num_ind;
                        word_ind_list.add(temp_num);
                        //temp_num = new int[2]
                    }
                } else if (e.next_node.flag == 1) {
                    if (e.weight >= 0.05734) {
                        str = str + e.next_node.text;
                        temp_num_ind += 1;
                        conti = true;
                    } else {
                        str = "";
                        temp_num_ind = 0;
                        temp_num = new int[2];
                        temp_num[0] = i + stop_count;
                        conti = false;
                    }
                } else {
                    str = str + e.next_node.text;

                    if (str.length() > 1) {
                        temp_num_ind += 1;
                    }
                }
            }

            pre_e = e;
        }

        i = 0;
        while (i < wordList.size()) {

            String tempstr = wordList.get(i);

            int[] num0 = word_ind_list.get(i);
            Data data = new Data();
            int[] num1 = new int[2];
            num1[0] = num0[0];
            if (num0[0] == 105) {
                boolean s = true;
            }
            num1[1] = num0[1];
            data.setNum(num1);
            data.setStr(tempstr);
            data = delete_rest(data);
            if (data.num[1] != num0[1]) {
                next_char_list.set(i, "#");
            }
            wordList.set(i, data.str);
            word_ind_list.set(i, data.num);
            if ((data.num[1] <= data.num[0]) && i > 0) {
                if (data.num[0] == word_ind_list.get(i - 1)[0]) {
                    int n1 = tempstr.length() - wordList.get(i - 1).length();
                    word_ind_list.get(i - 1)[1] = word_ind_list.get(i - 1)[1] - n1;
                }
            }

            i++;
        }
        return word_ind_list;
    }

    Data delete_rest(Data data) {


        if (data.num[1] > data.num[0]) {

            if (data.str.length() >= 5) {

                String stre = String.valueOf(data.str.charAt(data.str.length() - 5)) + String.valueOf(data.str.charAt(data.str.length() - 4)) + String.valueOf(data.str.charAt(data.str.length() - 3)) + String.valueOf(data.str.charAt(data.str.length() - 2) + String.valueOf(data.str.charAt(data.str.length() - 1)));
                if (data.str.length() > 1 && (stre.equals("本实用新型") || stre.equals("初始位置时"))) {
                    if (data.str.replace(stre, "").length() > 1) {
                        int ind = data.str.lastIndexOf(stre);
                        data.str = data.str.substring(0, ind);
                    } else {
                        data.str = data.str.replace(stre, "");
                    }
                    data.changeNum1(data.num[1] - 5);
                    data = delete_rest(data);
                }
                if (data.str.length() >= 5) {
                    String strs = String.valueOf(data.str.charAt(0)) + String.valueOf(data.str.charAt(1)) + String.valueOf(data.str.charAt(2)) + String.valueOf(data.str.charAt(3) + String.valueOf(data.str.charAt(4)));
                    if (data.str.length() > 1 && (strs.equals("本实用新型") || strs.equals("大大增加了") || strs.equals("初始位置时"))) {
                        if (data.str.replace(strs, "").length() > 1) {
                            int ind = data.str.indexOf(strs);
                            data.str = data.str.substring(ind + 5, data.str.length());
                        } else {
                            data.str = data.str.replace(strs, "");
                        }
                        data.changeNum0(data.num[0] + 5);
                        data = delete_rest(data);
                    }
                }

            }


            if (data.str.length() >= 4) {

                String stre = String.valueOf(data.str.charAt(data.str.length() - 4)) + String.valueOf(data.str.charAt(data.str.length() - 3)) + String.valueOf(data.str.charAt(data.str.length() - 2)) + String.valueOf(data.str.charAt(data.str.length() - 1));
                if (data.str.length() > 1 && (stre.equals("实用新型") || stre.equals("权利要求") || stre.equals("固定连接") || stre.equals("分别设在") || stre.equals("分别对应") || stre.equals("技术方案") || stre.equals("进一步地")
                        || stre.equals("共同组成") || stre.equals("有效防止"))) {
                    if (data.str.replace(stre, "").length() > 1) {
                        int ind = data.str.lastIndexOf(stre);
                        data.str = data.str.substring(0, ind);
                    } else {
                        data.str = data.str.replace(stre, "");
                    }
                    data.changeNum1(data.num[1] - 4);
                    data = delete_rest(data);
                }
                if (data.str.length() >= 4) {
                    String strs = String.valueOf(data.str.charAt(0)) + String.valueOf(data.str.charAt(1)) + String.valueOf(data.str.charAt(2)) + String.valueOf(data.str.charAt(3));
                    if (data.str.length() > 1 && (strs.equals("实用新型") || strs.equals("权利要求") || strs.equals("固定连接") || strs.equals("分别设在") || strs.equals("分别对应") || strs.equals("技术方案")
                            || strs.equals("共同组成") || strs.equals("有效防止"))) {
                        if (data.str.replace(strs, "").length() > 1) {
                            int ind = data.str.indexOf(strs);
                            data.str = data.str.substring(ind + 4, data.str.length());
                        } else {
                            data.str = data.str.replace(strs, "");
                        }
                        data.changeNum0(data.num[0] + 4);
                        data = delete_rest(data);
                    }
                }

            }

            if (data.str.length() >= 3) {

                String stre = String.valueOf(data.str.charAt(data.str.length() - 3)) + String.valueOf(data.str.charAt(data.str.length() - 2)) + String.valueOf(data.str.charAt(data.str.length() - 1));
                if (data.str.length() > 1 && (stre.equals("设置在") || stre.equals("设置有") || stre.equals("设置为") || stre.equals("将所述") || stre.equals("其特征") || stre.equals("发送给") || stre.equals("连接有") || stre.equals("连接在") || stre.equals("上连接")
                        || stre.equals("相连接") || stre.equals("就可以") || stre.equals("与剩余") || stre.equals("该装置") || stre.equals("适用于") || stre.equals("周期长") || stre.equals("周期短") || stre.equals("配置成") || stre.equals("输入到")
                        || stre.equals("一致时") || stre.equals("一起时") || stre.equals("本实用") || stre.equals("本发明") || stre.equals("优选地") || stre.equals("上留有") || stre.equals("相对应") || stre.equals("放置在") || stre.equals("插入到")
                        || stre.equals("相匹配") || stre.equals("本公开") || stre.equals("分布在") || stre.equals("节省了") || stre.equals("最后用") || stre.equals("然后用") || stre.equals("如权利") || stre.equals("缓存在") || stre.equals("进一步")
                        || stre.equals("以便在") || stre.equals("分别沿") || stre.equals("上增加") || stre.equals("或所述") || stre.equals("条件下") || stre.equals("增加了") || stre.equals("提高了") || stre.equals("还提供") || stre.equals("避免了")
                        || stre.equals("材质为") || stre.equals("于所述") || stre.equals("实施例") || stre.equals("不需要") || stre.equals("后通过") || stre.equals("排布有") || stre.equals("来实现") || stre.equals("安装于") || stre.equals("会出现")
                        || stre.equals("降低了") || stre.equals("连接于") || stre.equals("安置有") || stre.equals("设置着") || stre.equals("分别与") || stre.equals("还包括") || stre.equals("装置于") || stre.equals("相配合") || stre.equals("解决了")
                        || stre.equals("套设于") || stre.equals("来源于") || stre.equals("会造成") || stre.equals("发生时"))) {
                    if (data.str.replace(stre, "").length() > 1) {
                        int ind = data.str.lastIndexOf(stre);
                        data.str = data.str.substring(0, ind);
                    } else {
                        data.str = data.str.replace(stre, "");
                    }
                    data.changeNum1(data.num[1] - 3);
                    data = delete_rest(data);
                }
                if (data.str.length() >= 3) {
                    String strs = String.valueOf(data.str.charAt(0)) + String.valueOf(data.str.charAt(1)) + String.valueOf(data.str.charAt(2));
                    if (data.str.length() > 1 && (strs.equals("设置在") || strs.equals("设置有") || strs.equals("设置为") || strs.equals("将所述") || strs.equals("其特征") || strs.equals("在一个") || strs.equals("在多个") || strs.equals("是否将") || strs.equals("本公开")
                            || strs.equals("由以下") || strs.equals("加入到") || strs.equals("连接有") || strs.equals("连接在") || strs.equals("就可以") || strs.equals("安装在") || strs.equals("固定在") || strs.equals("该装置") || strs.equals("配置成") || strs.equals("输入到")
                            || strs.equals("适用于") || strs.equals("本实用") || strs.equals("本发明") || strs.equals("可减少") || strs.equals("优选地") || strs.equals("相对应") || strs.equals("放置在") || strs.equals("插入到") || strs.equals("相匹配") || strs.equals("分布在")
                            || strs.equals("节省了") || strs.equals("最后用") || strs.equals("然后用") || strs.equals("如权利") || strs.equals("缓存在") || strs.equals("进一步") || strs.equals("以便在") || strs.equals("分别沿") || strs.equals("上增加") || strs.equals("或所述")
                            || strs.equals("条件下") || strs.equals("增加了") || strs.equals("提高了") || strs.equals("还提供") || strs.equals("避免了") || strs.equals("材质为") || strs.equals("于所述") || strs.equals("实施例") || strs.equals("不需要") || strs.equals("后通过")
                            || strs.equals("排布有") || strs.equals("来实现") || strs.equals("安装于") || strs.equals("会出现") || strs.equals("降低了") || strs.equals("连接于") || strs.equals("安置有") || strs.equals("设置着") || strs.equals("分别与") || strs.equals("还包括")
                            || strs.equals("装置于") || strs.equals("相配合") || strs.equals("解决了") || strs.equals("套设于") || strs.equals("来源于") || strs.equals("会造成") || strs.equals("发生时"))) {
                        if (data.str.replace(strs, "").length() > 1) {
                            int ind = data.str.indexOf(strs);
                            data.str = data.str.substring(ind + 3, data.str.length());
                        } else {
                            data.str = data.str.replace(strs, "");
                        }
                        data.changeNum0(data.num[0] + 3);
                        data = delete_rest(data);
                    }
                }

            }


            if (data.str.length() >= 2) {

                String stre = String.valueOf(data.str.charAt(data.str.length() - 2)) + String.valueOf(data.str.charAt(data.str.length() - 1));
                if ((stre.equals("设有") || stre.equals("使用") || stre.equals("一种") || stre.equals("在于") || stre.equals("使得") || stre.equals("以下")
                        || stre.equals("所述") || stre.equals("根据") || stre.equals("用于") || stre.equals("包括") || stre.equals("两端") || stre.equals("是否")
                        || stre.equals("进行") || stre.equals("设为") || stre.equals("之间") || stre.equals("形成") || stre.equals("采用") || stre.equals("得到")
                        || stre.equals("具有") || stre.equals("之间") || stre.equals("产生") || stre.equals("设置") || stre.equals("适于") || stre.equals("可以")
                        || stre.equals("即可") || stre.equals("能够") || stre.equals("完成") || stre.equals("位于") || stre.equals("处于") || stre.equals("一端")
                        || stre.equals("现有") || stre.equals("装有") || stre.equals("作为") || stre.equals("当前") || stre.equals("是由") || stre.equals("不同")
                        || stre.equals("适宜") || stre.equals("彼此") || stre.equals("制成") || stre.equals("本体") || stre.equals("放入") || stre.equals("设在")
                        || stre.equals("以便") || stre.equals("并将") || stre.equals("不会") || stre.equals("有一") || stre.equals("为一") || stre.equals("涉及")
                        || stre.equals("小于") || stre.equals("大于") || stre.equals("起到") || stre.equals("接在") || stre.equals("带有") || stre.equals("以及")
                        || stre.equals("上述") || stre.equals("开设") || stre.equals("然后") || stre.equals("上安") || stre.equals("任一"))) {
                    if (data.str.replace(stre, "").length() > 1) {
                        int ind = data.str.lastIndexOf(stre);
                        data.str = data.str.substring(0, ind);
                    } else {
                        data.str = data.str.replace(stre, "");
                    }
                    data.changeNum1(data.num[1] - 2);
                    data = delete_rest(data);
                }
                if (data.str.length() >= 2) {
                    String strs = String.valueOf(data.str.charAt(0)) + String.valueOf(data.str.charAt(1));
                    if ((strs.equals("设有") || strs.equals("使用") || strs.equals("一种") || strs.equals("在于") || strs.equals("一些") || strs.equals("使得")
                            || strs.equals("所述") || strs.equals("根据") || strs.equals("用于") || strs.equals("包括") || strs.equals("以下") || strs.equals("两端")
                            || strs.equals("进行") || strs.equals("设为") || strs.equals("更为") || strs.equals("具有") || strs.equals("采用") || strs.equals("得到")
                            || strs.equals("之间") || strs.equals("或将") || strs.equals("许多") || strs.equals("上述") || strs.equals("适于") || strs.equals("可以")
                            || strs.equals("可以") || strs.equals("通过") || strs.equals("作为") || strs.equals("能够") || strs.equals("处于") || strs.equals("一端")
                            || strs.equals("完成") || strs.equals("从而") || strs.equals("因此") || strs.equals("位于") || strs.equals("装有") || strs.equals("作为")
                            || strs.equals("设于") || strs.equals("置于") || strs.equals("由以") || strs.equals("加入") || strs.equals("是由") || strs.equals("不同")
                            || strs.equals("经过") || strs.equals("这样") || strs.equals("现有") || strs.equals("适宜") || strs.equals("形成") || strs.equals("彼此")
                            || strs.equals("基于") || strs.equals("进入") || strs.equals("本体") || strs.equals("放入") || strs.equals("通过") || strs.equals("设在")
                            || strs.equals("以便") || strs.equals("并将") || strs.equals("本实") || strs.equals("不会") || strs.equals("有一") || strs.equals("为一")
                            || strs.equals("并对") || strs.equals("涉及") || strs.equals("排入") || strs.equals("起到") || strs.equals("接在") || strs.equals("带有")
                            || strs.equals("以及") || strs.equals("上述") || strs.equals("开设") || strs.equals("然后") || strs.equals("上安") || strs.equals("任一"))) {
                        if (data.str.replace(strs, "").length() > 1) {
                            int ind = data.str.indexOf(strs);
                            data.str = data.str.substring(ind + 2, data.str.length());
                        } else {
                            data.str = data.str.replace(strs, "");
                        }

                        data.changeNum0(data.num[0] + 2);
                        data = delete_rest(data);
                    }
                }

            }

            if (data.str.length() >= 1) {

                String stre = String.valueOf(data.str.charAt(data.str.length() - 1));
                if ((stre.equals("的") || stre.equals("上") || stre.equals("下") || stre.equals("中") || stre.equals("内") || stre.equals("呈"))) {
                    if (data.str.replace(stre, "").length() > 1) {
                        int ind = data.str.lastIndexOf(stre);
                        data.str = data.str.substring(0, ind);
                    } else {
                        data.str = data.str.replace(stre, "");
                    }
                    data.changeNum1(data.num[1] - 1);
                    data = delete_rest(data);
                }
                if (data.str.length() >= 1) {
                    String strs = String.valueOf(data.str.charAt(0));
                    if ((strs.equals("的") || strs.equals("及") || strs.equals("占") || strs.equals("且") || strs.equals("为") || strs.equals("在") || strs.equals("把"))) {
                        if (data.str.replace(strs, "").length() > 1) {
                            int ind = data.str.indexOf(strs);
                            data.str = data.str.substring(ind + 1, data.str.length());
                        } else {
                            data.str = data.str.replace(strs, "");
                        }
                        data.changeNum0(data.num[0] + 1);
                        data = delete_rest(data);
                    }
                }

            }
        }

        return data;

    }


    void cleanGraph(List<Node> node_list) {
        List<String> cleanNode_list_t = new ArrayList<>();

        Iterator iterator = node_list.iterator();
        while (iterator.hasNext()) {
            //node2 当前循环到的节点
            Node node2 = (Node) iterator.next();
            //node1 用来表示与node2字符内容相同的已有节点
            Node node1;
            //node2的前一节点
            Node node2_pre = new Node();
            //node2的后一节点
            Node node2_next = new Node();

            if (node2.pre_edge != null) {
                //若node2的前一节点不为空 则获取前一节点
                // node2_pre = node_list.get(node_list.indexOf(node2) - 1);
                node2_pre = node2.pre_edge.pre_node;
            }

            if (node2.next_edge != null) {
                //若node2的后一节点不为空 则获取前一节点
                //node2_next = node_list.get(node_list.indexOf(node2) + 1);
                node2_next = node2.next_edge.next_node;
            }

            String node2_text = String.valueOf(node2.text);//得到node2的字符内容


            if (!cleanNode_list_t.contains(node2_text)) {//如果这个字符节点的字符在前面循环中没有被扫描到
                cleanNode_list_t.add(node2_text);//储存到无重复字符表
                cleanNode_list.add(node2);//储存到无重复字符节点表
                node2.ind_list_Add(node2.node_index);
                if (node2.pre_edge != null) {
                    node2.Pre_edge_list_Add(node2.pre_edge);//此节点的入边储存到此节点的入边列表
                }

                if (node2.next_edge != null) {
                    node2.Next_edge_list_Add(node2.next_edge);//此节点的出边储存到此节点的出边列表
                }

            } else {////如果这个字符节点的字符在前面循环中已经被扫描过
                node1 = cleanNode_list.get(cleanNode_list_t.indexOf(node2_text));//获取字符相重复的节点
                node1.ind_list_Add(node2.node_index);

                if (node2.pre_edge != null) {//若此node2有入边
                    Edge new_pre_e = new Edge();//新建一条边
                    new_pre_e.set_index(node2.pre_edge.index);//新边接收node2入边的索引
                    new_pre_e.setNext_node(node1);//新边指向node1
                    new_pre_e.setPre_node(node2_pre);//新边的来源设置为node2的前一节点

                    node1.Pre_edge_list_Add(new_pre_e);//node1入边列表中加入新边
                    node2_pre.Next_edge_list_Add(new_pre_e);//node2前一节点的出边列表中加入新边
                    node2_pre.next_edge = new_pre_e;
                    //edge_list.add(new_pre_e);//总边表加入新边
                    node2_pre.Next_edge_list_Remove(node2.pre_edge);//node2的前一节点的出边列表中移除旧边
                    //edge_list.remove(node2.pre_edge);//总边表移除旧边
                }

                if (node2.next_edge != null) {//处理新的出边，逻辑同上
                    Edge new_next_e = new Edge();
                    new_next_e.set_index(node2.next_edge.index);
                    new_next_e.setNext_node(node2_next);
                    new_next_e.setPre_node(node1);
                    node1.Next_edge_list_Add(new_next_e);
                    node2_next.Pre_edge_list_Add(new_next_e);
                    node2_next.pre_edge = new_next_e;
                    //edge_list.add(new_next_e);
                    node2_next.Pre_edge_list_Remove(node2.next_edge);
                    //edge_list.remove(node2.next_edge);
                }


            }

        }

    }

    void cleanEdge() {
        Iterator iterator = cleanNode_list.iterator();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            List<String> ind = new ArrayList<>();

            Iterator iterator1 = node.pre_edge_list.iterator();
            while (iterator1.hasNext()) {
                Edge e = (Edge) iterator1.next();
                String index = String.valueOf(e.index);
                cleanEage_list.add(e);
            }


        }

    }


    void Classification_for_NodeEdgeList() {

        int max_infre = 0;
        int max_outfre = 0;

        int max_inSize = 0;
        int max_outSize = 0;

        for (Node node : cleanNode_list) {

            List<String> word_save1 = new ArrayList<>();
            List<String> word_save2 = new ArrayList<>();

            int ind = 0;
            for (Edge e : node.pre_edge_list) {

                if (!word_save1.contains(String.valueOf(e.pre_node.text))) {

                    word_save1.add(String.valueOf(e.pre_node.text));

                    In in = new In();
                    in.setIndex(ind++);
                    in.setPre_text(e.pre_node.text);
                    in.setFrequency(1);
                    in.Add_edgeindList(e.index);

                    node.Add_inList(in);
                    if (node.in_list.size() > max_inSize) {
                        max_inSize = node.in_list.size();
                    }


                } else {

                    int index = word_save1.indexOf(String.valueOf(e.pre_node.text));

                    In in = node.in_list.get(index);
                    in.fre_plus();
                    if (in.frequency > max_infre) {
                        max_infre = in.frequency;
                        //System.out.println(max_infre + String.valueOf(in.pre_text) + String.valueOf(node.text));

                    }
                    in.Add_edgeindList(e.index);
                }
            }

            ind = 0;

            for (Edge e : node.next_edge_list) {

                if (!word_save2.contains(String.valueOf(e.next_node.text))) {

                    word_save2.add(String.valueOf(e.next_node.text));

                    Out out = new Out();
                    out.setIndex(ind++);
                    out.setNext_text(e.next_node.text);
                    out.setFrequency(1);
                    out.Add_edgeindList(e.index);

                    node.Add_outList(out);
                    if (node.out_list.size() > max_outSize) {
                        max_outSize = node.out_list.size();
                    }

                } else {

                    int index = word_save2.indexOf(String.valueOf(e.next_node.text));

                    Out out = node.out_list.get(index);
                    out.fre_plus();
                    if (out.frequency > max_outfre) {
                        max_outfre = out.frequency;
                        //System.out.println(max_outfre + String.valueOf(node.text) + String.valueOf(out.next_text));
                    }
                    out.Add_edgeindList(e.index);
                }
            }

        }


        for (Node node : cleanNode_list) {

            double in_q2 = ((double) node.in_list.size() / (double) max_inSize) + 1;

            for (In in : node.in_list) {
                double p = (double) in.frequency / (double) node.pre_edge_list.size();
                double q1 = Math.log(in.frequency) / Math.log(max_infre);
                double weight = p * q1 / in_q2;
                in.setWeight(weight);
            }

            double out_q2 = ((double) node.out_list.size() / (double) max_outSize) + 1;

            for (Out out : node.out_list) {
                double p = (double) out.frequency / (double) node.next_edge_list.size();
                double q1 = Math.log(out.frequency) / Math.log(max_outfre);
                double weight = p * q1 / out_q2;
                out.setWeight(weight);
            }
        }

    }


    void set_weightOfEdegs() {

        for (Edge e : cleanEage_list) {

            double out_weight = 0.0;
            double in_weight = 0.0;

            if (e.next_node.text == '的' || e.pre_node.text == '的') {
                e.setWeight(0.0);
                continue;
            }

            for (Out out : e.pre_node.out_list) {
                for (String ind : out.edge_ind_list) {

                    if (String.valueOf(e.index).equals(ind)) {
                        out_weight = out.weight;
                    }

                }
            }

            for (In in : e.next_node.in_list) {
                for (String ind : in.edge_ind_list) {

                    if (String.valueOf(e.index).equals(ind)) {
                        in_weight = in.weight;
                    }

                }
            }

            double weight = Math.min(in_weight, out_weight);
            e.setWeight(weight);


        }

        List<Edge> edgeList = new ArrayList<>();
        edgeList.addAll(cleanEage_list);
        cleanEage_list = new ArrayList<>();

        int i = 0;
        int ind = 1;
        while (i < edgeList.size()) {
            for (Edge e : edgeList) {
                if (e.index == ind) {
                    cleanEage_list.add(e);
                    //System.out.println("第" + e.index + "条边： " + e.pre_node.text + " -" + e.weight + "- " + e.next_node.text);
                    i++;
                    break;
                }
            }
            ind++;
        }


    }


    void set_weightOfNode() {

        for (Node node : cleanNode_list) {

            double max_in_weight = 0.0;

            for (In in : node.in_list) {
                if (in.weight > max_in_weight) {
                    max_in_weight = in.weight;
                }
            }

            double max_out_weight = 0.0;

            for (Out out : node.out_list) {
                if (out.weight > max_out_weight) {
                    max_out_weight = out.weight;
                }
            }

            node.setIn_weight(max_in_weight);
            node.setOut_weight(max_out_weight);
        }

    }

//    List<String> comparison(List<int[]> word_num_list2, String page) {
//
//        Result result = ToAnalysis.parse(page); //分词结果的一个封装，主要是一个List<Term>的terms
//        List<Term> terms = result.getTerms(); //拿到terms
//
//        List<String> wordList = new ArrayList<>();
//        wordList.addAll(wordList_sort);
//        List<String> wordlist1 = new ArrayList<>();
//        wordlist1.addAll(wordList);
//
//        List<String> word_num_s = new ArrayList<>();
//        List<String> word_num_e = new ArrayList<>();
//        List<String> wordlist0 = new ArrayList<>();
//        List<String> wordnature = new ArrayList<>();
//        int count = 0;
//        boolean start = false;
//        boolean stop = false;
//        boolean stopped = false;
//        int[] num0 = new int[2];
//        for (int i = 0; i < terms.size(); i++) {
//            String word = terms.get(i).getName(); //拿到词
//            if (!start && !word.equals("#") && !word.equals("\n")) {
//                start = true;
//            }
//
//            if (start) {
//                if (word.equals("#") || word.equals("\n")) {
//                    stop = true;
//                } else {
//                    stopped = false;
//                }
//
//
//                if (stop) {
//                    if (!stopped) {
//                        count += 1;
//                    }
//                    stopped = true;
//                    stop = false;
//                    word_num_s.add(null);
//                    word_num_e.add(null);
//                    wordlist0.add(null);
//                    wordnature.add(null);
//                } else {
//                    num0[0] = count;
//                    num0[1] = count + word.length() - 1;
//                    count = num0[1] + 1;
//                    word_num_s.add(String.valueOf(num0[0]));
//                    word_num_e.add(String.valueOf(num0[1]));
//                    wordlist0.add(word);
//                    wordnature.add(terms.get(i).getNatureStr());
//                }
//
//            }
//
//            String natureStr = terms.get(i).getNatureStr(); //拿到词性
//        }
//
//        for (int i = 0; i < word_num_list2.size(); i++) {
//            boolean correct_s = false, correct_e = false;
//            int[] num = word_num_list2.get(i);
//            int s = num[0];
//
//            boolean p = false;
//            int pre_ind = 0;
//            for (String num_s : word_num_s) {
//                int ind = word_num_s.indexOf(num_s);
//                if (num_s != null) {
//                    int seg_s = Integer.valueOf(num_s);
//                    if (s == seg_s) {
//                        if (s == 538) {
//                            boolean r = true;
//                        }
//
//                        if (ind > 0 && wordnature.get(pre_ind) != null && (wordnature.get(pre_ind).equals("p")
//                                || (wordlist0.get(pre_ind).equals("所述") && wordnature.get(pre_ind - 1) != null && wordnature.get(pre_ind - 1).equals("p")))) {
//                            p = true;
//                        }
//                        if ((wordnature.get(ind).equals("p") || wordnature.get(ind).equals("c") || wordnature.get(ind).equals("d") || wordnature.get(ind).equals("mq") || wordnature.get(ind).equals("m")
//                                || wordnature.get(ind).equals("u") || wordnature.get(ind).equals("r")
//                                || (wordnature.get(ind).equals("f") && wordnature.get(pre_ind) != null && wordnature.get(pre_ind).equals("u") && wordnature.get(ind + 1) != null && wordnature.get(ind + 1).equals("v")))
//                                && Collections.frequency(wordList, wordList.get(i)) <= 4
//                                || (wordnature.get(ind).equals("p") && (wordlist0.get(ind).equals("与"))) || (wordnature.get(ind).equals("c") && (wordlist0.get(ind).equals("和")))) {
//
//                        } else {
//                            correct_s = true;
//                            break;
//                        }
//
//
//                    }
//                }
//                pre_ind = ind;
//            }
//            int e = num[1];
//
//            for (String num_e : word_num_e) {
//                int ind = word_num_e.indexOf(num_e);
//                if (num_e != null) {
//                    int seg_e = Integer.valueOf(num_e);
//                    if (e == seg_e) {
//
//                        if ((wordnature.get(ind).equals("p") || wordnature.get(ind).equals("c") || wordnature.get(ind).equals("d") || wordnature.get(ind).equals("m")
//                                || wordnature.get(ind).equals("u") || wordnature.get(ind).equals("f") || wordnature.get(ind).equals("s")
//                                || wordnature.get(ind).equals("a") || wordnature.get(ind).equals("ad")) && Collections.frequency(wordList, wordList.get(i)) <= 4
//                                || (wordnature.get(ind).equals("c") && wordlist0.get(ind).equals("和") || (wordnature.get(ind).equals("p") && (wordlist0.get(ind).equals("与") || wordlist0.get(ind).equals("为"))))) {
//
//                        } else {
//                            if (p) {
//                                if (wordnature.get(ind).equals("v") || wordnature.get(ind).equals("vn")) {
//                                    correct_e = false;
//                                    break;
//                                } else {
//                                    correct_e = true;
//                                    break;
//                                }
//                            } else {
//                                correct_e = true;
//                                break;
//                            }
//                        }
//
//                    }
//                }
//            }
//
//            if (!correct_s || !correct_e) {
//                wordlist1.remove(wordList.get(i));
//            } else {
//                String str = wordList.get(i);
//                if ((str.contains("和") || str.contains("与") || str.contains("或") || str.contains("或者") || str.contains("而") || str.contains("及") || str.contains("以及"))) {
//                    //System.out.println(str + Collections.frequency(wordList, str));
//                    String c = cleanC(str);
//                    String[] words = str.split(c);
//                    for (String w : words) {
//                        if (wordList.contains(w)) {
//                            wordlist1.add(w);
//                        }
//                    }
//                    wordlist1.remove(wordList.get(i));
//                }
//            }
//        }
//
//        return wordlist1;
//    }

    String cleanC(String str) {
        String c = "";
        if (str.contains("和")) {
            c = "和";
        } else if (str.contains("与")) {
            c = "与";
        } else if (str.contains("或")) {
            c = "或";
        } else if (str.contains("或者")) {
            c = "或者";
        } else if (str.contains("而")) {
            c = "而";
        } else if (str.contains("及")) {
            c = "及";
        } else if (str.contains("以及")) {
            c = "以及";
        }

        return c;
    }


}
