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
package org.orbisgis.tablegui.impl;

import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;

import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

/**
 * Lock/Unlock table edition action.
 * @author Nicolas Fortin
 */
public class ActionUndo extends ActionAbstractEdition {
    private UndoManager undoManager;

    public ActionUndo(TableEditableElement editable,UndoManager undoManager) {
        super(I18N.tr("Undo"), TableEditorIcon.getIcon("edit-undo"),editable);
        this.undoManager = undoManager;
        putValue(ActionTools.MENU_ID, TableEditorActions.A_UNDO);
        putValue(SHORT_DESCRIPTION,I18N.tr("Undo the last modification"));
        onSourceUpdate();
    }

    @Override
    public void onSourceUpdate() {
        setEnabled(undoManager.canUndo());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            undoManager.undo();
        } catch (CannotUndoException ex) {
            LOGGER.error(ex.getLocalizedMessage(),ex);
        }
    }
}
