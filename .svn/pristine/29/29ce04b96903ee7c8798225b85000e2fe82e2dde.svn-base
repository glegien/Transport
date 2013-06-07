package pl.edu.agh.cs.kraksim.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.ministat.LinkMiniStatExt;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.real.RealSimulationParams;

public class CityMapVisualisator {
	private class VisualLinkStat {
		public String stat;
		public Color color = Color.WHITE;

		public VisualLinkStat(Link link) {
			getStatisticForLink(link);
		}

		private void getStatisticForLink(final Link link) {
			LinkMiniStatExt linkStat = statView.ext(link);
			float avgVelocity = linkStat.getAvgVelocity();
			int avgVelKph = (int) RealSimulationParams
					.convertToKPH(avgVelocity);
			int carCount = linkStat.getCarCount();

			stat = String.format("%2d:%2d", avgVelKph, carCount);
			int len = link.getLength();
			double level = (double) carCount / (double) (len * link.getMainLanes().size());
			if (level > 0.6) {
				color = Color.RED;
			} else if (level > 0.3) {
				color = Color.ORANGE;
			}
		}
	}

	// nizej sie nie zejdzie bo jednostka sa punkty 1pkt =1/72 cala a 0.0
	// oznacza rysuj najcieniej jak potrafisz

	public final static float MARGIN = 30.0f;
	public final static float LINE_THICKNESS = 3.0f;
	public final static float NODE_THICKNESS = 18.0f * 1.5f;
	public final static float VEHICLE_THICKNESS = 2.0f * 2;
	public final static float LANE_WIDTH = 3.0f;

	public final static Color LINE_COLOR = Color.LIGHT_GRAY;
	public final static Color SELECTED_LINE_COLOR = Color.CYAN; // NOPMD by Bartosz Rybacki on 7/16/07 9:53 PM
	public final static Color GATEWAY_COLOR = Color.GREEN;
	public final static Color INTERSECTION_COLOR = Color.BLUE; // NOPMD by Bartosz Rybacki on 7/16/07 9:53 PM
	public final static Color DESCRIPTION_COLOR = Color.WHITE;//Color.WHITE;
	public static final Color BACKGROUND_COLOR = Color.GRAY;//Color.GRAY;

	int multipplier = 2;
	public final static BasicStroke LANE_STROKE = new BasicStroke(
			LINE_THICKNESS);
	/*
	 *new BasicStroke( LINE_THICKNESS,
	 * BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
	 * 10.0f, new float[] { 1 }, 0.0f);
	 */

	public final static BasicStroke MAIN_LANE_STROKE = new BasicStroke(
			LINE_THICKNESS);

	/**
	 * do rysowania po mapie miasta
	 */
	private final Graphics2D g2d;
	private final City city;
	private final BufferedImage cityMap;
	private final BlockIView blockView;
	private final MiniStatEView statView;

	public CityMapVisualisator(City city, BlockIView blockView,
			MiniStatEView statView, int width, int height) {
		cityMap = new BufferedImage((int) (width + NODE_THICKNESS + MARGIN)
				* multipplier, (int) (height + NODE_THICKNESS + MARGIN)
				* multipplier, BufferedImage.TYPE_INT_RGB);
		g2d = (Graphics2D) cityMap.getGraphics();
		this.city = city;
		this.blockView = blockView;
		this.statView = statView;
	}

