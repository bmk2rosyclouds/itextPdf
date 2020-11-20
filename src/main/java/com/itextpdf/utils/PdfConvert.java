package com.itextpdf.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BMK
 * @Version 1.0
 * @since 2020/11/16 16:48
 */
public class PdfConvert {

    /**
     * 对dataMap分页字段裁剪，并且调用对应的模板进行插入数据，合并
     * @param dataMap   插入数据Map
     * @param field     裁剪的字段
     * @param realLineStringNum     一行有多少个数
     * @param pageLine              一页的行数
     * @param pageTwoLine           第二页的行数
     * @param defaultPdfName        默认的pdf名称
     */
    public static void pdfConvert(Map<String, String> dataMap, String field, Integer realLineStringNum,Integer pageLine,Integer pageTwoLine,String defaultPdfName){
        String inputDirectory = "D:\\一些文件\\2-itextPdf\\project\\itextpdf2.0\\src\\main\\resources\\input\\";
        String outputDirectory = "D:\\一些文件\\2-itextPdf\\project\\itextpdf2.0\\src\\main\\resources\\output\\";

        //初始行数为0
        int lineNumber = 0;

        //需要重构的String字符串
        String fieldString = dataMap.get(field);
        //替换所有的空格为圆角
        fieldString=fieldString.replace(" ","　");

        //先通过换行符“\n”对需要重构的字符串分组
        String[] lineList = fieldString.split("\n");

        //获取总共有多少行
        lineNumber = ItextStringUtils.getLineNumber(realLineStringNum, lineNumber, lineList);

        //如果重构区域行数<pageLine，说明默认pdf模板可以解决需求
        if (lineNumber <= pageLine) {
            dataMap.put(field+"_1",dataMap.get(field));
            dataMap.remove(field);

            ItextPdfConvertUtils.convertToPdf(dataMap,defaultPdfName,defaultPdfName,inputDirectory,outputDirectory);

        }
        //如果大于一页的行数，则说明需要进行裁剪分页
        if(lineNumber>pageLine){
            //计算估计有多少分页
            int maxPage = 1+(int) Math.ceil((double)(lineNumber-pageLine)/(double)pageTwoLine);
            //裁剪字符串，第一次裁剪的list为lineList
            ItextStringUtils.cutPage(dataMap, field, lineList,1,pageLine,pageTwoLine,realLineStringNum,maxPage);

            //pdf分页名称字符数组
            String[] files = new String[maxPage];
            //两页进行转换
            if(maxPage == 2){
                dataMap.put(field+"_3",dataMap.get(field+"_2"));
                ItextPdfConvertUtils.convertToPdf(dataMap,"1-pagefenye-"+defaultPdfName,"1-pagefenye-"+defaultPdfName,inputDirectory,outputDirectory);
                ItextPdfConvertUtils.convertToPdf(dataMap,"3-pagefenye-"+defaultPdfName,"3-pagefenye-"+defaultPdfName,inputDirectory,outputDirectory);
                files[0] = outputDirectory+"副本-"+"1-pagefenye-"+defaultPdfName;
                files[1] = outputDirectory+"副本-"+"3-pagefenye-"+defaultPdfName;
            } else{
                //多页进行转换
                //将第一页和最后一夜数据导入
                convertOneAndLastPage(dataMap, field, defaultPdfName, maxPage,inputDirectory,outputDirectory);

                //中间页进行生成
                ArrayList<String> middlePageNameResults = getMiddlePage(dataMap, field, defaultPdfName, maxPage,inputDirectory,outputDirectory);

                //进行合并
                if (maxPage==2){
                    files[0] = outputDirectory+"副本-"+"1-pagefenye-"+defaultPdfName;
                    files[1] = outputDirectory+"副本-"+"3-pagefenye-"+defaultPdfName;
                }else{
                    files[0] = outputDirectory+"副本-"+"1-pagefenye-"+defaultPdfName;
                    files[maxPage-1] = outputDirectory+"副本-"+"3-pagefenye-"+defaultPdfName;
                    for (int i = 0; i < maxPage; i++) {
                        if(i == 0){ continue; }
                        if(i == maxPage-1){ continue; }

                        files[i] = outputDirectory+"副本-"+middlePageNameResults.get(i-1);
                    }
                }
            }
            String savepath = outputDirectory+"final.pdf";
            ItextPdfConvertUtils.mergePdfFiles(files, savepath);


        }


    }

    /**
     * 导出中间页
     * @param dataMap    数据map
     * @param field     导入字段
     * @param defaultPdfName    默认的pdf名称
     * @param maxPage           最大页码
     * @param inputDirectory    输入文件夹
     * @param outPutDirectory   输出文件夹
     * @return
     */
    private static ArrayList<String> getMiddlePage(Map<String, String> dataMap, String field, String defaultPdfName, int maxPage,String inputDirectory,String outPutDirectory) {
        //中间页的表单域名
        String middlePageField = field+"_2";

        //中间页的数据内容
        ArrayList<String> middlePageValue = new ArrayList<String>();
        //中间页导出以后的名称数组
        ArrayList<String> pageNameResults = new ArrayList<String>();

        //获取对应中间页的数据
        for (int i = 2; i < maxPage; i++) {
            String value = dataMap.get(field + "_" + i);
            middlePageValue.add(value);
        }

        //转出pdf，并将名称加入数组
        for (int i = 0; i < middlePageValue.size(); i++) {
            HashMap<String,String> map = new HashMap<String, String>();
            map.put(middlePageField,middlePageValue.get(i));
            ItextPdfConvertUtils.convertToPdf(map,"2-pagefenye-"+defaultPdfName,
                    ""+i+"2-pagefenye-"+defaultPdfName,
                    inputDirectory,
                    outPutDirectory);
            pageNameResults.add(""+i+"2-pagefenye-"+defaultPdfName);
        }
        return pageNameResults;
    }

    /**
     * 转换第一页，和最后一页的pdf
     * @param dataMap    数据map
     * @param field     导入字段
     * @param defaultPdfName    默认的pdf名称
     * @param maxPage           最大页码
     * @param inputDirectory    输入文件夹
     * @param outPutDirectory   输出文件夹
     */
    private static void convertOneAndLastPage(Map<String, String> dataMap, String field, String defaultPdfName, int maxPage,String inputDirectory,String outPutDirectory) {
        ItextPdfConvertUtils.convertToPdf(dataMap,"1-pagefenye-"+defaultPdfName,"1-pagefenye-"+defaultPdfName,inputDirectory,outPutDirectory);

        HashMap<String,String> thirdPageMap = new HashMap<String, String>();
        thirdPageMap.putAll(dataMap);
        thirdPageMap.remove(field+"_3");
        thirdPageMap.put(field+"_3",dataMap.get(field+"_"+maxPage));
        ItextPdfConvertUtils.convertToPdf(thirdPageMap,"3-pagefenye-"+defaultPdfName,"3-pagefenye-"+defaultPdfName,inputDirectory,outPutDirectory);
    }





}
