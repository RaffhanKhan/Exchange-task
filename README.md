# Exchange Rate Service

## Overview

This project is a Spring Boot microservice that provides foreign exchange rate data. It fetches exchange rates for USD from the European Central Bank's API, stores the data in a PostgreSQL/MySQL database, and provides REST endpoints to access this data. The service includes Swagger documentation for easy API exploration.

## Features

- **Fetch Exchange Rates**: Retrieves and stores USD exchange rates for EUR, GBP, JPY, CZK.
- **Retrieve Latest Rates**: Provides the latest exchange rates for a specified target currency.
- **Retrieve Historical Rates**: Provides a time series of the three most recent exchange rates for a specified target currency.
- **Swagger Documentation**: Interactive API documentation.

## Requirements

- **Java 17** or higher
- **Spring Boot 3.x**
- **PostgreSQL** or **MySQL** (depending on your configuration)
- **Maven** (for building the project)


## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-repository/exchange-rate-service.git
cd exchange-rate-service

server.port=61001 (change in application property if needed)
Once the application is running, you can access the Swagger UI at:

http://localhost:61001/exchangeservice/swagger-ui/index.html#/

![image](https://github.com/user-attachments/assets/d2b04df0-2ebe-41ed-81b2-fcb1b3f79c26)

