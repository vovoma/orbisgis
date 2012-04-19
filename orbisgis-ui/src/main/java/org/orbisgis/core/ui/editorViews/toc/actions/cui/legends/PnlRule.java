/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import javax.swing.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.IRulePanel;
import org.orbisgis.utils.I18N;

/**
 * Panel associated to {@code Rule} instances in the legend edition UI.
 * @author alexis
 */
public class PnlRule extends JPanel implements IRulePanel {

	private JButton btnCurrentScaleToMin;
	private JButton btnCurrentScaleToMax;
	private JTextField txtMinScale;
	private JTextField txtMaxScale;
        private JTextField txtName;
        private JTextArea txtDescription;
        private Rule rule;
        private LegendContext legendContext;
        private String id;

        /**
         * Sets the Rule associated to this panel.
         * @param r
         */
        public void setRule(Rule r){
                rule = r;
        }

        /**
         * Gets the Rule associated to this panel.
         * @return
         */
        public Rule getRule(){
                return rule;
        }

        @Override
        public Component getComponent() {
                removeAll();
		FlowLayout fl = new FlowLayout();
		fl.setVgap(0);
		this.setLayout(fl);
                //We need the map transform to use the buttons
                final MapTransform mt = legendContext.getCurrentMapTransform();
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                //We display the title
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel("Title : "), gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                txtName = new JTextField(rule.getName(),10);
                txtName.addFocusListener(EventHandler.create(FocusListener.class, this, "setTitle","source.text","focusLost"));
                panel.add(txtName, gbc);
                //We display the description
                //Label
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel("Description : "), gbc);
                //Text field
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.insets = new Insets(5 , 5, 5, 5);
                gbc.anchor = GridBagConstraints.LINE_START;
                txtDescription = new JTextArea(rule.getDescription());
                txtDescription.setColumns(40);
                txtDescription.setRows(6);
                txtDescription.setLineWrap(true);
                txtDescription.addFocusListener(EventHandler.create(
                        FocusListener.class, this, "setDescription","source.text","focusLost"));
                JScrollPane jsp = new JScrollPane(txtDescription);
                jsp.setPreferredSize(txtDescription.getPreferredSize());
                panel.add(jsp, gbc);
                //We display the minScale
		KeyListener keyAdapter = EventHandler.create(KeyListener.class, this, "applyScales");
                //Text
                //We put the text field and the button in a single panel in order to
                JPanel min = new JPanel();
                FlowLayout flowMin = new FlowLayout();
                flowMin.setHgap(5);
                min.setLayout(flowMin);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScale")),gbc);
                //Text field
		txtMinScale = new JTextField(10);
		txtMinScale.addKeyListener(keyAdapter);
                txtMinScale.setText(getMinscale());
                min.add(txtMinScale);
                //Button
		btnCurrentScaleToMin = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMin.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMinScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
                min.add(btnCurrentScaleToMin);
                //We add this dedicated panel to our GridBagLayout.
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 2;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(min,gbc);
                //We display the maxScale
                //Text
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.maxScale")),gbc);
                //We put the text field and the button in a single panel in order to
                //improve the UI.
                //Text field
                JPanel max = new JPanel();
                FlowLayout flowMax = new FlowLayout();
                flowMax.setHgap(5);
                max.setLayout(flowMax);
		txtMaxScale = new JTextField(10);
		txtMaxScale.addKeyListener(keyAdapter);
                txtMaxScale.setText(getMaxscale());
                max.add(txtMaxScale,gbc);
                //Button
		btnCurrentScaleToMax = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMax.addActionListener(new ActionListener() {

                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMaxScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
                max.add(btnCurrentScaleToMax);
                //We add this dedicated panel to our GridBagLayout.
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(max,gbc);
		this.add(panel);
		this.setPreferredSize(new Dimension(200, 100));
		this.setBorder(BorderFactory.createTitledBorder(
			I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.scale")));
		return this;
        }

        @Override
        public void initialize(LegendContext lc) {
                legendContext=lc;
                getComponent();
        }

        @Override
        public IRulePanel newInstance() {
                return new PnlRule();
        }

        @Override
        public String getId(){
                return id;
        }

        @Override
        public void setId(String id){
                this.id = id;
        }

        /**
         * Apply to the Rule's name the text contained in the editor used to
         * manage it.
         */
        public void setTitle(String s){
                rule.setName(s);
        }

        /**
         * Apply to the Rule's description the text contained in the editor used
         * to manage it.
         */
        public void setDescription(String s){
                rule.setDescription(s);
        }

        /**
         * Apply the scales registered in the text fields of this panel to
         * the underlying {@code Rule}.
         */
        public void applyScales(){
                String minScale = txtMinScale.getText();
                if (minScale.trim().length() != 0) {
                        try {
                                Double min = Double.parseDouble(minScale);
                                rule.setMinScaleDenom(min);
                        } catch (NumberFormatException e1) {
                        }
                } else {
                        rule.setMinScaleDenom(Double.NEGATIVE_INFINITY);
                }
                String maxScale = txtMaxScale.getText();
                if (maxScale.trim().length() != 0) {
                        try {
                                Double max = Double.parseDouble(maxScale);
                                rule.setMaxScaleDenom(max);
                        } catch (NumberFormatException e1) {
                        }
                } else {
                        rule.setMaxScaleDenom(Double.POSITIVE_INFINITY);
                }
        }

        @Override
        public String validateInput() {
		String minScale = txtMinScale.getText();
		String maxScale = txtMaxScale.getText();
                StringBuilder stringBuilder = new StringBuilder();

		if (minScale.trim().length() != 0) {
			try {
				Integer.parseInt(minScale);
			} catch (NumberFormatException e) {
                                stringBuilder.append(
                                        I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScaleIsNotAValidNumber"));

			}
		}
		if (maxScale.trim().length() != 0) {
			try {
				Integer.parseInt(maxScale);
			} catch (NumberFormatException e) {
                                stringBuilder.append("\n");
                                stringBuilder.append(
                                        I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.maxScaleIsNotAValidNumber"));
			}
		}
                String res = stringBuilder.toString();
                if(res != null && !res.isEmpty()){
                        return res;
                } else {
                        return null;
                }
        }

        private String getMinscale() {
                if(rule.getMinScaleDenom() != null && rule.getMinScaleDenom()>Double.NEGATIVE_INFINITY){
                        Double d = rule.getMinScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }

        private String getMaxscale() {
                if(rule.getMaxScaleDenom() != null && rule.getMaxScaleDenom()<Double.POSITIVE_INFINITY){
                        Double d = rule.getMaxScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }

}
