package com.finadv.assets.entities;

import java.time.LocalDate;

public class Transaction {

	private LocalDate date;
	private String transactionDetail;
	private Double amount;
	private Double units;
	private Double price;
	private Double unitBalance;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getTransactionDetail() {
		return transactionDetail;
	}

	public void setTransactionDetail(String transactionDetail) {
		this.transactionDetail = transactionDetail;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getUnits() {
		return units;
	}

	public void setUnits(Double units) {
		this.units = units;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getUnitBalance() {
		return unitBalance;
	}

	public void setUnitBalance(Double unitBalance) {
		this.unitBalance = unitBalance;
	}

}
