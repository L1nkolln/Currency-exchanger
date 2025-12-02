package com.petprj.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class ErrorHandler {

    public static void handle (HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof SQLException) {
            HttpUtil.sendError(resp, 500, "Database error");
        } else if (e instanceof IllegalArgumentException) {
            HttpUtil.sendError(resp, 400, e.getMessage());
        } else {
            HttpUtil.sendError(resp, 500, "Internal server error");
        }
        e.printStackTrace();
    }
}
