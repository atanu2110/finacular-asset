package com.finadv.assets.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.CAMS;
import com.finadv.assets.entities.FundInfo;
import com.finadv.assets.entities.HolderInfo;
import com.finadv.assets.entities.Institution;
import com.finadv.assets.entities.Transaction;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;

import io.github.jonathanlink.PDFLayoutTextStripper;

@Service
public class CAMSServiceImpl implements CAMSService {

	private AssetService assetService;

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

	public static final String MUTUAL_FUND = "Mutual Fund";

	public static final int OPEN_TO_TXN_LINE_SKIP = 1;
	public static final String FOLIO_NO = "FolioNo:";
	public static final String READ_MODE = "r";
	public static final String CLOSING_UNIT_BALANCE = "ClosingUnitBalance:";
	public static final String DATE_TRANSACTION_AMOUNT = "DateTransactionAmount";
	public static final String PAGE_END_REGEX = "Page[0-9]+of[0-9]+";
	public static final int DATE_TRANSACTION_SKIP_LINES = 2;
	public static final String NUMBER_REGEX = "[(),a-z]";
	public static final String PAN = "PAN:";
	public static final String EMAIL_ID = "EmailId";
	public static final String REGULAR = "Regular";
	public static final String DIRECT = "Direct";
	public static final String ADVISOR_REGEX = "Advisor";
	public static final String OPENING_UNIT_BALANCE = "OpeningUnitBalance";
	public static final String AMOUNT_REGEX = "[(),]";

