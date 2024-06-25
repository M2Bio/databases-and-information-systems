"""
Fact table:
    Date
    ShopID --> ShopID(name, city, region, country)
    ArticleID --> ArticleID(name, ProductGroup, ProductFamily, ProductCategory)
    Sold
    Revenue
"""

import csv

import pandas as pd
import psycopg2

conn_data_source = psycopg2.connect(
    dbname="data_source",
    user="postgres",
    password="postgres",
    host="localhost",
    port="5432",
)

conn_data_warehouse = psycopg2.connect(
    dbname="data_warehouse",
    user="postgres",
    password="postgres",
    host="localhost",
    port="5433",
)


def create_tables():
    with conn_data_warehouse.cursor() as cursor:
        # if tables do already exist, return
        cursor.execute(
            """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
            """
        )
        tables = cursor.fetchall()
        if tables:
            return
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS fact_table (
                WholeDate date NOT NULL,
                ShopID int NOT NULL,
                ArticleID int NOT NULL,
                Sold int NOT NULL,
                Revenue double precision NOT NULL
            );
            CREATE TABLE IF NOT EXISTS Shop (
                ShopID int PRIMARY KEY,
                Name varchar(255) NOT NULL,
                City varchar(255) NOT NULL,
                Region varchar(255) NOT NULL,
                Country varchar(255) NOT NULL
            );
            CREATE TABLE IF NOT EXISTS Article (
                ArticleID int PRIMARY KEY,
                Name varchar(255) NOT NULL,
                ProductGroup varchar(255) NOT NULL,
                ProductFamily varchar(255) NOT NULL,
                ProductCategory varchar(255) NOT NULL
            );
            CREATE TABLE IF NOT EXISTS Date (
                WholeDate date PRIMARY KEY,
                Day int NOT NULL,
                Month int NOT NULL,
                Quarter int NOT NULL,
                Year int NOT NULL
            );
            ALTER TABLE fact_table
                ADD FOREIGN KEY (ShopID) REFERENCES Shop(ShopID);
            ALTER TABLE fact_table
                ADD FOREIGN KEY (ArticleID) REFERENCES Article(ArticleID);
            ALTER TABLE fact_table
                ADD FOREIGN KEY (WholeDate) REFERENCES Date(WholeDate);
            """
        )

    conn_data_warehouse.commit()


def insert_from_source_database():
    """
    Inserting data from the source database to the data warehouse, i.e. the data from
    stores-and-products.sql
    """
    with conn_data_source.cursor() as cursor:
        cursor.execute(
            """
            SELECT Shop.ShopID, Shop.Name, city.Name, region.Name, country.Name FROM Shop
            JOIN city ON Shop.cityID = city.cityID
            JOIN region ON city.regionID = region.regionID
            JOIN country ON region.countryID = country.countryID
            """
        )
        shops = cursor.fetchall()

        cursor.execute(
            """
            SELECT Article.ArticleID, Article.Name, ProductGroup.Name, ProductFamily.Name, ProductCategory.Name FROM Article
            JOIN ProductGroup ON Article.ProductGroupID = ProductGroup.ProductGroupID
            JOIN ProductFamily ON ProductGroup.ProductFamilyID = ProductFamily.ProductFamilyID
            JOIN ProductCategory ON ProductFamily.ProductCategoryID = ProductCategory.ProductCategoryID
            """
        )
        articles = cursor.fetchall()

        for shop in shops:
            with conn_data_warehouse.cursor() as cursor_dw:
                cursor_dw.execute(
                    """
                    INSERT INTO Shop (ShopID, Name, City, Region, Country)
                    VALUES (%s, %s, %s, %s, %s)
                    """,
                    shop,
                )

        for article in articles:

            with conn_data_warehouse.cursor() as cursor_dw:
                cursor_dw.execute(
                    """
                    INSERT INTO Article (ArticleID, Name, ProductGroup, ProductFamily, ProductCategory)
                    VALUES (%s, %s, %s, %s, %s)
                    """,
                    article,
                )

    conn_data_warehouse.commit()


if __name__ == "__main__":
    # check if tables exist if not create them
    create_tables()

    # insert data from source database
    insert_from_source_database()
