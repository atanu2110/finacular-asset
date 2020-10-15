package com.finadv.assets.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finadv.assets.model.*;
import io.github.jonathanlink.PDFLayoutTextStripper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CAGService {

    public static final String READ_MODE = "r";
    public static final String AMOUNT_REGEX = "[(),]";

    public String extractCAGData(MultipartFile cagFile, String password) throws IOException {
        String temDirectory = "java.io.tmpdir";
        File tempCAGFile = new File(System.getProperty(temDirectory) + "/" + cagFile.getOriginalFilename());
        cagFile.transferTo(tempCAGFile);

        PDFParser parser = new PDFParser(new RandomAccessFile(tempCAGFile, READ_MODE), password);

        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFLayoutTextStripper();
        pdfStripper.setAddMoreFormatting(false);
        PDDocument pdDoc = new PDDocument(cosDoc);
        pdDoc.setAllSecurityToBeRemoved(true);
        String parsedText = pdfStripper.getText(pdDoc);
        List<String> linesList = parsedText.lines().collect(Collectors.toList());

        CAGDetails cagDetails = new CAGDetails();
        for (int index = 0; index < linesList.size() && linesList.get(index).length() > 0; index++) {
            String line = linesList.get(index);
            if (removeWhiteSpaces(line).contains("AMC")) {
                Fund fund = new Fund();
                String amc = removeWhiteSpaces(line.substring(0, 70)).split(":")[1];
                fund.setAMC(amc);
                String topDate = removeWhiteSpaces(line.substring(70));
                fund.setTopDate(parseDate(topDate));
                String folioLine = linesList.get(index + 1);

                String folioNum = removeWhiteSpaces(folioLine.substring(0, 40)).split("No.")[1];
                String holderName = removeWhiteSpaces(folioLine.substring(40, 70)).split(":")[1];
                String status = removeWhiteSpaces(folioLine.substring(70, 150)).split(":")[1];
                String PAN = removeWhiteSpaces(folioLine.substring(150, 180)).split("PANNo.")[1];
                fund.setFolioNumber(folioNum);
                fund.setFolioStatus(status);
                fund.setFolioPAN(PAN);
                fund.setHolderName(holderName);
                List<FundDetails> fundDetails = new ArrayList<>();
                int fundIndex = index + 9;
                fundDetails.add(getFundDetails(linesList, fundIndex));
                while (linesList.get(fundIndex + 6).strip().length() > 0) {
                    fundIndex = fundIndex + 6;
                    fundDetails.add(getFundDetails(linesList, fundIndex));
                }
                fund.setFundDetails(fundDetails);
                cagDetails.getFundList().add(fund);
                index = fundIndex + 6;
            }
        }
        return new ObjectMapper().writeValueAsString(cagDetails);
    }

    private static LocalDate parseDate(String topDate) {
        return LocalDate.parse(topDate.strip(), DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }


    private static FundDetails getFundDetails(List<String> linesList, int fundIndex) {
        String schemeLine = linesList.get(fundIndex);

        String transaction = linesList.get(fundIndex + 1);
        String totalLine = linesList.get(fundIndex + 3);

        int purchaseIndex = transaction.indexOf("Purchase");
        String sectionALine = transaction.substring(0, purchaseIndex);
        String sectionATotalLine = totalLine.substring(0, purchaseIndex);
        String sectionBLine = transaction.substring(purchaseIndex, purchaseIndex + 70);
        String sectionBTotalLine = transaction.substring(purchaseIndex, purchaseIndex + 70);
        String sectionC = transaction.substring(purchaseIndex + 70, purchaseIndex + 70 + 30);
        String sectionDLine = transaction.substring(purchaseIndex + 70 + 30);
        String sectionDTotalLine = transaction.substring(purchaseIndex + 70 + 30);

        String[] sectionASplit = sectionALine.strip().split("\\s+");
        SectionA sectionA = new SectionA();
        sectionA.setDescription(sectionASplit[0].strip());
        sectionA.setDate(parseDate(sectionASplit[1]));
        sectionA.setUnits(parseAmount(sectionASplit[2]));
        sectionA.setAmount(parseAmount(sectionASplit[3]));
        sectionA.setPrice(parseAmount(sectionASplit[4]));
        sectionA.setStt(parseAmount(sectionASplit[5]));

        String[] sectionBSplit = sectionBLine.stripLeading().split("\\s+", -1);

        SectionB sectionB = new SectionB(sectionBSplit[0].stripLeading(), parseDate(sectionBSplit[1]), parseAmount(sectionBSplit[2]),
                parseAmount(sectionBSplit[3]), parseAmount(sectionBSplit[4]), parseAmount(sectionBSplit[5]));

        SectionD sectionD = new SectionD(parseAmount(sectionDLine.substring(0, 15)),
                parseAmount(sectionDLine.substring(15, 25)),
                parseAmount(sectionDLine.substring(25, 34)));

        String[] sectionATotalSplit = sectionATotalLine.stripLeading().split("\\s+", -1);

        Total total = new Total(parseAmount(sectionATotalSplit[1]), parseAmount(sectionATotalSplit[2]));
        return new FundDetails(removeWhiteSpaces(schemeLine.strip()), sectionA, sectionB, sectionD, total);
    }

    private static String removeWhiteSpaces(String strip) {
        return strip.replaceAll("\\s", "");
    }

    private static Double parseAmount(String amountString) {
        if (!amountString.isBlank()) {
            try {
                return Double.valueOf(amountString.strip().replaceAll(AMOUNT_REGEX, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
