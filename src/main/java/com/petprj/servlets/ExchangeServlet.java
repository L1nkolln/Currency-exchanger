package com.petprj.servlets;

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

//changed
// http://localhost:8080/currencyexchanger_war_exploded/exchange?from=USD&to=BYN&amount=2
@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        try {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String amountParam = req.getParameter("amount");

            if (from == null || to == null || amountParam == null) {
                HttpUtil.sendError(resp, 400, "Поле Amount пустое");
                return;
            }
            from = from.trim().toUpperCase();
            to = to.trim().toUpperCase();

            if (!from.matches("^[A-Z]{3}$") || !to.matches("^[A-Z]{3}$")) {
                HttpUtil.sendError(resp, 400, "Валюты введены неверно");
                return;
            }

            if (from.equals(to)){
                HttpUtil.sendError(resp, 400, "Валюта base и валюта target одинаковые");
                return;
            }
            String amountParamStr = amountParam.trim().replace(",", ".");
            if (!amountParamStr.matches("\\d+(\\.\\d+)?")) {
                HttpUtil.sendError(resp, 400, "Неверно введен курс");
                return;
            }
            BigDecimal parsedAmount = new BigDecimal(amountParamStr).setScale(6, RoundingMode.HALF_UP);
            if (parsedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                HttpUtil.sendError(resp, 400, "Курс должен быть больше нуля");
                return;
            }

            Currency fromCode;
            Currency toCode;
            try {
                fromCode = currencyDao.findByCode(from);
                toCode = currencyDao.findByCode(to);
            } catch (NotFoundException e) {
                HttpUtil.sendError(resp, 404, "Валюта не найдена");
                return;
            }

            BigDecimal rate = null;

            ExchangeRate directAB = safeFindByCodes(from, to);
            ExchangeRate directBA = safeFindByCodes(to, from);
            if (directAB != null) {
                rate = directAB.getRate();
            }
            if (directAB == null && directBA != null) {
                rate = BigDecimal.ONE.divide(directBA.getRate(), 6, RoundingMode.HALF_UP);
            }
//////////////////////////////////////////////////////////////
            if (rate == null) {
                Currency usdCurrency = currencyDao.findByCode("USD");
                if (usdCurrency == null) {
                    HttpUtil.sendError(resp, 404, "Валюта не найдена\"");
                    return;
                }
                int usdId = usdCurrency.getId();

                ExchangeRate usdA = safeFindByCodes("USD", from);
                if (usdA == null) usdA = safeFindByCodes(from, "USD");

                ExchangeRate usdB = safeFindByCodes("USD", to);
                if (usdB == null) usdB = safeFindByCodes(to, "USD");

                if (usdA != null && usdB != null) {
                    BigDecimal usdToA = (usdA.getBaseCurrencyId() == usdId)
                            ? usdA.getRate()
                            : BigDecimal.ONE.divide(usdA.getRate(), 6, RoundingMode.HALF_UP);

                    BigDecimal usdToB = (usdB.getBaseCurrencyId() == usdId)
                            ? usdB.getRate()
                            : BigDecimal.ONE.divide(usdB.getRate(), 6, RoundingMode.HALF_UP);

                    rate = usdToB.divide(usdToA, 6, RoundingMode.HALF_UP);
                }
            }

            if (rate == null) {
                HttpUtil.sendError(resp, 404, "Курс не найден");
                return;
            }

            BigDecimal result = parsedAmount.multiply(rate).setScale(6, RoundingMode.HALF_UP);

            ExchangeResponse responseObj = new ExchangeResponse(fromCode, toCode, rate, parsedAmount, result);

            String json = JsonUtil.toJson(responseObj);

            HttpUtil.sendJsonResponse(resp, 201, json);

        } catch (IOException e) {
            ErrorHandler.handle(resp, e);
        }
    }

    private ExchangeRate safeFindByCodes(String base, String target) {
        try {
            return exchangeRateDao.findByCodes(base, target);
        } catch (NotFoundException e) {
            return null;
        }
    }
}