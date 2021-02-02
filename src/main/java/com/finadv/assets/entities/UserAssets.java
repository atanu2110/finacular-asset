package com.finadv.assets.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author atanu
 *
 */
@Entity
@Table(name = "user_assets")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserAssets {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@OneToOne
	@JoinColumn(name = "institution_id", referencedColumnName = "id")
	private Institution assetProvider;

	@OneToOne
	@JoinColumn(name = "asset_type_id", referencedColumnName = "id")
	private AssetType assetType;

	@OneToOne
	@JoinColumn(name = "asset_instrument_id", referencedColumnName = "id")
	private AssetInstrument assetInstrument;

	@Column(name = "user_Id")
	private long userId;

	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "holder_name")
	private String holderName;

	@Column(name = "amount")
	private double amount;

	@Column(name = "returns")
	private float expectedReturn;

	@Column(name = "maturity_date")
	private LocalDateTime maturityDate;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "modified_at")
	private LocalDateTime updatedAt;

	@Column(name = "equity_debt_name")
	private String equityDebtName;

	@Column(name = "details")
	private String details;

	@Column(name = "code")
	private String code;

	@Column(name = "units")
	private int units;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Institution getAssetProvider() {
		return assetProvider;
	}

	public void setAssetProvider(Institution assetProvider) {
		this.assetProvider = assetProvider;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public AssetInstrument getAssetInstrument() {
		return assetInstrument;
	}

	public void setAssetInstrument(AssetInstrument assetInstrument) {
		this.assetInstrument = assetInstrument;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

}
