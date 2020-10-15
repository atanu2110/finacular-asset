package com.finadv.assets.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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
}
