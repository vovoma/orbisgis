/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.PointTextGraphicType;
import net.opengis.se._2_0.core.TranslateType;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.UomNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.label.PointLabel;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 * A {@code PointTextGraphic} is used to paint a text label using a given translation. It is consequently
 * dependant on :
 * <ul><li>A x-coordinate</li>
 * <li>A y-coordinate</li>
 * <li>A {@code PointLabel}</li></ul>
 * @author Alexis Guéganno
 */
public final class PointTextGraphic extends Graphic implements UomNode {

        private Uom uom;
        private PointLabel pointLabel;
        private RealParameter x;
        private RealParameter y;

        /**
         * Build a new {@code PointTextGraphic}, at the position of its container. 
         */
        public PointTextGraphic() {
                setPointLabel(new PointLabel());
        }

        PointTextGraphic(JAXBElement<PointTextGraphicType> tge) throws InvalidStyle {
                PointTextGraphicType tgt = tge.getValue();

                if (tgt.getUom() != null) {
                        this.setUom(Uom.fromOgcURN(tgt.getUom()));
                }

                if (tgt.getPointLabel() != null) {
                        this.setPointLabel(new PointLabel(tgt.getPointLabel()));
                }

                if (tgt.getPointPosition() != null) {
                        TranslateType pp = tgt.getPointPosition();
                        if (pp.getX() != null) {
                                setX(SeParameterFactory.createRealParameter(pp.getX()));
                        }

                        if (pp.getY() != null) {
                                setY(SeParameterFactory.createRealParameter(pp.getY()));
                        }
                }
        }

        @Override
        public Uom getUom() {
                if (uom != null) {
                          return uom;
                } else if(getParent() instanceof UomNode){
                          return ((UomNode)getParent()).getUom();
                } else {
                        return Uom.PX;
                }
        }

        @Override
        public Uom getOwnUom() {
                return uom;
        }

        @Override
        public void setUom(Uom uom) {
                this.uom = uom;
        }

        /**
         * Get the inner label, contained in this {@code PointTextGraphic}.
         * @return 
         */
        public PointLabel getPointLabel() {
                return pointLabel;
        }

        /**
         * Set the inner label, contained in this {@code PointTextGraphic}.
         * @param pointLabel 
         */
        public void setPointLabel(PointLabel pointLabel) {
                this.pointLabel = pointLabel;
                if (pointLabel != null) {
                        pointLabel.setParent(this);
                }
        }

        @Override
        public Rectangle2D getBounds(Map<String,Object> map, MapTransform mt) throws ParameterException, IOException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void draw(Graphics2D g2, Map<String,Object> map,
                boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

                AffineTransform at = new AffineTransform(fat);
                double px = 0;
                double py = 0;

                if (getX() != null) {
                        px = Uom.toPixel(getX().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }
                if (getY() != null) {
                        py = Uom.toPixel(getY().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }

                Rectangle2D.Double bounds = new Rectangle2D.Double(px - 5, py - 5, 10, 10);
                Shape atShp = at.createTransformedShape(bounds);

                pointLabel.draw(g2, map, atShp, selected, mt);
        }


        /*@Override
        public double getMaxWidth(DataSet sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
        }*/
        @Override
        public JAXBElement<PointTextGraphicType> getJAXBElement() {
                PointTextGraphicType t = new PointTextGraphicType();

                if (pointLabel != null) {
                        t.setPointLabel(pointLabel.getJAXBType());
                }

                if (x != null || y != null) {
                        TranslateType ppt = new TranslateType();
                        if (x != null) {
                                ppt.setX(x.getJAXBParameterValueType());
                        }
                        if (y != null) {
                                ppt.setY(y.getJAXBParameterValueType());
                        }

                        t.setPointPosition(ppt);
                }
                if (getOwnUom() != null) {
                        t.setUom(getOwnUom().toURN());
                }
                ObjectFactory of = new ObjectFactory();
                return of.createPointTextGraphic(t);
        }

        /**
         * Get the x-displacement in the associated translation.
         * @return 
         */
        public RealParameter getX() {
                return x;
        }

        /**
         * Set the x-displacement in the associated translation.
         * @param x 
         */
        public void setX(RealParameter x) {
                this.x = x;
                if (this.x != null) {
                        this.x.setContext(RealParameterContext.REAL_CONTEXT);
                        this.x.setParent(this);
                }
        }

        /**
         * Get the y-displacement in the associated translation.
         * @return 
         */
        public RealParameter getY() {
                return y;
        }

        /**
         * Set the y-displacement in the associated translation.
         * @param y 
         */
        public void setY(RealParameter y) {
                this.y = y;
                if (this.y != null) {
                        this.y.setContext(RealParameterContext.REAL_CONTEXT);
                        this.y.setParent(this);
                }
        }

        @Override
        public void updateGraphic() {
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                if (pointLabel != null) {
                        ls.add(pointLabel);
                }
                if (x != null) {
                        ls.add(x);
                }
                if (y != null) {
                        ls.add(y);
                }
                return ls;
        }
}
