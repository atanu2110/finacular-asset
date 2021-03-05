package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class AssetPercentage {
	private float equity;
	private float debt;
	private float cash;
	private float govFund;

	private float realEstate;
	private float commodities;

	public float getEquity() {
		return equity;
	}

	public void setEquity(float equity) {
		this.equity = equity;
	}

	public float getDebt() {
		return debt;
	}

	public void setDebt(float debt) {
		this.debt = debt;
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public float getGovFund() {
		return govFund;
	}

	public void setGovFund(float govFund) {
		this.govFund = govFund;
	}

	public float getRealEstate() {
		return realEstate;
	}

	public void setRealEstate(float realEstate) {
		this.realEstate = realEstate;
	}

	public float getCommodities() {
		return commodities;
	}

	public void setCommodities(float commodities) {
		this.commodities = commodities;
	}

}
