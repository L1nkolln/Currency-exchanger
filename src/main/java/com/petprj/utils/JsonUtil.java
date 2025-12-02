package com.petprj.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.petprj.model.Currency;
import com.petprj.model.ExchangeRate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }









////    private static final ObjectMapper mapper = new ObjectMapper()
////            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
////            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
//
//
//    public static String toJson(Currency currency) {
//        JSONObject json = new JSONObject(new LinkedHashMap<>());
//        json.put("id", currency.getId());
//        json.put("code", currency.getCode());
//        json.put("fullName", currency.getFullName());
//        json.put("sign", currency.getSign());
//        return json.toString();
//    }
//
////    public static String toJsonCurrencies(List<Currency> list) {
////        JSONArray array = new JSONArray();
////        for (Currency currency : list) {
////            array.put(new JSONObject(toJson(currency)));
////        }
////        return array.toString();
////    }
//
//    public static String toJsonCurrencies(List<Currency> list) {
//        JSONArray array = new JSONArray();
//        for (Currency currency : list) {
//            JSONObject json = new JSONObject(new LinkedHashMap<>());
//            json.put("id", currency.getId());
//            json.put("code", currency.getCode());
//            json.put("fullName", currency.getFullName());
//            json.put("sign", currency.getSign());
//            array.put(json);
//        }
//        return array.toString();
//    }
//
//    public static String toJson(ExchangeRate exchangeRate) {
//        JSONObject json = new JSONObject(new LinkedHashMap<>());
//        json.put("id", exchangeRate.getId());
//        json.put("baseCurrencyId", exchangeRate.getBaseCurrencyId());
//        json.put("targetCurrencyId", exchangeRate.getTargetCurrencyId());
//        json.put("rate", exchangeRate.getRate());
//        return json.toString();
//    }
//
//    public static String toJsonExchangeRates(List<ExchangeRate> list) {
//        JSONArray array = new JSONArray();
//        for (ExchangeRate exchangeRate : list) {
//            array.put(new JSONObject(toJson(exchangeRate)));
//        }
//        return array.toString();
//    }
}
