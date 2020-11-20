package com.itextpdf.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author BMK
 * @Version 1.0
 * @since 2020/11/12 9:34
 */
public class ItextPdfConvertUtils {

    /**
     * 动态插入数据
     *
     * @param dataMap  需要插入的map数据
     * @param fileName Pdf名称
     * @return
     * @throws Exception
     */
    public static void convertToPdf(Map dataMap, String fileName,String outFileName,String inputDirectory,String outPutDirectory) {

        //判断传入的map数据是否为空，如果为空则直接返回null
        if (dataMap == null || dataMap.isEmpty() || dataMap.keySet() == null) {
            return;
        }
        //开始标识符，并且记录开始转换时间
        System.out.println("强大的pdf转换工具开始转换啦");
        long startTime = System.currentTimeMillis();

        //获取输入pdf模板地址
        String template = inputDirectory + fileName;
        try {
            //根据输入的模板地址获取输入流
            InputStream in = new FileInputStream(new File(template));
            /**
             * 通过generate方法导入数据并且返回输出流
             */
            PdfReader reader = new PdfReader(in);
            ByteArrayOutputStream out = (ByteArrayOutputStream) generate(reader, dataMap);


            //将pdf字节流输出到文件流,并且输出文件
            OutputStream fos = new FileOutputStream(outPutDirectory + "副本-" + outFileName);
            fos.write(out.toByteArray());
            fos.close();
            out.close();
            reader.close();
            in.close();
            long endTime = System.currentTimeMillis();
            System.out.println("强大的pdf转换工具转换结束啦");
            System.out.println("总共耗时:" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态给pdf表单域插入数据
     *
     * @param template Pdf模板
     * @param dataMap  动态插入的数据
     * @return
     * @throws Exception
     */
    private static OutputStream generate(PdfReader template, Map dataMap) {
        try {

            //设置字体
            //BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            BaseFont bfChinese = BaseFont.createFont("C:\\Windows\\Fonts\\simkai.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            //输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //创建pdfStamper来向pdf添加多余内容
            PdfStamper stamp = new PdfStamper(template, out);

            //获取所有的表单域
            AcroFields form = stamp.getAcroFields();

            // set the field values in the pdf form
            //便利datamap的数据
            for (Iterator it = dataMap.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String value = (String) dataMap.get(key);
                //设置表单域的字体
                form.setFieldProperty(key, "textfont", bfChinese, null);
                if (key.equals("text_2")) {
                    //设置表单域的字体大小
                    form.setFieldProperty(key, "textsize", 3f, null);
                }
                //给对应表单域添加值
                form.setField(key, value);

            }


            stamp.setFormFlattening(true);
            stamp.close();
            template.close();

            return out;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }

    }


    /**
     * 合并pdf
     * @param name
     * @param newfile
     */
    public static void mergePdfFiles(String[] name, String newfile)  {

        List<String> files = Arrays.asList(name);
        try {
            Document document = new Document(new PdfReader(files.get(0)).getPageSize(1));
            FileOutputStream fileOutputStream = new FileOutputStream(newfile);
            PdfCopy copy = new PdfCopy(document, fileOutputStream);
            document.open();
            for (int i = 0; i < files.size(); i++) {
                PdfReader reader = new PdfReader(files.get(i));
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
                reader.close();
            }
            document.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

}
