package com.finadv.assets.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.finadv.assets.dto.PortfolioHistoryResponse;
import com.finadv.assets.dto.PortfolioHistoryResponseList;
import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.dto.UserAssetOverviewDto;
import com.finadv.assets.dto.UserAssetsDto;
import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.CurrentAsset;
import com.finadv.assets.entities.CurrentGrowthRequest;
import com.finadv.assets.entities.CurrentGrowthResponse;
import com.finadv.assets.entities.CurrentGrowthResponseList;
import com.finadv.assets.entities.FundDataList;
import com.finadv.assets.entities.FundDataResponse;
import com.finadv.assets.entities.MutualFundData;
import com.finadv.assets.entities.MutualFundDataList;
import com.finadv.assets.entities.PortfolioHistory;
import com.finadv.assets.entities.StockData;
import com.finadv.assets.entities.StockDataList;
import com.finadv.assets.entities.TargetAllocation;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.UserIncomeExpenseDetail;
import com.finadv.assets.mapper.AssetMapper;
import com.finadv.assets.repository.AssetInstrumentRepository;
import com.finadv.assets.repository.AssetTypeRepository;
import com.finadv.assets.repository.PortfolioHistoryRepository;
import com.finadv.assets.repository.UserAssetRepository;
import com.finadv.assets.repository.UserIncomeExpenseRepository;
import com.finadv.assets.util.AssetUtil;

