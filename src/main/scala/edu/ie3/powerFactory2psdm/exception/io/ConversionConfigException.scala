/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.exception.io

final case class ConversionConfigException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends IoException(msg, cause)
