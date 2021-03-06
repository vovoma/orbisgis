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
package org.orbisgis.coremap.renderer.se.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to retrieve all the analysis that are made in a SE style.
 * All the nodes of the tree are asked if an analysis is used. If it is, the
 * matching boolean is set to true here. In the end, the symbolizer can know
 * which analysis are used.
 * @author Alexis Guéganno
 */
public class UsedAnalysis {

        private boolean categorizeUsed = false;
        private boolean interpolateUsed = false;
        private boolean recodeUsed = false;
        private List<SeParameter> analysis = new ArrayList<SeParameter>();

        /**
         * If true, a recode has been found in the SE tree.
         * @return
         */
        public boolean isRecodeUsed() {
                return recodeUsed;
        }

        /**
         * If true, a recode has been found in the SE tree.
         * @return
         */
        public boolean isCategorizeUsed() {
                return categorizeUsed;
        }

        /**
         * If true, an interpolate has been found in the SE tree.
         * @return
         */
        public boolean isInterpolateUsed() {
                return interpolateUsed;
        }

        /**
         * Includes the given SeParameter. For instance, if it is an instance of
         * {@code Interpolate}, it will set {@code interpolateUsed} to {@code
         * true}. If it is not, it will let {@code interpolateUsed} unchanged.
         * </p>
         * <p>{@code rp} is added to the list of analysis if and only if it is
         * not a literal.
         * @param rp
         */
        public void include(SeParameter rp){
                if(rp instanceof Interpolate){
                        interpolateUsed = true;
                        analysis.add(rp);
                }
                if(rp instanceof Recode){
                        recodeUsed = true;
                        analysis.add(rp);
                }
                if(rp instanceof Categorize){
                        categorizeUsed = true;
                        analysis.add(rp);
                }
        }

        /**
         * Merge this {@code UsedAnalysis} with {@code other}. Each analysis
         * will be considered {@code true} if {@code true} in {@code this} or in
         * {@code other}. Moreover, the two lists of analysis are merged too.
         * @param other
         */
        public void merge(UsedAnalysis other){
                recodeUsed = recodeUsed || other.recodeUsed;
                categorizeUsed = categorizeUsed || other.categorizeUsed;
                interpolateUsed = interpolateUsed || other.interpolateUsed;
                analysis.addAll(other.analysis);
        }

        /**
         * Get the list of {@code SeParameter} that have been recognized during
         * the feeding of this {@code UsedAnalysis} instance.
         * @return
         */
        public List<SeParameter> getAnalysis(){
                return analysis;
        }

}
