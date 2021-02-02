package com.finadv.assets.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.Institution;
import com.finadv.assets.entities.NSDLAssetAmount;
import com.finadv.assets.entities.NSDLEquity;
import com.finadv.assets.entities.NSDLMutualFund;
import com.finadv.assets.entities.NSDLReponse;
import com.finadv.assets.entities.NSDLValueTrend;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;

/**
 * @author ATANU
 *
 */
@Service
public class NSDLServiceImpl implements NSDLService {
	private AssetService assetService;

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

	@Override
	public NSDLReponse extractFromNSDL(MultipartFile nsdlFile, String password, Long userId, String source) {
		String temDirectory = "java.io.tmpdir";
		File tempNSDLFile = new File(System.getProperty(temDirectory) + "/" + nsdlFile.getOriginalFilename());
		PDDocument doc;

		NSDLReponse nsdlReponse = new NSDLReponse();
		List<UserAssets> userAssetList = new ArrayList<UserAssets>();
		try {
			nsdlFile.transferTo(tempNSDLFile);

			doc = PDDocument.load(tempNSDLFile, password);
			String text = new PDFTextStripper().getText(doc);
			String lines[] = text.split("\\r?\\n");
			List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
			List<NSDLValueTrend> valuetrend = new ArrayList<NSDLValueTrend>();
			List<NSDLMutualFund> mutualFunds = new ArrayList<NSDLMutualFund>();
			int linecounter = 0;
			for (String line : lines) {
				if (line.toLowerCase().contains("statement for the period")) {
					nsdlReponse.setPeriod((Stream.of(line.split(" ")).reduce((first, last) -> last).get()));
				}

				if (line.toLowerCase().contains("grand total")) {
					nsdlReponse.setAmount(
							Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
				}
				if (line.contains("CAS ID")) {
					nsdlReponse.setHolderName(lines[linecounter + 1]);
				}
				// Get portfolio distribution
				if (line.contains("ASSET CLASS Value in ` %")) {
					NSDLAssetAmount nsdlAssetAmount = new NSDLAssetAmount();
					nsdlAssetAmount.setEquities(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 1].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setPreferenceShares(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 2].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setMutualFunds(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 3].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setCorporateBonds(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 4].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setMoneyMarketInstruments(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 5].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setSecuritisedInstruments(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 6].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setGovernmentSecurities(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 7].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setPostalSavingScheme(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 8].replaceAll(",", ""))))
									.longValue());
					nsdlAssetAmount.setMutualFundFolios(
							Double.valueOf(Double.parseDouble(stringSplit(lines[linecounter + 9].replaceAll(",", ""))))
									.longValue());

					nsdlReponse.setNsdlAssetAmount(nsdlAssetAmount);
				}
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
					if (lines[linecounter + 1].contains("NSE")) {
						nsdlEquity.setIsin(line.trim());
						nsdlEquity.setStockSymbol(lines[linecounter + 1].trim());
						for (int i = 1; i <= 5; i++) {
							if (lines[linecounter + i + 1].trim().contains(".")
									|| lines[linecounter + i + 1].trim().contains(",")) {
								lineSplit = lines[linecounter + i + 1].split(" ");
								break;
							}

						}

						nsdlEquity
								.setShares(Long.parseLong(lineSplit[lineSplit.length - 3].replaceAll(",", "").trim()));
						// current value
						nsdlEquity.setCurrentValue(
								Double.parseDouble(lineSplit[lineSplit.length - 1].replaceAll(",", "").trim()));

					} else {
						lineSplit = line.trim().split(" ");
						nsdlEquity.setIsin(lineSplit[0]);
						nsdlEquity.setStockSymbol(lineSplit[1]);
						for (int i = 5; i <= 20; i++) {
							if (lines[linecounter + i + 1].trim().matches("^(INE).*")
									|| lines[linecounter + i + 1].trim().contains("Sub Total")) {
								lineSplit = lines[linecounter + i].split(" ");
								break;
							}

						}
						// current value
						nsdlEquity.setCurrentValue(Double.parseDouble(lineSplit[1].replaceAll(",", "").trim()));
						// shares
						nsdlEquity.setShares(Math.round(nsdlEquity.getCurrentValue()
								/ Double.parseDouble(lineSplit[0].replaceAll(",", "").trim())));
					}
					// Get percentage share
					float per = (float) (nsdlEquity.getCurrentValue() / nsdlReponse.getNsdlAssetAmount().getEquities())
							* 100;
					nsdlEquity.setEquityPercentage(per);
					nsdlEquities.add(nsdlEquity);
					
					//Create asset
					if ("portal".equalsIgnoreCase(source))
							createAssetForEquities(nsdlEquity, userId, userAssetList, nsdlReponse.getHolderName());
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
				// System.out.println(line);
			}

			nsdlReponse.setNsdlEquities(nsdlEquities);
			nsdlReponse.setNsdlValueTrend(valuetrend);
			nsdlReponse.setNsdlMutualFunds(mutualFunds);
			doc.close();

			if ("portal".equalsIgnoreCase(source)) {
				UserAsset userAsset = new UserAsset();
				userAsset.setUserId(userId);
				userAsset.setAssets(userAssetList);
				assetService.saveUserAssetsByUserId(userAsset, "nsdl");
			}
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
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
			userAssets.setAssetType(assetType);
			AssetInstrument assetInstrument = new AssetInstrument();
			assetInstrument.setId(7);
			userAssets.setAssetInstrument(assetInstrument);
			userAssets.setExpectedReturn(10);
			userAssets.setEquityDebtName(nsdlEquity.getStockSymbol());
			userAssets.setCode(nsdlEquity.getIsin());
			userAssets.setUnits((int) nsdlEquity.getShares());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}

	}

	private String stringSplit(String str) {
		String[] strSplit = str.trim().split(" ");
		return strSplit[strSplit.length - 2];
	}

}
