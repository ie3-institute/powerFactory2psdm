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
        val x = json
          .foldWith(ClassFolder(className, `package`))
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

  private type RawFieldType = String
  private type RawFieldName = String
  private type Field = String
  private type CollectionStack = Int
  private type Fields =
    Iterable[(RawFieldType, RawFieldName, CollectionStack, Field)]

  final case class ComplexClass(
      name: String,
      fields: Fields,
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
          name,
          Vector(
            (
              defaultOnNullType,
              name,
              collectionStack,
              simpleString(name, defaultOnNullType, collectionStack)
            )
          )
        )
      )

    override def onBoolean(value: Boolean): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            (
              "Boolean",
              name,
              collectionStack,
              simpleString(name, "Boolean", collectionStack)
            )
          )
        )
      )

    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            (
              "Double",
              name,
              collectionStack,
              simpleString(name, "Double", collectionStack)
            )
          )
        )
      )

    override def onString(value: String): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            (
              "String",
              name,
              collectionStack,
              simpleString(name, "String", collectionStack)
            )
          )
        )
      )

    override def onArray(value: Vector[Json]): Vector[ComplexClass] = {
      val u = value
        .flatMap(
          _.foldWith(
            this.copy(isRoot = false, collectionStack = collectionStack + 1)
          )
        ) match {
        case array
            if array.isEmpty => // if empty default to collection with default type
          Vector(
            ComplexClass(
              name,
              Vector(
                (
                  defaultOnNullType,
                  name,
                  collectionStack + 1,
                  simpleString(name, defaultOnNullType, collectionStack + 1)
                )
              )
            )
          )
        case nonEmptyArray =>
          nonEmptyArray.distinct // keep only uniques
      }
      u
    }
    //        .getOrElse(
    //          Vector(
    //            ComplexClass(
    //              Vector(simpleString(name, "String", collectionStack + 1))
    //            )
    //          )
    //        )

    override def onObject(value: JsonObject): Vector[ComplexClass] = {

      val fieldsOrClasses1 = value.toMap
        .map {
          case (objName, jsonObjs) =>
            (objName, jsonObjs.asArray match {
              case Some(objArr) =>
                // filter multiple json objects
                objArr
                  .flatMap(
                    _.foldWith(
                      this.copy(
                        name = objName,
                        isRoot = false,
                        collectionStack = 1,
                        isObj = true
                      )
                    )
                  )

              //                match {
              //                  case nonEmpty => nonEmpty // todo JH double check
              //                  case array if array.isEmpty => Vector.empty
              //                }
              //                  .getOrElse(
              //                    Vector.empty
              //                  )
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

      val fieldsOrClasses = fieldsOrClasses1.flatMap {
        case (className, cplxClasses) if isRoot && cplxClasses.isEmpty =>
          // empty case class @ root level
          Vector(
            ComplexClass(
              name,
              Vector.empty,
              Vector(SimpleClass(className, Vector.empty))
            )
          )
        case (className, cplxClasses) if isRoot && cplxClasses.nonEmpty =>
          // case class @ root level
          val reducedClasses = collapseClasses(
            cplxClasses,
            defaultOnNullType,
            collectionStack,
            isObj
          )

          val u = reducedClasses
            .map(
              cplxClass =>
                ComplexClass(
                  name,
                  Vector.empty,
                  cplxClasses.flatMap(_.classes) ++ Vector(
                    SimpleClass(className, cplxClass.fields.map(_._4)) // todo JH adapt + collapse field types
                  )
                )
            )
          u
        case (cName, cplxClasses)
            //          if cplxClasses.size == 1 &&
            if cplxClasses.head.isObj &&
              !isRoot =>
          // complex nested case class
          val field =
            simpleString(cName, className(cName), cplxClasses.head.cStack)
          Vector(
            ComplexClass(
              name,
              Vector((className(cName), cName, 0, field)),
              cplxClasses.flatMap(_.classes) :+ SimpleClass(
                cName,
                cplxClasses.head.fields
                  .map(_._4) // todo JH adapt in order to double check complex classes with default type
              )
            )
          )
        case (_, cplxClasses) =>
          // if not root level and not an object, map the field vals
          Vector(
            ComplexClass(
              name,
              collapseSameFieldTypes(
                cplxClasses.flatMap(_.fields),
                defaultOnNullType
              ), // todo replace map with collapse field types
              cplxClasses.flatMap(_.classes)
            )
          )
      }

      Vector(
        ComplexClass(
          name,
          fieldsOrClasses
            .flatMap(_.fields)
            .filterNot(_._4.isBlank) // todo jh adapt
            .filterNot(_._4.isEmpty), // todo jh adapt
          fieldsOrClasses.flatMap(_.classes),
          collectionStack,
          isObj
        )
      )
    }
  }

  private def collapseSameFieldTypes(
      fields: Fields,
      defaultOnNullType: String
  ): Fields =
    // check if field types contains a non default value
    // if any use this one, of not, keep string
    fields.filterNot(_._1.equals(defaultOnNullType)).toSet match {
      case noneDefaultType if noneDefaultType.size == 1 =>
        noneDefaultType.headOption
      case noneDefaultType if noneDefaultType.size > 1 =>
        throw new IllegalArgumentException(
          s"More than one field type identified: ${noneDefaultType.mkString(",")}"
        )
      case empty if empty.isEmpty =>
        fields
    }

  private def collapseClasses(
      classes: Seq[ComplexClass],
      defaultOnNullType: String,
      collectionStack: Int,
      isObj: Boolean
  ) = {

    // todo collapse classes + fields

    val x = classes.distinct.groupBy(_.name).flatMap {
      case (_, distClasses)
          if distClasses.size == 1 => // all classes are equal, return only one of them
        distClasses
      case (name, distClasses) => // multiple classes with same name but maybe different fields, try to merge them ...
        // merge and collapse fields
        val y = distClasses
          .flatMap(_.fields)
          .groupMap(x => (x._2, x._3))(_._1)
        val allFields = distClasses
          .flatMap(_.fields)
          .groupMap(x => (x._2, x._3))(_._1)
          .flatMap {
            case ((name, colStack), types) =>
              types.distinct.filterNot(_.equals(defaultOnNullType)) match {
                case noneDefaultType if noneDefaultType.size == 1 =>
                  noneDefaultType.headOption.map(
                    fieldType =>
                      (
                        fieldType,
                        name,
                        colStack,
                        simpleString(name, fieldType, colStack)
                      )
                  )
//              case noneDefaultType if name.equals(name) => // fields @ ma
                case noneDefaultType if noneDefaultType.size > 1 =>
                  throw new IllegalArgumentException(
                    s"More than one field type identified: ${noneDefaultType.mkString(",")}"
                  )
                case empty
                    if empty.isEmpty => // if default type is given we end up here, as we filtered all default types
                  Some(
                    defaultOnNullType,
                    name,
                    colStack,
                    simpleString(name, defaultOnNullType, colStack)
                  )
              }
          }
        Iterable(
          ComplexClass(
            name,
            allFields,
            distClasses.flatMap(_.classes),
            collectionStack,
            isObj
          )
        )
    }

    //    classes.distinct match {
    //      case distClasses if distClasses.size == 1 => // all classes are equal, return only one of them
    //        distClasses
    //      case distClasses => // multiple classes with same name but maybe different fields, try to merge them ...
    //        val s = distClasses
    //
    //
    //        val fields = distClasses.flatMap(_.fields)
    //          .groupMap(x => (x._2, x._3))(_._1).flatMap {
    //          case ((name, colStack), types) =>
    //            types.distinct.filterNot(_.equals(defaultOnNullType)) match {
    //              case noneDefaultType if noneDefaultType.size == 1 =>
    //                noneDefaultType.headOption.map(fieldType => (fieldType, name, colStack, simpleString(name, fieldType, colStack)))
    //              case noneDefaultType if noneDefaultType.size > 1 =>
    //                throw new IllegalArgumentException(s"More than one field type identified: ${noneDefaultType.mkString(",")}")
    //              case empty if empty.isEmpty => // if default type is given we end up here, as we filtered all default types
    //                Some(defaultOnNullType, name, colStack, simpleString(name, defaultOnNullType, colStack))
    //            }
    //        }
    //
    //        val k = ""
    //
    //    }

    val s = ""

    x
  }

}
