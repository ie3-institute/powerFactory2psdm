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
        s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/simpleSampleGrid.json"
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
        val classes: Vector[ComplexClass] =
          json.foldWith(ClassFolder(className, `package`))
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
             |    ${classes.map(_.fields).mkString("\n")}
             |
             |    ${classes.flatMap(_.classes).mkString("\n")}
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
      collectionStack: Int
  ): String =
    if (collectionStack == 0) {
      s"$name: Option[${`type`}]"
    } else {
      s"$name:" + (0 until collectionStack)
        .foldLeft("Option[")((cur, _) => cur + s"List[Option[") + `type` + "]" * (collectionStack * 2 + 1)
    }

  private def className(name: String) =
    StringUtils.snakeCaseToCamelCase(StringUtils.cleanString(name)).capitalize

  final case class ComplexClass(
      fields: String,
      classes: Iterable[String] = Vector.empty,
      cStack: Int = 0,
      isObj: Boolean = false
  )

  final case class ClassFolder(
      name: String,
      `package`: String,
      isRoot: Boolean = true,
      collectionStack: Int = 0,
      isObj: Boolean = false,
      defaultOnNullType: String = "String"
  ) extends Folder[Vector[ComplexClass]] {

    override def onNull: Vector[ComplexClass] =
      Vector(
        ComplexClass(simpleString(name, defaultOnNullType, collectionStack))
      )

    override def onBoolean(value: Boolean): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "Boolean", collectionStack)))

    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "Double", collectionStack)))

    override def onString(value: String): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "String", collectionStack)))

    override def onArray(value: Vector[Json]): Vector[ComplexClass] =
      value.headOption
        .map(
          _.foldWith(
            this.copy(isRoot = false, collectionStack = collectionStack + 1)
          )
        )
        .getOrElse(
          Vector(
            ComplexClass(simpleString(name, "String", collectionStack + 1))
          )
        )

    override def onObject(value: JsonObject): Vector[ComplexClass] = {

      def caseClassString(name: String, fields: String) =
        s"""
           |final case class ${className(name)} ($fields)
           |""".stripMargin

      val fieldsOrClasses: Iterable[ComplexClass] = value.toMap
        .map {
          case (objName, jsonObjs) =>
            (objName, jsonObjs.asArray match {
              case Some(objArr) =>
                // filter multiple objects
                objArr.headOption
                  .map(
                    _.foldWith(
                      this.copy(
                        name = objName,
                        isRoot = false,
                        collectionStack = 1,
                        isObj = true
                      )
                    )
                  )
                  .getOrElse(
                    Vector.empty
                  )
              case _ =>
                jsonObjs.foldWith(
                  this.copy(
                    name = objName,
                    isRoot = false,
                    collectionStack = 0,
                    isObj = true
                  )
                )
            })
        }
        .flatMap {
          case (className, cplxClasses) if isRoot && cplxClasses.isEmpty =>
            // empty case class @ root level
            Vector(ComplexClass("", Vector(caseClassString(className, ""))))
          case (className, cplxClasses) if isRoot && cplxClasses.nonEmpty =>
            // case class @ root level
            cplxClasses
              .map(
                cplxClass =>
                  ComplexClass(
                    "",
                    cplxClasses.flatMap(_.classes) ++ Vector(
                      caseClassString(className, cplxClass.fields)
                    )
                  )
              )
          case (cName, cplxClasses)
              if cplxClasses.size == 1 &&
                cplxClasses.head.isObj &&
                !isRoot =>
            // complex nested case class
            val field =
              simpleString(cName, className(cName), cplxClasses.head.cStack)
            val cClassString = caseClassString(cName, cplxClasses.head.fields)
            Vector(
              ComplexClass(
                field,
                cplxClasses.flatMap(_.classes) ++ Vector(cClassString)
              )
            )
          case (_, cplxClasses) =>
            // if not root level and not an object, map the field vals
            Vector(
              ComplexClass(
                cplxClasses.map(_.fields).mkString("\n"),
                cplxClasses.flatMap(_.classes)
              )
            )
        }

      Vector(
        ComplexClass(
          fieldsOrClasses
            .map(_.fields)
            .filterNot(_.isBlank)
            .filterNot(_.isEmpty)
            .mkString(",\n"),
          fieldsOrClasses.flatMap(_.classes),
          collectionStack,
          isObj
        )
      )
    }
  }

}
