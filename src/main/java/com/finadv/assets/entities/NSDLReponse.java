package com.finadv.assets.entities;

import java.util.List;
import java.util.Map;

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

	private List<MutualFundAnalysisResponse> mfaResponse;

	private List<OverallStockData> overallStock;

	private Map<String, Double> equitySector;

	private Map<String, Double> mfSector;

	private Map<String, Double> overallSector;

	private List<String> mfAnalyzed;

	private List<String> mfNotAnalyzed;

	private List<MutualFundGrowthAnalysis> mfGrowthAnalysis;

	private PortfolioAnalysisReponse portfolioAnalysis;

	public List<String> getMfAnalyzed() {
		return mfAnalyzed;
	}

	public void setMfAnalyzed(List<String> mfAnalyzed) {
		this.mfAnalyzed = mfAnalyzed;
	}

	public List<String> getMfNotAnalyzed() {
		return mfNotAnalyzed;
	}

	public void setMfNotAnalyzed(List<String> mfNotAnalyzed) {
		this.mfNotAnalyzed = mfNotAnalyzed;
	}

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

	public List<MutualFundAnalysisResponse> getMfaResponse() {
		return mfaResponse;
	}

	public void setMfaResponse(List<MutualFundAnalysisResponse> mfaResponse) {
		this.mfaResponse = mfaResponse;
	}

	public List<OverallStockData> getOverallStock() {
		return overallStock;
	}

	public void setOverallStock(List<OverallStockData> overallStock) {
		this.overallStock = overallStock;
	}

	public Map<String, Double> getEquitySector() {
		return equitySector;
	}

	public void setEquitySector(Map<String, Double> equitySector) {
		this.equitySector = equitySector;
	}

	public Map<String, Double> getMfSector() {
		return mfSector;
	}

	public void setMfSector(Map<String, Double> mfSector) {
		this.mfSector = mfSector;
	}

	public Map<String, Double> getOverallSector() {
		return overallSector;
	}

	public void setOverallSector(Map<String, Double> overallSector) {
		this.overallSector = overallSector;
	}

	public List<MutualFundGrowthAnalysis> getMfGrowthAnalysis() {
		return mfGrowthAnalysis;
	}

	public void setMfGrowthAnalysis(List<MutualFundGrowthAnalysis> mfGrowthAnalysis) {
		this.mfGrowthAnalysis = mfGrowthAnalysis;
	}

	public PortfolioAnalysisReponse getPortfolioAnalysis() {
		return portfolioAnalysis;
	}

	public void setPortfolioAnalysis(PortfolioAnalysisReponse portfolioAnalysis) {
		this.portfolioAnalysis = portfolioAnalysis;
	}

}
