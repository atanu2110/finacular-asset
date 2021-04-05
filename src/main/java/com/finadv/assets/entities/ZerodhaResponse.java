package com.finadv.assets.entities;

import java.util.List;
import java.util.Map;

/**
 * @author atanu
 *
 */
public class ZerodhaResponse {

	private String clientId;

	private String period;

	private List<NSDLEquity> nsdlEquities;

	private List<NSDLMutualFund> nsdlMutualFunds;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public List<NSDLEquity> getNsdlEquities() {
		return nsdlEquities;
	}

	public void setNsdlEquities(List<NSDLEquity> nsdlEquities) {
		this.nsdlEquities = nsdlEquities;
	}

	public List<NSDLMutualFund> getNsdlMutualFunds() {
		return nsdlMutualFunds;
	}

	public void setNsdlMutualFunds(List<NSDLMutualFund> nsdlMutualFunds) {
		this.nsdlMutualFunds = nsdlMutualFunds;
	}

}
