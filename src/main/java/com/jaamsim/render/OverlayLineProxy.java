package com.jaamsim.render;

import java.util.ArrayList;
import java.util.List;

import com.jaamsim.math.Color4d;
import com.jaamsim.math.Vec2d;

public class OverlayLineProxy implements RenderProxy {

	private final List<Vec2d> lineSegments;
	private final Color4d colour;
	private final double lineWidth;
	//private final long pickingID;
	private OverlayLine cachedLine;
	private final VisibilityInfo visInfo;
	private final boolean originTop;
	private final boolean originRight;

/***
 *
 * @param lineSegments -
 *   Pairs of points that make a line, coordinates are in positive pixels
 *   from the origin specified by the booleans below
 * @param colour
 *   Color of the line
 * @param originTop
 *   Does this use the top of the screen as the origin (default is bottom)
 * @param originRight
 *   Does this use the right of the screen as the origin (default is left)
 * @param lineWidth
 * @param visInfo
 *   View visibility info
 * @param pickingID
 *   Currently unused
 */
	public OverlayLineProxy(  List<Vec2d> lineSegments, Color4d colour,
	                          boolean originTop, boolean originRight,
	                          double lineWidth,
	                          VisibilityInfo visInfo, long pickingID) {
		this.lineSegments = lineSegments;
		this.colour = colour;
		this.lineWidth = lineWidth;
		//_pickingID = pickingID;
		this.visInfo = visInfo;
		this.originTop = originTop;
		this.originRight = originRight;
	}

	@Override
	public void collectRenderables(Renderer r, ArrayList<Renderable> outList) {
		// None
	}

	@Override
	public void collectOverlayRenderables(Renderer r, ArrayList<OverlayRenderable> outList) {

		if (lineSegments == null || lineSegments.size() < 2)
			return;

		if (cachedLine == null) {
			cachedLine = new OverlayLine(lineSegments, colour, originTop, originRight, lineWidth, visInfo);
		}
		outList.add(cachedLine);

	}

}
