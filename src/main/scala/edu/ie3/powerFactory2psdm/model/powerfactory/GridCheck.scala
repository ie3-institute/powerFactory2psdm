/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ConElms,
  Lines,
  Nodes,
  Switches
}

class GridCheck extends LazyLogging {

  case class Invalid(reason: String)

  def isValidEdge(
      conElms: Option[List[Option[ConElms]]]
  ): Either[Invalid, true] = conElms match {
    case Some(conElms) =>
      conElms match {
        case List(Some(_), Some(_)) => Right(true)
        case _ =>
          Left(
            Invalid(
              "There are more or less than two elements connected to the edge."
            )
          )
      }
    case None => Left(Invalid("There are no elements connected to the edge."))
  }

  def checkLines(lines: Option[List[Lines]]): Option[List[Lines]] = {
    ???
  }

  def checkSwitches(
      maybeSwitches: Option[List[Switches]]
  ): Option[List[Switches]] =
    maybeSwitches match {
      case Some(switches) =>
        Option(
          switches.foldLeft(List[Switches]())(
            (acc, switch) =>
              isValidEdge(switch.conElms) match {
                case Right(_) => switch :: acc
                case Left(Invalid(reason)) => {
                  logger.warn(
                    s"Switch: ${switch.uid.getOrElse("NO_ID")} will be excluded, because: $reason"
                  )
                  acc
                }
              }
          )
        )
      case None => None
    }

  def check(pfGrid: PowerFactoryGrid) = {
    val checkedSwitches = checkSwitches(pfGrid.switches)
    pfGrid.copy(switches = checkedSwitches)
  }
}
