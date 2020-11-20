package com.itextpdf.utils;

import java.util.Map;

/**
 * @author BMK
 * @Version 1.0
 * @since 2020/11/12 9:57
 */
public class ItextStringUtils {


    /**
     *
     * @param dataMap   传入的数据
     * @param field     需要分组的字段
     * @param cutList   需要裁剪的数组
     * @param page      页码
     * @param pageLine  一页有多少行
     * @param realLineStringNum     一行限制多少字符
     */
    /**
     * 对传入的数组进行分页  分满第一页，剩下的放到下一个数组
     * @param dataMap   传入的数据
     * @param field 需要分组的字段
     * @param cutList   需要裁剪的数组
     * @param page  当前进行的页码
     * @param pageLine  一页有多少行
     * @param pageTwoLine   第二页有多少行
     * @param realLineStringNum 一行限制多少字符
     * @param maxPage   最大页数
     */
    public static void cutPage(Map<String, String> dataMap, String field, String[] cutList,Integer page,Integer pageLine,Integer pageTwoLine,Integer realLineStringNum,Integer maxPage) {
        if(page!=1){
            pageLine = pageTwoLine;
        }

        if(page==maxPage){
            return;
        }
        //重构当前行号为0，进行循环
        int currentLineNumber = 0;
        //定义需要截取的List中的字符串i的为止
        int cutLineNum = 0;
        //定义需要截取的行数
        int needCut = 0;

        //进入循环
        for (int i = 0; i < cutList.length; i++) {
            //计算一行中字符串个数
            int lineStringNum = getLineStringNum(cutList[i]);

            //如果当前行数小于15进入第一层if
            if (currentLineNumber <=pageLine) {
                //记录当前需要裁剪的行数，最后一次小于15进入的时候有效
                needCut = pageLine - currentLineNumber;

                //根据字符个数统计行数，小于28为一行，大于28计算行数并且加入
                if (lineStringNum <= realLineStringNum) {
                    currentLineNumber++;
                }
                if (lineStringNum > realLineStringNum) {
                    currentLineNumber = ((int) Math.ceil(((double) lineStringNum) / ((double) realLineStringNum))) + currentLineNumber;
                }
            }
            //当第一次大于14行 并且行字符串数小于28 则需要剪切的为一行
            if(currentLineNumber >pageLine){
                if (lineStringNum <= realLineStringNum) {
                    needCut = 1;
                }
                if (lineStringNum > realLineStringNum) {
                    //needCut等于自己不做任何事情

                }
                //需要裁剪的i
                cutLineNum = i;
                break;
            }
        }
        //需要裁剪的字符串
        String cutLineString = cutList[cutLineNum];
        //裁剪出来的字符数组
        String[] cutStringList = cutString(cutLineString,needCut,realLineStringNum);

        //计算第一部分的数组   +1是因为数组个数
        String[] firstList = new String[cutLineNum+1];
        for (int i = 0; i < firstList.length; i++) {
            if(i == firstList.length-1){
                firstList[i]= cutStringList[0];
                continue;
            }
            firstList[i] = cutList[i];
        }

        //计算第二部分数组
        String[] secondList = new String[cutList.length-cutLineNum];
        for (int i = 0; i < secondList.length; i++) {
            if(i==0){
                secondList[i]= cutStringList[1];
                continue;
            }
            secondList[i] = cutList[i+cutLineNum];
        }

        //还原为字符串
        String secondPart = arrayToString(secondList);

        //放入对应页码字段，取名
        dataMap.put(field+"_"+(page+1),secondPart);
        String firstPart = arrayToString(firstList);
        dataMap.put(field+"_"+page,firstPart);
        //递归进行调用，直到才见到最后一夜结束
        cutPage(dataMap,field,secondList,page+1,pageLine,pageTwoLine,realLineStringNum,maxPage);
    }

