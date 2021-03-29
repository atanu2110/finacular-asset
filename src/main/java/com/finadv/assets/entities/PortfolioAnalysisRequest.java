package com.finadv.assets.entities;

import java.util.List;


public class PortfolioAnalysisRequest extends MutualFundAnalysis {

	private List<Equity> equities;

	public List<Equity> getEquities() {
		return equities;
	}

	public void setEquities(List<Equity> equities) {
		this.equities = equities;
	}
}
