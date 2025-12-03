package com.petprj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.Objects;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRate {

    private Integer id;
    @JsonIgnore
    private int baseCurrencyId;
    @JsonIgnore
    private int targetCurrencyId;
    private BigDecimal rate;
    private Currency baseCurrency;
    private Currency targetCurrency;

    public ExchangeRate(Integer id, int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRate() {
    }


    public Integer getId() {
        return id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public void setRate(BigDecimal rate) {
        if (rate != null) {
            this.rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
        }else {
            this.rate = null;
        }
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRate that)) return false;
        return baseCurrencyId == that.baseCurrencyId && targetCurrencyId == that.targetCurrencyId && Objects.equals(id, that.id) && Objects.equals(rate, that.rate) && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate, baseCurrency, targetCurrency);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
               "id=" + id +
               ", baseCurrencyId=" + baseCurrencyId +
               ", targetCurrencyId=" + targetCurrencyId +
               ", rate=" + rate +
               ", baseCurrency=" + baseCurrency +
               ", targetCurrency=" + targetCurrency +
               '}';
    }
}
