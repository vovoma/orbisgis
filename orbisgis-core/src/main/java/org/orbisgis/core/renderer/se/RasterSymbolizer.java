/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */


package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.Drawer;
import org.orbisgis.core.renderer.persistance.se.RasterSymbolizerType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.raster.OverlapBehavior;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.raster.Channel;
import org.orbisgis.core.renderer.se.raster.ContrastEnhancement;


/**
 * @ todo implements almost all...
 * @author maxence
 */
public class RasterSymbolizer extends Symbolizer {

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uom getUom() {
        return Uom.MM;
    }

    public Channel getBlueChannel() {
        return blueChannel;
    }

    public void setBlueChannel(Channel blueChannel) {
        this.blueChannel = blueChannel;
    }

    public Categorize2Color getCategorizedColorMap() {
        return categorizedColorMap;
    }

    public void setCategorizedColorMap(Categorize2Color categorizedColorMap) {
        this.categorizedColorMap = categorizedColorMap;
    }

    public ContrastEnhancement getContrastEnhancement() {
        return contrastEnhancement;
    }

    public void setContrastEnhancement(ContrastEnhancement contrastEnhancement) {
        this.contrastEnhancement = contrastEnhancement;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public Channel getGrayChannel() {
        return grayChannel;
    }

    public void setGrayChannel(Channel grayChannel) {
        this.grayChannel = grayChannel;
    }

    public Channel getGreenChannel() {
        return greenChannel;
    }

    public void setGreenChannel(Channel greenChannel) {
        this.greenChannel = greenChannel;
    }

    public Interpolate2Color getInterpolatedColorMap() {
        return interpolatedColorMap;
    }

    public void setInterpolatedColorMap(Interpolate2Color interpolatedColorMap) {
        this.interpolatedColorMap = interpolatedColorMap;
    }

    public boolean isIsColored() {
        return isColored;
    }

    public void setIsColored(boolean isColored) {
        this.isColored = isColored;
    }

    public RealParameter getOpacity() {
        return opacity;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
		if (this.opacity != null){
			this.opacity.setContext(RealParameterContext.percentageContext);
		}
    }

    public AreaSymbolizer getOutline() {
        return outline;
    }

    public void setOutline(AreaSymbolizer outline) {
        this.outline = outline;
    }

    public OverlapBehavior getOverlapBehavior() {
        return overlapBehavior;
    }

    public void setOverlapBehavior(OverlapBehavior overlapBehavior) {
        this.overlapBehavior = overlapBehavior;
    }

    public Channel getRedChannel() {
        return redChannel;
    }

    public void setRedChannel(Channel redChannel) {
        this.redChannel = redChannel;
    }

    public double getShadedReliefFactor() {
        return shadedReliefFactor;
    }

    public void setShadedReliefFactor(double shadedReliefFactor) {
        this.shadedReliefFactor = shadedReliefFactor;
    }

    public boolean isShadedReliefOnlyBrightness() {
        return shadedReliefOnlyBrightness;
    }

    public void setShadedReliefOnlyBrightness(boolean shadedReliefOnlyBrightness) {
        this.shadedReliefOnlyBrightness = shadedReliefOnlyBrightness;
    }

    public boolean isUseInterpolationForColorMap() {
        return useInterpolationForColorMap;
    }

    public void setUseInterpolationForColorMap(boolean useInterpolationForColorMap) {
        this.useInterpolationForColorMap = useInterpolationForColorMap;
    }

    @Override
    public JAXBElement<RasterSymbolizerType> getJAXBElement() {
        return null;
    }

    public RasterSymbolizer(JAXBElement<RasterSymbolizerType> st) {
		super(st);
        RasterSymbolizerType lst = st.getValue();
        System.out.println("RasterSymb");

        System.out.println("  Name: " + lst.getName());
        System.out.println("  UoM: " + lst.getUnitOfMeasure());
        System.out.println("  Version: " + lst.getVersion());
        System.out.println("  Desc: " + lst.getDescription());
        System.out.println("  Geom: " + lst.getGeometry());
    }

    
    private RealParameter opacity;
    private Channel redChannel;
    private Channel greenChannel;
    private Channel blueChannel;
    private Channel grayChannel;
    private boolean isColored; // true => use red, green and blue channels; false => use gray
    private OverlapBehavior overlapBehavior;
    private Interpolate2Color interpolatedColorMap;
    private Categorize2Color categorizedColorMap;
    private boolean useInterpolationForColorMap; // true => interpolatedColorMap, False => CategorizedColorMap
    private ContrastEnhancement contrastEnhancement;
    private double gamma;
    private boolean shadedReliefOnlyBrightness;
    private double shadedReliefFactor;

    /*
     * SE request either a LineSymbolizer or an AreaSymbolizer
     * Since a line symbolizer is an area one witout the fill element, we only provide the latter
     */
    private AreaSymbolizer outline;

	@Override
	public void draw(Drawer drawer, long fid, boolean selected) {
		drawer.drawRasterSymbolizer(fid, selected);
	}

}
