package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class StockData {

	private String companyname;

	private float de_ratio;

	private float eps_cagr;

	private String isin;

	private double nav;

	private float roce;

	private float sales_cagr;

	private String sector;

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public float getDe_ratio() {
		return de_ratio;
	}

	public void setDe_ratio(float de_ratio) {
		this.de_ratio = de_ratio;
	}

	public float getEps_cagr() {
		return eps_cagr;
	}

	public void setEps_cagr(float eps_cagr) {
		this.eps_cagr = eps_cagr;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public double getNav() {
		return nav;
	}

	public void setNav(double nav) {
		this.nav = nav;
	}

	public float getRoce() {
		return roce;
	}

	public void setRoce(float roce) {
		this.roce = roce;
	}

	public float getSales_cagr() {
		return sales_cagr;
	}

	public void setSales_cagr(float sales_cagr) {
		this.sales_cagr = sales_cagr;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

}
