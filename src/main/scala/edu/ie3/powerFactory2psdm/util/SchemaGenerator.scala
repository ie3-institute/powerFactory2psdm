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
  def main(args: Array[String]): Unit = {
    val source =
      Source.fromFile(
        s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"
      )
    val jsonString = try source.mkString
    finally source.close
    run(jsonString)
  }

  def run(jsonString: String): Unit = {
    run(
      jsonString,
      "PowerFactoryGrid",
      "edu.ie3.powerFactory2psdm.model.powerfactory"
    ).foreach(formatedClassString => {
      val pw = new PrintWriter(
        new File(
          s"${new File(".").getCanonicalPath}/src/main/scala/edu/ie3/powerFactory2psdm/model/powerfactory/PowerFactoryGrid.scala"
        )
      )
      pw.write(formatedClassString)
      pw.close()
    })
  }

  def run(
      jsonString: String,
      className: String,
      `package`: String
  ): Option[String] =
    parse(jsonString) match {
      case Left(error) =>
        logger.error(
          s"Exception during json file parsing: '${error.getMessage()}'"
        )
        None
      case Right(json) =>
        generateClass(
          json,
          className,
          `package`
        ).map(Formatter.format(_, None))
    }

  private def generateClass(
      json: Json,
      className: String,
      `package`: String
  ): Option[String] = {

    json.asObject match {
      case Some(jsonObject) if !jsonObject.isEmpty =>
        val classes: Iterable[SimpleClass] =
          json
            .foldWith(ClassFolder(className, `package`))
            .flatMap(_.classes)
            .groupBy(_.name)
            .flatMap {
              case (name, elems) if elems.size > 1 =>
                // duplicated class name, merge the fields and provide default None for all of them
                Vector(SimpleClass(name, elems.flatMap(_.fields).distinct))
              case (_, elems) => elems
            }

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
             |
             |    ${classes.map(_.toString).mkString("")}
             | }
             |""".stripMargin

        Some(packageStatement + importStatement + wrapperClass + wrapperObj)
      case _ =>
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
      fields: Iterable[String],
      classes: Iterable[SimpleClass] = Vector.empty,
      cStack: Int = 0,
      isObj: Boolean = false
  )

  final case class SimpleClass(
      name: String,
      fields: Iterable[String]
  ) {

    private def caseClassString(name: String, fields: String) =
      s"""
         |final case class ${className(name)} ($fields)
         |""".stripMargin

    override def toString: String = caseClassString(name, fields.mkString(","))
  }

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
        ComplexClass(
          Vector(simpleString(name, defaultOnNullType, collectionStack))
        )
      )

    override def onBoolean(value: Boolean): Vector[ComplexClass] =
      Vector(
        ComplexClass(Vector(simpleString(name, "Boolean", collectionStack)))
      )

    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
      Vector(
        ComplexClass(Vector(simpleString(name, "Double", collectionStack)))
      )

    override def onString(value: String): Vector[ComplexClass] =
      Vector(
        ComplexClass(Vector(simpleString(name, "String", collectionStack)))
      )

    override def onArray(value: Vector[Json]): Vector[ComplexClass] =
      value.headOption
        .map(
          _.foldWith(
            this.copy(isRoot = false, collectionStack = collectionStack + 1)
          )
        )
        .getOrElse(
          Vector(
            ComplexClass(
              Vector(simpleString(name, "String", collectionStack + 1))
            )
          )
        )

    override def onObject(value: JsonObject): Vector[ComplexClass] = {

      val fieldsOrClasses: Iterable[ComplexClass] = value.toMap
        .map {
          case (objName, jsonObjs) =>
            (objName, jsonObjs.asArray match {
              case Some(objArr) =>
                // filter multiple json objects
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
            Vector(
              ComplexClass(
                Vector(""),
                Vector(SimpleClass(className, Vector.empty))
              )
            )
          case (className, cplxClasses) if isRoot && cplxClasses.nonEmpty =>
            // case class @ root level
            cplxClasses
              .map(
                cplxClass =>
                  ComplexClass(
                    Vector(""),
                    cplxClasses.flatMap(_.classes) ++ Vector(
                      SimpleClass(className, cplxClass.fields)
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
            Vector(
              ComplexClass(
                Vector(field),
                cplxClasses.flatMap(_.classes) :+ SimpleClass(
                  cName,
                  cplxClasses.head.fields
                )
              )
            )
          case (_, cplxClasses) =>
            // if not root level and not an object, map the field vals
            Vector(
              ComplexClass(
                cplxClasses.flatMap(_.fields),
                cplxClasses.flatMap(_.classes)
              )
            )
        }

      Vector(
        ComplexClass(
          fieldsOrClasses
            .flatMap(_.fields)
            .filterNot(_.isBlank)
            .filterNot(_.isEmpty),
          fieldsOrClasses.flatMap(_.classes),
          collectionStack,
          isObj
        )
      )
    }
  }

}
