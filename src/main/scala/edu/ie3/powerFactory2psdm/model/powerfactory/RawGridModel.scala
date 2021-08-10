/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  ExtGrid,
  PowerPlants,
  Trafos3w,
  Lines,
  Pvs,
  Switches,
  TrafoTypes3w,
  LoadsLV,
  LoadsMV,
  Nodes,
  Trafos2w,
  StatGen,
  Loads,
  ProjectSettings,
  LineTypes,
  TrafoTypes2w
}

final case class RawGridModel(
    trafos2w: Option[List[Trafos2w]],
    loadsMV: Option[List[LoadsMV]],
    nodes: Option[List[Nodes]],
    projectSettings: Option[List[ProjectSettings]],
    powerPlants: Option[List[PowerPlants]],
    trafoTypes3w: Option[List[TrafoTypes3w]],
    pvs: Option[List[Pvs]],
    lineTypes: Option[List[LineTypes]],
    switches: Option[List[Switches]],
    loadsLV: Option[List[LoadsLV]],
    statGen: Option[List[StatGen]],
    loads: Option[List[Loads]],
    trafos3w: Option[List[Trafos3w]],
    extGrid: Option[List[ExtGrid]],
    trafoTypes2w: Option[List[TrafoTypes2w]],
    lines: Option[List[Lines]]
)

object RawGridModel {

  final case class Switches(
      id: Option[String],
      bus1Id: Option[String],
      bus2Id: Option[String]
  )

  final case class Pvs()

  final case class ConElms(id: Option[String], pfCls: Option[String])

  final case class Loads(id: Option[String])

  final case class LineTypes(
      bline: Option[Double],
      gline: Option[Double],
      id: Option[String],
      sline: Option[Double],
      uline: Option[Double],
      xline: Option[Double],
      rline: Option[Double]
  )

  final case class StatGen(id: Option[String])

  final case class Lines(
      id: Option[String],
      bus1Id: Option[String],
      bus2Id: Option[String]
  )

  final case class PowerPlants(id: Option[String])

  final case class Trafos3w()

  final case class ExtGrid(id: Option[String])

  final case class TrafoTypes2w(
      nntap0: Option[Double],
      pfe: Option[Double],
      uktr: Option[Double],
      id: Option[String],
      ntpmn: Option[Double],
      dutap: Option[Double],
      strn: Option[Double],
      utrn_l: Option[Double],
      curmg: Option[Double],
      tap_side: Option[Double],
      ntpmx: Option[Double],
      pcutr: Option[Double],
      phitr: Option[Double],
      utrn_h: Option[Double]
  )

  final case class ProjectSettings(
      unitSystem: Option[Double],
      prefixPQS: Option[String],
      prefixLength: Option[String]
  )

  final case class LoadsLV(id: Option[String])

  final case class Nodes(
      vtarget: Option[Double],
      conElms: Option[List[Option[ConElms]]],
      GPSlat: Option[Double],
      id: Option[String],
      GPSlon: Option[Double],
      uknom: Option[Double]
  )

  final case class Trafos2w(
      id: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class TrafoTypes3w()

  final case class LoadsMV(id: Option[String])

}
