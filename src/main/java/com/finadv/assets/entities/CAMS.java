package com.finadv.assets.entities;

import java.util.ArrayList;
import java.util.List;

public class CAMS {

	private HolderInfo holderInfo;
	private List<FundInfo> fundInfoList = new ArrayList<>();

	public HolderInfo getHolderInfo() {
		return holderInfo;
	}

	public void setHolderInfo(HolderInfo holderInfo) {
		this.holderInfo = holderInfo;
	}

	public List<FundInfo> getFundInfoList() {
		return fundInfoList;
	}

	public void setFundInfoList(List<FundInfo> fundInfoList) {
		this.fundInfoList = fundInfoList;
	}

}
