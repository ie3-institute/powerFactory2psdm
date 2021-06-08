/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

/**
  * Denotes an element that connects two nodes within a Subnet
  */
trait Edge {
  val nodeAId: String
  val nodeBId: String
}
