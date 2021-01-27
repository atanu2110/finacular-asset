package com.finadv.assets.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.dto.UserAssetsDto;
import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.CurrentAsset;
import com.finadv.assets.entities.CurrentGrowthRequest;
import com.finadv.assets.entities.CurrentGrowthResponse;
import com.finadv.assets.entities.CurrentGrowthResponseList;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.UserIncomeExpenseDetail;
import com.finadv.assets.mapper.AssetMapper;
import com.finadv.assets.repository.AssetInstrumentRepository;
import com.finadv.assets.repository.AssetTypeRepository;
import com.finadv.assets.repository.UserAssetRepository;
import com.finadv.assets.repository.UserIncomeExpenseRepository;

@Service
public class AssetServiceImpl implements AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetServiceImpl.class);

	private AssetInstrumentRepository assetInstrumentRepository;
	private AssetTypeRepository assetTypeRepository;
	private UserAssetRepository userAssetRepository;
	private UserIncomeExpenseRepository userIncomeExpenseRepository;

	private AssetMapper assetMapper;

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
		// TODO - Find all asset current value- Temp

		calculateCurrentValueOfAssets(userAssetDto);
		return userAssetDto;
	}

	@Override
	public void saveUserAssetsByUserId(UserAsset userAsset) {

		// If user already has the mutual fund then simply add the amount to exisitng
		// one
		List<UserAssets> assetList = userAssetRepository.findUserAssetByUserId(userAsset.getUserId());
		for (UserAssets userAssets : userAsset.getAssets()) {
			if (userAssets.getAssetInstrument().getId() == 8 && assetList.size() > 0) {
				UserAssets assetInDB = assetList.stream()
						.filter(x -> StringUtils.isNotEmpty(x.getEquityDebtName())
								&& x.getEquityDebtName().equalsIgnoreCase(userAssets.getEquityDebtName()))
						.findFirst().orElse(null);
				if (assetInDB != null) {
					assetInDB.setAmount(assetInDB.getAmount() + userAssets.getAmount());
					updateUserAsset(assetInDB);
				} else {
					userAssetRepository.save(userAssets);
				}

			} else {
				userAssetRepository.save(userAssets);
			}
		}

		// userAssetRepository.saveAll(userAsset.getAssets());
	}

	@Override
	public UserAssets updateUserAsset(UserAssets userAsset) {
		UserAssets userAssetsInDB = userAssetRepository.findById(userAsset.getId()).orElse(null);
		if (userAssetsInDB != null) {
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
	public CurrentGrowthResponseList getCurrentGrowth(CurrentGrowthRequest currentGrowth) {
		CurrentGrowthResponseList currentGrowthResponseList = new CurrentGrowthResponseList();
		float returnPercentage = 0.0f;
		// Total asset amount
		long totalAmount = currentGrowth.getCurrentAssetList().stream().map(item -> item.getAmount()).reduce(0l,
				(a, b) -> a + b);

		// Calculate Estimated return %
		float totalWeight = 0;
		for (CurrentAsset currentAsset : currentGrowth.getCurrentAssetList()) {
			totalWeight += currentAsset.getExpectedReturn() * ((currentAsset.getAmount() * 100) / totalAmount);
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
		/*
		 * List<String> equityMFISINList = userAssetDto.getAssets().stream() .filter(a
		 * -> a.getAssetType().getId() == 4 && a.getAssetInstrument().getId() == 8)
		 * .map(UserAssetsDto::getCode).collect(Collectors.toList());
		 */

		// Get equity Stock ISIN list
		/*
		 * List<String> equityStockISINList = userAssetDto.getAssets().stream()
		 * .filter(a -> a.getAssetType().getId() == 4 && a.getAssetInstrument().getId()
		 * == 7) .map(UserAssetsDto::getCode).collect(Collectors.toList());
		 */

		for (UserAssetsDto uaDto : userAssetDto.getAssets()) {
			/*
			 * switch (uaDto.getAssetInstrument().getInstrumentName()) { case "savings":
			 * getCurrentAmount(uaDto.getExpectedReturn(), uaDto.getAmount(), UserAssetsDto
			 * uaDto); break; case "default": System.out.println("default fallback"); }
			 */
			if(uaDto.getAssetInstrument().getId() == 1) {
				getCurrentAmount(uaDto.getExpectedReturn(), uaDto.getAmount(), uaDto);
			}
		}

	}

	private void getCurrentAmount(double rate, double principal, UserAssetsDto uaDto) {
		long days = ChronoUnit.DAYS.between(uaDto.getCreatedAt(), LocalDateTime.now());
		rate = rate / 100;
		double yrs = (double) days / 365;
		double multiplier = Math.pow(1.0 + (rate / 4), 4 * yrs);
		uaDto.setCurrentValue(multiplier * principal);
	}


}
