package com.finadv.assets.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.Equity;
import com.finadv.assets.entities.Institution;
import com.finadv.assets.entities.MutualFundAnalysis;
import com.finadv.assets.entities.MutualFundAnalysisResponse;
import com.finadv.assets.entities.MutualFundAnalysisResponseList;
import com.finadv.assets.entities.MutualFundAnalysisScheme;
import com.finadv.assets.entities.MutualFundGrowthAnalysis;
import com.finadv.assets.entities.NSDLAssetAmount;
import com.finadv.assets.entities.NSDLEquity;
import com.finadv.assets.entities.NSDLMutualFund;
import com.finadv.assets.entities.NSDLReponse;
import com.finadv.assets.entities.NSDLValueTrend;
import com.finadv.assets.entities.OverallStockData;
import com.finadv.assets.entities.PortfolioAnalysisReponse;
import com.finadv.assets.entities.PortfolioAnalysisRequest;
import com.finadv.assets.entities.StockData;
import com.finadv.assets.entities.StockDataList;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.error.RestServiceException;
import com.finadv.assets.util.AssetUtil;

/**
 * @author ATANU
 *
 */
@Service
public class CDSLServiceImpl implements CDSLService {

	private static final Logger LOG = LoggerFactory.getLogger(CDSLServiceImpl.class);

	private AssetService assetService;

	private AsyncService asyncService;

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

	@Autowired
	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private AssetUtil assetUtil;

