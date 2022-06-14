/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.exception.pf

final case class MissingGridElementException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends PfException(msg, cause)
