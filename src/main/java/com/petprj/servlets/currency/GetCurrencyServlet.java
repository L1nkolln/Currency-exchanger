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

@WebServlet("/currency/*")
public class GetCurrencyServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if(pathInfo == null || pathInfo.equals("/")) {
                HttpUtil.sendError(resp, 400, "Не введен код валюты");
                return;
            }

            String code = pathInfo.substring(1).toUpperCase();

            if (!code.matches("^[A-Z]{3}$")) {
                HttpUtil.sendError(resp, 400, "Код валюты введен неверно");
                return;
            }

            Currency currency = currencyDao.findByCode(code);

            if (currency == null) {
                HttpUtil.sendError(resp, 404, "Валюта не найдена");
                return;
            }

            String json = JsonUtil.toJson(currency);
            HttpUtil.sendJsonResponse(resp, 200, json);

        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