	@Override
	public NSDLReponse extractFromCDSL(MultipartFile cdslFile, String password, Long userId, String source) {
		String temDirectory = "java.io.tmpdir";
		File tempCDSLFile = new File(System.getProperty(temDirectory) + "/" + cdslFile.getOriginalFilename()
				+ RandomStringUtils.random(4, true, true));
		PDDocument doc;
		
		NSDLReponse nsdlReponse = new NSDLReponse();
		List<UserAssets> userAssetList = new ArrayList<UserAssets>();
		String email = "";
		try {
			cdslFile.transferTo(tempCDSLFile);

			doc = PDDocument.load(tempCDSLFile, password);
			String text = new PDFTextStripper().getText(doc);
			String lines[] = text.split("\\r?\\n");
			List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
			List<NSDLValueTrend> valuetrend = new ArrayList<NSDLValueTrend>();
			List<NSDLMutualFund> mutualFunds = new ArrayList<NSDLMutualFund>();
			int linecounter = 0;
			Boolean flagGrandTotal = false;
			for (String line : lines) {
				if (line.toLowerCase().contains("statement for the period")) {
					nsdlReponse.setPeriod((Stream.of(line.split(" ")).reduce((first, last) -> last).get()));
					//System.out.println("Period: " + (Stream.of(line.split(" ")).reduce((first, last) -> last).get()));
				}

				if (!flagGrandTotal && line.toLowerCase().contains("grand total")) {
					flagGrandTotal = true;
					nsdlReponse.setAmount(
							Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
					//System.out.println("grand total: " + Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
				}
				if (line.contains("CAS ID:") || line.contains("NSDL ID")) {
					nsdlReponse.setHolderName(lines[linecounter+1]);
					//System.out.println("Holder Name  " + lines[linecounter+1]);
				}
				if (line.contains("Email Id :")) {
					email = Stream.of(line.split(" ")).reduce((first, last) -> last).get();
					if (email.equalsIgnoreCase("Not Registered"))
						email = "";
					//System.out.println("Email Id : " + email);
				}
				// Get portfolio distribution
				if (line.contains("Assets Class Value in ` %")) {
					NSDLAssetAmount nsdlAssetAmount = new NSDLAssetAmount();
					int i=1;
					while(!lines[linecounter+i].contains("Total")) {
						if(lines[linecounter+i].contains("Equity"))
							nsdlAssetAmount.setEquities(
									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
									.longValue());
//						if(lines[linecounter+i].contains("Preference Shares"))
//							nsdlAssetAmount.setPreferenceShares(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
						if(lines[linecounter+i].contains("Mutual Fund Folios"))
							nsdlAssetAmount.setMutualFundFolios(
									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
									.longValue());
						if(lines[linecounter+i].contains("Mutual Funds Held in Demat Form"))
							nsdlAssetAmount.setMutualFunds(
									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
									.longValue());
//						if(lines[linecounter+i].contains(""))
//							nsdlAssetAmount.setCorporateBonds(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
//						if(lines[linecounter+i].contains(""))
//							nsdlAssetAmount.setMoneyMarketInstruments(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
//						if(lines[linecounter+i].contains(""))
//							nsdlAssetAmount.setSecuritisedInstruments(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
//						if(lines[linecounter+i].contains(""))
//							nsdlAssetAmount.setGovernmentSecurities(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
//						if(lines[linecounter+i].contains(""))
//							nsdlAssetAmount.setPostalSavingScheme(
//									Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + i].replaceAll(",", ""))))
//									.longValue());
						i++;
					}

					nsdlReponse.setNsdlAssetAmount(nsdlAssetAmount);
				}
				// Get portfolio value trend
				if (line.contains("Month-Year Portfolio Valuation(In `) Changes in ` Changes in %")) {
					for (int i = linecounter + 1; i < linecounter + 13; i++) {
//						if (!(lines[i].contains("+") || lines[i].contains("-") || lines[i].contains("NA NA")))
//							break;
						String[] trendSplit = lines[i].trim().split(" ");
						NSDLValueTrend nsdlValueTrend = new NSDLValueTrend();
						if(trendSplit.length >= 5)
							nsdlValueTrend.setChangePercentage(trendSplit[4]);
						if(trendSplit.length >= 4)
							nsdlValueTrend.setChangeRs(trendSplit[3]);
						nsdlValueTrend.setPortfolioValue(trendSplit[2]);
						nsdlValueTrend.setMonth(trendSplit[0] + trendSplit[1]);
						System.out.print("Value Trend: ");
						System.out.print(trendSplit[0] + trendSplit[1] + " ");
						System.out.print(trendSplit[2] + " ");
						if(trendSplit.length >= 4)
							System.out.print(trendSplit[3] + " ");
						if(trendSplit.length >= 5)
							System.out.print(trendSplit[4] + " ");
						//System.out.println();
						valuetrend.add(nsdlValueTrend);
					}
				}
				// Get all Equity shares
				String[] lineSplit = new String[0];
//				String[] extralineSplit = new String[0];
//				if (line.trim().matches("^(INE)[a-zA-Z0-9]{9,}$") || line.trim().matches("^(INE).*")) {
//					NSDLEquity nsdlEquity = new NSDLEquity();
//					if (lines[linecounter + 1].contains("NSE") || lines[linecounter + 1].contains("BSE")) {
//						nsdlEquity.setIsin(line.trim());
//						nsdlEquity.setStockSymbol(lines[linecounter + 1].trim());
//						for (int i = 1; i <= 5; i++) {
//							if (lines[linecounter + i + 1].trim().contains(".")
//									|| lines[linecounter + i + 1].trim().contains(",")) {
//								lineSplit = lines[linecounter + i + 1].split(" ");
//								if (lineSplit.length > 2 && !lines[linecounter + i + 1].contains("locked")) {
//									break;
//								} else {
//									extralineSplit = lines[linecounter + i + 3].split(" ");
//									break;
//								}
//
//							}
//
//						}
//
//						if (lineSplit.length > 2) {
//							nsdlEquity.setShares(
//									Long.parseLong(lineSplit[lineSplit.length - 3].replaceAll(",", "").trim()));
//						} else {
//							nsdlEquity.setShares(
//									Long.parseLong(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()));
//						}
//
//						// current value
//						if (lineSplit.length > 2) {
//							nsdlEquity.setCurrentValue(
//									Double.parseDouble(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()));
//						} else {
//							nsdlEquity.setCurrentValue(Double
//									.parseDouble(extralineSplit[extralineSplit.length - 1].replaceAll(",", "").trim()));
//						}
//
//					} else {
//						lineSplit = line.trim().split(" ");
//						nsdlEquity.setIsin(lineSplit[0]);
//						nsdlEquity.setStockSymbol(lineSplit[1]);
//						for (int i = 0; i <= 20; i++) {
//							if (lines[linecounter + i + 1].trim().matches("^(INE).*")
//									|| lines[linecounter + i + 1].trim().matches("^(IN9).*")
//									|| lines[linecounter + i + 1].trim().contains("Sub Total")
//									|| lines[linecounter + i + 1].trim().contains("Consolidated Account Statement")) {
//								lineSplit = lines[linecounter + i].split(" ");
//								break;
//							}
//
//						}
//						if (lineSplit.length == 2) {
//							// current value
//							nsdlEquity.setCurrentValue(Double.parseDouble(lineSplit[1].replaceAll(",", "").trim()));
//							// shares
//							nsdlEquity.setShares(Math.round(nsdlEquity.getCurrentValue()
//									/ Double.parseDouble(lineSplit[0].replaceAll(",", "").trim())));
//						} else {
//							nsdlEquity.setCurrentValue(
//									Double.parseDouble(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()));
//							// shares
//							nsdlEquity.setShares(Math.round(nsdlEquity.getCurrentValue()
//									/ Double.parseDouble(lineSplit[lineSplit.length - 2].replaceAll(",", "").trim())));
//						}
//
//					}
					// Get percentage share
//					float per = (float) (nsdlEquity.getCurrentValue() / nsdlReponse.getNsdlAssetAmount().getEquities())
//							* 100;
//					nsdlEquity.setEquityPercentage(per);
//					nsdlEquities.add(nsdlEquity);

					// Create asset
//					if ("portal".equalsIgnoreCase(source))
//						createAssetForEquities(nsdlEquity, userId, userAssetList, nsdlReponse.getHolderName());
//				}

				// Get all mutual fund details
				if (line.contains("HOLDING STATEMENT") && !line.contains("Other Details")) {
					int l = linecounter;
					linecounter += 13; 
					String tempLine = line;
					line = lines[linecounter];
					while (!(line.contains("HOLDING STATEMENT") && line.contains("(Other Details)")) && !line.contains("Portfolio Value")) {
						
						lineSplit = line.trim().split(" ");
						if ((lineSplit[0].trim().matches("^(INF)[a-zA-Z0-9]{9,}$") || lineSplit[0].trim().matches("^(INF).*"))
								&& !lineSplit[0].trim().contains("INFRA")) {
							NSDLMutualFund nsdlMutualFund = new NSDLMutualFund();
							if (lineSplit[0].matches("^(INF)[a-zA-Z0-9]{9,}$")) {
								nsdlMutualFund.setIsin(line.trim());
								int track = 0;
								StringBuilder mfISINDescription = new StringBuilder();
								for (int i = 1; i <= 10; i++) {
									if (lines[linecounter + i + 1].trim().contains(".")
											|| lines[linecounter + i + 1].trim().contains(",")) {
										lineSplit = lines[linecounter + i + 1].split(" ");
										track = i;
										break;
									}

								}
								for (int j = linecounter + 1; j <= linecounter + track; j++) {
									mfISINDescription.append(" ").append(lines[j]);
								}
								if (!(Float.parseFloat(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()) == 0)) {
									nsdlMutualFund.setIsinDescription(mfISINDescription.toString());
									//System.out.println("SecurityName: " + mfISINDescription.toString());
									nsdlMutualFund.setUnits(Float.parseFloat(lineSplit[0].replaceAll(",", "").trim()));
									//System.out.println("Units: " + Float.parseFloat(lineSplit[0].replaceAll(",", "").trim()));
									// Current value
									nsdlMutualFund.setCurrentValue(Double.parseDouble(lineSplit[6].replaceAll(",", "").trim()));
									//System.out.println("Current Value: " + Double.parseDouble(lineSplit[6].replaceAll(",", "").trim()));
								}
								if (nsdlMutualFund.getIsin()!=null) {
									mutualFunds.add(nsdlMutualFund);
									if ("portal".equalsIgnoreCase(source))
										createAssetForMutualFund(nsdlMutualFund, userId, userAssetList, nsdlReponse.getHolderName());
								}
									
							} 
						}else if(lineSplit[0].trim().matches("^(INE)[a-zA-Z0-9]{9,}$") || lineSplit[0].trim().matches("^(INE).*")) {
							NSDLEquity nsdlEquity = new NSDLEquity();
							int end = 0;
							ArrayList<String> extralineSplit = new ArrayList<String>();
							for(String ele:lineSplit)
								extralineSplit.add(ele);
							
							for (int i = 0; i <= 5; i++) {
								if (lines[linecounter + i + 1].trim().contains("Page")
										|| lines[linecounter + i + 1].trim().split(" ")[0].matches("^(INE).*") 
										|| lines[linecounter + i + 1].trim().split(" ")[0].matches("^(INE)[a-zA-Z0-9]{9,}$")
										|| lines[linecounter + i + 1].trim().contains("Portfolio")
										|| (lines[linecounter + i + 1].trim().contains("HOLDING STATEMENT") 
												&& lines[linecounter + i + 1].trim().contains("(Other Details)"))) {
									end = i;
									break;
								}
							}
							for (int j = linecounter + 1; j <= linecounter + end; j++) {
								lineSplit = lines[j].trim().split(" ");
								for(String ele:lineSplit)
									extralineSplit.add(ele);
							}
							nsdlEquity.setIsin(extralineSplit.get(0));
//							System.out.println("ISIN: " + extralineSplit.get(0));
							StringBuilder symbol = new StringBuilder();
							int i=1;
							for(;i<extralineSplit.size();i++) {
								if(!(extralineSplit.get(i).contains(".")) 
										|| extralineSplit.get(i).contains("RS.")
										|| extralineSplit.get(i).contains("RE.")) {
									symbol.append(extralineSplit.get(i));
									symbol.append(" ");
								} else {
									break;										
								}
							}
							nsdlEquity.setStockSymbol(symbol.toString().trim());
//							System.out.println("SYMBOL: " + symbol.toString().trim());
							if(i<extralineSplit.size()) {
								nsdlEquity.setShares(
										(long)Float.parseFloat(extralineSplit.get(i).replaceAll(",", "").trim()));
//								System.out.println((long)Float.parseFloat(extralineSplit.get(i).replaceAll(",", "").trim()));
							}
							nsdlEquity.setCurrentValue(Double.parseDouble(extralineSplit.get(extralineSplit.size()-1).replaceAll(",", "").trim()));
//							System.out.println("Value: " + extralineSplit.get(extralineSplit.size()-1).replaceAll(",", "").trim());
							nsdlEquities.add(nsdlEquity);
							
							// Create asset
							if ("portal".equalsIgnoreCase(source))
								createAssetForEquities(nsdlEquity, userId, userAssetList, nsdlReponse.getHolderName());
						}
						linecounter++;
						line = lines[linecounter];
					}
					line = tempLine;
					linecounter = l;
				}
				if (line.contains("MUTUAL FUND UNITS HELD AS")) {
					int l = linecounter;
					linecounter += 7; 
					String tempLine = line;
					line = lines[linecounter];
					while (!line.contains("Grand Total")) {
						if ((line.split(" ")[0].trim().matches("^([0-9])[a-zA-Z0-9]*$"))) {
							NSDLMutualFund nsdlMutualFund = new NSDLMutualFund();
							
							if (!line.split(" ")[0].trim().matches("^(INF)[a-zA-Z0-9]{9,}$")) {
								StringBuilder mfISINDescription = new StringBuilder();
								int track = 0;
								for (int i = 0; i <= 10; i++) {
									if (lines[linecounter + i + 1].trim().contains(".")
											|| lines[linecounter + i + 1].trim().contains(",")) {
										lineSplit = lines[linecounter + i + 1].split(" ");
										track = i;
										break;
									}
	
								}
								for (int j = linecounter; j <= linecounter + track; j++) {
									mfISINDescription.append(" ").append(lines[j]);
								}
								int k = 0;
								for (; k < lineSplit.length; k++) {
									if(lineSplit[k].matches("^(INF)[a-zA-Z0-9]{9,}$"))
										break;
									mfISINDescription.append(" ").append(lineSplit[k]);
								}
								nsdlMutualFund.setIsin(lineSplit[k].trim());
								//System.out.println("Isin: " + lineSplit[k].trim());
								
								if (!(Float.parseFloat(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()) == 0)) {
									nsdlMutualFund.setIsinDescription(mfISINDescription.toString());
									//System.out.println("SecurityName: " + mfISINDescription.toString());
									nsdlMutualFund.setUnits(Float.parseFloat(lineSplit[k+2].replaceAll(",", "").trim()));
									//System.out.println("Units: " + Float.parseFloat(lineSplit[k+2].replaceAll(",", "").trim()));
									// Current value
									nsdlMutualFund.setCurrentValue(Double.parseDouble(lineSplit[k+5].replaceAll(",", "").trim()));
									//System.out.println("Current Value: " + Double.parseDouble(lineSplit[k+5].replaceAll(",", "").trim()));
								}
								if (nsdlMutualFund.getIsin()!=null) {
									mutualFunds.add(nsdlMutualFund);
									// Temp commented out as we donot use CDSL for mutual fund
									//if ("portal".equalsIgnoreCase(source))
									//	createAssetForMutualFund(nsdlMutualFund, userId, userAssetList, nsdlReponse.getHolderName());
								}
									
							}
						}
						linecounter++;
						line = lines[linecounter];
					}
					line = tempLine;
					linecounter = l;
				}
				linecounter++;
	//			System.out.println(line);
			}
			nsdlEquities.sort(Comparator.comparing(NSDLEquity::getCurrentValue).reversed());
			nsdlReponse.setNsdlEquities(nsdlEquities);
			nsdlReponse.setNsdlValueTrend(valuetrend);
			nsdlReponse.setNsdlMutualFunds(mutualFunds);
			doc.close();

			if ("portal".equalsIgnoreCase(source) && !userAssetList.isEmpty()) {
				String nick = email.trim().replaceAll(" ", "").toLowerCase();
				UserAsset userAsset = new UserAsset();
				userAsset.setUserId(userId);
				userAssetList.forEach(ua -> ua.setNickName(nick));
				userAsset.setAssets(userAssetList);
				assetService.saveUserAssetsByUserId(userAsset, "cdsl");
			} else {
				getAnalysisData(nsdlReponse, email, cdslFile.getOriginalFilename(), password);
			}
		} catch (IllegalStateException | IOException e) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, e);
		}

