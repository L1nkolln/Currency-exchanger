package com.petprj.servlets.exchangeRate;

import com.petprj.dao.CurrencyDao;
import com.petprj.dao.ExchangeRateDao;
import com.petprj.exeptions.NotFoundException;
import com.petprj.model.Currency;
import com.petprj.model.ExchangeRate;
import com.petprj.utils.ErrorHandler;
import com.petprj.utils.ExchangeResponse;
import com.petprj.utils.HttpUtil;
import com.petprj.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        } catch (Exception e) {
            e.printStackTrace();
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
                HttpUtil.sendError(resp, 400, "Поля 'baseCurrency', 'targetCurrency', 'rate' пустые");
                return;
            }

            baseCurrencyCode = baseCurrencyCode.trim().toUpperCase();
            targetCurrencyCode = targetCurrencyCode.trim().toUpperCase();
            if (!baseCurrencyCode.matches("^[A-Z]{3}")){
                HttpUtil.sendError(resp, 400, "Код 1 валюты введен неверно");
                return;
            }
            if (!targetCurrencyCode.matches("^[A-Z]{3}")){
                HttpUtil.sendError(resp, 400, "Код 2 валюты введен неверно");
                return;
            }

            if (baseCurrencyCode.equalsIgnoreCase(targetCurrencyCode)){
                HttpUtil.sendError(resp, 400,
                        "Невозможно создать курс с одинаковым кодом");
                return;
            }

            Currency baseCode = currencyDao.findByCode(baseCurrencyCode);
            Currency targetCode = currencyDao.findByCode(targetCurrencyCode);
            if (baseCode == null) {
                HttpUtil.sendError(resp, 404, "Валюта 1 не найдена");
                return;
            }
            if (targetCode == null) {
                HttpUtil.sendError(resp, 404, "Валюта 2 не найдена");
                return;
            }
            try {
                ExchangeRate existing = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
                if (existing != null) {
                    HttpUtil.sendError(resp, 409,
                            "Курс =  " + baseCurrencyCode + "/" + targetCurrencyCode + " уже существует");
                    return;
                }
            } catch (NotFoundException ignore) {

            }

            rate = rate.trim().replace(',', '.');
            if (!rate.matches("\\d+(\\.\\d+)?")){
                HttpUtil.sendError(resp, 400, "Курс должен быть больше нуля");
                return;
            }
            BigDecimal parseRate = new BigDecimal(rate).setScale(6, RoundingMode.HALF_UP);
            if (parseRate.compareTo(BigDecimal.ZERO) <= 0){
                HttpUtil.sendError(resp, 400, "Курс не может быть нулем");
                return;
            }

//            ExchangeResponse responseObj = new ExchangeResponse(baseCode, targetCode, parseRate);
//            String json = JsonUtil.toJson(responseObj);

            ExchangeRate created = exchangeRateDao.create(
                    new ExchangeRate(0, baseCode.getId(), targetCode.getId(), parseRate));
            created.setBaseCurrency(baseCode);
            created.setTargetCurrency(targetCode);
            String json = JsonUtil.toJson(created);
            HttpUtil.sendJsonResponse(resp, 201, json);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
