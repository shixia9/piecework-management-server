package com.jackson.common.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * PDF操作（插入文字或图片内容）
 */
public class PDFUtils {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtils.class);

    public static boolean fillContent(String templatePath, String newPDFPath, Map<String, PDFFillForm> fillMap) {
        try {
            FileOutputStream out = new FileOutputStream(newPDFPath);
            PdfReader reader = new PdfReader(templatePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            Iterator<String> it = form.getFields().keySet().iterator();
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            while (it.hasNext()) {
                String name = it.next().toString();
                if(fillMap.containsKey(name)){
                    PDFFillForm pdfFillForm = fillMap.get(name);
                    if("1".equals(pdfFillForm.getType())){
                        //1为文字，则将文字填充到PDF
                        form.setFieldProperty(pdfFillForm.getName(), "textfont", baseFont, null);
                        form.setField(pdfFillForm.getName(), pdfFillForm.getContent());
                    }else if("2".equals(pdfFillForm.getType())){
                        //2为图片，则将图片填充到PDF
                        Image image = Image.getInstance(pdfFillForm.getContent());
                        form.addSubstitutionFont(baseFont);
                        int pageNo = form.getFieldPositions(pdfFillForm.getName()).get(0).page;
                        Rectangle signRect = form.getFieldPositions(pdfFillForm.getName()).get(0).position;
                        float x = signRect.getLeft();
                        float y = signRect.getBottom();
                        PdfContentByte under = stamper.getOverContent(pageNo);
                        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                        image.setAbsolutePosition(x, y);
                        under.addImage(image);
                    }
                }
            }
            stamper.setFormFlattening(true);
            stamper.close();
            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = null;
            for (int i=1;i<=reader.getNumberOfPages();i++){
                importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
                copy.addPage(importPage);
            }
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            return false;
        }
        return true;
    }

}
