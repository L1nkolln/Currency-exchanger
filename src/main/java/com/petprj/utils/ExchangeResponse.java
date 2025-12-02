package com.petprj.utils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.petprj.model.Currency;

@JsonPropertyOrder ({
        "baseCurrency",
        "targetCurrency",
        "rate",
        "amount",
        "result"
})
public class ExchangeResponse {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
    private double amount;
    private double result;

    public ExchangeResponse(Currency baseCurrency, Currency targetCurrency, double rate, double amount, double result) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.result = result;
    }

    public ExchangeResponse() {

    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public double getAmount() {
        return amount;
    }

    public double getResult() {
        return result;
    }
}
