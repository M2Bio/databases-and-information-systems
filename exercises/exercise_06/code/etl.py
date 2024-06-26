"""
Fact table:
    Date
    ShopID --> ShopID(name, city, region, country)
    ArticleID --> ArticleID(name, ProductGroup, ProductFamily, ProductCategory)
    Sold
    Revenue
"""

import csv
import psycopg2


def create_tables(conn_data_warehouse):
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


def insert_from_source_database(conn_data_source, conn_data_warehouse):
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
                    ON CONFLICT DO NOTHING
                    """,
                    shop,
                )

        for article in articles:

            with conn_data_warehouse.cursor() as cursor_dw:
                cursor_dw.execute(
                    """
                    INSERT INTO Article (ArticleID, Name, ProductGroup, ProductFamily, ProductCategory)
                    VALUES (%s, %s, %s, %s, %s)
                    ON CONFLICT DO NOTHING
                    """,
                    article,
                )

    conn_data_warehouse.commit()


def insert_row(row, conn_data_warehouse):
    with conn_data_warehouse.cursor() as cursor:
        # check if the row is valid
        if len(row) != 5:
            return
        # check if any of the values is empty
        if "" in row:
            return
        date = row[0]
        date = "-".join(reversed(date.split(".")))
        day = date.split("-")[2]
        month = date.split("-")[1]
        quarter = (int(month) - 1) // 3 + 1
        year = date.split("-")[0]
        shop = row[1]
        article = row[2]
        sold = row[3]
        revenue = row[4]
        revenue = revenue.replace(",", ".")

        cursor.execute(
            """
            SELECT ShopID FROM Shop WHERE Name = %s
            """,
            (shop,),
        )
        shop_id = cursor.fetchone()
        if not shop_id:
            return

        cursor.execute(
            """
            SELECT ArticleID FROM Article WHERE Name = %s
            """,
            (article,),
        )
        article_id = cursor.fetchone()
        if not article_id:
            return

        cursor.execute(
            """
            INSERT INTO Date (WholeDate, Day, Month, Quarter, Year)
            VALUES (%s, %s, %s, %s, %s)
            ON CONFLICT DO NOTHING
            """,
            (
                date,
                day,
                month,
                quarter,
                year,
            ),
        )

        cursor.execute(
            """
            INSERT INTO fact_table (WholeDate, ShopID, ArticleID, Sold, Revenue)
            VALUES (%s, %s, %s, %s, %s)
            ON CONFLICT DO NOTHING
            """,
            (date, shop_id, article_id, sold, revenue),
        )

    conn_data_warehouse.commit()


def insert_from_csv_file(csv_file, conn_data_warehouse, test_mode=False):
    """
    Inserting data from the csv file to the data warehouse
    The file has the header columns:
    Date;Shop;Article;Sold;Revenue
    Example row:
    01.01.2019;Superstore Berlin;AEG Öko Lavatherm 59850 Sensidry;25;24975,00
    """
    # csv_reader = pd.read_csv(csv_file, sep=";", encoding="ISO-8859-15", low_memory=False)
    counter = 0
    with open(csv_file, "r", encoding="ISO-8859-15") as file:
        next(file)  # skip the header
        csv_reader = csv.reader(file, delimiter=";")

        for row in csv_reader:
            insert_row(row, conn_data_warehouse)
            counter += 1
            if test_mode and counter > 100:
                break


def main():
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

    create_tables(conn_data_warehouse)
    insert_from_source_database(conn_data_source, conn_data_warehouse)

    csv_file = "data/sales.csv"
    insert_from_csv_file(csv_file, conn_data_warehouse, test_mode=False)

    conn_data_source.close()
    conn_data_warehouse.close()


if __name__ == "__main__":
    main()
