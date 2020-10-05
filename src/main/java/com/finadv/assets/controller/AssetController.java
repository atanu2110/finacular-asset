package com.finadv.assets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.service.AssetService;

/**
 * @author atanu
 *
 */
@RestController
@RequestMapping(value = "/api/v1/assets")
public class AssetController {
	
	
	private AssetService assetService;
	
	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}


	/**
	 * @return
	 */
	@GetMapping(value = "/test")
	public String test() {
		return "asset service is up";
	}

	
	/**
	 * @return asset list
	 */
	@GetMapping("/instruments")
	public List<AssetInstrument> showAssetInstruments() {
		return assetService.getAssetDetails();

	}
	
	/**
	 * @param type
	 * @return
	 */
	@GetMapping("/instruments/list")
	public List<AssetInstrument> showAssetInstrumentsByType(@RequestParam int type) {
		return assetService.getAssetDetailsByType(type);

	}
	
	/**
	 * @return asset types
	 */
	@GetMapping("/types")
	public List<AssetType> getAssetsTypes() {
		return assetService.getAllAssetTypes();

	}
}
