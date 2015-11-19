/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.model;

import org.orbisgis.orbistoolboxapi.annotations.model.FieldType;

import java.net.URI;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

public class DataField extends ComplexData{

    private URI dataStoreIdentifier;
    private boolean isSourceLoaded = false;
    private List<FieldType> fieldTypeList;

    public DataField(Format format, List<FieldType> fieldTypeList, URI dataStoreURI) throws MalformedScriptException {
        super(format);
        this.fieldTypeList = fieldTypeList;
        this.dataStoreIdentifier = dataStoreURI;
    }

    public URI getDataStoreIdentifier(){
        return dataStoreIdentifier;
    }

    public boolean isSourceLoaded() {
        return isSourceLoaded;
    }

    public void setIsSourceLoaded(boolean isSourceLoaded) {
        this.isSourceLoaded = isSourceLoaded;
    }

    public List<FieldType> getFieldTypeList() {
        return fieldTypeList;
    }
}
