package com.finadv.assets.service;

import java.util.List;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;

/**
 * @author atanu
 *
 */
public interface AssetService {
	
	List<AssetInstrument> getAssetDetails();
	
	List<AssetInstrument> getAssetDetailsByType(int type);

	List<AssetType> getAllAssetTypes();
	
	List<UserAssets> getUserAssetByUserId(long userId);
	
	void saveUserAssetsByUserId(UserAsset userAsset);
	
	UserAssets updateUserAsset(UserAssets userAsset);
	
	void deleteUserAsset(long assetId);
}
