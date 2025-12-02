package com.petprj.servlets;

import com.petprj.dao.CurrencyDao;
import com.petprj.dao.ExchangeRateDao;
import com.petprj.exeptions.NotFoundException;
import com.petprj.model.Currency;
import com.petprj.model.ExchangeRate;
import com.petprj.utils.ErrorHandler;
import com.petprj.utils.HttpUtil;
import com.petprj.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class GetAndAddExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
            String json = JsonUtil.toJson(exchangeRates);
            HttpUtil.sendJsonResponse(resp, 200, json);
        } catch (IOException e) {
            ErrorHandler.handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rate = req.getParameter("rate");

            if (baseCurrencyCode == null || baseCurrencyCode.isBlank() ||
                targetCurrencyCode == null || targetCurrencyCode.isBlank() ||
                rate == null || rate.isBlank()) {
                HttpUtil.sendError(resp, 400, "Fields 'baseCurrencyCode', 'targetCurrencyCode', 'rate' are empty");
                return;
            }

            baseCurrencyCode = baseCurrencyCode.trim().toUpperCase();
            targetCurrencyCode = targetCurrencyCode.trim().toUpperCase();
            if (baseCurrencyCode.length() != 3 || targetCurrencyCode.length() != 3){
                HttpUtil.sendError(resp, 400, "Code length must be 3 letters");
                return;
            }

            if (baseCurrencyCode.equalsIgnoreCase(targetCurrencyCode)){
                HttpUtil.sendError(resp, 400,
                        "It is impossible to create an exchange rate because the codes are identical.");
                return;
            }

            Currency baseCode = currencyDao.findByCode(baseCurrencyCode);
            Currency targetCode = currencyDao.findByCode(targetCurrencyCode);
            if (baseCode == null || targetCode == null) {
                HttpUtil.sendError(resp, 404, "Currency not found");
                return;
            }

            try {
                ExchangeRate existing = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
                if (existing != null) {
                    HttpUtil.sendError(resp, 409,
                            "Rate =  " + baseCurrencyCode + "/" + targetCurrencyCode + " already exists");
                    return;
                }
            } catch (NotFoundException ignore) {

            }


            rate = rate.trim().replace(',', '.');
            if(!rate.matches("\\d+(\\.\\d+)?")){
                HttpUtil.sendError(resp, 400, "Rate must be a valid number");
                return;
            }
            double parseDouble = Double.parseDouble(rate);
            if (parseDouble <= 0){
                HttpUtil.sendError(resp, 400, "Rate must be a positive number");
                return;
            }

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrencyId(baseCode.getId());
            exchangeRate.setTargetCurrencyId(targetCode.getId());
            exchangeRate.setRate(parseDouble);

            ExchangeRate created = exchangeRateDao.create(exchangeRate);

            String json = JsonUtil.toJson(created);
            HttpUtil.sendJsonResponse(resp, 201, json);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
