/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.widgets.figureparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScale;
import org.eclipse.nebula.visualization.xygraph.linearscale.AbstractScale.LabelSide;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScale.Orientation;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**The color map figure which can be used as the ramp of intensity graph.
 * @author Xihui Chen
 *
 */
public class ColorMapRamp extends Figure {

	private double min, max;
	
	private double[] mapData;
	
	private ColorMap colorMap;
	private LinearScale scale;
	private ColorMapFigure colorMapFigure;
	private final static int RAMP_WIDTH = 25;
	private ImageData imageData;

	public ColorMapRamp() {
		mapData = new double[256];
		min = 0;
		max = 1;
		updateMapData();
		colorMap = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		
		scale = new LinearScale();
		scale.setOrientation(Orientation.VERTICAL);
		scale.setScaleLineVisible(true);
		scale.setTickLabelSide(LabelSide.Secondary);
		scale.setMinorTicksVisible(false);
		scale.setRange(min, max);
		scale.setMajorTickMarkStepHint(50);
		scale.setFont(getFont());
		colorMapFigure = new ColorMapFigure();
		add(colorMapFigure);
		add(scale);	
	}


	private void updateMapData() {
		for(int j=0; j<256; j++)
			mapData[j] = max-j*(max-min)/255.0;
	}
	
	
	@Override
	protected void layout() {
		if (scale.getFont()==null) return;
		if (getChildren()==null || getChildren().isEmpty()) return;

		Rectangle clientArea = getClientArea();
		Dimension scaleSize = scale.getPreferredSize(clientArea.width, clientArea.height);		
		scale.setBounds(new Rectangle(clientArea.x + clientArea.width - scaleSize.width, clientArea.y,
				scaleSize.width, clientArea.height));
		
		colorMapFigure.setBounds(new Rectangle(clientArea.x, scale.getValuePosition(max, false),
				clientArea.width - scaleSize.width, scale.getTickLength()));
		super.layout();
		
	}
	
	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		Dimension result = super.getPreferredSize(hint, hint2);		
		result.width = RAMP_WIDTH + scale.getPreferredSize(hint, hint2).width;
		return result;
		
	}

	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		if (Double.isInfinite(min)) return;
		if (Double.isNaN(min))      return;
		this.min = min;
		scale.setRange(min, max);
		updateMapData();
		repaint();
	}

	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		if (Double.isInfinite(max)) return;
		if (Double.isNaN(max))      return;
		this.max = max;
		scale.setRange(min, max);
		updateMapData();
		repaint();
	}

	/**
	 * @param colorMap the colorMap to set
	 */
	public final void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		scale.setFont(f);
	}
	
	class ColorMapFigure extends Figure{
		
		@Override
		protected void paintClientArea(Graphics graphics) {
			super.paintClientArea(graphics);
			ImageData data = imageData==null
					       ? colorMap.drawImage(mapData, 1, 256, max, min)
			               : imageData;

			final Rectangle ca = getClientArea();
			data = data.scaledTo(ca.width, ca.height);

			final Image image = new Image(Display.getDefault(), data);
			graphics.drawImage(image, ca.x, ca.y);
			image.dispose();
		}		
		
	}

	/**
	 * Sets the overridden image data to use when drawing the color map.
	 * 
	 * @param imageData
	 *            the new image data to use, or <code>null</code> to create the
	 *            image data from the color map set by
	 *            {@link ColorMapRamp#setColorMap(ColorMap)}
	 * @see ColorMapRamp#setColorMap(ColorMap)
	 */
	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}

	/**
	 * Sets the Log 10 on or off
	 *
	 * @param log
	 */
	public void setLog10(boolean log) {
		this.scale.setLogScale(log);
	}
}
