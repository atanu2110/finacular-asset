
package com.finadv.assets.model;


import lombok.Data;

import java.time.LocalDate;

@Data
public class Transaction {

    private LocalDate date;
    private String transactionDetail;
    private Double amount;
    private Double units;
    private Double price;
    private Double unitBalance;
}
