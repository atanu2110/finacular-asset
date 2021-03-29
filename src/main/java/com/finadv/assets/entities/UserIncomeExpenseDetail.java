package com.finadv.assets.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author atanu
 *
 */
@Entity
@Table(name = "user_income_expense_detail")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserIncomeExpenseDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "user_Id")
	private long userId;

	@Column(name = "month_income")
	private long monthlyIncome;

	@Column(name = "month_epf")
	private long monthlyEPF;

	@Column(name = "month_rent_income")
	private long monthlyRentalIncome;

	@Column(name = "month_other")
	private long monthlyOtherIncome;

	@Column(name = "annual_one_time_income")
	private long annualOneTimeIncome;

	@Column(name = "month_expense")
	private long monthlyExpense;

	@Column(name = "annual_one_time_expense")
	private long annualOneTimeExpense;

	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "updated_at")
	private Date updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(long monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public long getMonthlyEPF() {
		return monthlyEPF;
	}

	public void setMonthlyEPF(long monthlyEPF) {
		this.monthlyEPF = monthlyEPF;
	}

	public long getMonthlyRentalIncome() {
		return monthlyRentalIncome;
	}

	public void setMonthlyRentalIncome(long monthlyRentalIncome) {
		this.monthlyRentalIncome = monthlyRentalIncome;
	}

	public long getMonthlyOtherIncome() {
		return monthlyOtherIncome;
	}

	public void setMonthlyOtherIncome(long monthlyOtherIncome) {
		this.monthlyOtherIncome = monthlyOtherIncome;
	}

	public long getAnnualOneTimeIncome() {
		return annualOneTimeIncome;
	}

	public void setAnnualOneTimeIncome(long annualOneTimeIncome) {
		this.annualOneTimeIncome = annualOneTimeIncome;
	}

	public long getMonthlyExpense() {
		return monthlyExpense;
	}

	public void setMonthlyExpense(long monthlyExpense) {
		this.monthlyExpense = monthlyExpense;
	}

	public long getAnnualOneTimeExpense() {
		return annualOneTimeExpense;
	}

	public void setAnnualOneTimeExpense(long annualOneTimeExpense) {
		this.annualOneTimeExpense = annualOneTimeExpense;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
