package com.petprj.servlets.currency;

import com.petprj.dao.CurrencyDao;
import com.petprj.model.Currency;
import com.petprj.utils.ErrorHandler;
import com.petprj.utils.HttpUtil;
import com.petprj.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    public final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyDao.findAll();
            String json = JsonUtil.toJson(currencies);
            HttpUtil.sendJsonResponse(resp, 200, json);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String code = req.getParameter("code");
            String fullName = req.getParameter("name");
            String sign = req.getParameter("sign");

            if (code == null || code.isBlank() ||
                fullName == null || fullName.isBlank() ||
                sign == null || sign.isBlank()) {
                HttpUtil.sendError(resp, 400, "Поля 'code', 'name', 'sign' пустые");
                return;
            }

            code = code.trim().toUpperCase();
            if (!code.matches("^[A-Z]{3}$")){
                HttpUtil.sendError(resp, 400, "Код валюты должен состоять из 3-х латинских букв");
                return;
            }

            if (fullName.length() > 70){
                HttpUtil.sendError(resp, 400, "Наименование валюты слишком длинное");
                return;
            }

            if (sign.length() > 7){
                HttpUtil.sendError(resp, 400, "Знак валюты слишком длинный");
            }

            Currency existing = currencyDao.findByCode(code);
            if (existing != null) {
                HttpUtil.sendError(resp, 409, "Валюта с кодом =  " + code + " уже существует");
                return;
            }

            Currency currency = new Currency();
            currency.setCode(code);
            currency.setName(fullName.trim());
            currency.setSign(sign.trim());

            Currency created = currencyDao.create(currency);

            String json = JsonUtil.toJson(created);
            HttpUtil.sendJsonResponse(resp, 201, json);

        } catch (IOException e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
