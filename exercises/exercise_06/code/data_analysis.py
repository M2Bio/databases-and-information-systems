from collections import defaultdict
import psycopg2
from tabulate import tabulate


def analysis(geo, time, product):
    valid_geo = ["name", "city", "region", "country"]
    valid_time = ["wholedate", "day", "month", "quarter", "year"]
    valid_product = ["name", "productgroup", "productfamily", "productcategory"]

    if geo.lower() not in valid_geo or time.lower() not in valid_time or product.lower() not in valid_product:
        print("Invalid input.")
        return

    query = f"""
        SELECT shop.{geo}, date.{time}, article.{product}, SUM(sold) AS total_sales
        FROM fact_table
        JOIN Shop ON fact_table.shopid = Shop.shopid
        JOIN Article ON fact_table.articleid = Article.articleid
        JOIN Date ON fact_table.wholedate = date.wholedate
        GROUP BY CUBE (shop.{geo}, date.{time}, article.{product})
        ORDER BY shop.{geo}, date.{time}, article.{product};
        """

    with psycopg2.connect(
        dbname="data_warehouse",
        user="postgres",
        password="postgres",
        host="localhost",
        port="5433",
    ) as conn:
        with conn.cursor() as cur:
            cur.execute(query)
            all_data = cur.fetchall()

    all_articles = set()
    result = defaultdict(lambda: defaultdict(dict))
    for d in all_data:
        d = ["_Total" if v is None else v for v in d]
        result[d[0]][d[1]][d[2]] = d[3]
        all_articles.add(d[2])

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
    analysis("country", "quarter", "productFamily")
    analysis("city", "year", "productCategory")
