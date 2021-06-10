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
        s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"
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

  private def className(name: String) =
    StringUtils.snakeCaseToCamelCase(StringUtils.cleanString(name)).capitalize

  final case class FieldMeta(
      rawFieldType: String,
      rawFieldName: String,
      collectionStack: Int
  ) {

    def fieldString: String =
      simpleString(rawFieldName, rawFieldType, collectionStack)

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
  }

  final case class ComplexClass(
      name: String,
      fields: Iterable[FieldMeta],
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
            FieldMeta(defaultOnNullType, name, collectionStack)
          )
        )
      )

    override def onBoolean(value: Boolean): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            FieldMeta("Boolean", name, collectionStack)
          )
        )
      )

    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            FieldMeta("Double", name, collectionStack)
          )
        )
      )

    override def onString(value: String): Vector[ComplexClass] =
      Vector(
        ComplexClass(
          name,
          Vector(
            FieldMeta("String", name, collectionStack)
          )
        )
      )

    override def onArray(value: Vector[Json]): Vector[ComplexClass] =
      value
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
                FieldMeta(defaultOnNullType, name, collectionStack + 1)
              )
            )
          )
        case nonEmptyArray =>
          nonEmptyArray.distinct // keep only uniques
      }

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
          collapseClasses(
            cplxClasses,
            defaultOnNullType,
            collectionStack,
            isObj
          ).map(
            cplxClass =>
              ComplexClass(
                name,
                Vector.empty,
                cplxClasses.flatMap(_.classes) ++ Vector(
                  SimpleClass(className, cplxClass.fields.map(_.fieldString))
                )
              )
          )

        case (cName, cplxClasses)
            if cplxClasses.nonEmpty &&
              cplxClasses.head.isObj &&
              !isRoot =>
          // complex nested case class
          Vector(
            ComplexClass(
              name,
              Vector(
                FieldMeta(className(cName), cName, cplxClasses.head.cStack)
              ),
              cplxClasses.flatMap(_.classes) :+ SimpleClass(
                cName,
                cplxClasses.head.fields.map(_.fieldString)
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
              ),
              cplxClasses.flatMap(_.classes)
            )
          )
      }

      Vector(
        ComplexClass(
          name,
          fieldsOrClasses
            .flatMap(_.fields)
            .filterNot(_.fieldString.isBlank)
            .filterNot(_.fieldString.isEmpty),
          fieldsOrClasses.flatMap(_.classes),
          collectionStack,
          isObj
        )
      )
    }
  }

  private def collapseSameFieldTypes(
      fields: Iterable[FieldMeta],
      defaultOnNullType: String
  ): Iterable[FieldMeta] = {
    // check if field types contains a non default value
    // if any use this one, of not, keep string
    fields.filterNot(_.rawFieldType.equals(defaultOnNullType)).toSet match {
      case noneDefaultType if noneDefaultType.size == 1 =>
        noneDefaultType.headOption
      case noneDefaultType if noneDefaultType.size > 1 =>
        throw new IllegalArgumentException(
          s"More than one field type identified: ${noneDefaultType.mkString(",")}"
        )
      case empty if empty.isEmpty =>
        // we just filtered the defaultOnNullType and the vector is empty
        // => field is the same as the defaultType and we can just return
        fields
    }
  }

  private def collapseClasses(
      classes: Seq[ComplexClass],
      defaultOnNullType: String,
      collectionStack: Int,
      isObj: Boolean
  ): Iterable[ComplexClass] = classes.distinct.groupBy(_.name).flatMap {
    case (_, distClasses)
        if distClasses.size == 1 => // all classes are equal, return only one of them
      distClasses
    case (name, distClasses) => // multiple classes with same name but maybe different fields, try to merge them ...
      // merge and collapse fields
      val allFields = distClasses
        .flatMap(_.fields)
        .groupMap(
          fieldMeta => (fieldMeta.rawFieldName, fieldMeta.collectionStack)
        )(_.rawFieldType)
        .flatMap {
          case ((name, colStack), types) =>
            types.distinct.filterNot(_.equals(defaultOnNullType)) match {
              case noneDefaultType if noneDefaultType.size == 1 =>
                noneDefaultType.headOption.map(
                  fieldType =>
                    FieldMeta(
                      fieldType,
                      name,
                      colStack
                    )
                )
              case noneDefaultType if noneDefaultType.size > 1 =>
                throw new IllegalArgumentException(
                  s"More than one field type identified: ${noneDefaultType.mkString(",")}"
                )
              case empty
                  if empty.isEmpty => // if default type is given we end up here, as we filtered all default types
                Some(
                  FieldMeta(
                    defaultOnNullType,
                    name,
                    colStack
                  )
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

}
