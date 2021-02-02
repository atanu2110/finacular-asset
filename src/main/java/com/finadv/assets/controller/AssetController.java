package com.finadv.assets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.CurrentGrowthRequest;
import com.finadv.assets.entities.CurrentGrowthResponseList;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.UserIncomeExpenseDetail;
import com.finadv.assets.service.AssetService;

/**
 * @author atanu
 *
 */
@RestController
@RequestMapping(value = "/api/v1/assets")
@CrossOrigin(origins = "*")
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

	/**
	 * @param userid
	 * @return user assets
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<UserAssetDto> getUserAssetsById(@PathVariable long userId) {
		return new ResponseEntity<>(assetService.getUserAssetByUserId(userId), HttpStatus.OK);
	}

	/**
	 * @param userAsset
	 * @return
	 */
	@PostMapping("/{userId}")
	public ResponseEntity<String> createUserAsset(@RequestBody UserAsset userAsset) {
		assetService.saveUserAssetsByUserId(userAsset, "api");
		return new ResponseEntity<>("User Assets successfully saved !!", HttpStatus.OK);
	}

	/**
	 * @param userAsset
	 * @return udated user asset
	 */
	@PutMapping("/{assetId}")
	public ResponseEntity<?> updateUserAsset(@RequestBody UserAssets userAsset) {
		UserAssets userUpdate = assetService.updateUserAsset(userAsset);
		if (userUpdate != null) {
			return new ResponseEntity<>(userUpdate, HttpStatus.OK);
		}

		return new ResponseEntity<>("Asset does not exists !!", HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping("/{assetId}")
	public ResponseEntity<?> deleteUserAsset(@PathVariable long assetId) {
		assetService.deleteUserAsset(assetId);
		return new ResponseEntity<>("Asset deleted successfully !!", HttpStatus.OK);

	}

	@PostMapping("/current/growth")
	public ResponseEntity<?> getCurrentGrowth(@RequestBody CurrentGrowthRequest currentGrowth) {
		CurrentGrowthResponseList currentGrowthResponseList = assetService.getCurrentGrowth(currentGrowth);

		return new ResponseEntity<>(currentGrowthResponseList, HttpStatus.OK);

	}

	/**
	 * @param userId
	 * @return user income and expense details
	 */
	@GetMapping("/income/expense/{userId}")
	public ResponseEntity<UserIncomeExpenseDetail> getUserIncomeExpenseById(@PathVariable long userId) {
		UserIncomeExpenseDetail userIncomeExpenseDetail = assetService.getUserIncomeExpenseById(userId);
		return new ResponseEntity<>(userIncomeExpenseDetail, HttpStatus.OK);

	}

	@PostMapping("/income/expense/{userId}")
	public ResponseEntity<String> createUserIncomeExpense(
			@RequestBody UserIncomeExpenseDetail userIncomeExpenseDetail) {
		assetService.createUserIncomeExpense(userIncomeExpenseDetail);
		return new ResponseEntity<>("User Income Expense successfully saved !!", HttpStatus.OK);
	}

}
