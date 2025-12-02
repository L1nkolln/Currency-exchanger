package com.petprj.servlets;

import com.petprj.dao.ExchangeRateDao;
import com.petprj.model.ExchangeRate;
import com.petprj.utils.ErrorHandler;
import com.petprj.utils.HttpUtil;
import com.petprj.utils.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class GetAndPatchExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                HttpUtil.sendError(resp, 404, "Currency pair not provided");
            }
            String pair = pathInfo.substring(1).toUpperCase();
            if (pair.length() != 6) {
                HttpUtil.sendError(resp, 400, "Invalid pair format. Expected format: USDEUR");
                return;
            }
            String baseCode = pair.substring(0,3);
            String targetCode = pair.substring(3);

            ExchangeRate rate = exchangeRateDao.findByCodes(baseCode, targetCode);

            if (rate == null) {
                HttpUtil.sendError(resp, 404, "Pair not found");
                return;
            }

            String json = JsonUtil.toJson(rate);
            HttpUtil.sendJsonResponse(resp, 200, json);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }

//    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                HttpUtil.sendError(resp, 404, "Currency pair not provided");
                return;
            }
            String pair = pathInfo.substring(1).toUpperCase();
            if (pair.length() != 6) {
                HttpUtil.sendError(resp, 400, "Invalid pair format. Expected format: USDEUR");
                return;
            }
            String baseCode = pair.substring(0,3);
            String targetCode = pair.substring(3,6);

            ExchangeRate updated = exchangeRateDao.findByCodes(baseCode, targetCode);
            if (updated == null) {
                HttpUtil.sendError(resp, 404, "Pair not found");
                return;
            }

            String rate = req.getReader()
                    .lines()
                    .collect(Collectors.joining());
            if (rate == null || rate.isBlank()) {
                HttpUtil.sendError(resp, 400, "Invalid rate");
                return;
            }
            String[] parts = rate.split("=");
            if (parts.length != 2 || !parts[0].equals("rate")) {
                HttpUtil.sendError(resp, 400, "Invalid form data");
            }

            String rateStr = parts[1].trim().replace(',', '.');
            if (!rateStr.matches("\\d+(\\.\\d+)?")){
                HttpUtil.sendError(resp, 400, "Rate must be a valid number");
                return;
            }
            double parseDouble = Double.parseDouble(rateStr);
            if (parseDouble <= 0) {
                HttpUtil.sendError(resp, 400, "Rate must be a positive number");
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
