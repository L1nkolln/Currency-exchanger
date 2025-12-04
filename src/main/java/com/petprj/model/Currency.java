package com.petprj.model;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"id", "code", "fullName", "sign"})
public class Currency {

    private Integer id;
    private String code;
    private String name;
    private String sign;

    public Currency(Integer id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public Currency() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(code, currency.code) && Objects.equals(name, currency.name) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, sign);
    }

    @Override
    public String toString() {
        return "Currency{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + name + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
