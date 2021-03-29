package com.finadv.assets.dto;

/**
 * @author atanu
 *
 */
public class AssetInstrumentDto {

	private long id;

	private String instrumentName;

	private float defaultReturns;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public float getDefaultReturns() {
		return defaultReturns;
	}

	public void setDefaultReturns(float defaultReturns) {
		this.defaultReturns = defaultReturns;
	}

}
