/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editors.map.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;

import org.orbisgis.Services;
import org.orbisgis.editors.map.tool.DrawingException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.outputManager.OutputManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class MesurePolygonTool extends AbstractPolygonTool {

	protected void polygonDone(Polygon g, MapContext vc, ToolManager tm)
			throws TransitionException {
		OutputManager om = (OutputManager) Services
				.getService(OutputManager.class);
		Color color = Color.blue;

		om.append("Area : " + getArea(g) + " Perimeter : " + getPerimeter(g)
				+ "\n", color);
		om.makeVisible();

	}

	private String getPerimeter(Geometry g) {
		return new DecimalFormat("0.000").format(g.getLength());
	}

	private String getArea(Geometry g) {
		return new DecimalFormat("0.000").format(g.getArea());
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return vc.getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	@Override
	public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		super.drawIn_Point(g, vc, tm);
		Geometry geom = getCurrentPolygon(tm);
		tm.addTextToDraw("Area: " + getArea(geom));
		tm.addTextToDraw("Perimeter: " + getPerimeter(geom));
	}
}