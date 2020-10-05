package com.finadv.assets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.repository.AssetInstrumentRepository;

@Service
public class AssetServiceImpl implements AssetService{
	
	private AssetInstrumentRepository assetInstrumentRepository;

	@Autowired
	public void setAssetInstrumentRepository(AssetInstrumentRepository assetInstrumentRepository) {
		this.assetInstrumentRepository = assetInstrumentRepository;
	}

	@Override
	public List<AssetInstrument> getAssetDetails() {
		return assetInstrumentRepository.findAll();
	}

}
