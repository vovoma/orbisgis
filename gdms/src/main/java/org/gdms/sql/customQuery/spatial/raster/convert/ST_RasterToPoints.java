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
package org.gdms.sql.customQuery.spatial.raster.convert;

import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ST_RasterToPoints implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, ProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			sds.open();
			if (1 == values.length) {
				// if no raster's field's name is provided, the default (first)
				// one is arbitrarily chosen.
				sds.setDefaultGeometry(values[0].toString());
			}

			final GenericObjectDriver driver = new GenericObjectDriver(
					getMetadata(null));

			final long rowCount = sds.getRowCount();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final GeoRaster geoRasterSrc = sds.getRaster(rowIndex);
				final ImageProcessor processor = geoRasterSrc.getImagePlus()
						.getProcessor();
				final float ndv = (float) geoRasterSrc.getNoDataValue();

				for (int y = 0, i = 0; y < geoRasterSrc.getHeight(); y++) {

					if (y / 100 == y / 100.0) {
						if (pm.isCancelled()) {
							break;
						} else {
							pm
									.progressTo((int) (100 * y * rowIndex / (geoRasterSrc
											.getHeight() * rowCount)));
						}
					}

					for (int x = 0; x < geoRasterSrc.getWidth(); x++) {
						final float height = processor.getPixelValue(x, y);
						if (height != ndv) {
							final Point2D point2D = geoRasterSrc
									.fromPixelToRealWorld(x, y);
							final Geometry point = geometryFactory
									.createPoint(new Coordinate(point2D.getX(),
											point2D.getY(), height));
							driver.addValues(new Value[] {
									ValueFactory.createValue(i),
									ValueFactory.createValue(point),
									ValueFactory.createValue(height) });
						}
						i++;
					}
				}
			}
			sds.close();
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Transform a Raster into a set of points based on the pixel centroid";
	}

	public String getName() {
		return "ST_RasterToPoints";
	}

	public String getSqlOrder() {
		return "select ST_RasterToPoints([raster]) from mytif;";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "gid",
				"the_geom", "value" });
	}

	public TableDefinition[] getTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.RASTER };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER),
				new Arguments() };
	}
}