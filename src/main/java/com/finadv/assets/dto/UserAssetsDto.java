package com.finadv.assets.dto;

import java.time.LocalDateTime;

/**
 * @author atanu
 *
 */
public class UserAssetsDto {

	private int assetId;

	private AssetTypeDto assetType;

	private AssetInstrumentDto assetInstrument;

	private String nickName;

	private double amount;

	private float expectedReturn;

	private LocalDateTime maturityDate;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String equityDebtName;

	private String details;

	private double currentValue;

	private String code;

	public int getAssetId() {
		return assetId;
	}

	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}

	public AssetTypeDto getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetTypeDto assetType) {
		this.assetType = assetType;
	}

	public AssetInstrumentDto getAssetInstrument() {
		return assetInstrument;
	}

	public void setAssetInstrument(AssetInstrumentDto assetInstrument) {
		this.assetInstrument = assetInstrument;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public float getExpectedReturn() {
		return expectedReturn;
	}

	public void setExpectedReturn(float expectedReturn) {
		this.expectedReturn = expectedReturn;
	}

	public LocalDateTime getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDateTime maturityDate) {
		this.maturityDate = maturityDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getEquityDebtName() {
		return equityDebtName;
	}

	public void setEquityDebtName(String equityDebtName) {
		this.equityDebtName = equityDebtName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
