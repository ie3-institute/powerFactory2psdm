/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.util.quantities.QuantityUtils.RichQuantityDouble
import io.circe._
import io.circe.parser._
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Power
import scala.io.Source
import scala.util.{Failure, Success, Using}

final case class ParticipantInformation(
    power: ComparableQuantity[Power],
    count: Int
)

object ParticipantInformation {

  object Participants extends Enumeration {
    val LOAD: Participants.Value = Value("load")
    val PV: Participants.Value = Value("pv")
    val EVCS: Participants.Value = Value("evcs")
    val STORAGE: Participants.Value = Value("storage")
    val WEC: Participants.Value = Value("wec")
    val HP: Participants.Value = Value("hp")
    val NSH: Participants.Value = Value("nsh")
    val DIRECT_HEATING: Participants.Value = Value("direct_heating")
    val WATER_HEATER: Participants.Value = Value("water_heater")
    val CHP: Participants.Value = Value("chp")
    val BM: Participants.Value = Value("bm")
    val MISC: Participants.Value = Value("misc")
  }

  implicit val participantInformationDecoder: Decoder[ParticipantInformation] =
    cursor =>
      for {
        power <- cursor.get[Double]("power")
        count <- cursor.get[Int]("count")
      } yield ParticipantInformation(power.asKiloWatt, count)

  implicit val participantInformationMapDecoder
      : Decoder[Map[Participants.Value, ParticipantInformation]] = cursor => {
    val map = Participants.values.toSeq
      .flatMap(participant => {
        cursor.get[ParticipantInformation](participant.toString) match {
          case Left(_)     => None
          case Right(info) => Some(participant -> info)
        }
      })
      .toMap
    Right(map)
  }

  implicit val nodalParticipantInformationDecoder: Decoder[
    Map[String, Map[Participants.Value, ParticipantInformation]]
  ] = cursor => {
    cursor.keys match {
      case Some(keys) => {
        val (lefts, rights) = keys.foldLeft(
          Seq.empty[(String, DecodingFailure)],
          Seq.empty[(String, Map[Participants.Value, ParticipantInformation])]
        )((lr, key) => {
          val (left, right) = lr
          val res =
            cursor.get[Map[Participants.Value, ParticipantInformation]](key)
          res match {
            case Left(decodingFailure) =>
              (left :+ (key, decodingFailure), right)
            case Right(participantInfoMap) =>
              (left, right :+ (key, participantInfoMap))
          }
        })
        if (lefts.isEmpty) {
          Right(rights.toMap)
        } else {
          val history = lefts.flatMap { case (_, failure) => failure.history }
          val keys = lefts.map(_._1).mkString("-")
          Left(
            DecodingFailure(
              s"Could not retrieve information for keys: $keys",
              history.toList
            )
          )
        }
      }
      case None => Right(Map.empty)
    }
  }

  def fromJson(
      filePath: String
  ): Map[String, Map[Participants.Value, ParticipantInformation]] = {
    Using(Source.fromFile(filePath)) { src =>
      src.getLines().reduce((a, b) => a + " " + b)
    } match {
      case Failure(exception) => throw exception
      case Success(jsonString) =>
        parse(jsonString) match {
          case Left(failure) => throw failure
          case Right(json) =>
            json.as[
              Map[String, Map[Participants.Value, ParticipantInformation]]
            ] match {
              case Left(decodingFailure: DecodingFailure) =>
                throw decodingFailure
              case Right(result) => result
            }
        }
    }
  }
}
