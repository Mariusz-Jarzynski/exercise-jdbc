CREATE SCHEMA IF NOT EXISTS 'exercise-jdbc';

CREATE TABLE IF NOT EXISTS Persons
(
    ID      int AUTO_INCREMENT PRIMARY KEY,
    NAME    varchar(50),
    SURNAME varchar(50),
    AGE     int
)