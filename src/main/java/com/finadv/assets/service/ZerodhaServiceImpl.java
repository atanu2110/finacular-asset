package com.finadv.assets.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.finadv.assets.entities.ZerodhaResponse;
import com.finadv.assets.util.AssetUtil;

@Service
public class ZerodhaServiceImpl implements ZerodhaService {

	private static final Logger LOG = LoggerFactory.getLogger(ZerodhaServiceImpl.class);

	private AssetService assetService;

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

	private AsyncService asyncService;

	@Autowired
	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private AssetUtil assetUtil;

	@Override
	public ZerodhaResponse extractFromZerodhaExcel(MultipartFile excelFile, Long userId, String source) {
		LOG.info("Inside  extractFromZerodhaExcel for source : " + source + "  and userId : " + userId);
		ZerodhaResponse zerodhaResponse = new ZerodhaResponse();
		List<UserAssets> userAssetList = new ArrayList<UserAssets>();
		readExcel(excelFile, userId, zerodhaResponse, userAssetList);

		// Saving the User Data
		String nick = zerodhaResponse.getClientId();
		UserAsset userAsset = new UserAsset();
		userAsset.setUserId(userId);
		userAssetList.forEach(ua -> ua.setNickName(nick));
		userAsset.setAssets(userAssetList);
		assetService.saveUserAssetsByUserId(userAsset, "zerodha");
		LOG.info("Exit  extractFromZerodhaExcel for source : " + source + "  and userId : " + userId);
		return zerodhaResponse;
	}

	private void readExcel(MultipartFile excelFile, Long userId, ZerodhaResponse zerodhaResponse,
			List<UserAssets> userAssetList) {
		LOG.info("Inside  readExcel for Zerodha for userId : " + userId);
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream());

			List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
			List<NSDLMutualFund> mutualFunds = new ArrayList<NSDLMutualFund>();
			// List<UserAssets> userAssetList = new ArrayList<UserAssets>();

			XSSFSheet worksheet = workbook.getSheetAt(0);

			// Get Client Id
			XSSFRow row = worksheet.getRow(6);
			String clientId = row.getCell(2).toString();
			zerodhaResponse.setClientId(clientId);
			;

			// Get period
			row = worksheet.getRow(10);
			zerodhaResponse
					.setPeriod((Stream.of(row.getCell(1).toString().split(" ")).reduce((first, last) -> last).get()));
			;

			// Get all Equities
			for (int i = 15; i <= worksheet.getLastRowNum(); i++) {
				NSDLEquity nsdlEquity = new NSDLEquity();

				XSSFRow tempRow = worksheet.getRow(i);

				nsdlEquity.setIsin(tempRow.getCell(2).toString());
				nsdlEquity.setStockSymbol(tempRow.getCell(1).toString());
				nsdlEquity.setShares((long) Double.parseDouble(tempRow.getCell(4).toString()));
				nsdlEquity.setCurrentValue(Double.parseDouble(tempRow.getCell(10).toString()) * nsdlEquity.getShares());

				nsdlEquities.add(nsdlEquity);
				createAssetForEquities(nsdlEquity, userId, userAssetList, zerodhaResponse.getClientId());
			}

			// Mutual Fund Data
			worksheet = workbook.getSheetAt(1);

			for (int i = 15; i <= worksheet.getLastRowNum(); i++) {
				NSDLMutualFund nsdlMutualFund = new NSDLMutualFund();

				XSSFRow tempRow = worksheet.getRow(i);

				String schemeSymbol = tempRow.getCell(1).toString();
				nsdlMutualFund.setIsin(tempRow.getCell(2).toString());
				nsdlMutualFund.setUnits(Float.parseFloat(tempRow.getCell(4).toString()));
				nsdlMutualFund
						.setCurrentValue(Double.parseDouble(tempRow.getCell(9).toString()) * nsdlMutualFund.getUnits());
				nsdlMutualFund.setIsinDescription(schemeSymbol);

				mutualFunds.add(nsdlMutualFund);
				createAssetForMutualFund(nsdlMutualFund, schemeSymbol, userId, userAssetList,
						zerodhaResponse.getClientId());
			}

