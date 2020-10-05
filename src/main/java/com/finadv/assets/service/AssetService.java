package com.finadv.assets.service;

import java.util.List;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;

/**
 * @author atanu
 *
 */
public interface AssetService {
	
	List<AssetInstrument> getAssetDetails();
	
	List<AssetInstrument> getAssetDetailsByType(int type);

	List<AssetType> getAllAssetTypes();
	
}
