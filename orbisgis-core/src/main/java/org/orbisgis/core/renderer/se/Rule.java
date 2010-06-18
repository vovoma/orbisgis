/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.RuleType;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public final class Rule implements SymbolizerNode {

	private static final String DEFAULT_NAME = "Default Rule";

	public Rule(){
		symbolizer = new CompositeSymbolizer();
		symbolizer.setParent(this);
	}

	public Rule(ILayer layer) {
		this();

		this.name = "Default Rule";

		Geometry geometry = null;
		try {
			geometry = layer.getDataSource().getGeometry(0);
		} catch (DriverException ex) {
		}

		Symbolizer symb;

		if (geometry != null) {
			switch (geometry.getDimension()) {
				case 1:
					symb = new LineSymbolizer();
					break;
				case 2:
					symb = new AreaSymbolizer();
					break;
				case 0:
				default:
					symb = new PointSymbolizer();
					break;
			}
		} else {
			symb = new PointSymbolizer();
		}

		symbolizer.addSymbolizer(symb);
	}

	public Rule(RuleType rt, ILayer layer) {
		this(layer);

		if (rt.getName() != null){
			this.name = rt.getName();
		}
		else{
			this.name = Rule.DEFAULT_NAME;
		}

		/*
		 * Is a fallback rule ?
		 */
		if (rt.getElseFilter() != null) {
			this.fallbackRule = true;
		} else {
			this.fallbackRule = false;
			//this.filter = new FilterOperator(rt.getFilter());
		}

		if (rt.getMinScaleDenominator() != null) {
			this.setMinScaleDenom(rt.getMinScaleDenominator());
		}

		if (rt.getMaxScaleDenominator() != null) {
			this.setMaxScaleDenom(rt.getMaxScaleDenominator());
		}

		if (rt.getSymbolizer() != null) {
			this.setCompositeSymbolizer(new CompositeSymbolizer(rt.getSymbolizer()));
		}
	}

	public void setCompositeSymbolizer(CompositeSymbolizer cs) {
		this.symbolizer = cs;
		cs.setParent(this);
	}

	public CompositeSymbolizer getCompositeSymbolizer() {
		return symbolizer;
	}

	public RuleType getJAXBType() {
		RuleType rt = new RuleType();

		if (!this.name.equals(Rule.DEFAULT_NAME)){
			rt.setName(this.name);
		}

		if (this.minScaleDenom > 0) {
			rt.setMinScaleDenominator(minScaleDenom);
		}

		if (this.maxScaleDenom > 0) {
			rt.setMaxScaleDenominator(maxScaleDenom);
		}

		rt.setSymbolizer(this.symbolizer.getJAXBElement());

		return rt;
	}
	private CompositeSymbolizer symbolizer;

	@Override
	public Uom getUom() {
		return null;
	}

	@Override
	public SymbolizerNode getParent() {
		return fts;
	}

	@Override
	public void setParent(SymbolizerNode fts) {
		this.fts = fts;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	/**
	 * Return a Spatial data source, according to rule filter and specified extent
	 * @return
	 * @throws DriverLoadException
	 * @throws DataSourceCreationException
	 * @throws DriverException
	 * @throws ParseException
	 * @throws SemanticException
	 */
	public SpatialDataSourceDecorator getFilteredDataSource(SpatialDataSourceDecorator sds) throws DriverLoadException, DataSourceCreationException, DriverException, ParseException, SemanticException {
		String query = "select * from " + sds.getName();

		if (where != null && !where.isEmpty()) {
			query += " WHERE " + where;
		}

		System.out.println(" here is the where: " + where);
		return new SpatialDataSourceDecorator(sds.getDataSourceFactory().getDataSourceFromSQL(query));
	}


	/**
	 * Return a Spatial data source, according to rule filter and specified extent
	 * @return
	 * @throws DriverLoadException
	 * @throws DataSourceCreationException
	 * @throws DriverException
	 * @throws ParseException
	 * @throws SemanticException
	 */
	public SpatialDataSourceDecorator getFilteredDataSource() throws DriverLoadException, DataSourceCreationException, DriverException, ParseException, SemanticException {
		FeatureTypeStyle ft = (FeatureTypeStyle) fts;

		ILayer layer = ft.getLayer();
		SpatialDataSourceDecorator sds = layer.getDataSource();
		return this.getFilteredDataSource(sds);
	}

	public boolean isFallbackRule() {
		return fallbackRule;
	}

	public void setFallbackRule(boolean fallbackRule) {
		this.fallbackRule = fallbackRule;
	}

	public double getMaxScaleDenom() {
		return maxScaleDenom;
	}

	public void setMaxScaleDenom(double maxScaleDenom) {
		this.maxScaleDenom = maxScaleDenom;
	}

	public double getMinScaleDenom() {
		return minScaleDenom;
	}

	public void setMinScaleDenom(double minScaleDenom) {
		this.minScaleDenom = minScaleDenom;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}



	public boolean isDomainAllowed(MapTransform mt) {
		double scale = mt.getScaleDenominator();
		System.out.println("Current scale is  1:" + scale);
		System.out.println("Min : " + this.minScaleDenom);
		System.out.println("Max : " + this.maxScaleDenom);

		return (this.minScaleDenom < 0 && this.maxScaleDenom < 0)
				|| (this.minScaleDenom < 0 && this.maxScaleDenom > scale)
				|| (scale > this.minScaleDenom && this.maxScaleDenom < 0)
				|| (scale > this.minScaleDenom && this.maxScaleDenom > scale);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	private String name = "";
	private String description = "";

	private boolean visible = true;
	private SymbolizerNode fts;
	private String where;
	private boolean fallbackRule = false;
	private double minScaleDenom = -1;
	private double maxScaleDenom = -1;
}
