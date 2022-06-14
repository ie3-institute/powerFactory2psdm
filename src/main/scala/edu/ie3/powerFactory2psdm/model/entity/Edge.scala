/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.model.entity

/** Denotes an element that connects two nodes within a Subnet
  */
trait Edge {
  val nodeAId: String
  val nodeBId: String
}