		return nsdlReponse;
	}

	private void createAssetForEquities(NSDLEquity nsdlEquity, Long userId, List<UserAssets> userAssetList,
			String holderName) {
		if (nsdlEquity.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(nsdlEquity.getCurrentValue());
			userAssets.setHolderName(holderName);
			userAssets.setCreatedAt(LocalDateTime.now());
			Institution institution = new Institution();
			institution.setId(1);
			userAssets.setAssetProvider(institution);
			AssetType assetType = new AssetType();
			assetType.setId(4);
			assetType.setTypeName("equity");
			userAssets.setAssetType(assetType);
			AssetInstrument assetInstrument = new AssetInstrument();
			assetInstrument.setId(7);
			userAssets.setAssetInstrument(assetInstrument);
			userAssets.setExpectedReturn(12);
			userAssets.setEquityDebtName(nsdlEquity.getStockSymbol());
			userAssets.setCode(nsdlEquity.getIsin());
			userAssets.setUnits((int) nsdlEquity.getShares());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}

	}
	
	private void createAssetForMutualFund(NSDLMutualFund mutualFund, Long userId, List<UserAssets> userAssetList,
			String holderName) {
		if (mutualFund.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(mutualFund.getCurrentValue());
			userAssets.setHolderName(holderName);
			userAssets.setCreatedAt(LocalDateTime.now());
			Institution institution = new Institution();
			institution.setId(1);
			userAssets.setAssetProvider(institution);
			AssetType assetType = new AssetType();
			assetType.setId(4);
			assetType.setTypeName("equity");
			userAssets.setAssetType(assetType);
			AssetInstrument assetInstrument = new AssetInstrument();
			assetInstrument.setId(8);
			userAssets.setAssetInstrument(assetInstrument);
			userAssets.setExpectedReturn(12);
			userAssets.setEquityDebtName(mutualFund.getIsinDescription());
			userAssets.setCode(mutualFund.getIsin());
			userAssets.setUnits((int) mutualFund.getUnits());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}

	}

	private String stringSplit(String str) {
		String[] strSplit = str.trim().split(" ");
		return strSplit[strSplit.length - 2];
	}

	private void getAnalysisData(NSDLReponse nsdlReponse, String email, String fileName, String password) {
		// Get mutual fund underlying stocks
		//System.out.println("1");
		MutualFundAnalysisResponseList mutualFundAnalysisResponseList = getSchemeAnalysis(
				nsdlReponse.getNsdlMutualFunds());
		if(mutualFundAnalysisResponseList.getMfaResponse() == null) 
			mutualFundAnalysisResponseList.setMfaResponse(new ArrayList<MutualFundAnalysisResponse>());
		if(mutualFundAnalysisResponseList.getMfAnalyzed() == null) 
			mutualFundAnalysisResponseList.setMfAnalyzed(new ArrayList<String>());
		if(mutualFundAnalysisResponseList.getMfNotAnalyzed() == null) 
			mutualFundAnalysisResponseList.setMfNotAnalyzed(new ArrayList<String>());
		if(mutualFundAnalysisResponseList.getMfGrowthAnalysis()== null) 
			mutualFundAnalysisResponseList.setMfGrowthAnalysis(new ArrayList<MutualFundGrowthAnalysis>());
		mutualFundAnalysisResponseList.getMfaResponse()
				.sort(Comparator.comparing(MutualFundAnalysisResponse::getAmount).reversed());
		//System.out.println("3");
		nsdlReponse.setMfaResponse(mutualFundAnalysisResponseList.getMfaResponse());
		//System.out.println("4");
		nsdlReponse.setMfAnalyzed(mutualFundAnalysisResponseList.getMfAnalyzed());
		//System.out.println("5");
		nsdlReponse.setMfNotAnalyzed(mutualFundAnalysisResponseList.getMfNotAnalyzed());
		//System.out.println("6");
		nsdlReponse.setMfGrowthAnalysis(mutualFundAnalysisResponseList.getMfGrowthAnalysis());
		//System.out.println("7");
		// Get equity details and sectors
		// Get equity Stock ISIN list
		if (nsdlReponse.getNsdlEquities().size() > 0) {
			String equityStockISINList = nsdlReponse.getNsdlEquities().stream().map(NSDLEquity::getIsin)
					.collect(Collectors.joining(","));
			StockDataList stockDataList = getStockDetails(equityStockISINList);
			// Calculate sector details for equities
			calculateSectorForEquities(nsdlReponse.getNsdlEquities(), stockDataList, nsdlReponse);
		}
		//System.out.println("8");
		List<OverallStockData> overallStock = mutualFundAnalysisResponseList.getMfaResponse().stream()
				.map(m -> new OverallStockData(m.getSymbol(), m.getAmount(),
						(float) ((m.getAmount()
								/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
								* 100),
						m.getIndustry()))
				.collect(Collectors.toList());
		if (nsdlReponse.getNsdlEquities().size() > 0) {
			for (NSDLEquity equity : nsdlReponse.getNsdlEquities()) {
				String[] companyNameSplit = equity.getStockSymbol().split("\\.");
				/*
				 * overallStock.stream().map(x -> { if
				 * (x.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase())
				 * ) { x.setCurrentValue(x.getCurrentValue() + equity.getCurrentValue()); return
				 * x; } else { OverallStockData o = new
				 * OverallStockData(equity.getStockSymbol(), equity.getCurrentValue(), 0,
				 * "TODO"); overallStock.add(o); return o; }
				 * 
				 * });
				 */

				if (overallStock.stream()
						.anyMatch(x -> x.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase()))) {
					/*
					 * overallStock.stream() .filter(x ->
					 * x.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase()))
					 * .findFirst() .peek(x -> x.setCurrentValue(x.getCurrentValue() +
					 * equity.getCurrentValue())) .collect(Collectors.toList());
					 */

					int indexMatch = IntStream.range(0, overallStock.size()).filter(i -> overallStock.get(i)
							.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase())).findFirst()
							.orElse(-1);
					overallStock.get(indexMatch)
							.setCurrentValue(overallStock.get(indexMatch).getCurrentValue() + equity.getCurrentValue());
					overallStock.get(indexMatch)
							.setEquityPercentage((float) ((overallStock.get(indexMatch).getCurrentValue()
									/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
									* 100));

				} else {
					OverallStockData o = new OverallStockData(equity.getStockSymbol(), equity.getCurrentValue(),
							(float) ((equity.getCurrentValue()
									/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
									* 100),
							equity.getIndustry());
					overallStock.add(o);
				}

			}
		}
		//System.out.println("8");
		overallStock.sort(Comparator.comparing(OverallStockData::getCurrentValue).reversed());
		// Testing
		//System.out.println("9");
		double suma = overallStock.stream().filter(o -> o.getEquityPercentage() > 1)
				.mapToDouble(OverallStockData::getEquityPercentage).sum();
		double sumb = overallStock.stream().filter(o -> o.getEquityPercentage() < 1)
				.mapToDouble(OverallStockData::getEquityPercentage).sum();
		System.out.println(suma + "*****************" + sumb);
		//System.out.println("10");
		nsdlReponse.setOverallStock(overallStock);
		
		// Calculate mf sector details
		if (nsdlReponse.getNsdlMutualFunds().size() > 0) {
			/*
			 * Map<String, Double> mfMap =
			 * mutualFundAnalysisResponseList.getMfaResponse().stream()
			 * .collect(Collectors.groupingBy(MutualFundAnalysisResponse::getIndustry,
			 * Collectors.summingDouble(MutualFundAnalysisResponse::getAmount)));
			 */
			Map<String, Double> mfMap = mutualFundAnalysisResponseList.getMfaResponse().stream()
					.collect(Collectors.groupingBy(mf -> mf.getIndustry().toUpperCase(),
							Collectors.summingDouble(MutualFundAnalysisResponse::getAmount)));

			Map<String, Double> mfMapSorted = mfMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));

			nsdlReponse.setMfSector(mfMapSorted);
		}

		// Calculate overall sector details
		if (nsdlReponse.getOverallStock().size() > 0) {
			/*
			 * Map<String, Double> oMap =
			 * nsdlReponse.getOverallStock().stream().collect(Collectors.groupingBy(
			 * OverallStockData::getSector,
			 * Collectors.summingDouble(OverallStockData::getCurrentValue)));
			 */

			Map<String, Double> oMap = nsdlReponse.getOverallStock().stream().collect(Collectors.groupingBy(
					os -> os.getSector().toUpperCase(), Collectors.summingDouble(OverallStockData::getCurrentValue)));
			// oMap.entrySet().stream().sorted(Map.Entry.<String,
			// Double>comparingByValue());
			Map<String, Double> oMapSorted = oMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));
			nsdlReponse.setOverallSector(oMapSorted);
		}

		// Get portfolio analysis
		PortfolioAnalysisReponse portfolioAnalysisReponse = getPortfolioAnalysis(nsdlReponse.getNsdlMutualFunds(),
				nsdlReponse.getNsdlEquities());
		nsdlReponse.setPortfolioAnalysis(portfolioAnalysisReponse);

		// Save user data for future
		asyncService.saveNSDLData(nsdlReponse.getHolderName(), email, fileName, password);
	}

	private MutualFundAnalysisResponseList getSchemeAnalysis(List<NSDLMutualFund> nsdlMutualFunds) {
		if (!nsdlMutualFunds.isEmpty()) {
			LOG.info("API call to POST mutual fund underlying stocks : " + nsdlMutualFunds.size());

			StringBuilder getMFAnalysisURL = new StringBuilder(assetUtil.getProperty("mf.base.url"));
			getMFAnalysisURL.append(assetUtil.getProperty("mf.analysis.url.path"));

			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMFAnalysisURL.toString())
					.queryParam("source", "nsdl");
			MutualFundAnalysis mutualFundAnalysis = new MutualFundAnalysis();
			List<MutualFundAnalysisScheme> mfSchemes = nsdlMutualFunds.stream()
					.map(n -> new MutualFundAnalysisScheme(
							Stream.of(n.getIsinDescription().trim().split("-")).reduce((first, last) -> first).get(),
							n.getCurrentValue(), n.getIsin().trim()))
					.collect(Collectors.toList());
			mutualFundAnalysis.setMfSchemes(mfSchemes);
			HttpEntity<?> entity = new HttpEntity<>(mutualFundAnalysis, headers);

			ResponseEntity<MutualFundAnalysisResponseList> response = restTemplate
					.postForEntity(builder.build().toUri(), entity, MutualFundAnalysisResponseList.class);

			/*
			 * for (MutualFundAnalysisResponse mfr : response.getBody().getMfaResponse()) {
			 * System.out.println("symbol" + mfr.getSymbol() + mfr.getPercentage() +
			 * " list : "); mfr.getSchemeNames().stream().forEach((c) ->
			 * System.out.println(c)); }
			 */
			/*
			 * double suma = response.getBody().getMfaResponse().stream().filter(o ->
			 * o.getPercentage() > 1)
			 * .mapToDouble(MutualFundAnalysisResponse::getPercentage).sum(); double sumb =
			 * response.getBody().getMfaResponse().stream().filter(o -> o.getPercentage() <
			 * 1) .mapToDouble(MutualFundAnalysisResponse::getPercentage).sum();
			 * System.out.println("mf data  " + suma + "*****************" + sumb);
			 */

			LOG.info("API Response for  POST mutual fund underlying stocks : " + response.getStatusCodeValue());
			return response.getBody();

		}
		return new MutualFundAnalysisResponseList();
	}

	private StockDataList getStockDetails(String equityStockISINList) {
		if (StringUtils.isNoneEmpty(equityStockISINList)) {
			LOG.info("API call to GET details for stocks : " + equityStockISINList);
			StringBuilder getSchemeURL = new StringBuilder(assetUtil.getProperty("fund.base.url"));
			getSchemeURL.append(assetUtil.getProperty("fund.equity.url.path"));

			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSchemeURL.toString()).queryParam("isin",
					equityStockISINList);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<StockDataList> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
					entity, StockDataList.class);
			LOG.info("API Response for GET stocks call " + response.getStatusCodeValue());
			return response.getBody();

		}
		return new StockDataList();
	}

	private void calculateSectorForEquities(List<NSDLEquity> nsdlEquities, StockDataList stockDataList,
			NSDLReponse nsdlReponse) {
		LOG.info("Calculate equity sector composition");

		for (NSDLEquity ne : nsdlEquities) {
			StockData stockData = stockDataList.getResponse().stream()
					.filter(x -> x.getIsin() != null && x.getIsin().equals(ne.getIsin())).findFirst().orElse(null);
			if (stockData != null && stockData.getNav() != 0.0) {
				ne.setStockSymbol(stockData.getCompanyname());
				ne.setIndustry(stockData.getSector());
			} else {
				ne.setIndustry("OTHER");
			}
		}

		/*
		 * Map<String, Double> equityMap = nsdlEquities.stream().collect(
		 * Collectors.groupingBy(NSDLEquity::getIndustry,
		 * Collectors.summingDouble(NSDLEquity::getCurrentValue)));
		 */

		Map<String, Double> equityMap = nsdlEquities.stream().collect(Collectors.groupingBy(
				ne -> ne.getIndustry().toUpperCase(), Collectors.summingDouble(NSDLEquity::getCurrentValue)));

		Map<String, Double> equityMapSorted = equityMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		nsdlReponse.setEquitySector(equityMapSorted);
	}

	private PortfolioAnalysisReponse getPortfolioAnalysis(List<NSDLMutualFund> nsdlMutualFunds,
			List<NSDLEquity> nsdlEquities) {
		LOG.info("API call to POST Portfolio analysis FOR EQUITIES " + nsdlEquities.size() + " and for schemes : "
				+ nsdlMutualFunds.size());

		PortfolioAnalysisRequest portfolioAnalysisRequest = new PortfolioAnalysisRequest();

		StringBuilder postPorfolioAnalysisURL = new StringBuilder(assetUtil.getProperty("portfolio.base.url"));
		postPorfolioAnalysisURL.append(assetUtil.getProperty("portfolio.analysis.url.path"));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(postPorfolioAnalysisURL.toString())
				.queryParam("source", "nsdl");
		MutualFundAnalysis mutualFundAnalysis = new MutualFundAnalysis();
		List<MutualFundAnalysisScheme> mfSchemes = nsdlMutualFunds.stream()
				.map(n -> new MutualFundAnalysisScheme(
						Stream.of(n.getIsinDescription().trim().split("-")).reduce((first, last) -> first).get(),
						n.getCurrentValue(), n.getIsin().trim()))
				.collect(Collectors.toList());

		portfolioAnalysisRequest.setMfSchemes(mfSchemes);

		List<Equity> equities = new ArrayList<Equity>();
		if (nsdlEquities.size() > 0)
			equities = nsdlEquities.stream().map(n -> new Equity(n.getIsin(), n.getCurrentValue()))
					.collect(Collectors.toList());

		portfolioAnalysisRequest.setEquities(equities);

		HttpEntity<?> entity = new HttpEntity<>(portfolioAnalysisRequest, headers);

		ResponseEntity<PortfolioAnalysisReponse> response = restTemplate.postForEntity(builder.build().toUri(), entity,
				PortfolioAnalysisReponse.class);
		LOG.info("API call to POST Portfolio analysis " + response.getStatusCodeValue());
		return response.getBody();

	}

}
