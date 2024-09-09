import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.sql.SparkSession

class ExampleTest extends AnyFunSuite {
  test("Spark session test") {
    val spark = SparkSession.builder()
      .appName("TestApp")
      .master("local")
      .getOrCreate()

    val data = Seq(("Alice", 25), ("Bob", 30))
    val df = spark.createDataFrame(data).toDF("name", "age")

    assert(df.count() == 2)
    spark.stop()
  }
}