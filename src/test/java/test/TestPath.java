package test;

import com.itextpdf.utils.PdfConvert;
import org.junit.Test;

import java.util.Map;

/**
 * @author BMK
 * @Version 1.0
 * @since 2020/11/16 16:35
 */

public class TestPath {
    @Test
    public void testOnePage(){
        Map dataMap = StringUse.dataMapOne;
        PdfConvert.pdfConvert(dataMap,"text_18",32,19,39,"shigudafu.pdf");
    }

    @Test
    public void testThreePage(){
        Map dataMap = StringUse.dataMapThree;
        PdfConvert.pdfConvert(dataMap,"text_18",32,19,39,"shigudafu.pdf");
    }

    @Test
    public void testNPage(){
        Map dataMap = StringUse.dataMapN;
        PdfConvert.pdfConvert(dataMap,"text_18",32,19,39,"shigudafu.pdf");
    }

    @Test
    public void testTwoPage(){
        Map dataMap = StringUse.dataMapTwo;
        PdfConvert.pdfConvert(dataMap,"text_18",32,19,39,"shigudafu.pdf");
    }
}
