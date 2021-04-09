package edu.ie3.powerFactory2psdm.model.powerfactory
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ExtGrid,
  PowerPlants,
  Loads,
  Lines,
  Pvs,
  LoadsLV,
  LoadsMV,
  Nodes,
  Trafos2w,
  StatGen,
  LineTypes,
  TrafoTypes2w
}

final case class PowerFactoryGrid(
    trafos2w: Option[List[Trafos2w]],
    loads: Option[List[Loads]],
    loadsMV: Option[List[LoadsMV]],
    nodes: Option[List[Nodes]],
    powerPlants: Option[List[PowerPlants]],
    pvs: Option[List[Pvs]],
    lineTypes: Option[List[LineTypes]],
    loadsLV: Option[List[LoadsLV]],
    statGen: Option[List[StatGen]],
    extGrid: Option[List[ExtGrid]],
    trafoTypes2w: Option[List[TrafoTypes2w]],
    lines: Option[List[Lines]]
)

object PowerFactoryGrid {

  final case class ExtGrid()

  final case class Pvs(
      q_min: Option[Double],
      Qfu_max: Option[Double],
      albedo: Option[Double],
      QtargetBase: Option[Double],
      Qmax_a: Option[Double],
      efficiencyLPconsum: Option[Double],
      GPSlat: Option[Double],
      inveff: Option[Double],
      Inom: Option[Double],
      Pmin_a: Option[Double],
      PmaxInv: Option[Double],
      Pmin_ucPU: Option[Double],
      Pmin_uc: Option[Double],
      q_max: Option[Double],
      pQPcurve: Option[String],
      efficiencyCurveGen: Option[String],
      efficiencyCurveConsum: Option[String],
      efficiencyLPgen: Option[Double],
      PminInv: Option[Double],
      RelEff_a: Option[Double],
      PmaxInvPU: Option[Double],
      Pmax_uc: Option[Double],
      Pnom: Option[Double],
      Qmin_a: Option[Double],
      Pmax_a: Option[Double],
      Pmax_ucPU: Option[Double],
      Qfu_min: Option[Double],
      GPSlon: Option[Double],
      PminInvPU: Option[Double],
      P_max: Option[Double],
      QtargetRPR: Option[Double],
      albedo_a: Option[Double]
  )

  final case class Loads(loc_name: Option[String])

  final case class TrafoTypes2w(
      root_id: Option[String],
      nntap0: Option[Double],
      tap_side2: Option[Double],
      r1pu: Option[Double],
      tap_side: Option[Double],
      dphitap2: Option[Double],
      x1pu: Option[Double],
      itapch2: Option[Double],
      ntpmn: Option[Double],
      dutap2: Option[Double],
      dutap: Option[Double],
      phitr2: Option[Double],
      utrn_l: Option[Double],
      ntpmn2: Option[Double],
      bm1: Option[Double],
      ntpmx: Option[Double],
      ntpmx2: Option[Double],
      r0pu: Option[Double],
      dphitap: Option[Double],
      gm1: Option[Double],
      itapch: Option[Double],
      phitr: Option[Double],
      utrn_h: Option[Double],
      strn: Option[Double],
      x0pu: Option[Double],
      nntap02: Option[Double]
  )

  final case class LineTypes(
      side_b: Option[Double],
      side_d: Option[Double],
      loc_name: Option[String],
      aohl__safe: Option[String],
      cohl__safe: Option[Double]
  )

  final case class StatGen()

  final case class LoadsMV(nntap: Option[Double])

  final case class Lines(
      Irated: Option[Double],
      X1: Option[Double],
      dline: Option[Double],
      nlnum: Option[Double],
      G0: Option[Double],
      R1: Option[Double],
      B1: Option[Double],
      B0: Option[Double],
      G1: Option[Double],
      X0: Option[Double],
      R0: Option[Double],
      tmat: Option[List[Option[List[Option[String]]]]],
      ciDist: Option[Double],
      i_dist: Option[Double],
      loc_name: Option[String],
      Unom: Option[Double],
      dist_a: Option[Double],
      cDisplayName: Option[String]
  )

  final case class PowerPlants()

  final case class LoadsLV()

  final case class CpZone(loc_name: Option[String])

  final case class Nodes(
      vmin: Option[Double],
      root_id: Option[String],
      vtarget: Option[Double],
      cpZone: Option[CpZone],
      vmax: Option[Double],
      AccessTime: Option[Double],
      GPSlat: Option[Double],
      GPSlon: Option[Double],
      loc_name: Option[String],
      cpSubstat: Option[String],
      Vtarget: Option[Double],
      uknom: Option[Double]
  )

  final case class Trafos2w(
      ntnum: Option[Double],
      tapctrl: Option[String],
      Vtolerance: Option[Double],
      GPSlat: Option[Double],
      Snom_a: Option[Double],
      GPSlon: Option[Double],
      nntap: Option[Double],
      optap2min: Option[Double],
      root_id: Option[String],
      nntapabs: Option[Double],
      optapmin: Option[Double],
      optap2max: Option[Double],
      Vtolerance2: Option[Double],
      re0tr_l: Option[Double],
      coldloadtab2: Option[List[Option[List[Option[String]]]]],
      c_ptapc: Option[String],
      buslvn: Option[String],
      re0tr_h: Option[Double],
      nntap2: Option[Double],
      optaplimit: Option[Double],
      optapmax: Option[Double],
      Snom: Option[Double],
      ntrcn: Option[Double],
      loc_name: Option[String],
      mTaps: Option[List[Option[List[Option[String]]]]],
      bushvn: Option[String]
  )

}
