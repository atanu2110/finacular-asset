package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class NSDLReponse {

	private String holderName;

	private String period;

	private Double amount;

	private List<NSDLEquity> nsdlEquities;

	private List<NSDLValueTrend> nsdlValueTrend;

	private List<NSDLMutualFund> nsdlMutualFunds;

	private NSDLAssetAmount nsdlAssetAmount;

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public List<NSDLEquity> getNsdlEquities() {
		return nsdlEquities;
	}

	public void setNsdlEquities(List<NSDLEquity> nsdlEquities) {
		this.nsdlEquities = nsdlEquities;
	}

	public List<NSDLValueTrend> getNsdlValueTrend() {
		return nsdlValueTrend;
	}

	public void setNsdlValueTrend(List<NSDLValueTrend> nsdlValueTrend) {
		this.nsdlValueTrend = nsdlValueTrend;
	}

	public List<NSDLMutualFund> getNsdlMutualFunds() {
		return nsdlMutualFunds;
	}

	public void setNsdlMutualFunds(List<NSDLMutualFund> nsdlMutualFunds) {
		this.nsdlMutualFunds = nsdlMutualFunds;
	}

	public NSDLAssetAmount getNsdlAssetAmount() {
		return nsdlAssetAmount;
	}

	public void setNsdlAssetAmount(NSDLAssetAmount nsdlAssetAmount) {
		this.nsdlAssetAmount = nsdlAssetAmount;
	}

}
