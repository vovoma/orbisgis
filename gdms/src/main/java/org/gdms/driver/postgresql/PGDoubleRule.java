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
package org.gdms.driver.postgresql;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.AbstractConversionRule;

public class PGDoubleRule extends AbstractConversionRule {

        @Override
        public String getSQL(String fieldName, Type fieldType) {
                return fieldName + " " + getTypeExpr(fieldType);
        }

        private String getTypeExpr(Type fieldType) {
                int precision = fieldType.getIntConstraint(Constraint.PRECISION);
                int scale = fieldType.getIntConstraint(Constraint.SCALE);
                if (scale == -1) {
                        return "double precision";
                } else if (scale < 15) {
                        return "double precision";
                } else {
                        return "numeric(" + precision + ", " + scale + ")";
                }
        }

        @Override
        public boolean canApply(Type type) {
                return type.getTypeCode() == Type.FLOAT
                        || type.getTypeCode() == Type.DOUBLE;
        }

        @Override
        public String getTypeName() {
                return "double precision";
        }

        @Override
        protected int getOutputTypeCode() {
                return Type.DOUBLE;
        }
}