package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class PortfolioAnalysisReponse {
	private float salesCAGR;

	private float epsCAGR;

	private float deRatio;

	private float roce;

	public float getSalesCAGR() {
		return salesCAGR;
	}

	public void setSalesCAGR(float salesCAGR) {
		this.salesCAGR = salesCAGR;
	}

	public float getEpsCAGR() {
		return epsCAGR;
	}

	public void setEpsCAGR(float epsCAGR) {
		this.epsCAGR = epsCAGR;
	}

	public float getDeRatio() {
		return deRatio;
	}

	public void setDeRatio(float deRatio) {
		this.deRatio = deRatio;
	}

	public float getRoce() {
		return roce;
	}

	public void setRoce(float roce) {
		this.roce = roce;
	}
}
