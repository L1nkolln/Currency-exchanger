package com.petprj.dao;

import com.petprj.exeptions.DatabaseCreateException;
import com.petprj.exeptions.DatabaseException;
import com.petprj.exeptions.NotFoundException;
import com.petprj.model.ExchangeRate;
import com.petprj.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao <Integer, ExchangeRate> {

    private static final String FIND_ALL = """
            SELECT * 
            FROM exchange_rates;
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

    private static final String FIND_BY_CODE = """
            SELECT er.*
            FROM "exchange_rates" er
            JOIN currency bc ON er.base_currency_id = bc.id
            JOIN currency tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?;
            """;

    public ExchangeRate findByCodes(String baseCode, String targetCode) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {
            preparedStatement.setString(1, baseCode.toUpperCase());
            preparedStatement.setString(2, targetCode.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildExchangeRate(resultSet);
            } else {
                throw new NotFoundException("Exchange Rate " + baseCode + "-" + targetCode + " not found");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String UPDATE = """
            UPDATE exchange_rates
            SET base_currency_id = ?, target_currency_id = ?, rate = ?
            WHERE id = ?;
            """;

    @Override
    public void update(ExchangeRate entity){
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setInt(1, entity.getBaseCurrencyId());
            preparedStatement.setInt(2, entity.getTargetCurrencyId());
            preparedStatement.setDouble(3, entity.getRate());
            preparedStatement.setInt(4, entity.getId());
            int updated = preparedStatement.executeUpdate();
            if (updated == 0) {
                throw new NotFoundException("Курс с id= " + entity.getId() + " не найдена");
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
            preparedStatement.setDouble(3, entity.getRate());
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
        return new ExchangeRate(
                resultSet.getObject("id", Integer.class),
                resultSet.getObject("base_currency_id", Integer.class),
                resultSet.getObject("target_currency_id", Integer.class),
                resultSet.getObject("rate", Double.class)
        );
    }
}
