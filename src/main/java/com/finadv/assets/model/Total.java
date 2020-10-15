package com.finadv.assets.model;

import lombok.Data;

@Data
public class Total {
    private Double units;
    private Double amount;

    public Total(Double units, Double amount) {
        this.units = units;
        this.amount = amount;
    }
}
