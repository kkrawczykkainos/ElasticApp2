import java.sql.{Connection, DriverManager, ResultSet}

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.search.SearchResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import net.liftweb.json._
import net.liftweb.json.Serialization.write

object Main extends App {

  val url = "jdbc:mysql://10.0.0.3/retail_db"
  val driver = "com.mysql.jdbc.Driver"
  val username = "retail_dba"
  val password = "cloudera"
  var connection:Connection = _

  import com.sksamuel.elastic4s.http.ElasticDsl._
  implicit val formats = net.liftweb.json.DefaultFormats
  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

  client.execute{
    deleteIndex("products")
    createIndex("products")
  }

  try {
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement
    val rs: ResultSet = statement.executeQuery("SELECT product_name, category_name, product_price FROM products p left join categories c on p.product_category_id = c.category_id")
    while (rs.next) {
      val productName = rs.getString("product_name")
      val categoryName = rs.getString("category_name")
      val productPrice = rs.getBigDecimal("product_price")
      println(s" $productName, $categoryName, $productPrice")
      val product = Product(productName, categoryName, productPrice)
      client.execute {
        val category: String = Option(product.categoryName).getOrElse("None").toLowerCase().replaceAll("\\s", "")
        indexInto("products" / category).doc(write(product))
      }.await

    }
    println("--")
    println()
    while (true) {
      println("What's your query? ")
      val q = scala.io.StdIn.readLine()
      val resp: SearchResponse = client.execute{
        search("products") query q
      }.await

      println("---- Search Results ----")
      println("took: " + resp.took)
      println("size: " + resp.totalHits)
      resp.hits.hits.toList.foreach(println)

    }

  } catch {
    case e: Exception => e.printStackTrace
  }
  connection.close

  client.close()

}

case class Product(productName: String, categoryName: String, productPrice: BigDecimal){
  def apply(productName: String, categoryName: String, productPrice: BigDecimal): Product = new Product(productName, categoryName, productPrice)
}