package com.finadv.assets.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SectionB {
    private final String description;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;
    private final Double purchasedUnits;
    private final Double redeemedUnits;
    private final Double unitCost;
    private final Double indexedCost;

    public SectionB(String description, LocalDate date, Double purchasedUnits, Double redeemedUnits, Double unitCost, Double indexedCost) {

        this.description = description;
        this.date = date;
        this.purchasedUnits = purchasedUnits;
        this.redeemedUnits = redeemedUnits;
        this.unitCost = unitCost;
        this.indexedCost = indexedCost;
    }
}
