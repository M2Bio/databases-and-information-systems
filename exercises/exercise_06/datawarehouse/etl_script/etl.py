import pandas as pd
import psycopg
import time
from sqlalchemy import create_engine

# Wait for the database to be ready
time.sleep(10)

# Database connection
conn = psycopg.connect(
    dbname="postgres", user="postgres", password="postgres", host="localhost", port="5432"
)
cur = conn.cursor()
engine = create_engine("postgresql+psycopg://postgres:postgres@localhost:5432/postgres")

# Load sales data which has the following header:
# Date;Shop;Article;Sold;Revenue
sales_data = pd.read_csv("etl_script/sales.csv", sep=";", on_bad_lines="skip")

print(sales_data.head())

sales_data.dropna(inplace=True)
sales_data["Sold"] = pd.to_numeric(sales_data["Sold"], errors="coerce")
sales_data["Revenue"] = pd.to_numeric(sales_data["Revenue"], errors="coerce")
sales_data["Date"] = pd.to_datetime(sales_data["Date"], format="%d.%m.%Y")

# Prepare star schema tables
create_star_schema = """
CREATE TABLE IF NOT EXISTS sales_fact (
    date DATE,
    shop_id INT,
    product_id INT,
    quantity_sold INT,
    turnover FLOAT,
    PRIMARY KEY (date, shop_id, product_id)
);

CREATE TABLE IF NOT EXISTS time_dim (
    date DATE PRIMARY KEY,
    day INT,
    month INT,
    quarter INT,
    year INT
);

CREATE TABLE IF NOT EXISTS shop_dim (
    shop_id INT PRIMARY KEY,
    city_id INT,
    region_id INT,
    country_id INT
);

CREATE TABLE IF NOT EXISTS product_dim (
    product_id INT PRIMARY KEY,
    product_group_id INT,
    product_family_id INT,
    product_category_id INT
);
"""

cur.execute(create_star_schema)
conn.commit()

# Load data into the star schema tables
time_dim = sales_data[["Date"]].drop_duplicates()
time_dim["day"] = time_dim["Date"].dt.day
time_dim["month"] = time_dim["Date"].dt.month
time_dim["quarter"] = time_dim["Date"].dt.quarter
time_dim["year"] = time_dim["Date"].dt.year
time_dim.to_sql("time_dim", engine, if_exists="append", index=False)

shop_dim = sales_data[["Shop"]].drop_duplicates().reset_index()
shop_dim.rename(columns={"index": "shop_id"}, inplace=True)
shop_dim.to_sql("shop_dim", engine, if_exists="append", index=False)

product_dim = sales_data[["Article"]].drop_duplicates().reset_index()
product_dim.rename(columns={"index": "product_id"}, inplace=True)
product_dim.to_sql("product_dim", engine, if_exists="append", index=False)

sales_data = (
    sales_data.merge(time_dim, on="Date")
    .merge(shop_dim, on="Shop")
    .merge(product_dim, on="Article")
)
sales_data.drop(columns=["Date", "Shop", "Article"], inplace=True)
sales_data.rename(columns={"Sold": "quantity_sold", "Revenue": "turnover"}, inplace=True)
sales_data.to_sql("sales_fact", engine, if_exists="append", index=False)

# Close database connection
cur.close()

# Wait for the database to be ready
time.sleep(10)
