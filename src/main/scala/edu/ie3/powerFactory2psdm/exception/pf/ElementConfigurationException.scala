/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.exception.pf

case class ElementConfigurationException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends PfException(msg, cause)
