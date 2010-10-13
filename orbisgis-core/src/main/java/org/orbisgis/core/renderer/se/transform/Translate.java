/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TranslateType;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Translate implements Transformation {

    public Translate(RealParameter x, RealParameter y) {
        this.x = x;
        this.y = y;
    }

    Translate(TranslateType t) {
        if (t.getX() != null)
            this.x = SeParameterFactory.createRealParameter(t.getX());
        if (t.getY() != null)
            this.y = SeParameterFactory.createRealParameter(t.getY());
    }



    @Override
    public boolean allowedForGeometries() {
        return true;
    }

    @Override
    public AffineTransform getAffineTransform(Feature feat, Uom uom, MapTransform mt, Double width, Double height) throws ParameterException {
        double tx = 0.0;
        if (x != null) {
            tx = Uom.toPixel(x.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), width);
        }

        double ty = 0.0;
        if (y != null) {
            ty = Uom.toPixel(y.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), height);
        }

        return AffineTransform.getTranslateInstance(tx, ty);
    }


    @Override
    public boolean dependsOnFeature() {
        if (this.x != null && x.dependsOnFeature())
            return true;
        if (this.y != null && y.dependsOnFeature())
            return true;
        return false;
    }

    @Override
    public JAXBElement<?> getJAXBElement(){
        TranslateType t = this.getJAXBType();
        ObjectFactory of = new ObjectFactory();
        return of.createTranslate(t);
    }

    @Override
    public TranslateType getJAXBType(){
        TranslateType t = new TranslateType();

        if (x != null)
            t.setX(x.getJAXBParameterValueType());

        if (y != null)
            t.setY(y.getJAXBParameterValueType());

        return t;
    }

    private RealParameter x;
    private RealParameter y;
}
