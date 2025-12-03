package com.petprj.servlets.exchangeRate;

import com.petprj.dao.ExchangeRateDao;
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
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class GetAndPatchExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                HttpUtil.sendError(resp, 400, "Не введена валютная пара");
                return;
            }
            String pair = pathInfo.substring(1).toUpperCase();
            if (!pair.matches("^[A-Z]{6}")) {
                HttpUtil.sendError(resp, 400, "Введен неправильный формат валютной пары. Ожидается формат: USDEUR");
                return;
            }
            String baseCode = pair.substring(0,3);
            String targetCode = pair.substring(3);

            ExchangeRate rate = exchangeRateDao.findByCodes(baseCode, targetCode);

            if (rate == null) {
                HttpUtil.sendError(resp, 404, "Валютная пара не найдена");
                return;
            }

            String json = JsonUtil.toJson(rate);
            HttpUtil.sendJsonResponse(resp, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handle(resp, e);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                HttpUtil.sendError(resp, 404, "Не введена валютная пара");
                return;
            }
            String pair = pathInfo.substring(1).toUpperCase();
            if (!pair.matches("^[A-Z]{6}")) {
                HttpUtil.sendError(resp, 400, "Введен неправильный формат валютной пары. Ожидается формат: USDEUR");
                return;
            }
            String baseCode = pair.substring(0,3);
            String targetCode = pair.substring(3,6);

            ExchangeRate updated = exchangeRateDao.findByCodes(baseCode, targetCode);
            if (updated == null) {
                HttpUtil.sendError(resp, 404, "Валютная пара не найдена");
                return;
            }

            String body = req.getReader()
                    .lines()
                    .collect(Collectors.joining("&"));
            String rate = null;
            for (String param : body.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("rate")) {
                    rate = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    break;
                }
            }

            if (rate == null || rate.isBlank()) {
                HttpUtil.sendError(resp, 400, "Не введен курс");
                return;
            }
            rate = rate.replace(',', '.');
            if (!rate.matches("\\d+(\\.\\d+)?")){
                HttpUtil.sendError(resp, 400, "Курс введен неверно");
                return;
            }
            BigDecimal parseDouble = new BigDecimal(rate).setScale(6, RoundingMode.HALF_UP);
            if (parseDouble.compareTo(BigDecimal.ZERO) <= 0) {
                HttpUtil.sendError(resp, 400, "Он должен быть больше нуля");
                return;
            }

            updated.setRate(parseDouble);
            exchangeRateDao.update(updated);
            HttpUtil.sendJsonResponse(resp, 200, JsonUtil.toJson(updated));

        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
