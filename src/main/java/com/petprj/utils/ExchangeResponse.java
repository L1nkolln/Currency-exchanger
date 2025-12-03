package com.petprj.utils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.petprj.model.Currency;

import java.math.BigDecimal;

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
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal result;

    public ExchangeResponse(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal result) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.result = result;
    }

    public ExchangeResponse(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeResponse() {

    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getResult() {
        return result;
    }
}
