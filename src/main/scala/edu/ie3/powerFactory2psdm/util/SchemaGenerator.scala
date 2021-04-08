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
        s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/nestedTest.json"
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
        val classes: Vector[ComplexClass] = json.foldWith(ClassFolder(className, `package`))
        val wrapperClass =
          s"""
             | final case class ${this.className(className)}(
             |  ${
            jsonObject.toMap.keys
              .map(key => s"$key: Option[List[${this.className(key)}]]")
              .mkString(",\n")
          }
             | )
             |""".stripMargin

        val importStatement =
          s"""
             |  import ${`package`}.${this.className(className)}.{
             |    ${
            jsonObject.toMap.keys
              .map(key => this.className(key))
              .mkString(",\n")
          }
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
                            collectionStack: Vector[String]
                          ): String =

    if (collectionStack.isEmpty) {
      s"$name: Option[${`type`}]"
    } else {
      s"$name:" + collectionStack
        .foldLeft("Option[")((cur, col) =>
          cur + s"$col[Option["
        ) + `type` + "]" * (collectionStack.size * 2 + 1)
    }

  //
  //    collection
  //    .map(col => s"$name: Option[$col[Option[${`type`}]]]")
  //    .getOrElse(s"$name: Option[${`type`}]")


  private def className(name: String) =
    StringUtils.snakeCaseToCamelCase(StringUtils.cleanString(name)).capitalize

  final case class ComplexClass(fields: String, classes: Iterable[String] = Vector.empty, cStack: Iterable[String] = Vector.empty, isObj: Boolean = false)

  //  final case class ClassObj(id: String, `type`: String, nestedElem: Iterable[ClassObj])


  final case class ClassFolder(
                                name: String,
                                `package`: String,
                                isRoot: Boolean = true,
                                collectionStack: Vector[String] = Vector.empty,
                                isObj: Boolean = false,
                                defaultOnNullType: String = "String"
                              ) extends Folder[Vector[ComplexClass]] {

    override def onNull: Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, defaultOnNullType, collectionStack)))

    override def onBoolean(value: Boolean): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "Boolean", collectionStack)))

    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "Double", collectionStack)))

    override def onString(value: String): Vector[ComplexClass] =
      Vector(ComplexClass(simpleString(name, "String", collectionStack)))

    override def onArray(value: Vector[Json]): Vector[ComplexClass] = {
      // if value is empty and type cannot be determined, return empty list of strings
      //      if (value.isEmpty)
      //        return value.foldWith(this.copy(isRoot = false))

      //     return Vector(ComplexClass(simpleString(name, "String", collection)))

      // if only one value is available and it is not an object & not an array,
      // return the array string with the type of this value
      // todo maybe enable again
      //      if (value.size == 1 && !value.exists(_.isObject) && !value.exists(
      //        _.isArray
      //      ))
      //        return value.head.foldWith(
      //          this.copy(isRoot = false, collectionStack = collectionStack :+ "List")
      //        )

      // if this is a nested array, return an empty string
      //      if (value.exists(_.isArray)) {
      //        logger.warn(s"Ignoring invalid nested field '$name'.")
      //        Vector.empty
      //      } else {
      // if this is an array, we only want the first element to be processed
      value.headOption.map(_.foldWith(this.copy(isRoot = false, collectionStack = collectionStack :+ "List")))
        .getOrElse(Vector(ComplexClass(simpleString(name, "String", collectionStack :+ "List"))))
      //      }
    }

    override def onObject(value: JsonObject): Vector[ComplexClass] = {

      def caseClassString(name: String, fields: String) =
        s"""
           |final case class ${className(name)} ($fields)
           |""".stripMargin

      val k: Map[String, Vector[ComplexClass]] = value.toMap // todo check if map can be replaced by vector with CplxClass only
        .map {
          case (objName, jsonObjs) =>
            (objName, jsonObjs.asArray match {
              //              case Some(objArr) if objArr.size > 1 && isRoot =>
              //                // filter multiple objects only on root level
              //                objArr.head.foldWith(this.copy(name = objName, isRoot = false))
              //              case Some(objArr) if objArr.isEmpty && isRoot =>
              //                // return empty case classes directly
              //
              //                Vector.empty
              case Some(objArr) =>
                // filter multiple objects
                objArr.headOption
                  .map(_.foldWith(this.copy(name = objName, isRoot = false, collectionStack = Vector("List"), isObj = true)))
                  .getOrElse(
                    //                    Vector(ComplexClass("", Vector(objName)))
                    Vector.empty
                  )
              case _ =>
                jsonObjs.foldWith(this.copy(name = objName, isRoot = false, collectionStack = Vector.empty, isObj = true))
            })
        }


      val fieldsOrClasses: Map[Vector[ComplexClass], Option[String]] = k.map {
        case (className, cplxClasses) if isRoot && cplxClasses.isEmpty =>
          // empty case class @ root level
          (Vector(ComplexClass("", Vector(caseClassString(className, "")))), None)
        case (className, cplxClasses) if isRoot && cplxClasses.nonEmpty =>
          // case class @ root level
          (cplxClasses
            .map(cplxClass => ComplexClass("", cplxClasses.flatMap(_.classes) ++ Vector(caseClassString(className, cplxClass.fields)))), None)

        //          if (cplxClasses.isEmpty) {
        //            // happens if root level objects don't have fields
        //            (Vector(ComplexClass("", Vector(caseClassString(className, "")))), None)
        //          } else {
        //            // if root level, build case class
        //            (cplxClasses
        //              .map(cplxClass => ComplexClass("", cplxClasses.flatMap(_.classes) ++ Vector(caseClassString(className, cplxClass.fields)))), None)
        //          }
        // className, classFields given and NOT root level
        // fieldNameWithType a\nb
        //          case (className, classFields) if classFields.fields.split("\n").length > 1 && !isRoot =>
        //
        //            val todoMoveToMethod = ComplexClass(className.concat(s": $className"))
        //
        //            (todoMoveToMethod, Some(caseClassString(className, classFields.fields)))

        case (cName, cplxClasses) if cplxClasses.size == 1 &&
          //          cplxClasses.head.fields.split("\n").length > 1 &&
          cplxClasses.head.isObj &&
          !isRoot =>
          // complex nested case class in collection


          // todo cleanup
          val field = simpleString(cName, className(cName), cplxClasses.head.cStack.toVector)
          val cClassString = caseClassString(cName, cplxClasses.head.fields)

          (Vector(ComplexClass(field, cplxClasses.flatMap(_.classes) ++ Vector(cClassString))), None)

        case (_, cplxClasses) =>
          // if not root level and not an object, map the field vals
          (
            Vector(
              ComplexClass(cplxClasses.map(_.fields).mkString("\n"), cplxClasses.flatMap(_.classes))), None)
      }

      if (isRoot) {
        Vector(ComplexClass(fieldsOrClasses.keys.flatten.map(_.fields).mkString("\n"), fieldsOrClasses.keys.flatten.flatMap(_.classes)))
      } else {
        // process the map keys -> fields
        // one row per field, if a split leads to multiple elements,
        // we have nested collections and this is not supported!
        // the nested fields can be safely discarded as they are ignored if a json string is read in
        Vector(ComplexClass(fieldsOrClasses.keys.flatten.map(_.fields)
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
          .mkString(",\n"), fieldsOrClasses.keys.flatten.flatMap(_.classes), collectionStack, isObj))
      }
    }
  }


}


//  final case class ComplexClass(fields: String, classes: Iterable[String] = Vector.empty)
//
//
//  final case class ClassFolder(
//                                name: String,
//                                `package`: String,
//                                isRoot: Boolean = true,
//                                collectionStack: Vector[String] = Vector.empty,
//                                defaultOnNullType: String = "String"
//                              ) extends Folder[Vector[ComplexClass]] {
//
//    override def onNull: Vector[ComplexClass] =
//      Vector(ComplexClass(simpleString(name, defaultOnNullType, collectionStack)))
//
//    override def onBoolean(value: Boolean): Vector[ComplexClass] =
//      Vector(ComplexClass(simpleString(name, "Boolean", collectionStack)))
//
//    override def onNumber(value: JsonNumber): Vector[ComplexClass] =
//      Vector(ComplexClass(simpleString(name, "Double", collectionStack)))
//
//    override def onString(value: String): Vector[ComplexClass] =
//      Vector(ComplexClass(simpleString(name, "String", collectionStack)))
//
//    override def onArray(value: Vector[Json]): Vector[ComplexClass] = {
//      // if value is empty and type cannot be determined, return empty list of strings
//      //      if (value.isEmpty)
//      //        return value.foldWith(this.copy(isRoot = false))
//
//      //     return Vector(ComplexClass(simpleString(name, "String", collection)))
//
//      // if only one value is available and it is not an object & not an array,
//      // return the array string with the type of this value
//      if (value.size == 1 && !value.exists(_.isObject) && !value.exists(
//        _.isArray
//      ))
//        return value.head.foldWith(
//          this.copy(isRoot = false, collectionStack = collectionStack :+ "List")
//        )
//
//      // if this is a nested array, return an empty string
//      //      if (value.exists(_.isArray)) {
//      //        logger.warn(s"Ignoring invalid nested field '$name'.")
//      //        Vector.empty
//      //      } else {
//      // if this is an array, we only want the first element to be processed
//      value.headOption.map(_.foldWith(this.copy(isRoot = false, collectionStack = collectionStack :+ "List")))
//        .getOrElse(Vector(ComplexClass(simpleString(name, "String", collectionStack :+ "List"))))
//      //      }
//    }
//
//    override def onObject(value: JsonObject): Vector[ComplexClass] = {
//
//      def caseClassString(name: String, fields: String) =
//        s"""
//           |final case class ${className(name)} ($fields)
//           |""".stripMargin
//
//      val k: Map[String, Vector[ComplexClass]] = value.toMap // todo check if map can be replaced by vector with CplxClass only
//        .map {
//          case (objName, jsonObjs) =>
//            (objName, jsonObjs.asArray match {
//              //              case Some(objArr) if objArr.size > 1 && isRoot =>
//              //                // filter multiple objects only on root level
//              //                objArr.head.foldWith(this.copy(name = objName, isRoot = false))
//              //              case Some(objArr) if objArr.isEmpty && isRoot =>
//              //                // return empty case classes directly
//              //
//              //                Vector.empty
//              case Some(objArr) =>
//                // filter multiple objects
//                objArr.headOption
//                  .map(_.foldWith(this.copy(name = objName, isRoot = false, collectionStack = Vector("List"))))
//                  .getOrElse(
//                    //                    Vector(ComplexClass("", Vector(objName)))
//                    Vector.empty
//                  )
//              case _ =>
//                jsonObjs.foldWith(this.copy(name = objName, isRoot = false, collectionStack = Vector.empty))
//            })
//        }
//
//      // todo empty nested object
//
//      val fieldsOrClasses: Map[Vector[ComplexClass], Option[String]] = k.map {
//        case (className, cplxClasses) if isRoot && cplxClasses.isEmpty =>
//          // empty case class @ root level
//          (Vector(ComplexClass("", Vector(caseClassString(className, "")))), None)
//        case (className, cplxClasses) if isRoot && cplxClasses.nonEmpty =>
//          // case class @ root level
//          (cplxClasses
//            .map(cplxClass => ComplexClass("", cplxClasses.flatMap(_.classes) ++ Vector(caseClassString(className, cplxClass.fields)))), None)
//
////          if (cplxClasses.isEmpty) {
////            // happens if root level objects don't have fields
////            (Vector(ComplexClass("", Vector(caseClassString(className, "")))), None)
////          } else {
////            // if root level, build case class
////            (cplxClasses
////              .map(cplxClass => ComplexClass("", cplxClasses.flatMap(_.classes) ++ Vector(caseClassString(className, cplxClass.fields)))), None)
////          }
//        // className, classFields given and NOT root level
//        // fieldNameWithType a\nb
//        //          case (className, classFields) if classFields.fields.split("\n").length > 1 && !isRoot =>
//        //
//        //            val todoMoveToMethod = ComplexClass(className.concat(s": $className"))
//        //
//        //            (todoMoveToMethod, Some(caseClassString(className, classFields.fields)))
//
//        case (cName, cplxClasses) if cplxClasses.size == 1 &&
//          cplxClasses.head.fields.split("\n").length > 1 &&
//          !isRoot =>
//          // complex nested case class in collection
//
//
//          // todo cleanup
//          val field = simpleString(cName, className(cName), collectionStack)// todo this collection stack is invalid
//          val cClassString = caseClassString(cName, cplxClasses.head.fields)
//
//          (Vector(ComplexClass(field, cplxClasses.flatMap(_.classes) ++ Vector(cClassString))), None)
//
//        case (_, cplxClasses) =>
//          // if not root level, map the vals
//          (
//            Vector(
//              ComplexClass(cplxClasses.map(_.fields).mkString("\n"), cplxClasses.flatMap(_.classes))), None)
//        //            (fieldNameWithType, None)
//      }
//
//      // if root we create a case class, if not, we create a field string
//      if (isRoot) {
//        Vector(ComplexClass(fieldsOrClasses.keys.flatten.map(_.fields).mkString("\n"), fieldsOrClasses.keys.flatten.flatMap(_.classes)))
//      }
//
//      //      else if (fieldsOrClasses.keys.size > 1) {
//      //
//      ////        throw new RuntimeException
//      //
//      //        val className = name.toUpperCase()
//      //        val field = name + ":" + className
//      //
//      //        val caseClassString = "CASECLASSSTRING"
//      //
//      //        Vector(ComplexClass(field, Vector(caseClassString)))
//      //
//      //      }
//
//      else {
//        // process the map keys -> fields
//        // one row per field, if a split leads to multiple elements,
//        // we have nested collections and this is not supported!
//        // the nested fields can be safely discarded as they are ignored if a json string is read in
//        Vector(ComplexClass(fieldsOrClasses.keys.flatten.map(_.fields)
//          .filterNot(_.isBlank)
//          .filterNot(_.isEmpty)
//          .flatMap(fieldString => {
//            val fieldStrings = fieldString.split("\n")
//            if (fieldStrings.length > 1) {
//              // create seq element
//              val fieldName = fieldStrings.last.split(":")(0).trim
//              val fieldType = fieldStrings.last.split(":")(1).trim
//              Some(s"$fieldName:Option[List[$fieldType]]")
//            } else {
//              Some(fieldString)
//            }
//          })
//          .mkString(",\n"), fieldsOrClasses.keys.flatten.flatMap(_.classes)))
//      }
//    }
//  }
//
//
//}
