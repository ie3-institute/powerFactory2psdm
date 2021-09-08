/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.util.SchemaGenerator.run
import edu.ie3.util.io.FileIOUtils
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.reflect.runtime.universe
import scala.util.{Try, Using}

class SchemaGeneratorIT
    extends Matchers
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  // create test dir
  protected val testTmpDir: String = System.getProperty(
    "user.dir"
  ) + File.separator + "test" + File.separator + "tmp_" + this.getClass.getSimpleName

  override protected def beforeAll(): Unit = new File(testTmpDir).mkdirs()

  override protected def afterAll(): Unit =
    FileIOUtils.deleteRecursively(testTmpDir)

  "A SchemaGenerator" should {

    "generate the schema from a test file correctly" in {

      val source =
        Source.fromFile(
          s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"
        )
      val jsonString =
        try source.mkString
        finally source.close

      val outputFile = new File(
        s"$testTmpDir${File.separator}PowerFactoryGrid.scala"
      )

      SchemaGenerator
        .run(
          jsonString,
          "PowerFactoryGrid",
          "edu.ie3.powerFactory2psdm.model.powerfactory"
        )
        .foreach(formatedClassString => {
          val pw = new PrintWriter(
            outputFile
          )
          pw.write(formatedClassString)
          pw.close()
        })
      assert(outputFile.exists())
    }
  }
}
