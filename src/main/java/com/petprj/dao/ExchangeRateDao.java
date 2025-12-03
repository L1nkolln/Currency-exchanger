package com.petprj.dao;

import com.petprj.exeptions.DatabaseCreateException;
import com.petprj.exeptions.DatabaseException;
import com.petprj.exeptions.NotFoundException;
import com.petprj.model.Currency;
import com.petprj.model.ExchangeRate;
import com.petprj.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao <Integer, ExchangeRate> {

    private static final String FIND_ALL = """
            SELECT er.id, er.rate, er.base_currency_id, er.target_currency_id,
                   bc.id AS bc_id, bc.full_name AS bc_fullName, bc.code AS bc_code, bc.sign AS bc_sign,
                   tc.id AS tc_id, tc.full_name AS tc_fullName, tc.code AS tc_code, tc.sign AS tc_sign
            FROM exchange_rates er
            JOIN currency bc ON er.base_currency_id = bc.id
            JOIN currency tc ON er.target_currency_id = tc.id
            """;

    @Override
    public List<ExchangeRate> findAll() {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String FIND_BY_ID = """
            SELECT id
            FROM exchange_rates
            WHERE id = ?;
            """;

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildExchangeRate(resultSet));
            } else {
                throw new NotFoundException("Курс с id= " + id + " не найден");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String FIND_BY_CODES = """
            SELECT er.id, er.rate,
                    bc.id AS bc_id, bc.full_name AS bc_fullName, bc.code AS bc_code, bc.sign AS bc_sign,
                    tc.id AS tc_id, tc.full_name AS tc_fullName, tc.code AS tc_code, tc.sign AS tc_sign
            FROM "exchange_rates" er
            JOIN currency bc ON er.base_currency_id = bc.id
            JOIN currency tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?;
            """;

    public ExchangeRate findByCodes(String baseCode, String targetCode) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES)) {
            preparedStatement.setString(1, baseCode.toUpperCase());
            preparedStatement.setString(2, targetCode.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildExchangeRate(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String UPDATE = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE id = ?;
            """;
//base_currency_id = ?, target_currency_id = ?,
    @Override
    public void update(ExchangeRate entity){
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
//            preparedStatement.setInt(1, entity.getBaseCurrencyId());
//            preparedStatement.setInt(2, entity.getTargetCurrencyId());
            preparedStatement.setBigDecimal(1, entity.getRate());
            preparedStatement.setInt(2, entity.getId());
            int updated = preparedStatement.executeUpdate();
            if (updated == 0) {
                throw new NotFoundException("Курс с id = " + entity.getId() + " не найдена");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String CREATE_N = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)""";

    @Override
    public ExchangeRate create(ExchangeRate entity) {
        int idGen = -1;
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(CREATE_N, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, entity.getBaseCurrencyId());
            preparedStatement.setInt(2, entity.getTargetCurrencyId());
            preparedStatement.setBigDecimal(3, entity.getRate());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseCreateException("Не удалось добавить курс");
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                idGen = generatedKeys.getInt(1);
            } else {
                throw new DatabaseCreateException("Не удалось получить id нового курса");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return new ExchangeRate(idGen, entity.getBaseCurrencyId(), entity.getTargetCurrencyId(), entity.getRate());
    }

    private ExchangeRate buildExchangeRate (ResultSet resultSet) throws SQLException {
        ExchangeRate rate = new ExchangeRate();
        rate.setId(resultSet.getInt("id"));
        rate.setRate(resultSet.getBigDecimal("rate"));

        Currency base = new Currency(
                resultSet.getInt("bc_id"),
                resultSet.getString("bc_fullName"),
                resultSet.getString("bc_code"),
                resultSet.getString("bc_sign")
        );

        Currency target = new Currency(
                resultSet.getInt("tc_id"),
                resultSet.getString("tc_fullName"),
                resultSet.getString("tc_code"),
                resultSet.getString("tc_sign")
        );

        rate.setBaseCurrency(base);
        rate.setTargetCurrency(target);

        return rate;
    }
}