	public BufferedImage getCityMap() {
		g2d.setColor(BACKGROUND_COLOR);
		g2d.fillRect(0, 0, cityMap.getWidth() * multipplier, cityMap
				.getHeight()
				* multipplier);

		// rysowanie wezlow
		for (Iterator<Gateway> it = city.gatewayIterator(); it.hasNext();) {
			final Gateway gateway = it.next();
			g2d.setColor(GATEWAY_COLOR);

			Rectangle2D rectangle = new Rectangle2D.Double(gateway.getPoint()
					.getX()
					* multipplier - NODE_THICKNESS / 2.0, gateway.getPoint()
					.getY()
					* multipplier - NODE_THICKNESS / 2.0, NODE_THICKNESS,
					NODE_THICKNESS);
			g2d.fill(rectangle);
			drawNames(gateway.getPoint(), new Point2D.Double(NODE_THICKNESS,
					NODE_THICKNESS), gateway.getId(), DESCRIPTION_COLOR);
		}

		// rysowanie wezlow
		for (Iterator<Intersection> it = city.intersectionIterator(); it
				.hasNext();) {
			final Intersection its = it.next();
			g2d.setColor(INTERSECTION_COLOR);

			Rectangle2D rectangle = new Rectangle2D.Double(its.getPoint()
					.getX()
					* multipplier - NODE_THICKNESS / 2.0, its.getPoint().getY()
					* multipplier - NODE_THICKNESS / 2.0, NODE_THICKNESS,
					NODE_THICKNESS);
			g2d.fill(rectangle);
			drawNames(its.getPoint(), new Point2D.Double(NODE_THICKNESS,
					NODE_THICKNESS), its.getId(), DESCRIPTION_COLOR);
		}

		// rysowanie drog
		for (Iterator<Link> it = city.linkIterator(); it.hasNext();) {
			Link link = it.next();
			if (link == null) {
				continue;
			}
			drawRoad(link, LINE_COLOR);
		}

		return cityMap;
	}

	private void drawRoad(final Link link, final Color color) {

		double[] vectorOrtogonal;
		// wektor jednostkowy wyznaczajacy kierunek i zwrot ze start do end
		double[] vectorAB;
		double[] vectorPair;

		// if ( link == null ) return;
		Point2D start = link.getBeginning().getPoint();
		Point2D end = link.getEnd().getPoint();

		vectorPair = GeometryHelper.computeVectors(start, end);
		vectorAB = new double[] { vectorPair[0], vectorPair[1] };
		vectorOrtogonal = new double[] { -vectorPair[1], vectorPair[0] };

		float offset = CityMapVisualisator.NODE_THICKNESS + 2.0f;

		double xStart = start.getX() * multipplier + vectorAB[0]
				* (offset / 2.0);
		double yStart = start.getY() * multipplier + vectorAB[1]
				* (offset / 2.0);

		double xEnd = end.getX() * multipplier - vectorAB[0] * (offset / 2.0);
		double yEnd = end.getY() * multipplier - vectorAB[1] * (offset / 2.0);

		start = new Point2D.Double(xStart, yStart);
		end = new Point2D.Double(xEnd, yEnd);

		VisualLinkStat linkStat = new VisualLinkStat(link);
		/*
		 * double celluarWidth = (start.distance(end) 
		 * CityMapVisualisator.NODE_THICKNESS) / link.getLength();
		 */
		double celluarWidth = start.distance(end) / link.getLength();

		// odleglosc krawedzi pasa od osi jezdni
		float laneAxisOffset = LANE_WIDTH;

		// [START] Rysowanie lewych pasow
		// dlugosc pasa glownego w jednostkach z Cityu
		final int mainLanelength = link.getLength();

		for (int j = link.leftLaneCount() - 1; j >= 0; j--) {
			Lane lane = link.getLeftLane(j);
			int num = mainLanelength - lane.getLength();
			//link.getLeftLane(j)
			/*
			 * drawLane(start, end, laneRoadAxisOffset, vectorOrtogonal,
			 * laneStartOffset, vectorAB, LANE_STROKE);
			 */

			drawLane(start, end, laneAxisOffset, vectorOrtogonal, num,
					vectorAB, celluarWidth, LANE_STROKE, color, blockView.ext(
							lane).isBlocked());

			laneAxisOffset += LANE_WIDTH;
		}
		// [END]

		// Rysowanie pasow glownych
		for (int j = 0; j < link.mainLaneCount() ; j++) {
			Lane lane = link.getMainLane(j);

			drawLane(start, end, laneAxisOffset, vectorOrtogonal, 0, vectorAB,
					celluarWidth, LANE_STROKE, color, blockView.ext(lane)
							.isBlocked());

			laneAxisOffset += LANE_WIDTH;
		}

		// [START] Rysowanie pasow prawych
		for (int j = link.rightLaneCount() - 1; j >= 0; j--) {
			Lane lane = link.getRightLane(j);
			int num = mainLanelength - lane.getLength();
			drawLane(start, end, laneAxisOffset, vectorOrtogonal, num,
					vectorAB, celluarWidth, LANE_STROKE, color, blockView.ext(
							lane).isBlocked());
			/*
			 * drawLane(start, end, laneRoadAxisOffset, vectorOrtogonal,
			 * laneStartOffset, vectorAB, LANE_STROKE);
			 */
			laneAxisOffset += LANE_WIDTH;
		}

		laneAxisOffset += 4 * LANE_WIDTH;

		drawStats(linkStat.stat, start, end, laneAxisOffset, vectorOrtogonal,
				(link.getLength() / 2), vectorAB, celluarWidth, linkStat.color);
		// [END]
	}

