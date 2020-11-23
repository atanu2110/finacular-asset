package com.finadv.assets.entities;

import java.util.ArrayList;
import java.util.List;

public class FundInfo {

	private String schemeName;
	private String schemeType;
	private String folioName;
	private String pan;
	private List<Transaction> transactions = new ArrayList<>();
	private Transaction lastTransaction;
	private Double closingBalance;
	private Double nav;
	private Double valuation;
	public String getSchemeName() {
		return schemeName;
	}
	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	public String getSchemeType() {
		return schemeType;
	}
	public void setSchemeType(String schemeType) {
		this.schemeType = schemeType;
	}
	public String getFolioName() {
		return folioName;
	}
	public void setFolioName(String folioName) {
		this.folioName = folioName;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public Transaction getLastTransaction() {
		return lastTransaction;
	}
	public void setLastTransaction(Transaction lastTransaction) {
		this.lastTransaction = lastTransaction;
	}
	public Double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}
	public Double getNav() {
		return nav;
	}
	public void setNav(Double nav) {
		this.nav = nav;
	}
	public Double getValuation() {
		return valuation;
	}
	public void setValuation(Double valuation) {
		this.valuation = valuation;
	}

}
