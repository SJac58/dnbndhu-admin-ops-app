package com.org.dnbndhu;

import com.org.dnbndhu.infrastructure.ocr.Tess4JOCRClient;

import java.io.File;

public class OCRTest {

    public static void main(String[] args) {

        Tess4JOCRClient client = new Tess4JOCRClient();

        File file = new File("C:\\Users\\saraj\\Downloads\\sample.jpg");

        String text = client.extractText(file);

        System.out.println("===== OCR OUTPUT =====");
        System.out.println(text);
    }
}
