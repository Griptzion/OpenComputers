package li.cil.oc.client.renderer.markdown.segment

import li.cil.oc.api
import li.cil.oc.client.Manual

private[markdown] class LinkSegment(parent: Segment, text: String, val url: String) extends TextSegment(parent, text) with InteractiveSegment {
  private final val normalColor = 0x66FF66
  private final val normalHoverColor = 0xAAFFAA
  private final val errorColor = 0xFF6666
  private final val errorHoverColor = 0xFFAAAA
  private final val fadeTime = 500
  private lazy val isLinkValid = (url.startsWith("http://") || url.startsWith("https://")) ||
    api.Manual.contentFor(Manual.makeRelative(url, Manual.history.top.path)) != null

  private var lastHovered = System.currentTimeMillis() - fadeTime

  override protected def color: Option[Int] = {
    val (color, hoverColor) = if (isLinkValid) (normalColor, normalHoverColor) else (errorColor, errorHoverColor)
    val timeSinceHover = (System.currentTimeMillis() - lastHovered).toInt
    if (timeSinceHover > fadeTime) Some(color)
    else Some(fadeColor(hoverColor, color, timeSinceHover / fadeTime.toFloat))
  }

  override def tooltip: Option[String] = Option(url)

  override def link: Option[String] = Option(url)

  override private[markdown] def notifyHover(): Unit = lastHovered = System.currentTimeMillis()

  private def fadeColor(c1: Int, c2: Int, t: Float): Int = {
    val (r1, g1, b1) = ((c1 >>> 16) & 0xFF, (c1 >>> 8) & 0xFF, c1 & 0xFF)
    val (r2, g2, b2) = ((c2 >>> 16) & 0xFF, (c2 >>> 8) & 0xFF, c2 & 0xFF)
    val (r, g, b) = ((r1 + (r2 - r1) * t).toInt, (g1 + (g2 - g1) * t).toInt, (b1 + (b2 - b1) * t).toInt)
    (r << 16) | (g << 8) | b
  }

  override def toString: String = s"{LinkSegment: text = $text, url = $url}"
}
