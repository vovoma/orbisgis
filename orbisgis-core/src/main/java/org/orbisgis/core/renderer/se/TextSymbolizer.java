package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextSymbolizerType;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class TextSymbolizer extends VectorSymbolizer {

	public TextSymbolizer() {
		super();
		this.name = "Label";
		setLabel(new PointLabel());
		uom = Uom.MM;
	}

	public void setLabel(Label label) {
		label.setParent(this);
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}

	public RealParameter getPerpendicularOffset() {
		return perpendicularOffset;
	}

	public void setPerpendicularOffset(RealParameter perpendicularOffset) {
		this.perpendicularOffset = perpendicularOffset;
		if (this.perpendicularOffset != null){
			this.perpendicularOffset.setContext(RealParameterContext.realContext);
		}
	}

	@Override
	public void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
		ArrayList<Shape> shapes = this.getShape(feat, mt);

		if (shapes != null) {
			for (Shape shp : shapes) {
				if (shp != null && label != null) {
					label.draw(g2, shp, feat, selected, mt);
				}
			}
		}
	}

	@Override
	public JAXBElement<TextSymbolizerType> getJAXBElement() {

		ObjectFactory of = new ObjectFactory();
		TextSymbolizerType s = of.createTextSymbolizerType();

		this.setJAXBProperty(s);


		if (this.getUom() != null){
			s.setUnitOfMeasure(this.getUom().toURN());
		}

		if (transform != null) {
			s.setTransform(transform.getJAXBType());
		}


		if (perpendicularOffset != null) {
			s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
		}

		if (label != null) {
			s.setLabel(label.getJAXBElement());
		}

		if (this.level >= 0){
			s.setLevel(this.level);
		}

		return of.createTextSymbolizer(s);
	}

	public TextSymbolizer(JAXBElement<TextSymbolizerType> st) {
		super(st);
		TextSymbolizerType tst = st.getValue();


		if (tst.getGeometry() != null) {
			// TODO
		}

		if (tst.getLevel() != null){
			this.setLevel(tst.getLevel());
		}

		if (tst.getUnitOfMeasure() != null) {
			this.uom = Uom.fromOgcURN(tst.getUnitOfMeasure());
		}

		if (tst.getPerpendicularOffset() != null) {
			this.setPerpendicularOffset(SeParameterFactory.createRealParameter(tst.getPerpendicularOffset()));
		}

		if (tst.getTransform() != null) {
			this.setTransform(new Transform(tst.getTransform()));
		}

		if (tst.getLabel() != null) {
			this.setLabel(Label.createLabelFromJAXBElement(tst.getLabel()));
		}
	}
	private RealParameter perpendicularOffset;
	private Label label;
}
