package com.finadv.assets.model;

import lombok.Data;

@Data
public class FundDetails {
    private String schemeName;
    private SectionA sectionA;
    private SectionB sectionB;
    private SectionD sectionD;
    private Total total;

    public FundDetails(String schemeName, SectionA sectionA, SectionB sectionB, SectionD sectionD, Total total) {
        this.schemeName = schemeName;
        this.sectionA = sectionA;
        this.sectionB = sectionB;
        this.sectionD = sectionD;
        this.total = total;
    }
}
