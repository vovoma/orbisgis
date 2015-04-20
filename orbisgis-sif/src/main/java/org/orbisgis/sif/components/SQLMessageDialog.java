/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.sif.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SQLMessageDialog extends JDialog {

    private static final I18n I18N = I18nFactory.getI18n(SQLMessageDialog.class);

    public enum CHOICE {

        CANCEL, OK
    };

    private CHOICE userCoice = CHOICE.CANCEL;

    private final String message;
    private final String details;

    private ActionListener cancelListener
            = EventHandler.create(ActionListener.class, this, "onCancel");
    private ActionListener okListener
            = EventHandler.create(ActionListener.class, this, "onOk");

    /**
     * Show this dialog and return only when the user : - close the dialog -
     * save all, some or none of the documents
     *
     * @param owner
     * @return Expected application behaviour, Close application or Cancel this
     * process
     */
    public static CHOICE showModal(Window owner, String title, String message, String details) {
        SQLMessageDialog sqlmd = new SQLMessageDialog(owner, title, message, details);        
        sqlmd.setResizable(false);
        sqlmd.setModalityType(ModalityType.APPLICATION_MODAL);
        sqlmd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
        sqlmd.setLocationRelativeTo(owner);
        sqlmd.create();
        sqlmd.setVisible(true);
        return sqlmd.userCoice;
    }

    /**
     * A custom JDialog to display a message and a SQL command(s)
     * @param owner
     * @param title
     * @param message
     * @param details 
     */
    public SQLMessageDialog(Window owner, String title, String message, String details) {
        super(owner);
        setTitle(title);
        this.message = message;
        this.details = details;
    }

    /**
     * Create the dialog
     */
    private void create() {
        add(new MainPane());
        pack();
    }

    /**
     * The user cancel the panel
     */
    public void onCancel() {
        userCoice = CHOICE.CANCEL;
        setVisible(false);
    }

    /**
     * The user valid the panel
     */
    public void onOk() {
        userCoice = CHOICE.OK;
        setVisible(false);
    }


    /**
     * Main panel that display the message and the SQL script.
     */
    public class MainPane extends JPanel {

        private MessagePane messagePane;
        private SQLPane sqlPane;

        public MainPane() {
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;

            messagePane = new MessagePane();
            add(messagePane, gbc);

            gbc.gridy++;
            gbc.weighty = 0;

            sqlPane = new SQLPane();
            sqlPane.setVisible(false);
            add(sqlPane, gbc);

            messagePane.addExpandCollapseListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    sqlPane.setVisible(messagePane.isExpanded());
                    Window window = SwingUtilities.windowForComponent(MainPane.this);
                    window.pack();
                }
            });
        }

        /**
         * A panel to display the message
         */
        public class MessagePane extends JPanel {

            private JButton expandButton;

            private boolean expanded = false;

            public MessagePane() {
                setLayout(new GridBagLayout());

                JButton okButton = new JButton(I18N.tr("Ok"));
                JButton cancelButton = new JButton(I18N.tr("Cancel"));
                cancelButton.addActionListener(cancelListener);
                okButton.addActionListener(okListener);
                
                expandButton = new JButton(I18N.tr("+ Details"));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(4, 4, 4, 4);
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.weightx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                add(new JLabel(message), gbc);

                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridwidth = 1;
                gbc.weightx = 0;
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.fill = GridBagConstraints.NONE;
                add(expandButton, gbc);

                JPanel pnlButtons = new JPanel(new GridLayout(1, 2));
                pnlButtons.add(okButton);
                pnlButtons.add(cancelButton);

                gbc.gridx = 1;
                gbc.gridy = 3;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                add(pnlButtons, gbc);

                expandButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        expanded = !expanded;
                        if (expanded) {
                            expandButton.setText(I18N.tr("- Details"));
                        } else {
                            expandButton.setText(I18N.tr("+ Details"));
                        }
                        fireStateChanged();
                    }
                });
            }

            public boolean isExpanded() {
                return expanded;
            }

            public void addExpandCollapseListener(ChangeListener listener) {
                listenerList.add(ChangeListener.class, listener);
            }

            public void removeExpandCollapseListener(ChangeListener listener) {
                listenerList.remove(ChangeListener.class, listener);
            }

            protected void fireStateChanged() {
                ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
                if (listeners.length > 0) {
                    ChangeEvent evt = new ChangeEvent(this);
                    for (ChangeListener listener : listeners) {
                        listener.stateChanged(evt);
                    }
                }
            }
        }

        /**
         * A panel to display the SQL query
         */
        public class SQLPane extends JPanel {

            public SQLPane() {
                setBorder(new TitledBorder(I18N.tr("SQL syntax")));
                setLayout(new BorderLayout());
                RSyntaxTextArea textArea = new RSyntaxTextArea();
                textArea.setLineWrap(false);
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
                textArea.setEditable(false);
                textArea.setText(details);
                RTextScrollPane scrollPane = new RTextScrollPane(textArea);
                add(scrollPane);
            }

        }

    }
}
