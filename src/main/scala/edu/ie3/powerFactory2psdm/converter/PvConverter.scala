package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator
import tech.units.indriya.ComparableQuantity

import java.util.UUID
import javax.measure.quantity.{Angle, Dimensionless, Power}

object PvConverter {


  def convert(input: StaticGenerator, node: NodeInput): PvInput = {

    /*
    - BA Sören Kaapke
    - Wertebereich doku?
    - OpenstreetMap
     */

    // reactive power characteristic to follow
    // todo look into technische Anschlussrichtlinien
    val qCharacteristics: ReactivePowerCharacteristic = ???

    /*
     - albedo of the plants surrounding -> how much radiation does the plant get from the reflection of its surroundings
     Albedo
     - measure of the diffuse reflection of solar radiation out of the total solar radiation
     - on a scale from 0 = black body that absorbs all incident radiation, to 1 = body that reflects all incident radiation.
     - incident radiation
     */
    val albedo: Double = ???
    // - inclination in compass direction South = 0°, West = 90°, East = -90°
    val azimuth: ComparableQuantity[Angle] = ???
    // - efficiency of the assets inverter
    val etaConv: ComparableQuantity[Dimensionless] = ???
    // - elevation angle
    val height: ComparableQuantity[Angle] = ???
    // Generator correction factor merging technical influences
    val kG: Double = ???
    // Temperature correction factor merging thermal influences
    val kT: Double = ???
    val sRated: ComparableQuantity[Power] = ???
    val cosPhiRated: Double = ???

    new PvInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristics,
      albedo,
      azimuth,
      etaConv,
      height,
      kG,
      kT,
      false, // market reaction
      sRated,
      cosPhiRated
    )
  }


  // todo: implement Conversion with .ElmPvSys

}
