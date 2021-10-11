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
    pqsPrefix: ConversionPrefix,
    lengthPrefix: ConversionPrefix
) {
  def loadPQSPrefix(): ConversionPrefix = pqsPrefix
  def lineLengthPrefix(): ConversionPrefix = lengthPrefix
}

object ConversionPrefixes {
  final case class ConversionPrefix(value: Double)

  def apply(
      pqsPrefSymbol: String,
      lengthPrefSymbol: String
  ): ConversionPrefixes = {
    new ConversionPrefixes(
      getMetricPrefixBySymbol(pqsPrefSymbol),
      getMetricPrefixBySymbol(lengthPrefSymbol)
    )
  }

  private def getMetricPrefixBySymbol(symbol: String): ConversionPrefix =
    symbol match {
      case "a" => ConversionPrefix(1e-18)
      case "f" => ConversionPrefix(1e-15)
      case "p" => ConversionPrefix(1e-12)
      case "n" => ConversionPrefix(1e-9)
      case "u" => ConversionPrefix(1e-6)
      case "m" => ConversionPrefix(1e-3)
      case ""  => ConversionPrefix(1)
      case "k" => ConversionPrefix(1e3)
      case "M" => ConversionPrefix(1e6)
      case "G" => ConversionPrefix(1e9)
      case "T" => ConversionPrefix(1e12)
      case "P" => ConversionPrefix(1e15)
      case "E" => ConversionPrefix(1e18)
    }

}
