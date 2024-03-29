/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.{
  ExtGrid,
  PowerPlants,
  Trafos3w,
  LineSections,
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

final case class RawPfGridModel(
    trafos2w: Option[List[Trafos2w]],
    loadsMV: Option[List[LoadsMV]],
    nodes: Option[List[Nodes]],
    projectSettings: Option[List[ProjectSettings]],
    lineSections: Option[List[LineSections]],
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

object RawPfGridModel {

  final case class Switches(
      id: Option[String],
      onOff: Option[Double],
      bus1Id: Option[String],
      bus2Id: Option[String]
  )

  final case class ConElms(id: Option[String], pfCls: Option[String])

  final case class Loads(
      pfRecap: Option[Double],
      busId: Option[String],
      id: Option[String],
      coslini: Option[Double],
      slini: Option[Double],
      scale0: Option[Double],
      iScale: Option[Double]
  )

  final case class LineTypes(
      bline: Option[Double],
      gline: Option[Double],
      id: Option[String],
      sline: Option[Double],
      uline: Option[Double],
      xline: Option[Double],
      rline: Option[Double]
  )

  final case class StatGen(
      cCategory: Option[String],
      busId: Option[String],
      id: Option[String],
      sgini: Option[Double],
      sgn: Option[Double],
      pfRecap: Option[Double],
      cosn: Option[Double],
      cosgini: Option[Double]
  )

  final case class Lines(
      GPScoords: Option[List[Option[List[Option[Double]]]]],
      typeId: Option[String],
      dline: Option[Double],
      id: Option[String],
      bus2Id: Option[String],
      bus1Id: Option[String]
  )

  final case class PowerPlants(id: Option[String], busId: Option[String])

  final case class Trafos3w()

  final case class ExtGrid(id: Option[String], busId: Option[String])

  final case class LineSections(
      id: Option[String],
      dline: Option[Double],
      typeId: Option[String]
  )

  final case class Pvs()

  final case class TrafoTypes2w(
      utrnH: Option[Double],
      nntap0: Option[Double],
      curmg: Option[Double],
      pfe: Option[Double],
      id: Option[String],
      ntpmn: Option[Double],
      dutap: Option[Double],
      phitr: Option[Double],
      utrnL: Option[Double],
      strn: Option[Double],
      tapSide: Option[Double],
      uktr: Option[Double],
      ntpmx: Option[Double],
      pcutr: Option[Double]
  )

  final case class ProjectSettings(
      unitSystem: Option[Double],
      prefixPQS: Option[String],
      prefixLength: Option[String]
  )

  final case class LoadsLV(
      pfRecap: Option[Double],
      busId: Option[String],
      id: Option[String],
      coslini: Option[Double],
      slini: Option[Double],
      scale0: Option[Double],
      iScale: Option[Double]
  )

  final case class Nodes(
      locName: Option[String],
      vtarget: Option[Double],
      conElms: Option[List[Option[ConElms]]],
      GPSlat: Option[Double],
      id: Option[String],
      GPSlon: Option[Double],
      uknom: Option[Double]
  )

  final case class Trafos2w(
      typeId: Option[String],
      id: Option[String],
      ntrcn: Option[Double],
      cPtapc: Option[String],
      busLvId: Option[String],
      nntap: Option[Double],
      busHvId: Option[String]
  )

  final case class TrafoTypes3w()

  final case class LoadsMV(
      pfRecap: Option[Double],
      busId: Option[String],
      id: Option[String],
      coslini: Option[Double],
      slini: Option[Double],
      scale0: Option[Double],
      iScale: Option[Double]
  )

}