	/**
	 * Extracts MF Details from Mutual Fund CAMS File Generated by user.
	 * 
	 * @param camsFile the multipart cams file.
	 * @param password file password
	 * @throws IOException exception on parsing pdf.
	 */
	@Override
	public String extractMFData(MultipartFile camsFile, String password, Long userId) throws IOException {
		String temDirectory = "java.io.tmpdir";
		File tempCAMSFile = new File(System.getProperty(temDirectory) + "/" + camsFile.getOriginalFilename()
				+ RandomStringUtils.random(4, true, true));
		camsFile.transferTo(tempCAMSFile);
		RandomAccessRead rar = new RandomAccessFile(tempCAMSFile, READ_MODE);
		PDFParser parser = new PDFParser(rar, password);

		parser.parse();
		COSDocument cosDoc = parser.getDocument();
		PDFTextStripper pdfStripper = new PDFLayoutTextStripper();
		pdfStripper.setAddMoreFormatting(false);
		PDDocument pdDoc = new PDDocument(cosDoc);
		pdDoc.setAllSecurityToBeRemoved(true);

		String parsedText = pdfStripper.getText(pdDoc);

		List<String> linesList = parsedText.lines().collect(Collectors.toList());

		boolean summaryFlag = parsedText.contains("Loads  and  Fees") ? true : false;

		CAMS camsData = new CAMS();
		HolderInfo holderInfo = new HolderInfo();
		List<FundInfo> fundInfoList = new ArrayList<>();
		for (int folioIndex = 0; folioIndex < linesList.size(); folioIndex++) {
			String line = linesList.get(folioIndex);
			if (line.replaceAll("\\s", "").contains(EMAIL_ID)) {
				holderInfo.setEmail(line.substring(0, 70).split(":")[1].strip());
				holderInfo.setName(linesList.get(folioIndex + 2).substring(0, 70).strip());
			}

			if (line.replaceAll("\\s", "").contains(FOLIO_NO) && !summaryFlag) {
				var folio = new FundInfo();
				List<Transaction> transactionList = new ArrayList<>();
				var folioNamePan = line.split(PAN);
				folio.setFolioName(folioNamePan[0].replaceAll("\\s", "").split(":")[1].strip());
				folio.setPan(folioNamePan[1].strip().substring(0, 10).strip());
				// log.info("Got the Folio No : {}", line);
				String scheme = "";
				for (int txnOpenIndex = folioIndex + 1; txnOpenIndex < linesList.size(); txnOpenIndex++) {
					if (linesList.get(txnOpenIndex).strip().replaceAll("\\s", "").contains(OPENING_UNIT_BALANCE)) {
						scheme = linesList.subList(folioIndex + 1, txnOpenIndex).stream()
								.map(schemeLine -> schemeLine.strip().replaceAll("\\s", ""))
								.collect(Collectors.joining());
						// var schemeAndAdvisor = scheme.split(ADVISOR_REGEX);
						String[] splitScheme = scheme.split("-");
						folio.setSchemeName(scheme);
						folio.setRtCode(splitScheme[0]);
						folioIndex = txnOpenIndex + OPEN_TO_TXN_LINE_SKIP;
						break;
					}
				}
				// log.info("Got the Scheme : {}", scheme);
				//System.out.println("Got the Scheme : {}" + scheme);
				for (int purchaseListIndex = folioIndex; purchaseListIndex < linesList.size(); purchaseListIndex++) {
					if (linesList.get(purchaseListIndex).replaceAll("\\s", "").contains(CLOSING_UNIT_BALANCE)) {
						folio.setClosingBalance(
								parseAmount(linesList.get(purchaseListIndex).substring(0, 50).split(":")[1]));
						folio.setNav(parseAmount(
								linesList.get(purchaseListIndex).substring(50, 100).split(":")[1].split("INR")[1]
										.strip()));
						folio.setValuation(parseAmount(
								linesList.get(purchaseListIndex).substring(100).split(":")[1].split("INR")[1].strip()));
						Transaction lastTransaction = new Transaction();
						lastTransaction.setDate(extractTransactionDate(linesList.get(purchaseListIndex - 1)));
						for (int transactionIndex = folioIndex; transactionIndex < purchaseListIndex; transactionIndex++) {
							String transactionDetail = linesList.get(transactionIndex).strip();
							if (transactionDetail.replaceAll("\\s", "").matches(PAGE_END_REGEX)) {
								// log.info(transactionDetail);
								// log.info("Looks like we have hit a page ending, searching for transactions in
								// next page...");
								for (int pageEndIndex = transactionIndex; pageEndIndex < linesList
										.size(); pageEndIndex++) {
									if (linesList.get(pageEndIndex).strip().replaceAll("\\s", "")
											.contains(DATE_TRANSACTION_AMOUNT)) {
										// log.info("Got the continued transaction list!");
										transactionIndex = pageEndIndex + DATE_TRANSACTION_SKIP_LINES;
										break;
									}
								}
							} else {
								if (transactionDetail.length() != 0) {
									transactionList.add(getTransactionDetails(transactionDetail));
								}
								// log.info(transactionDetail);
							}
						}
						// log.info("Transactions extracted for scheme : " + scheme);
						folioIndex = purchaseListIndex;
						break;
					}
				}
				fundInfoList.add(folio);
			}
		}

		if (summaryFlag) {
			int indexOpt = IntStream.range(0, linesList.size()).filter(i -> linesList.get(i).contains("Folio  No."))
					.findFirst().orElse(-1);
			;
			for (int i = indexOpt + 2; i < linesList.size(); i++) {
				FundInfo folio = new FundInfo();
				if (linesList.get(i).trim().contains("Total"))
					break;
				if (linesList.get(i).trim().matches("^[0-9].*$")) {
					String[] splitLine = linesList.get(i).trim().split("\\s+");
					folio.setFolioName(splitLine[0]);
					folio.setRtCode((Stream.of(splitLine[1].split("-")).reduce((first, last) -> first).get()));
					folio.setValuation(Double.valueOf(splitLine[splitLine.length - 2].replaceAll(",", "").trim()));
					folio.setClosingBalance(Double.valueOf(splitLine[splitLine.length - 5].replaceAll(",", "").trim()));
					folio.setNav(Double.valueOf(splitLine[splitLine.length - 3].replaceAll(",", "").trim()));

					fundInfoList.add(folio);
				}

			}
		}

		camsData.setFundInfoList(fundInfoList);
		camsData.setHolderInfo(holderInfo);
		List<UserAssets> userAssetList = new ArrayList<UserAssets>();
		for (FundInfo fundInfo : camsData.getFundInfoList()) {
			if (fundInfo.getValuation() != 0) {
				UserAssets userAssets = new UserAssets();
				userAssets.setAmount(fundInfo.getValuation());
				userAssets.setHolderName(camsData.getHolderInfo().getName());
				userAssets.setCreatedAt(LocalDateTime.now());
				Institution institution = new Institution();
				institution.setId(1);
				userAssets.setAssetProvider(institution);
				AssetType assetType = new AssetType();
				assetType.setId(4);
				userAssets.setAssetType(assetType);
				AssetInstrument assetInstrument = new AssetInstrument();
				assetInstrument.setId(8);
				userAssets.setAssetInstrument(assetInstrument);
				userAssets.setExpectedReturn(12);
				userAssets.setEquityDebtName(fundInfo.getSchemeName());
				userAssets.setCode(fundInfo.getRtCode());
				userAssets.setUnits((int) Math.round(fundInfo.getValuation() / fundInfo.getNav()));

				userAssets.setUserId(userId);
				userAssetList.add(userAssets);
			}

		}

		UserAsset userAsset = new UserAsset();
		userAsset.setUserId(userId);
		userAsset.setAssets(userAssetList);
		// temp comment
		assetService.saveUserAssetsByUserId(userAsset, "cams");
		tempCAMSFile.delete();
		rar.close();
		cosDoc.close();
		pdDoc.close();
		return new ObjectMapper().writeValueAsString(camsData);

	}

	private LocalDate extractTransactionDate(String transactionLine) {
		try {
			return LocalDate.parse(transactionLine.strip().substring(0, 11),
					DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
		} catch (Exception e) {
			// log.error("Ignored error : {}", e.getMessage());
		}
		return null;
	}

	private Transaction getTransactionDetails(String transactionDetail) {
		Transaction transaction = new Transaction();
		try {
			if (transactionDetail.contains("***")) {
				return transaction;
			}

			var date = transactionDetail.substring(0, 11);
			transaction.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
			if (transactionDetail.contains("(")) {
				//System.out.println(transactionDetail);
			} else {
				transaction.setTransactionDetail(transactionDetail.substring(12, 76).strip());
				transaction.setAmount(parseAmount(transactionDetail.substring(76, 90)));
				transaction.setUnits(
						Double.valueOf(transactionDetail.substring(95, 105).strip().replaceAll(AMOUNT_REGEX, "")));
				transaction.setPrice(
						Double.valueOf(transactionDetail.substring(110, 125).strip().replaceAll(AMOUNT_REGEX, "")));
				transaction.setUnitBalance(
						Double.valueOf(transactionDetail.substring(129).strip().replaceAll(AMOUNT_REGEX, "")));
				//System.out.println(transactionDetail.substring(12, 79));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transaction;
	}

	private static Double parseAmount(String amountString) {
		return Double.valueOf(amountString.strip().replaceAll(AMOUNT_REGEX, ""));
	}

}