@Service
public class AssetServiceImpl implements AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetServiceImpl.class);

	private AssetInstrumentRepository assetInstrumentRepository;
	private AssetTypeRepository assetTypeRepository;
	private UserAssetRepository userAssetRepository;
	private UserIncomeExpenseRepository userIncomeExpenseRepository;
	private PortfolioHistoryRepository portfolioHistoryRepository;

	private AssetMapper assetMapper;
	private AsyncService asyncService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private AssetUtil assetUtil;

	@Autowired
	public void setUserAssetRepository(UserAssetRepository userAssetRepository) {
		this.userAssetRepository = userAssetRepository;
	}

	@Autowired
	public void setAssetInstrumentRepository(AssetInstrumentRepository assetInstrumentRepository) {
		this.assetInstrumentRepository = assetInstrumentRepository;
	}

	@Autowired
	public void setAssetTypeRepository(AssetTypeRepository assetTypeRepository) {
		this.assetTypeRepository = assetTypeRepository;
	}

	@Autowired
	public void setUserIncomeExpenseRepository(UserIncomeExpenseRepository userIncomeExpenseRepository) {
		this.userIncomeExpenseRepository = userIncomeExpenseRepository;
	}

	@Autowired
	public void setAssetMapper(AssetMapper assetMapper) {
		this.assetMapper = assetMapper;
	}

	@Autowired
	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	@Autowired
	public void setPortfolioHistoryRepository(PortfolioHistoryRepository portfolioHistoryRepository) {
		this.portfolioHistoryRepository = portfolioHistoryRepository;
	}

	public static final float CASH_RETURN = 2;
	public static final float EQUITY_RETURN = 12;
	public static final float DEBT_RETURN = 5;
	public static final float GOV_FUND_RETURN = 7;
	public static final float REAL_ESTATE_RETURN = 5;
	public static final float COMMODITIES_RETURN = 6;

	@Override
	public List<AssetInstrument> getAssetDetails() {
		return assetInstrumentRepository.findAll();
	}

	/**
	 *
	 */
	@Override
	public List<AssetInstrument> getAssetDetailsByType(int type) {
		return assetInstrumentRepository.findAll().stream().filter(x -> x.getAssetTypeId().getId() == type)
				.collect(Collectors.toList());
	}

	/**
	 *
	 */
	@Override
	public List<AssetType> getAllAssetTypes() {
		return assetTypeRepository.findAll();
	}

	@Override
	public UserAssetDto getUserAssetByUserId(long userId) {
		UserAsset userAsset = new UserAsset();
		LOG.info("Get user asset for userId: " + userId);
		List<UserAssets> assets = userAssetRepository.findUserAssetByUserId(userId);
		userAsset.setAssets(assets);
		userAsset.setUserId(userId);

		UserAssetDto userAssetDto = assetMapper.convertToUserAssetDto(userAsset);
		calculateCurrentValueOfAssets(userAssetDto);
		return userAssetDto;
	}

	@Override
	public void saveUserAssetsByUserId(UserAsset userAsset, String source) {

		// If user already has the asset then simply add the amount to exisitng
		// one
		// List<UserAssets> assetList =
		// userAssetRepository.findUserAssetByUserId(userAsset.getUserId());

		userAsset.getAssets().forEach(a -> a.setCreatedAt(LocalDateTime.now()));

		// Check with email for mf or stock asset and on every cams/nsdl update removew
		// and recreate user asset
		if ("cams".equalsIgnoreCase(source)) {
			// delete existing asset to recreate
			int assetInstrumentId = "cams".equalsIgnoreCase(source) ? 8 : 7;
			userAssetRepository.deleteUserAssetByUserIdEmailAndInstrument(userAsset.getUserId(),
					userAsset.getAssets().get(0).getNickName(), assetInstrumentId);

			// Now add all the new assets from cams/nsdl
			userAssetRepository.saveAll(userAsset.getAssets());
		} else if ("nsdl".equalsIgnoreCase(source) || "cdsl".equalsIgnoreCase(source)
				|| "zerodha".equalsIgnoreCase(source)) {
			Set<Long> assetTypeSet = userAsset.getAssets().stream().map(a -> a.getAssetInstrument().getId())
					.collect(Collectors.toSet());
			if(userAsset.getAssets().size() > 0)
			userAssetRepository.deleteUserAssetByUserIdEmailAndInInstrument(userAsset.getUserId(),
					userAsset.getAssets().get(0).getNickName(), assetTypeSet);

			// Set proper rtCode for mutual fund ISIN
			// Get equity MF ISIN list
			String mfIsinList = userAsset.getAssets().stream()
					.filter(a -> a.getAssetType().getId() == 4 && a.getAssetInstrument().getId() == 8
							&& a.getCode().matches("^(INF)[a-zA-Z0-9]{9,}$"))
					.map(UserAssets::getCode).collect(Collectors.joining(","));
			if (StringUtils.isNoneEmpty(mfIsinList)) {
				MutualFundDataList mutualFundDataList = new MutualFundDataList();
				mutualFundDataList = getMFData(mfIsinList);

				for (UserAssets u : userAsset.getAssets()) {
					if (u.getAssetInstrument().getId() == 8) {
						MutualFundData mfd = mutualFundDataList.getResponse().stream()
								.filter(x -> !StringUtils.isEmpty(u.getCode())
										&& !StringUtils.isEmpty(x.getIsincodedirect())
										&& !StringUtils.isEmpty(x.getIsincoderegular())
										&& (x.getIsincodedirect().equalsIgnoreCase(u.getCode())
												|| x.getIsincoderegular().equals(u.getCode())))
								.findFirst().orElse(null);
						if (mfd != null && StringUtils.isNotEmpty(mfd.getRtcode())) {
							u.setCode(mfd.getRtcode());
						}
					}
				}
			}

			// Now add all the new assets from cams/nsdl
			userAssetRepository.saveAll(userAsset.getAssets());

		}

		else {
			/*
			 * for (UserAssets userAssets : userAsset.getAssets()) { if
			 * ((userAssets.getAssetInstrument().getId() == 8 ||
			 * userAssets.getAssetInstrument().getId() == 7) && assetList.size() > 0) {
			 * UserAssets assetInDB = assetList.stream().filter( x ->
			 * StringUtils.isNotEmpty(x.getCode()) &&
			 * x.getCode().equalsIgnoreCase(userAssets.getCode()))
			 * .findFirst().orElse(null); if (assetInDB != null) {
			 * assetInDB.setAmount(assetInDB.getAmount() + userAssets.getAmount()); if
			 * ("cams".equalsIgnoreCase(source) || "nsdl".equalsIgnoreCase(source))
			 * assetInDB.setAmount(userAssets.getAmount());
			 * assetInDB.setUnits(userAssets.getUnits()); updateUserAsset(assetInDB); } else
			 * { userAssets.setCreatedAt(LocalDateTime.now());
			 * userAssetRepository.save(userAssets); }
			 * 
			 * } else { userAssets.setCreatedAt(LocalDateTime.now());
			 * userAssetRepository.save(userAssets); } }
			 */
			userAssetRepository.saveAll(userAsset.getAssets());
		}

		// Async : call to save user investment as investment tracker - Temp commented as we donot user investment tracker for now
	//	asyncService.callSaveInvestment(userAsset);
	}

	@Override
	public UserAssets updateUserAsset(UserAssets userAsset) {
		UserAssets userAssetsInDB = userAssetRepository.findById(userAsset.getId()).orElse(null);
		if (userAssetsInDB != null) {
			userAsset.setUpdatedAt(LocalDateTime.now());
			userAssetRepository.save(userAsset);
			return userAsset;
		}
		return null;
	}

	@Override
	public void deleteUserAsset(long assetId) {
		userAssetRepository.deleteById((int) assetId);
	}

	@Override
	public void purgeUserAsset(long userId) {
		userAssetRepository.deleteUserAssetByUserId(userId);
	}

	@Override
	public CurrentGrowthResponseList getCurrentGrowth(CurrentGrowthRequest currentGrowth) {
		CurrentGrowthResponseList currentGrowthResponseList = new CurrentGrowthResponseList();
		float returnPercentage = 0.0f;
		// Total asset amount
		long totalAmount = currentGrowth.getCurrentAssetList().stream().map(item -> item.getAmount()).reduce(0l,
				(a, b) -> a + b);

		// Calculate Estimated return %
		float totalWeight = 0;
		for (CurrentAsset currentAsset : currentGrowth.getCurrentAssetList()) {
			totalWeight += currentAsset.getExpectedReturn() * ((float) (currentAsset.getAmount() * 100) / totalAmount);
		}

		returnPercentage = totalWeight / 100;

		List<CurrentGrowthResponse> currentAssetGrowth = new ArrayList<CurrentGrowthResponse>();
		CurrentGrowthResponse year0CurrentGrowthResponse = new CurrentGrowthResponse();
		year0CurrentGrowthResponse.setAge(currentGrowth.getAge());
		year0CurrentGrowthResponse.setAmount(totalAmount + currentGrowth.getIncome() - currentGrowth.getExpense());
		currentAssetGrowth.add(year0CurrentGrowthResponse);

		// Calculate CI
		long amountOnYear = totalAmount;
		long incomeYOY = currentGrowth.getIncome();
		long expenseYOY = currentGrowth.getExpense();

		for (int i = 1; i <= 30; i++) {
			amountOnYear = (long) (amountOnYear + (amountOnYear * (returnPercentage / 100)));
			incomeYOY = incomeYOY + ((incomeYOY * 10) / 100);
			expenseYOY = expenseYOY + ((expenseYOY * 8) / 100);
			CurrentGrowthResponse currentGrowthResponse = new CurrentGrowthResponse();
			currentGrowthResponse.setAge(currentGrowth.getAge() + i);
			currentGrowthResponse.setAmount(amountOnYear + incomeYOY - expenseYOY);
			currentAssetGrowth.add(currentGrowthResponse);
		}
		currentGrowthResponseList.setCurrentGrowth(currentAssetGrowth);
		currentGrowthResponseList.setCurrentGrowthRate(returnPercentage);
		return currentGrowthResponseList;
	}

	@Override
	public UserIncomeExpenseDetail getUserIncomeExpenseById(long userId) {
		return userIncomeExpenseRepository.findUserIncomeExpenseById(userId);
	}

	@Override
	public void createUserIncomeExpense(UserIncomeExpenseDetail userIncomeExpenseDetail) {
		userIncomeExpenseDetail.setCreatedAt(Date.from(Instant.now()));
		userIncomeExpenseRepository.save(userIncomeExpenseDetail);
	}

	private void calculateCurrentValueOfAssets(UserAssetDto userAssetDto) {
		LOG.info("Get current values for assets!!");
		userAssetDto.getAssets().forEach(a -> a.setCurrentValue(a.getAmount()));

		// "typeName": "cash" "instrumentName": "savings",
		// "typeName": "debt" "instrumentName": "Debt Mutual Fund",
		// "typeName": "equity""instrumentName": "Stocks",
		// "typeName": "equity" "instrumentName": "Mutual Fund",

		// Get equity MF ISIN list
		String equityMFISINList = userAssetDto.getAssets().stream()
				.filter(a -> a.getAssetType().getId() == 4 && a.getAssetInstrument().getId() == 8)
				.map(UserAssetsDto::getCode).collect(Collectors.joining(","));
		FundDataList fundDataList = getSchemeDetails(equityMFISINList);
		MutualFundDataList mutualFundDataList = getMFData(equityMFISINList);
		// Get equity Stock ISIN list
		String equityStockISINList = userAssetDto.getAssets().stream()
				.filter(a -> a.getAssetType().getId() == 4 && a.getAssetInstrument().getId() == 7)
				.map(UserAssetsDto::getCode).collect(Collectors.joining(","));
		StockDataList stockDataList = getStockDetails(equityStockISINList);
		// long[] instrumentIds = { 1, 2, 3, 4, 9, 10, 11 };

		List<UserAssetOverviewDto> assetOverview = new ArrayList<UserAssetOverviewDto>();

		for (UserAssetsDto uaDto : userAssetDto.getAssets()) {
			// if (LongStream.of(instrumentIds).anyMatch(x -> x ==
			// uaDto.getAssetInstrument().getId())) {

			// if (uaDto.getAssetInstrument().getId() == 1 ||
			// uaDto.getAssetInstrument().getId() == 2) {

			if (uaDto.getAssetInstrument().getId() == 2) {
				getCurrentAmount(uaDto.getExpectedReturn(), uaDto.getAmount(), uaDto);
			} else if (uaDto.getAssetInstrument().getId() == 8) {
				FundDataResponse fdResponse = fundDataList.getResponse().stream()
						.filter(x -> x.getData() != null && StringUtils.isNotEmpty(x.getData().getRtcode())
								&& x.getData().getRtcode().equalsIgnoreCase(uaDto.getCode()))
						.findFirst().orElse(null);
				if (fdResponse != null) {
					uaDto.setCurrentValue(fdResponse.getData().getNav() * uaDto.getUnits());
					uaDto.setEquityDebtName(fdResponse.getData().getSchemenamecmapis());
					if (StringUtils.isNotEmpty(fdResponse.getData().getCategory()))
						uaDto.setSchemeType(fdResponse.getData().getCategory());
					if (StringUtils.isNoneEmpty(fdResponse.getData().getIsin()))
						uaDto.setCode(fdResponse.getData().getIsin());

				} else {
					MutualFundData mutualFundData = mutualFundDataList.getResponse().stream()
							.filter(x -> (StringUtils.isNoneEmpty(x.getIsincodedirect())
									&& StringUtils.isNoneEmpty(x.getIsincoderegular()))
									&& (x.getIsincodedirect().equalsIgnoreCase(uaDto.getCode())
											|| x.getIsincoderegular().equalsIgnoreCase(uaDto.getCode())))
							.findFirst().orElse(null);
					if( mutualFundData != null && mutualFundData.getNav() != 0)
						uaDto.setCurrentValue(mutualFundData.getNav() * uaDto.getUnits());
					if (mutualFundData != null) {
						String schemename = StringUtils.isEmpty(mutualFundData.getSchemenamecmapis())
								? mutualFundData.getSchemename()
								: mutualFundData.getSchemenamecmapis();
						uaDto.setEquityDebtName(schemename);
					}

					if (mutualFundData != null && StringUtils.isNotEmpty(mutualFundData.getCategory()))
						uaDto.setSchemeType(mutualFundData.getCategory());
				}
				if (StringUtils.isNotEmpty(uaDto.getSchemeType())
						&& uaDto.getSchemeType().equalsIgnoreCase("Debt Scheme")) {
					float expectedReturns = uaDto.getEquityDebtName().toLowerCase().contains("liquid") ? 5f : 6.5f;
					uaDto.setExpectedReturn(expectedReturns);
					uaDto.getAssetType().setId(2);
					uaDto.getAssetType().setTypeName("debt");
					uaDto.getAssetInstrument().setId(5);
					uaDto.getAssetInstrument().setInstrumentName("Debt Mutual Fund");
					uaDto.getAssetInstrument().setDefaultReturns(8);
				}

			} else if (uaDto.getAssetInstrument().getId() == 7) {
				StockData stockData = stockDataList.getResponse().stream()
						.filter(x -> x.getIsin() != null && x.getIsin().equals(uaDto.getCode())).findFirst()
						.orElse(null);
				if (stockData != null && stockData.getNav() != 0.0) {
					uaDto.setCurrentValue(stockData.getNav() * uaDto.getUnits());
					uaDto.setEquityDebtName(stockData.getCompanyname());
				}
			} /*
				 * else { Period period = Period.between(uaDto.getUpdatedAt() == null ?
				 * uaDto.getCreatedAt().toLocalDate() : uaDto.getUpdatedAt().toLocalDate(),
				 * LocalDateTime.now().toLocalDate()); uaDto.setCurrentValue( uaDto.getAmount()
				 * + (uaDto.getAmount() * uaDto.getExpectedReturn() * period.getYears())); }
				 */

			String overviewTypeName = uaDto.getAssetType().getTypeName() + '-'
					+ uaDto.getAssetInstrument().getInstrumentName();
			int indexMatch = -1;
			if (uaDto.getAssetType().getId() != 1 && uaDto.getAssetType().getId() != 2
					&& uaDto.getAssetType().getId() != 4) {
				indexMatch = IntStream.range(0, assetOverview.size()).filter(
						i -> assetOverview.get(i).getType().equalsIgnoreCase(uaDto.getAssetType().getTypeName()))
						.findFirst().orElse(-1);
				overviewTypeName = uaDto.getAssetType().getTypeName();
			} else {
				indexMatch = IntStream.range(0, assetOverview.size())
						.filter(i -> assetOverview.get(i).getType()
								.equalsIgnoreCase(uaDto.getAssetType().getTypeName() + '-'
										+ uaDto.getAssetInstrument().getInstrumentName())
								&& assetOverview.get(i).getHolderName().equalsIgnoreCase(uaDto.getHolderName()))
						.findFirst().orElse(-1);
			}

			if (indexMatch == -1) {
				UserAssetOverviewDto userAssetOverviewDto = new UserAssetOverviewDto();
				userAssetOverviewDto.setAmount(uaDto.getCurrentValue());
				userAssetOverviewDto.setExpectedReturn(uaDto.getExpectedReturn());
				userAssetOverviewDto.setHolderName(uaDto.getHolderName());
				userAssetOverviewDto.setType(overviewTypeName.toUpperCase());

				assetOverview.add(userAssetOverviewDto);
			} else {

				assetOverview.get(indexMatch)
						.setAmount(assetOverview.get(indexMatch).getAmount() + uaDto.getCurrentValue());
				assetOverview.get(indexMatch).setExpectedReturn(
						(assetOverview.get(indexMatch).getExpectedReturn() + uaDto.getExpectedReturn()) / 2);

			}

		}

		double totalAmount = userAssetDto.getAssets().stream().mapToDouble(UserAssetsDto::getCurrentValue).sum();
		assetOverview.forEach(ao -> ao.setCurrentAllocation((float) (ao.getAmount() / totalAmount) * 100));
		assetOverview.sort(Comparator.comparing(UserAssetOverviewDto::getHolderName, String.CASE_INSENSITIVE_ORDER));
		userAssetDto.setAssetOverview(assetOverview);

	}

	private FundDataList getSchemeDetails(String equityMFISINList) {
		if (StringUtils.isNoneEmpty(equityMFISINList)) {
			LOG.info("API call to GET details for mutual funds : " + equityMFISINList);
			StringBuilder getSchemeURL = new StringBuilder(assetUtil.getProperty("fund.base.url"));
			getSchemeURL.append(assetUtil.getProperty("fund.schemes.url.path"));

			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSchemeURL.toString())
					.queryParam("rtcode", equityMFISINList);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<FundDataList> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
					FundDataList.class);
			LOG.info("API Response for GET mutual fund call " + response.getStatusCodeValue());
			return response.getBody();

		}
		return new FundDataList();
	}

	private MutualFundDataList getMFData(String mfIsinList) {
		if (!StringUtils.isEmpty(mfIsinList)) {
			LOG.info("API call to GET details for mutul fund : " + mfIsinList);
			StringBuilder getSchemeURL = new StringBuilder(assetUtil.getProperty("fund.base.url"));
			getSchemeURL.append(assetUtil.getProperty("fund.mutualfund.url.path"));

			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSchemeURL.toString()).queryParam("isin",
					mfIsinList.replaceAll(" ", ""));

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<MutualFundDataList> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
					entity, MutualFundDataList.class);
			LOG.info("API Response for GET mutul fund call " + response.getStatusCodeValue());
			return response.getBody();

		}
		return null;

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

	private void getCurrentAmount(double rate, double principal, UserAssetsDto uaDto) {
		long days = ChronoUnit.DAYS.between(uaDto.getUpdatedAt() == null ? uaDto.getCreatedAt() : uaDto.getUpdatedAt(),
				LocalDateTime.now());
		rate = rate / 100;
		double yrs = (double) days / 365;
		double multiplier = Math.pow(1.0 + (rate / 4), 4 * yrs);
		uaDto.setCurrentValue(multiplier * principal);
	}

	@Override
	public void saveUserPortfolioForPeriod(PortfolioHistory portfolioHistory) {
		portfolioHistoryRepository.save(portfolioHistory);

	}

	@Override
	public PortfolioHistoryResponseList getsaveUserPortfolioHistory(long userId) {
		PortfolioHistoryResponseList portfolioHistoryResponseList = new PortfolioHistoryResponseList();
		List<PortfolioHistory> portfolioHistory = portfolioHistoryRepository.getPortfolioHistoryForUser(userId);
		portfolioHistory.sort(Comparator.comparing(PortfolioHistory::getPeriod));
		if (!portfolioHistory.isEmpty()) {
			List<PortfolioHistoryResponse> history = new ArrayList<PortfolioHistoryResponse>();
			int count = 0;
			for (PortfolioHistory ph : portfolioHistory) {
				PortfolioHistoryResponse phr = new PortfolioHistoryResponse();
				phr.setAmount(ph.getAmount());
				// Set period in MMM-YY format
				// Inbuilt format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM");
				String formattedDateTime = ph.getPeriod().format(formatter);
				phr.setPeriod(formattedDateTime);

				// set change amount
				long changeAmount = 0;
				if (portfolioHistory.size() > 1 && count != 0)
					changeAmount = ph.getAmount() - portfolioHistory.get(count - 1).getAmount();
				phr.setAmountChange(String.valueOf(changeAmount));

				// set change percentage

				float percentageChange = 0;
				if (portfolioHistory.size() > 1 && count != 0)
					percentageChange = ((float) Long.parseLong(phr.getAmountChange())
							/ portfolioHistory.get(count - 1).getAmount()) * 100;
				phr.setPercentageChange(String.valueOf(percentageChange));

				count++;

				history.add(phr);
			}

			portfolioHistoryResponseList.setHistory(history);
		}

		return portfolioHistoryResponseList;
	}

	@Override
	public CurrentGrowthResponseList getTargetGrowth(TargetAllocation targetAllocation) {
		LOG.info("Inside getTargetGrowth method");

		CurrentGrowthResponseList currentGrowthResponseList = new CurrentGrowthResponseList();
		float returnPercentage = 0.0f;

		// Calculate Estimated return %
		float totalWeight = 0;
		totalWeight = (targetAllocation.getPercentAllocated().getCash() * CASH_RETURN)
				+ (targetAllocation.getPercentAllocated().getEquity() * EQUITY_RETURN)
				+ (targetAllocation.getPercentAllocated().getDebt() * DEBT_RETURN)
				+ (targetAllocation.getPercentAllocated().getGovFund() * GOV_FUND_RETURN)
				+ (targetAllocation.getPercentAllocated().getRealEstate() * REAL_ESTATE_RETURN)
				+ (targetAllocation.getPercentAllocated().getCommodities() * COMMODITIES_RETURN);

		returnPercentage = totalWeight / 100;

		List<CurrentGrowthResponse> currentAssetGrowth = new ArrayList<CurrentGrowthResponse>();
		CurrentGrowthResponse year0CurrentGrowthResponse = new CurrentGrowthResponse();
		year0CurrentGrowthResponse.setAge(targetAllocation.getAge());
		year0CurrentGrowthResponse.setAmount(targetAllocation.getNetWorth());
		currentAssetGrowth.add(year0CurrentGrowthResponse);

		// Calculate CI
		long amountOnYear = targetAllocation.getNetWorth();

		for (int i = 1; i <= 30; i++) {
			amountOnYear = (long) (amountOnYear + (amountOnYear * (returnPercentage / 100)));

			CurrentGrowthResponse currentGrowthResponse = new CurrentGrowthResponse();
			currentGrowthResponse.setAge(targetAllocation.getAge() + i);
			currentGrowthResponse.setAmount(amountOnYear);
			currentAssetGrowth.add(currentGrowthResponse);
		}
		currentGrowthResponseList.setCurrentGrowth(currentAssetGrowth);
		currentGrowthResponseList.setCurrentGrowthRate(returnPercentage);

		return currentGrowthResponseList;
	}

	@Override
	public UserIncomeExpenseDetail updateUserIncomeExpense(UserIncomeExpenseDetail userIncomeExpenseDetail) {
		UserIncomeExpenseDetail userIncomeExpenseDetailInDB = userIncomeExpenseRepository
				.findById(userIncomeExpenseDetail.getId()).orElse(null);
		if (userIncomeExpenseDetailInDB != null) {
			userIncomeExpenseDetail.setUpdatedAt(Date.from(Instant.now()));
			userIncomeExpenseRepository.save(userIncomeExpenseDetail);

		} else {
			if (userIncomeExpenseRepository.findUserIncomeExpenseById(userIncomeExpenseDetail.getUserId()) == null) {
				userIncomeExpenseDetail.setCreatedAt(Date.from(Instant.now()));
				userIncomeExpenseRepository.save(userIncomeExpenseDetail);
				userIncomeExpenseDetailInDB = userIncomeExpenseRepository.findById(userIncomeExpenseDetail.getId())
						.orElse(null);
			}

		}
		return userIncomeExpenseDetail;
	}

}