			zerodhaResponse.setNsdlEquities(nsdlEquities);
			zerodhaResponse.setNsdlMutualFunds(mutualFunds);
			LOG.info("Exit  readExcel for Zerodha for userId : " + userId);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	private void createAssetForEquities(NSDLEquity nsdlEquity, Long userId, List<UserAssets> userAssetList,
			String clientId) {
		if (nsdlEquity.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(nsdlEquity.getCurrentValue());
			userAssets.setHolderName(clientId);
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
			userAssets.setExpectedReturn(10);
			userAssets.setEquityDebtName(nsdlEquity.getStockSymbol());
			userAssets.setCode(nsdlEquity.getIsin());
			userAssets.setUnits((int) nsdlEquity.getShares());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}
	}

	private void createAssetForMutualFund(NSDLMutualFund nsdlMutualFund, String schemeSymbol, Long userId,
			List<UserAssets> userAssetList, String clientId) {
		if (nsdlMutualFund.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(nsdlMutualFund.getCurrentValue());
			userAssets.setHolderName(clientId);
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
			userAssets.setEquityDebtName(schemeSymbol);
			userAssets.setCode(nsdlMutualFund.getIsin());
			userAssets.setUnits((int) nsdlMutualFund.getUnits());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}
	}

	@Override
	public NSDLReponse portfolioAnalyzeFromZerodhaExcel(MultipartFile excelFile, Long userId, String source) {
		LOG.info("Inside  portfolioAnalyzeFromZerodhaExcel for source : " + source + "  and excel : " + excelFile.getOriginalFilename());
		ZerodhaResponse zerodhaResponse = new ZerodhaResponse();
		List<UserAssets> userAssetList = new ArrayList<UserAssets>();
		readExcel(excelFile, userId, zerodhaResponse, userAssetList);

		NSDLReponse nsdlReponse = new NSDLReponse();
		nsdlReponse.setPeriod(zerodhaResponse.getPeriod());
		nsdlReponse.setHolderName(zerodhaResponse.getClientId());

		double equityTotalAmount = zerodhaResponse.getNsdlEquities().stream().mapToDouble(NSDLEquity::getCurrentValue)
				.sum();
		double mfTotalAmount = zerodhaResponse.getNsdlMutualFunds().stream()
				.mapToDouble(NSDLMutualFund::getCurrentValue).sum();
		nsdlReponse.setAmount(equityTotalAmount + mfTotalAmount);

		NSDLAssetAmount nsdlAssetAmount = new NSDLAssetAmount();
		nsdlAssetAmount.setEquities(Double.valueOf(equityTotalAmount).longValue());
		nsdlAssetAmount.setMutualFundFolios(Double.valueOf(mfTotalAmount).longValue());
		nsdlReponse.setNsdlAssetAmount(nsdlAssetAmount);

		nsdlReponse.setNsdlEquities(zerodhaResponse.getNsdlEquities());
		nsdlReponse.setNsdlMutualFunds(zerodhaResponse.getNsdlMutualFunds());
		List<NSDLValueTrend> valuetrend = new ArrayList<NSDLValueTrend>();
		nsdlReponse.setNsdlValueTrend(valuetrend);

		getAnalysisData(nsdlReponse, excelFile.getOriginalFilename());

		LOG.info("Exit  portfolioAnalyzeFromZerodhaExcel for source : " + source + "  and excel : " + excelFile.getOriginalFilename());
		// Store file in S3
		// asyncService.uploadFile(tempNSDLFile);
		return nsdlReponse;
	}