    /**
     * 将数组加上换行符还原为字符串并且去掉最后一个换行符
     * @param list
     * @return
     */
    public static String arrayToString(String[] list){
        //用StringBuffer拼接字符串
        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0; i < list.length; i++){
            //每一个后面加上换行符
            stringBuffer. append(list[i]+"\n");
        }
        //去掉最后一个换行符
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        String result = stringBuffer.toString();
        return result;

    }


    /**
     * 将传入的字符串拆解成2部分数组
     * @param cutLineString
     * @param needCut
     * @return
     */
    public static String[] cutString(String cutLineString,int needCut,Integer realLineStringNum){
        //记录总字符数
        int strNum=0;
        //记录需要裁剪的地方
        int cutNum = 0;

        for (int i = 0; i < cutLineString.length(); i++) {
            //获取i位置的字符
            char c = cutLineString.charAt(i);
            //如果需要裁剪的字符小于一行
            if(i==cutLineString.length()-1){
                cutNum = i ;
            }
            //当需要裁剪的位置到了时，记录
            if(strNum>=needCut*2*realLineStringNum){
                cutNum = i ;
                break;
            }
            if (c >= 65 && c <= 90)
                strNum++;
            else if (c >= 97 && c <= 122)
                strNum++;
            else if (c >= 48 && c <= 57)
                strNum++;
            else if (c == 32)
                strNum++;
            else strNum=strNum+2;

        }
        //如果小于一行，则返回原路
        if(cutNum==cutLineString.length()-1){
            String firstString = cutLineString;
            String lastString = "";
            return new String[]{firstString,lastString};
        }else{
            String firstString = cutLineString.substring(0, cutNum);
            String lastString = cutLineString.substring(cutNum);
            return new String[]{firstString,lastString};
        }

    }

    /**
     * 判断一行字符串有多少个字符   汉字，符号,圆角空格算一个 英文字母，数字，
     *
     * @param str
     * @return
     */
    public static int getLineStringNum(String str) {
        int upCaseCnt = 0;    //大写字母
        int lowCaseCnt = 0;   //小写字母
        int spaceCnt = 0;     //空格
        int numCnt = 0;       //数字
        int otherCnt = 0;     //其他，字符串空格之类的
        int totalNum = 1;     //总计

        //System.out.println("开始统计字符串");
        //传入为空，直接返回0
        if (str == null) {
            return 0;
        }
        //获取字符串长度
        int len = str.length();

        //循环遍历
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c >= 65 && c <= 90)
                upCaseCnt++;
            else if (c >= 97 && c <= 122)
                lowCaseCnt++;
            else if (c >= 48 && c <= 57)
                numCnt++;
            else if (c == 32)
                spaceCnt++;
            else otherCnt++;
        }


        //计算总共个数，加1是为了防止小数
        totalNum = (upCaseCnt + lowCaseCnt  + numCnt + 1) / 2 + spaceCnt + otherCnt+totalNum-1;
        //System.out.println("大写字母:" + upCaseCnt);
        //System.out.println("小写字母:" + lowCaseCnt);
        //System.out.println("空格:" + spaceCnt);
        //System.out.println("数字:" + numCnt);
        //System.out.println("其他:" + otherCnt);
        //System.out.println("总计" + totalNum);
        return totalNum;
    }



    /**
     * 统计总共有多少行
     * @param realLineStringNum  一行字符串限制个数
     * @param lineNumber    初始行数
     * @param lineList  进行统计行数的list
     * @return  返回行数
     */
    public static int getLineNumber(Integer realLineStringNum, int lineNumber, String[] lineList) {
        for (String line : lineList) {
            //计算一行中字符串个数
            int lineStringNum = ItextStringUtils.getLineStringNum(line);
            //根据字符串个数统计行数
            if (lineStringNum <= realLineStringNum) {
                lineNumber++;
            }
            if (lineStringNum > realLineStringNum) {
                //一行realLineStringNum个字符串，向上取整，强转是为了防止数据丢失
                lineNumber = ((int) Math.ceil(((double) lineStringNum) / ((double) realLineStringNum))) + lineNumber;
            }
        }
        return lineNumber;
    }
}
