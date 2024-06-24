import psycopg2


def analysis(geo, time, product):
    conn = psycopg2.connect(dbname="dis-datawarehouse", user="marius", password="1234", host="db")
    cur = conn.cursor()
    query = f"""
    SELECT {geo}, {time}, {product}, SUM(sold_units) AS total_sales
    FROM sales_fact
    JOIN Shop ON sales_fact.shop_id = Shop.shop_id
    JOIN Article ON sales_fact.article_id = Article.article_id
    GROUP BY GROUPING SETS (
        ({geo}, {time}, {product}),
        ({geo}, {time}),
        ({time}, {product}),
        ({geo}, {product}),
        ({geo}),
        ({time}),
        ({product}),
        ()
    )
    ORDER BY {geo}, {time}, {product};
    """
    cur.execute(query)
    results = cur.fetchall()
    for row in results:
        print(row)
    cur.close()
    conn.close()


# Example usage
analysis("region", "quarter", "productFamily")
