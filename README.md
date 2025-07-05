# User Account API

REST-приложение на Java 11+ и Spring Boot для управления пользователями и их аккаунтами.

## Возможности

- Регистрация пользователей с email и телефоном
- Аутентификация по email/phone и паролю (JWT)
- Получение данных текущего пользователя
- Поиск пользователей по фильтрам
- Денежные переводы между аккаунтами
- Автоувеличение баланса по расписанию
- Swagger-документация

## Технологии

- Java 11
- Spring Boot 2.7
- PostgreSQL
- JPA (Hibernate)
- Liquibase
- JWT (jjwt)
- Swagger/OpenAPI
- TestContainers + H2 для тестов

## Как запустить

1. Поднять PostgreSQL:

   docker run --name user-db      -e POSTGRES_DB=userdb      -e POSTGRES_USER=postgres      -e POSTGRES_PASSWORD=password      -p 5432:5432 -d postgres

2. Собрать и запустить проект:

   mvn clean install
   mvn spring-boot:run

3. Swagger UI доступен по адресу:

   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Аутентификация

Отправь `POST` на `/auth/login-email` или `/auth/login-phone`:
{
  "login": "user@mail.com",
  "password": "123456"
}

Получи JWT и передавай в заголовке:

Authorization: Bearer <token>

## Тестирование

Юнит- и интеграционные тесты можно запустить:

mvn test

## Авторы

Тестовое задание от **Пионер Пиксель**  
Разработал: **Александр**