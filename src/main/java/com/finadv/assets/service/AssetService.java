package com.finadv.assets.service;

import java.util.List;

import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.CurrentGrowthRequest;
import com.finadv.assets.entities.CurrentGrowthResponseList;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.UserIncomeExpenseDetail;

/**
 * @author atanu
 *
 */
public interface AssetService {
	
	List<AssetInstrument> getAssetDetails();
	
	List<AssetInstrument> getAssetDetailsByType(int type);

	List<AssetType> getAllAssetTypes();
	
	UserAssetDto getUserAssetByUserId(long userId);
	
	void saveUserAssetsByUserId(UserAsset userAsset);
	
	UserAssets updateUserAsset(UserAssets userAsset);
	
	void deleteUserAsset(long assetId);
	
	CurrentGrowthResponseList getCurrentGrowth(CurrentGrowthRequest currentGrowth);
	
	UserIncomeExpenseDetail getUserIncomeExpenseById(long userId);
	
	void createUserIncomeExpense(UserIncomeExpenseDetail userIncomeExpenseDetail);
}
