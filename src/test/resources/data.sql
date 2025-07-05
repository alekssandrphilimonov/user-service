DELETE FROM phone_data;
DELETE FROM email_data;
DELETE FROM account;
DELETE FROM users;

INSERT INTO users (id, name, date_of_birth, password)
VALUES (100, 'Test User', '1993-05-01', '$2a$12$tsqfGe0rGCRIhTYz.Lrc1.ppb/ZaOn3YvoRhkTQZ.jEXwChG3t2TS');

INSERT INTO email_data (id, user_id, email)
VALUES (200, 100, 'test@mail.com');

INSERT INTO phone_data (id, user_id, phone)
VALUES (300, 100, '79207865432');

INSERT INTO account (id, user_id, balance, initial_balance)
VALUES (400, 100, 1000, 1000);
