/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.setting

/** For the active, reactive and apparent power of loads and the length of lines
  * the unit system within power factory can be adjusted. This class holds the
  * proper prefix values for their conversion.
  *
  * @param pqsPrefix
  *   prefix value for active, reactive and apparent power of loads
  * @param lengthPrefix
  *   prefix value for length of lines
  */
class ConversionPrefixes private (
    pqsPrefix: Double,
    lengthPrefix: Double
) {
  def loadPQSPrefixValue(): Double = pqsPrefix
  def lineLengthPrefixValue(): Double = lengthPrefix
}

object ConversionPrefixes {
  def apply(
      pqsPrefSymbol: String,
      lengthPrefSymbol: String
  ): ConversionPrefixes = {
    new ConversionPrefixes(
      getMetricPrefixBySymbol(pqsPrefSymbol),
      getMetricPrefixBySymbol(lengthPrefSymbol)
    )
  }

  private def getMetricPrefixBySymbol(symbol: String): Double = symbol match {
    case "a" => 1e-18
    case "f" => 1e-15
    case "p" => 1e-12
    case "n" => 1e-9
    case "u" => 1e-6
    case "m" => 1e-3
    case ""  => 1
    case "k" => 1e3
    case "M" => 1e6
    case "G" => 1e9
    case "T" => 1e12
    case "P" => 1e15
    case "E" => 1e18
  }

}
