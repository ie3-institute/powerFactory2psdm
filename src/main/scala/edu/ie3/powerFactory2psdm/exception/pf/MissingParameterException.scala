/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.exception.pf

final case class MissingParameterException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends PfException(msg, cause)
