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

import java.util.ArrayList;
import java.util.List;
import net.opengis.se._2_0.thematic.SliceType;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.FillNode;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code Slice}s are used in {@code PieChart}s instances to determine the size
 * of each rendered area. They are defined using :
 * <ul>
 * <li>A name (compulsory).</li>
 * <li>the value this {@code Slice} represents (compulsory).</li>
 * <li>A {@code Fill} intance to render its interior (compulsory).</li>
 * <li>A gap (optional).</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class Slice extends AbstractSymbolizerNode implements FillNode {

        private String name;
        private RealParameter value;
        private Fill fill;
        private RealParameter gap;

        @Override
        public Fill getFill() {
                return fill;
        }

        @Override
        public void setFill(Fill fill) {
                this.fill = fill;
                fill.setParent(this);
        }

        /**
         * Get the gap that must be maintained around this {@code Slice}.
         * @return
         * The gap as a non-negative {@link RealParameter}, or null if not set
         * before.
         */
        public RealParameter getGap() {
                return gap;
        }

        /**
         * Set the gap that must be maintained around this {@code Slice}.
         * @param gap
         */
        public void setGap(RealParameter gap) {
                this.gap = gap;
                if (gap != null) {
                        gap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                        gap.setParent(this);
                }
        }

        /**
         * Get the name of this {@code Slice}.
         * @return
         * The name as a {@code String}.
         */
        public String getName() {
                return name;
        }

        /**
         * Set the name of this {@code Slice}.
         * @param name
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * Get the value this slice represents.
         * @return
         * The value, as a {@link RealParameter}, so that external sources
         * can be used.
         */
        public RealParameter getValue() {
                return value;
        }

        /**
         * Set the value represented by this {@code Slice}.
         * @param value
         */
        public void setValue(RealParameter value) {
                this.value = value;
                if (value != null) {
                        value.setContext(RealParameterContext.REAL_CONTEXT);
                        value.setParent(this);
                }
        }

        /**
         * Get a {@code SliceType} that represents this {@code Slice}.
         * @return
         */
        public SliceType getJAXBType() {
                SliceType s = new SliceType();

                if (fill != null) {
                        s.setFill(fill.getJAXBElement());
                }
                if (gap != null) {
                        s.setGap(gap.getJAXBParameterValueType());
                }
                if (name != null) {
                        s.setName(name);
                }
                if (value != null) {
                        s.setValue(value.getJAXBParameterValueType());
                }

                return s;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                if (fill != null) {
                        ls.add(fill);
                }
                if (value != null) {
                        ls.add(value);
                }
                if (gap != null) {
                        ls.add(gap);
                }
                return ls;
        }
}
