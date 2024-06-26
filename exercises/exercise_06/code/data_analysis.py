from collections import defaultdict

import psycopg2
from tabulate import tabulate


def analysis(geo, time, product):
    if (
        geo.lower() in ["name", "city", "region", "country"]
        and time.lower() in ["wholedate", "day", "month", "quarter", "year"]
        and product.lower() in ["name", "productgroup", "productfamily", "productcategory"]
    ):
        query = f"""
            SELECT shop.{geo}, date.{time}, article.{product}, SUM(sold) AS total_sales
            FROM fact_table
            JOIN Shop ON fact_table.shopid = Shop.shopid
            JOIN Article ON fact_table.articleid = Article.articleid
            JOIN Date ON fact_table.wholedate = date.wholedate
            GROUP BY CUBE (shop.{geo}, date.{time}, article.{product})
            ORDER BY shop.{geo}, date.{time}, article.{product};
            """
        cur.execute(query)
        all_data = cur.fetchall()

        all_articles = set()
        result = defaultdict(lambda: defaultdict(dict))
        for d in all_data:
            d = ["_Total" if v is None else v for v in d]
            result[d[0]][d[1]][d[2]] = d[3]
            all_articles.add(d[2])
        # result: Ort -> Datum -> Produkt -> Anzahl

        header = ["location", "sales"] + sorted(all_articles) + ["total"]
        table_content = []

        for store in result:
            for day in result[store]:
                new_table_row = [store, day]
                for product in sorted(all_articles):
                    product_count = result[store][day].get(product, 0)
                    new_table_row.append(str(product_count))
                table_content.append(new_table_row)

        print(tabulate(table_content, headers=header, tablefmt="pretty"))


if __name__ == "__main__":
    conn = psycopg2.connect(
        dbname="data_warehouse",
        user="postgres",
        password="postgres",
        host="localhost",
        port="5433",
    )
    cur = conn.cursor()

    analysis("country", "quarter", "productFamily")

    cur.close()
    if conn is not None:
        conn.close()