	private void getAnalysisData(NSDLReponse nsdlReponse, String originalFilename) {
		LOG.info("Inside  getAnalysisData for Zerodha : " + originalFilename);
		// Get mutual fund underlying stocks
		MutualFundAnalysisResponseList mutualFundAnalysisResponseList = getSchemeAnalysis(
				nsdlReponse.getNsdlMutualFunds());
		mutualFundAnalysisResponseList.getMfaResponse()
				.sort(Comparator.comparing(MutualFundAnalysisResponse::getAmount).reversed());
		nsdlReponse.setMfaResponse(mutualFundAnalysisResponseList.getMfaResponse());
		nsdlReponse.setMfAnalyzed(mutualFundAnalysisResponseList.getMfAnalyzed());
		nsdlReponse.setMfNotAnalyzed(mutualFundAnalysisResponseList.getMfNotAnalyzed());
		nsdlReponse.setMfGrowthAnalysis(mutualFundAnalysisResponseList.getMfGrowthAnalysis());

		// Get equity details and sectors
		// Get equity Stock ISIN list
		if (nsdlReponse.getNsdlEquities().size() > 0) {
			String equityStockISINList = nsdlReponse.getNsdlEquities().stream().map(NSDLEquity::getIsin)
					.collect(Collectors.joining(","));
			StockDataList stockDataList = getStockDetails(equityStockISINList);
			// Calculate sector details for equities
			calculateSectorForEquities(nsdlReponse.getNsdlEquities(), stockDataList, nsdlReponse);
		}

		List<OverallStockData> overallStock = mutualFundAnalysisResponseList.getMfaResponse().stream()
				.map(m -> new OverallStockData(m.getSymbol(), m.getAmount(),
						(float) ((m.getAmount()
								/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
								* 100),
						(long) m.getAmount(), 0, m.getIndustry()))
				.collect(Collectors.toList());

		if (nsdlReponse.getNsdlEquities().size() > 0) {
			for (NSDLEquity equity : nsdlReponse.getNsdlEquities()) {
				String[] companyNameSplit = equity.getStockSymbol().split("\\.");

				if (overallStock.stream()
						.anyMatch(x -> x.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase()))) {
					int indexMatch = IntStream.range(0, overallStock.size()).filter(i -> overallStock.get(i)
							.getStockSymbol().toLowerCase().contains(companyNameSplit[0].toLowerCase())).findFirst()
							.orElse(-1);
					overallStock.get(indexMatch)
							.setCurrentValue(overallStock.get(indexMatch).getCurrentValue() + equity.getCurrentValue());
					overallStock.get(indexMatch)
							.setEquityPercentage((float) ((overallStock.get(indexMatch).getCurrentValue()
									/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
									* 100));
					overallStock.get(indexMatch).setDirectInvestment(
							(long) (overallStock.get(indexMatch).getDirectInvestment() + equity.getCurrentValue()));
				} else {
					OverallStockData o = new OverallStockData(equity.getStockSymbol(), equity.getCurrentValue(),
							(float) ((equity.getCurrentValue()
									/ (nsdlReponse.getAmount() - mutualFundAnalysisResponseList.getAmountNotAnalyzed()))
									* 100),
							0, (long) equity.getCurrentValue(), equity.getIndustry());
					overallStock.add(o);
				}

			}
		}

		overallStock.sort(Comparator.comparing(OverallStockData::getCurrentValue).reversed());
		double suma = overallStock.stream().filter(o -> o.getEquityPercentage() > 1)
				.mapToDouble(OverallStockData::getEquityPercentage).sum();
		double sumb = overallStock.stream().filter(o -> o.getEquityPercentage() < 1)
				.mapToDouble(OverallStockData::getEquityPercentage).sum();
		nsdlReponse.setOverallStock(overallStock);

		// Calculate mf sector details
		if (nsdlReponse.getNsdlMutualFunds().size() > 0) {
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

			Map<String, Double> oMap = nsdlReponse.getOverallStock().stream().collect(Collectors.groupingBy(
					os -> os.getSector().toUpperCase(), Collectors.summingDouble(OverallStockData::getCurrentValue)));

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
		asyncService.saveNSDLData(nsdlReponse.getHolderName(), "", "zerodha-"+originalFilename,
		 "");
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
