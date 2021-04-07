/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import java.io.{File, PrintWriter}

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.util.StringUtils
import io.circe.Json.Folder
import io.circe.{Json, JsonNumber, JsonObject}
import io.circe.parser._

import scala.io.Source

object SchemaGenerator extends LazyLogging {

  sealed trait Schema

  private final case class SchemaObj(objName: String, fields: Set[SchemaField])
      extends Schema

  private final case class SchemaField(name: String, `type`: String)
      extends Schema

  def main(args: Array[String]): Unit = {
    val source =
      Source.fromFile(
        s"${new File(".").getCanonicalPath}/pf2json/nestedTest.json"
      )
    val jsonString = try source.mkString
    finally source.close
    run(jsonString)
  }

  def run(jsonString: String): Unit = {
    parse(jsonString) match {
      case Left(error) =>
        logger.error(
          s"Exception during json file parsing: '${error.getMessage()}'"
        )
      case Right(json) =>
        generateClass(
          json,
          "PowerFactoryGrid",
          "edu.ie3.powerFactory2psdm.model.powerfactory"
        ).map(Formatter.format(_, None))
          .foreach(formatedClassString => {
            val pw = new PrintWriter(
              new File(
                s"${new File(".").getCanonicalPath}/src/main/scala/edu/ie3/powerFactory2psdm/model/powerfactory/PowerFactoryGrid.scala"
              )
            )
            pw.write(formatedClassString)
            pw.close()
          })

    }

  }

  private def generateClass(
      json: Json,
      className: String,
      `package`: String
  ): Option[String] = {

    json.asObject match {
      case Some(jsonObject) =>
        val classes: String = json.foldWith(ClassFolder(className, `package`))
        val wrapperClass =
          s"""
             | final case class ${this.className(className)}(
             |  ${jsonObject.toMap.keys
               .map(key => s"$key: Option[List[${this.className(key)}]]")
               .mkString(",\n")}
             | )
             |""".stripMargin

        val importStatement =
          s"""
             |  import ${`package`}.${this.className(className)}.{
             |    ${jsonObject.toMap.keys
               .map(key => this.className(key))
               .mkString(",\n")}
             |  }
             |""".stripMargin

        val packageStatement =
          s"package ${`package`}"

        val wrapperObj =
          s"""
             | object ${this.className(className)}{
             |    $classes
             | }
             |""".stripMargin

        Some(packageStatement + importStatement + wrapperClass + wrapperObj)
      case None =>
        None
    }

  }

  private def simpleString(
      name: String,
      `type`: String,
      collection: Option[String]
  ): String =
    collection
      .map(col => s"$name: Option[$col[Option[${`type`}]]]")
      .getOrElse(s"$name: Option[${`type`}]")

  private def className(name: String) =
    StringUtils.snakeCaseToCamelCase(StringUtils.cleanString(name)).capitalize

  final case class ClassFolder(
      name: String,
      `package`: String,
      isRoot: Boolean = true,
      collection: Option[String] = None,
      defaultOnNullType: String = "String"
  ) extends Folder[String] {

    override def onNull: String =
      simpleString(name, defaultOnNullType, collection)

    override def onBoolean(value: Boolean): String =
      simpleString(name, "Boolean", collection)

    override def onNumber(value: JsonNumber): String = {
      simpleString(name, "Double", collection)
    }

    override def onString(value: String): String =
      simpleString(name, "String", collection)

    override def onArray(value: Vector[Json]): String = {
      // if value is empty and type cannot be determined, return empty list of strings
      if (value.isEmpty)
        return s"$name: Option[List[String]]"

      // if only one value is available and it is not an object & not an array,
      // return the array string with the type of this value
      if (value.size == 1 && !value.exists(_.isObject) && !value.exists(
            _.isArray
          ))
        return value.head.foldWith(
          this.copy(isRoot = false, collection = Some("List"))
        )

      // if this is a nested array, return an empty string
      if (value.exists(_.isArray)) {
        logger.warn(s"Ignoring invalid nested field '$name'.")
        ""
      } else {
        value.map(_.foldWith(this.copy(isRoot = false))).mkString(",\n")
      }
    }

    override def onObject(value: JsonObject): String = {

      def caseClassString(name: String, fields: String) =
        s"""
           |final case class ${className(name)} ($fields)
           |""".stripMargin

      val fieldsOrClasses = value.toMap
        .map {
          case (objName, jsonObjs) =>
            (objName, jsonObjs.asArray match {
              case Some(objArr) if objArr.size > 1 && isRoot =>
                // filter multi objects only on root level
                objArr.head.foldWith(this.copy(name = objName, isRoot = false))
              case Some(objArr) if objArr.isEmpty && isRoot =>
                // return empty case classes directly
                ""
              case _ =>
                jsonObjs.foldWith(this.copy(name = objName, isRoot = false))
            })
        }
        .map {
          case (className, classFields) if isRoot =>
            // if root level, build case class
            caseClassString(className, classFields)
          case (_, fieldNameWithType) =>
            // if not root level, map the vals
            fieldNameWithType
        }

      // if root we create a case class, if not, we create a field string
      if (isRoot) {
        fieldsOrClasses.mkString("\n")


      } else {
        // one row per field, if a split leads to multiple elements,
        // we have nested collections and this is not supported!
        // the nested fields can be safely discarded as they are ignored if a json string is read in
        fieldsOrClasses
          .filterNot(_.isBlank)
          .filterNot(_.isEmpty)
          .flatMap(fieldString => {
            val fieldStrings = fieldString.split("\n")
            if (fieldStrings.length > 1) {
              // create seq element
              val fieldName = fieldStrings.last.split(":")(0).trim
              val fieldType = fieldStrings.last.split(":")(1).trim
              Some(s"$fieldName:Option[List[$fieldType]]")
            } else {
              Some(fieldString)
            }
          })
          .mkString(",\n")
      }
    }
  }

}
