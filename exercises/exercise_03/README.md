# Exercise 03

## 3.1 Isolation Levels

a) Open a connection to database dis-2024. What is the current isolation level? Which isolation levels does PostgreSQL support?

    Read uncommitted
    Read committed
    Repeatable read
    Serializable

    The current isolation level of dis-2024:
        SQL: SHOW TRANSACTION ISOLATION LEVEL;
        Output: read committed

b) Create a simple table sheet3 with columns id and name. Fill the table with some example data.