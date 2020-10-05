package com.finadv.assets.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author atanu
 *
 */
@Entity
@Table(name = "asset_instrument")
public class AssetInstrument {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "asset_type_id", referencedColumnName = "id")
	private AssetType assetTypeId;

	@Column(name = "instrument_name")
	private String instrumentName;

	@Column(name = "default_returns")
	private float defaultReturns;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AssetType getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(AssetType assetTypeId) {
		this.assetTypeId = assetTypeId;
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
