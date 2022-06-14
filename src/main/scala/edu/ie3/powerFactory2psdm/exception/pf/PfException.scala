/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.exception.pf

/** Base class for grouping power factory related exceptions
  */
class PfException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends Exception(msg, cause)
