package com.finadv.assets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.repository.AssetInstrumentRepository;
import com.finadv.assets.repository.AssetTypeRepository;
import com.finadv.assets.repository.UserAssetRepository;

@Service
public class AssetServiceImpl implements AssetService {

	private AssetInstrumentRepository assetInstrumentRepository;
	private AssetTypeRepository assetTypeRepository;
	private UserAssetRepository userAssetRepository;

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
	public List<UserAssets> getUserAssetByUserId(long userId) {

		return userAssetRepository.findUserAssetByUserId(userId);
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

}
