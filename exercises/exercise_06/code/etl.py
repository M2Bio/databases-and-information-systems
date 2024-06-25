"""
Fact table:
    Date
    ShopID --> ShopID(name, city, region, country)
    ArticleID --> ArticleID(name, ProductGroup, ProductFamily, ProductCategory)
    Sold
    Revenue
"""

import pandas as pd
import psycopg2

conn = psycopg2.connect("dbname=postgres user=postgres password=postgres")
cur = conn.cursor()

# Load data
