from collections import defaultdict
import psycopg2
from tabulate import tabulate


def analysis(geo, time, product):
    # Valid options for geo, time, and product
    valid_geo = ["name", "city", "region", "country"]
    valid_time = ["wholedate", "day", "month", "quarter", "year"]
    valid_product = ["name", "productgroup", "productfamily", "productcategory"]

    # Check if the input is valid
    if geo.lower() not in valid_geo or time.lower() not in valid_time or product.lower() not in valid_product:
        print("Invalid input.")
        return

    # SQL query to retrieve data from the fact_table
    query = f"""
        SELECT shop.{geo}, date.{time}, article.{product}, SUM(sold) AS total_sales
        FROM fact_table
        JOIN Shop ON fact_table.shopid = Shop.shopid
        JOIN Article ON fact_table.articleid = Article.articleid
        JOIN Date ON fact_table.wholedate = date.wholedate
        GROUP BY CUBE (shop.{geo}, date.{time}, article.{product})
        ORDER BY shop.{geo}, date.{time}, article.{product};
        """

    # Connect to the PostgreSQL database
    with psycopg2.connect(
        dbname="data_warehouse",
        user="postgres",
        password="postgres",
        host="localhost",
        port="5433",
    ) as conn:
        with conn.cursor() as cur:
            # Execute the SQL query
            cur.execute(query)
            # Fetch all the data
            all_data = cur.fetchall()

    # Create a nested dictionary to store the result
    result = defaultdict(lambda: defaultdict(dict))
    all_articles = set()

    # Process the fetched data and populate the result dictionary
    for d in all_data:
        d = ["_Total" if v is None else v for v in d]
        result[d[0]][d[1]][d[2]] = d[3]
        all_articles.add(d[2])

    # Prepare the table headers
    header = ["location", "sales"] + sorted(all_articles) + ["total"]
    table_content = []

    # Prepare the table content
    for store, store_data in result.items():
        for day, day_data in store_data.items():
            new_table_row = [store, day]
            for product in sorted(all_articles):
                product_count = day_data.get(product, 0)
                new_table_row.append(str(product_count))
            table_content.append(new_table_row)

    # Print the table using the tabulate library
    print(tabulate(table_content, headers=header, tablefmt="pretty"))


if __name__ == "__main__":
    # Example usage of the analysis function
    analysis("country", "quarter", "productFamily")
    analysis("city", "year", "productCategory")
