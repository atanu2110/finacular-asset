package com.finadv.assets.model;

import lombok.Data;

@Data
public class SectionD {
    private final Double shortTerm;
    private final Double longTermLongIndex;
    private final Double longTermNoIndex;

    public SectionD(Double shortTerm, Double longTermLongIndex, Double longTermNoIndex) {
        this.shortTerm = shortTerm;
        this.longTermLongIndex = longTermLongIndex;
        this.longTermNoIndex = longTermNoIndex;
    }
}