	private void drawStats(String linkstat, Point2D start, Point2D end,
			float laneRoadAxisOffset, double[] vectorOrtogonal, int num,
			double[] vectorAB, double celluarWidth, Color color) {
		double x = start.getX() + laneRoadAxisOffset * 2 * vectorOrtogonal[0]
				+ num * celluarWidth * vectorAB[0];
		double y = start.getY() + laneRoadAxisOffset * 2 * vectorOrtogonal[1]
				+ num * celluarWidth * vectorAB[1];

		g2d.setColor(color);
		//    g2d.drawString( String.format( "%3.2f", avgVelocity ), (float) x - 10, (float) y + 6 );
		// TODO: check direction, and decide about offset
		g2d.drawString(linkstat, (float) x - 30, (float) y + 12);

	}

	private void drawNames(Point2D namedObject, Point2D offset, String name,
			Color color) {
		double x = namedObject.getX() * multipplier + offset.getX();
		double y = namedObject.getY() * multipplier + offset.getY();

		g2d.setColor(color);
		//g2d.setFont(g2d.getFont().deriveFont( 124 ));
		Font f = new Font("Dialog", Font.PLAIN, 24);
		g2d.setFont(f);

		//    g2d.drawString("X", 80, 80);
		g2d.drawString(name, (float) x, (float) y);
	}

	private void drawLane(Point2D start, Point2D end, float laneRoadAxisOffset,
			double[] vectorOrtogonal, int num, double[] vectorAB,
			double celluarWidth, BasicStroke stroke, Color color, boolean red) {
		double xLaneStart, yLaneStart, xLaneEnd, yLaneEnd;

		xLaneStart = start.getX() + laneRoadAxisOffset * vectorOrtogonal[0]
				+ num * celluarWidth * vectorAB[0];
		yLaneStart = start.getY() + laneRoadAxisOffset * vectorOrtogonal[1]
				+ num * celluarWidth * vectorAB[1];

		xLaneEnd = end.getX() + laneRoadAxisOffset * vectorOrtogonal[0];
		yLaneEnd = end.getY() + laneRoadAxisOffset * vectorOrtogonal[1];
		drawLane(xLaneStart, yLaneStart, xLaneEnd, yLaneEnd, stroke, color, red);

	}

	private void drawLane(double x0, double y0, double x1, double y1,
			BasicStroke stroke, Color color, boolean red) {

		Line2D line = new Line2D.Double(x0, y0, x1, y1);
		g2d.setColor(color);
		// g2d.setStroke(new BasicStroke(LINE_THICKNESS));
		g2d.setStroke(stroke);
		g2d.draw(line);

		if (red == true) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(Color.green);
		}

		Ellipse2D elipse = new Ellipse2D.Double(x1, y1, 4, 4);
		g2d.fill(elipse);
	}
}
