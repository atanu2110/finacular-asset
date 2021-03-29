package com.finadv.assets.dto;

/**
 * @author atanu
 *
 */
public class PortfolioHistoryResponse {

	private String period;

	private long amount;

	private String amountChange;

	private String percentageChange;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getAmountChange() {
		return amountChange;
	}

	public void setAmountChange(String amountChange) {
		this.amountChange = amountChange;
	}

	public String getPercentageChange() {
		return percentageChange;
	}

	public void setPercentageChange(String percentageChange) {
		this.percentageChange = percentageChange;
	}

}
