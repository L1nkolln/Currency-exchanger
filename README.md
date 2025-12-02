# Проект Обмен валют

Для подключения
http://193.47.42.217:8080/currency-exchange/currencies

Запросы

GET /currencies - получение всего списка валют

GET /currency/{3-х значный код валюты} - получение конкретной валюты по коду

POST /currencies - добавления новой валюты
    
    Поля:
        code - код валюты
        fullName - полное название
        sign - знак валюты

GET /exchangeRates - получение всего списка курсов валют

GET /exchangeRate/{3-х значный код валюты}{3-х значный код валюты} - получение конкретного обменного курса

POST /exchangeRates - добавление нового обменного курса

Поля:
        baseCurrencyCode - валюта 1
        targetCurrencyCode - валюта 2
        rate - курс валюты

PATCH /exchangeRate/{3-х значный код валюты}{3-х значный код валюты} обновление существующего обменного курса

GET /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT

Расчёт перевода определённого количества средств из одной валюты в другую

BASE_CURRENCY_CODE - код валюты 1
TARGET_CURRENCY_CODE - код валюты 2
AMOUNT - количество


