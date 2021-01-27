package com.finadv.assets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.finadv.assets.entities.NSDLAssetAmount;
import com.finadv.assets.entities.NSDLEquity;
import com.finadv.assets.entities.NSDLMutualFund;
import com.finadv.assets.entities.NSDLValueTrend;

public class PDFMain {

	public static void main(String[] args) {
		
			PDDocument doc;
			try {
				doc = PDDocument.load(
						new File("D:/Atanu/projects/NSDL OCT _AUSPG0308D.PDF"),
						"AUSPG0308D");
				String text = new PDFTextStripper().getText(doc);
				String lines[] = text.split("\\r?\\n");
				 List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
				 List<NSDLValueTrend> valuetrend = new ArrayList<NSDLValueTrend>();
				 List<NSDLMutualFund> mutualFunds = new ArrayList<NSDLMutualFund>();
				int linecounter = 0;
				for (String line : lines) {
					if (line.toLowerCase().contains("statement for the period")) {
						System.out.println(Stream.of(line.split(" ")).reduce((first, last) -> last).get());
					}

					if (line.toLowerCase().contains("grand total")) {
						System.out.println(
								Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
					}
					if(line.contains("CAS ID")) {
						System.out.println(lines[linecounter + 1]);
					}
					
					// Get portfolio distribution
					/*
					 * if (line.contains("ASSET CLASS Value in ` %")) { NSDLAssetAmount
					 * nsdlAssetAmount = new NSDLAssetAmount(); nsdlAssetAmount.setEquities(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 1].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setPreferenceShares(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 2].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setMutualFunds(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 3].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setCorporateBonds(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 4].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setMoneyMarketInstruments(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 5].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setSecuritisedInstruments(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 6].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setGovernmentSecurities(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 7].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setPostalSavingScheme(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 8].replaceAll(",",
					 * "").trim())) .longValue()); nsdlAssetAmount.setMutualFundFolios(
					 * Double.valueOf(Double.parseDouble(lines[linecounter + 9].replaceAll(",",
					 * "").trim())) .longValue()); }
					 */
					// Get portfolio value trend
					if (line.trim().contains("Change") && lines[linecounter + 1].trim().contains("(%)")) {
						for (int i = linecounter + 2; i < linecounter + 20; i++) {
							if (!(lines[i].contains("+") || lines[i].contains("-") || lines[i].contains("NA NA")))
								break;
							String[] trendSplit = lines[i].trim().split(" ");
							NSDLValueTrend nsdlValueTrend = new NSDLValueTrend();
							nsdlValueTrend.setChangePercentage(trendSplit[trendSplit.length - 1]);
							nsdlValueTrend.setChangeRs(trendSplit[trendSplit.length - 2]);
							nsdlValueTrend.setPortfolioValue(trendSplit[trendSplit.length - 3]);
							nsdlValueTrend.setMonth(trendSplit[0] + trendSplit[1]);

							valuetrend.add(nsdlValueTrend);
						}
					}

					// Get all Equity shares
					String[] lineSplit = new String[0];
					if (line.trim().matches("^(INE)[a-zA-Z0-9]{9,}$") || line.trim().matches("^(INE).*")) {
						NSDLEquity nsdlEquity = new NSDLEquity();
						if(lines[linecounter + 1].contains("NSE")) {
							nsdlEquity.setIsin(line.trim());
							nsdlEquity.setStockSymbol(lines[linecounter + 1].trim());
							for (int i = 1; i <= 5; i++) {
								if (lines[linecounter + i + 1].trim().contains(".")
										|| lines[linecounter + i + 1].trim().contains(",")) {
									lineSplit = lines[linecounter + i + 1].split(" ");
									break;
								}

							}

							nsdlEquity.setShares(Long.parseLong(lineSplit[lineSplit.length - 3].replaceAll(",", "").trim()));
							// current value
							nsdlEquity.setCurrentValue(
									Double.parseDouble(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()));
							// Get percentage share
							/*
							 * float per = (float) (nsdlEquity.getCurrentValue() /
							 * nsdlReponse.getNsdlAssetAmount().getEquities()) 100;
							 * nsdlEquity.setEquityPercentage(per);
							 */
						}else {
							lineSplit = line.trim().split(" ");
							nsdlEquity.setIsin(lineSplit[0]);
							for (int i = 5; i <= 20; i++) {
								if (lines[linecounter + i + 1].trim().matches("^(INE).*") || lines[linecounter + i + 1].trim().contains("Sub Total")) {
									lineSplit = lines[linecounter + i ].split(" ");
									break;
								}

							}
							nsdlEquity.setCurrentValue(
									Double.parseDouble(lineSplit[1].replaceAll(",", "").trim()));
							nsdlEquity.setShares(Math.round(nsdlEquity.getCurrentValue() / Double.parseDouble(lineSplit[0].replaceAll(",", "").trim())));
						}
						
						nsdlEquities.add(nsdlEquity);
					}
					
					// Get all mutual fund details

					if (line.trim().matches("^(INF)[a-zA-Z0-9]{9,}$")) {
						NSDLMutualFund nsdlMutualFund = new NSDLMutualFund();
						nsdlMutualFund.setIsin(line.trim());
						int track = 0;
						StringBuilder mfISINDescription = new StringBuilder();
						for (int i = 1; i <= 7; i++) {
							if (lines[linecounter + i + 1].trim().contains(".")
									|| lines[linecounter + i + 1].trim().contains(",")) {
								lineSplit = lines[linecounter + i + 1].split(" ");
								track = i;
								break;
							}

						}
						for (int j = linecounter + 2; j <= linecounter + track; j++) {
							mfISINDescription.append(" ").append(lines[j]);
						}
						nsdlMutualFund.setIsinDescription(mfISINDescription.toString());
						nsdlMutualFund.setUnits(Float.parseFloat(lineSplit[1].replaceAll(",", "").trim()));
						// Current value
						nsdlMutualFund.setCurrentValue(Double.parseDouble(lineSplit[5].replaceAll(",", "").trim()));
						mutualFunds.add(nsdlMutualFund);
					}

					linecounter++;

					 System.out.println(line);
				}

				/*
				 * ObjectExtractor oe = new ObjectExtractor(doc); SpreadsheetExtractionAlgorithm
				 * sea = new SpreadsheetExtractionAlgorithm();
				 * 
				 * Page page = oe.extract(3); List<Table> table = sea.extract(page);
				 * 
				 * for(Table tables: table) { List<List<RectangularTextContainer>> rows =
				 * tables.getRows();
				 * 
				 * for(int i=0; i<rows.size(); i++) {
				 * 
				 * List<RectangularTextContainer> cells = rows.get(i);
				 * 
				 * for(int j=0; j<cells.size(); j++) {
				 * System.out.print(cells.get(j).getText()+"|"); }
				 * 
				 * System.out.println(); } }
				 */
				
				 System.out.println("outside");
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		

	}

}
