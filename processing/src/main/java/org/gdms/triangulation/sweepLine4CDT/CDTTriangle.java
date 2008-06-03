package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

public class CDTTriangle {
	private static Logger logger = Logger
			.getLogger(CDTTriangle.class.getName());

	private final static GeometryFactory gf = new GeometryFactory();

	private PSLG pslg;
	private Polygon pTriangle;
	private CDTCircumCircle circumCircle;
	private CDTVertex p0;
	private CDTVertex p1;
	private CDTVertex p2;
	private SortedSet<LineSegment> listOfConstrainingEdges;

	public CDTTriangle(final CDTVertex v0, final CDTVertex v1,
			final CDTVertex v2, final PSLG pslg) {
		this.pslg = pslg;

		// normalization process
		CDTVertex[] tmp = new CDTVertex[] { v0, v1, v2 };
		Arrays.sort(tmp);
		p0 = tmp[0];
		p1 = tmp[1];
		p2 = tmp[2];

		pTriangle = gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				p0.getCoordinate(), p1.getCoordinate(), p2.getCoordinate(),
				p0.getCoordinate() }), null);
		circumCircle = new CDTCircumCircle(p0.getCoordinate(), p1
				.getCoordinate(), p2.getCoordinate());

		listOfConstrainingEdges = p0.getEdges();
		listOfConstrainingEdges.addAll(p1.getEdges());
		listOfConstrainingEdges.addAll(p2.getEdges());
	}

	/**
	 * This method is known also as a Lawson's local optimization process. If
	 * the empty circle property is violated, the common edge of the two
	 * triangles are swapped.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean legalization() {
		final List<CDTTriangle> sublistOftriangles = pslg
				.getTrianglesSpatialIndex().query(
						circumCircle.getEnvelopeInternal());
		for (CDTTriangle cdtTriangle : sublistOftriangles) {
			if (!this.equals(cdtTriangle)) {
				CDTVertex[] tmp = shareACommonEdge(cdtTriangle);
				if (null != tmp) {
					CDTVertex oppositeVertex = tmp[3];
					// if
					// (!respectWeakerDelaunayProperty(oppositeVertex.getCoordinate()))
					// {
					if (!respectDelaunayProperty(oppositeVertex.getCoordinate())) {
						// swap the common edge of the two triangles
						pslg.addTriangle(new CDTTriangle(tmp[0], tmp[2],
								tmp[3], pslg));
						pslg.addTriangle(new CDTTriangle(tmp[1], tmp[2],
								tmp[3], pslg));
						pslg.removeTriangle(cdtTriangle);
						pslg.removeTriangle(this);
						// and stop the legalization process
						return true;
					}
				}
			}
		}
		return false;
	}

	// /**
	// * This method is known also as a Lawson's local optimization process. If
	// * the empty circle property is violated, the common edge of the two
	// * triangles are swapped.
	// *
	// * @param v
	// */
	// public void legalization(final CDTVertex v) {
	// if (!respectWeakerDelaunayProperty(v.getCoordinate())) {
	// if (pointsAreLocatedOnEachSidesOfTheAxis(p0.getCoordinate(), p1
	// .getCoordinate(), p2.getCoordinate(), v.getCoordinate())) {
	// // common edge [p0, p1] must be replaced with [p2, v]
	// pslg.addTriangle(new CDTTriangle(p0, p2, v, pslg));
	// pslg.addTriangle(new CDTTriangle(p1, p2, v, pslg));
	// } else if (pointsAreLocatedOnEachSidesOfTheAxis(p1.getCoordinate(),
	// p2.getCoordinate(), p0.getCoordinate(), v.getCoordinate())) {
	// // common edge [p1, p2] must be replaced with [p0, v]
	// pslg.addTriangle(new CDTTriangle(p0, p1, v, pslg));
	// pslg.addTriangle(new CDTTriangle(p0, p2, v, pslg));
	// } else if (pointsAreLocatedOnEachSidesOfTheAxis(p2.getCoordinate(),
	// p0.getCoordinate(), p1.getCoordinate(), v.getCoordinate())) {
	// // common edge [p0, p2] must be replaced with [p1, v]
	// pslg.addTriangle(new CDTTriangle(p1, p0, v, pslg));
	// pslg.addTriangle(new CDTTriangle(p1, p2, v, pslg));
	// } else {
	// throw new RuntimeException("Unreachable code");
	// }
	// pslg.removeTriangle(this);
	// }
	// }

	/**
	 * This method tests the classical "empty circumcircle rule". That is,
	 * current triangle is Delaunay if the unique circle on which lie its three
	 * vertices (ie the circumcircle) does not contain any other vertex.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean respectDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = pslg
				.getVerticesSpatialIndex().query(
						circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (!respectDelaunayProperty(v.getCoordinate())) {
				return false;
			}
		}
		return true;
	}

	public boolean respectDelaunayProperty(Coordinate v) {
		if (circumCircle.contains(v)) {
			if (pTriangle.contains(gf.createPoint(v))) {
				throw new RuntimeException("Unreachable code");
			}

			logger.info("point " + v
					+ " disturbs Delaunay property for triangle [ "
					+ p0.getCoordinate() + ", " + p1.getCoordinate() + ", "
					+ p2.getCoordinate() + " ]");
			return false;
		}
		return true;
	}

	/**
	 * This method is an implementation of the weaker Delaunay property
	 * described in the "Sweep-line algorithm for constrained Delaunay
	 * triangulation" article (V Domiter and B Zalik, p. 450). If a vertex
	 * violates Delaunay property for a triangle (ie if it lies within its
	 * circumcircle), it MUST be hidden by (behind) a constraining edge to
	 * respect a weaker Delaunay property. Thus this vertex is not visible from
	 * the triangle.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean respectWeakerDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = pslg
				.getVerticesSpatialIndex().query(
						circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (!respectWeakerDelaunayProperty(v.getCoordinate())) {
				return false;
			}
		}
		return true;
	}

	protected boolean respectWeakerDelaunayProperty(Coordinate v) {
		if (!respectDelaunayProperty(v)) {
			if (!newVertexIsHiddenByAConstrainingEdge(v)) {
				logger
						.info("point "
								+ v
								+ " disturbs _Weaker_ Delaunay property for triangle [ "
								+ p0.getCoordinate() + ", "
								+ p1.getCoordinate() + ", "
								+ p2.getCoordinate() + " ]");
				return false;
			}
		}
		return true;
	}

	protected boolean newVertexIsHiddenByAConstrainingEdge(Coordinate v) {
		if (pointsAreLocatedOnEachSidesOfTheAxis(p0.getCoordinate(), p1
				.getCoordinate(), p2.getCoordinate(), v)) {
			// what still remains is to test if [p0, p1] is a constraining edge
			LineSegment ls = new LineSegment(p0.getCoordinate(), p1
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		} else if (pointsAreLocatedOnEachSidesOfTheAxis(p1.getCoordinate(), p2
				.getCoordinate(), p0.getCoordinate(), v)) {
			// what still remains is to test if [p1, p2] is a constraining edge
			LineSegment ls = new LineSegment(p1.getCoordinate(), p2
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		} else if (pointsAreLocatedOnEachSidesOfTheAxis(p2.getCoordinate(), p0
				.getCoordinate(), p1.getCoordinate(), v)) {
			// what still remains is to test if [p2, p0] is a constraining edge
			LineSegment ls = new LineSegment(p2.getCoordinate(), p0
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		}
		throw new RuntimeException("Unreachable code");
	}

	/**
	 * This method tests if points c2 and c are located on each [c0,c1] axis
	 * side or not.
	 * 
	 * @param c0
	 * @param c1
	 * @param c2
	 * @param c
	 */
	protected static boolean pointsAreLocatedOnEachSidesOfTheAxis(
			Coordinate c0, Coordinate c1, Coordinate c2, Coordinate c) {
		// scalarProduct(normal(c0,c1),c2) * scalarProduct(normal(c0,c1),c)
		Coordinate normal = new Coordinate(-c1.y + c0.y, c1.x - c0.x);
		double tmp = (normal.x * (c2.x - c0.x) + normal.y * (c2.y - c0.y))
				* (normal.x * (c.x - c0.x) + normal.y * (c.y - c0.y));
		if (0 == tmp) {
			throw new RuntimeException("Unreachable code");
		}
		return (tmp < 0) ? true : false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p0 == null) ? 0 : p0.hashCode());
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CDTTriangle other = (CDTTriangle) obj;
		if (p0 == null) {
			if (other.p0 != null)
				return false;
		} else if (!p0.equals(other.p0))
			return false;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}

	public Polygon getPolygon() {
		return gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				p0.getCoordinate(), p1.getCoordinate(), p2.getCoordinate(),
				p0.getCoordinate() }), null);
	}

	@Override
	public String toString() {
		return getPolygon().toString();
	}

	public boolean isAVertex(final CDTVertex cdtVertex) {
		if (p0.equals(cdtVertex) || p1.equals(cdtVertex)
				|| p2.equals(cdtVertex)) {
			return true;
		}
		return false;
	}

	/**
	 * This methods returns an array of 4 CDTVertex. The two 1st elements
	 * correspond to the common edge, the 3rd one corresponds to the opposite
	 * vertex in the current triangle, and the 4th one corresponds to the
	 * opposite vertex in the cdtTriangle parameter.
	 * 
	 * If there is no common edge, null is returned.
	 * 
	 * @param cdtTriangle
	 * @return
	 */
	public CDTVertex[] shareACommonEdge(final CDTTriangle cdtTriangle) {
		if (cdtTriangle.isAVertex(p0)) {
			if (cdtTriangle.isAVertex(p1)) {
				return new CDTVertex[] { p0, p1, p2,
						cdtTriangle.getLastVertex(p0, p1) };
			} else if (cdtTriangle.isAVertex(p2)) {
				return new CDTVertex[] { p0, p2, p1,
						cdtTriangle.getLastVertex(p0, p2) };
			}
		} else if (cdtTriangle.isAVertex(p1)) {
			if (cdtTriangle.isAVertex(p2)) {
				return new CDTVertex[] { p1, p2, p0,
						cdtTriangle.getLastVertex(p1, p2) };
			}
		}
		return null;
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	protected CDTVertex getLastVertex(CDTVertex a, CDTVertex b) {
		if (p0.equals(a)) {
			if (p1.equals(b)) {
				return p2;
			} else if (p2.equals(b)) {
				return p1;
			}
		} else if (p0.equals(b)) {
			if (p1.equals(a)) {
				return p2;
			} else if (p2.equals(a)) {
				return p1;
			}
		} else {
			return p0;
		}
		throw new RuntimeException("Unreachable code");
	}

	public Envelope getEnvelope() {
		return getPolygon().getEnvelopeInternal();
	}

	public CDTCircumCircle getCircumCircle() {
		return circumCircle;
	}
}