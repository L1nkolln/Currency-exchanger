package com.petprj.dao;

import com.petprj.exeptions.DatabaseCreateException;
import com.petprj.utils.DatabaseManager;
import com.petprj.model.Currency;
import com.petprj.exeptions.DatabaseException;
import com.petprj.exeptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, Currency> {

    private static final String FIND_ALL = """
            SELECT *
            FROM currency
            """;

    @Override
    public List<Currency> findAll() {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement =  connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            if (currencies.isEmpty()) {
                throw new NotFoundException("Валюта не найдена");
            }
            return currencies;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String FIND_BY_ID = """
            SELECT *
            FROM currency
            WHERE id = ?
            """;

    @Override
    public Optional<Currency> findById(Integer id) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            } else {
                throw new NotFoundException("Валюта с id= " + id + " не найдена");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String FIND_BY_CODE = """
            SELECT *
            FROM currency
            WHERE code = ?
            """;

    public Currency findByCode(String code) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {
            preparedStatement.setString(1, code.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildCurrency(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String UPDATE = """
            UPDATE currency
            SET code = ?, full_name = ?,  sign = ?
            WHERE id = ?
            """;

    @Override
    public void update(Currency entity) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getSign());
            preparedStatement.setInt(4, entity.getId());
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new NotFoundException("Валюта с id= " + entity.getId() + " не найдена");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String CREATE_N = """
            INSERT INTO currency  (code, full_name, sign)
            VALUES (?, ?, ?)""";

    @Override
    public Currency create(Currency entity) {
        int idGen = -1;
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(CREATE_N, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getSign());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseCreateException("Не удалось добавить валюту");
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
               idGen = generatedKeys.getInt(1);
            } else {
                throw new DatabaseCreateException("Не удалось получить id новой валюты");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return new Currency(idGen, entity.getCode(), entity.getName(), entity.getSign());
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getObject("id", Integer.class),
                resultSet.getObject("code", String.class),
                resultSet.getObject("full_name", String.class),
                resultSet.getObject("sign", String.class)
        );
    }
}
