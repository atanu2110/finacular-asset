package com.finadv.assets.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.List;

public class Fund {

    private String AMC;
    private List<FundDetails> fundDetails;
    private String folioPAN;
    private String folioStatus;
    private String folioNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate topDate;
    private String holderName;

    public void setAMC(String amc) {
        this.AMC = amc;
    }

    public String getAMC() {
        return AMC;
    }

    public void setFundDetails(List<FundDetails> fundDetails) {
        this.fundDetails = fundDetails;
    }

    public List<FundDetails> getFundDetails() {
        return fundDetails;
    }

    public void setFolioPAN(String folioPAN) {
        this.folioPAN = folioPAN;
    }

    public String getFolioPAN() {
        return folioPAN;
    }

    public void setFolioStatus(String folioStatus) {
        this.folioStatus = folioStatus;
    }

    public String getFolioStatus() {
        return folioStatus;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setTopDate(LocalDate topDate) {
        this.topDate = topDate;
    }

    public LocalDate getTopDate() {
        return topDate;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getHolderName() {
        return holderName;
    }
}
