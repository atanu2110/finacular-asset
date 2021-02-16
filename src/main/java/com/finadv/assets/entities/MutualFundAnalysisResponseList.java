package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class MutualFundAnalysisResponseList {

	private List<MutualFundAnalysisResponse> mfaResponse;

	private List<String> mfAnalyzed;

	private List<String> mfNotAnalyzed;

	private List<MutualFundGrowthAnalysis> mfGrowthAnalysis;

	public List<MutualFundAnalysisResponse> getMfaResponse() {
		return mfaResponse;
	}

	public void setMfaResponse(List<MutualFundAnalysisResponse> mfaResponse) {
		this.mfaResponse = mfaResponse;
	}

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

	public List<MutualFundGrowthAnalysis> getMfGrowthAnalysis() {
		return mfGrowthAnalysis;
	}

	public void setMfGrowthAnalysis(List<MutualFundGrowthAnalysis> mfGrowthAnalysis) {
		this.mfGrowthAnalysis = mfGrowthAnalysis;
	}

}
