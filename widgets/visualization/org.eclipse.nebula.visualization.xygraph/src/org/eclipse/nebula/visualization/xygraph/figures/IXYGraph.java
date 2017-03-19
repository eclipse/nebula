/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.nebula.visualization.internal.xygraph.undo.OperationsManager;
import org.eclipse.nebula.visualization.internal.xygraph.undo.XYGraphMemento;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public interface IXYGraph extends IFigure {

	public static final String PROPERTY_CONFIG = "config"; //$NON-NLS-1$

	public static final String PROPERTY_XY_GRAPH_MEM = "xyGraphMem"; //$NON-NLS-1$

	public static final String PROPERTY_ZOOMTYPE = "zoomType"; //$NON-NLS-1$

	void fireConfigChanged();

	XYGraphMemento getXyGraphMem();

	void setXyGraphMem(XYGraphMemento xyGraphMem);

	/**
	 * @param zoomType
	 *            the zoomType to set
	 */
	void setZoomType(ZoomType zoomType);

	/**
	 * @return the zoomType
	 */
	ZoomType getZoomType();

	/**
	 * @param title
	 *            the title to set
	 */
	void setTitle(String title);

	/**
	 * @param showTitle
	 *            true if title should be shown; false otherwise.
	 */
	void setShowTitle(boolean showTitle);

	/**
	 * @return true if title should be shown; false otherwise.
	 */
	boolean isShowTitle();

	/**
	 * @param showLegend
	 *            true if legend should be shown; false otherwise.
	 */
	void setShowLegend(boolean showLegend);

	/**
	 * @return the showLegend
	 */
	boolean isShowLegend();

	/**
	 * Add an axis to the graph
	 * 
	 * @param axis
	 */
	void addAxis(Axis axis);

	/**
	 * Remove an axis from the graph
	 * 
	 * @param axis
	 * @return true if this axis exists.
	 */
	boolean removeAxis(Axis axis);

	/**
	 * Add a trace
	 * 
	 * @param trace
	 */
	void addTrace(Trace trace);

	/**
	 * Remove a trace.
	 * 
	 * @param trace
	 */
	void removeTrace(Trace trace);

	/**
	 * Add an annotation
	 * 
	 * @param annotation
	 */
	void addAnnotation(Annotation annotation);

	/**
	 * Remove an annotation
	 * 
	 * @param annotation
	 */
	void removeAnnotation(Annotation annotation);

	/**
	 * @param titleFont
	 *            the titleFont to set
	 */
	void setTitleFont(Font titleFont);

	/**
	 * @return the title font.
	 */
	Font getTitleFont();

	FontData getTitleFontData();

	/**
	 * @param titleColor
	 *            the titleColor to set
	 */
	void setTitleColor(Color titleColor);

	/**
	 * {@inheritDoc}
	 */
	void paintFigure(Graphics graphics);

	/**
	 * @param transparent
	 *            the transparent to set
	 */
	void setTransparent(boolean transparent);

	/**
	 * @return the transparent
	 */
	boolean isTransparent();

	/**
	 * @return the plotArea, which contains all the elements drawn inside it.
	 */
	PlotArea getPlotArea();

	/** @return Image of the XYFigure. Receiver must dispose. */
	Image getImage();

	/**
	 * @return the titleColor
	 */
	Color getTitleColor();

	RGB getTitleColorRgb();

	/**
	 * @return the title
	 */
	String getTitle();

	/**
	 * @return the operationsManager
	 */
	OperationsManager getOperationsManager();

	/**
	 * @return the xAxisList
	 */
	List<Axis> getXAxisList();

	/**
	 * @return the yAxisList
	 */
	List<Axis> getYAxisList();

	/**
	 * @return the all the axis include xAxes and yAxes. yAxisList is appended
	 *         to xAxisList in the returned list.
	 */
	List<Axis> getAxisList();

	/**
	 * @return the legendMap
	 */
	Map<Axis, Legend> getLegendMap();

	/**
	 * Perform forced autoscale to all axes.
	 */
	void performAutoScale();

	/**
	 * Stagger all axes: Autoscale each axis so that traces on various axes
	 * don't overlap
	 */
	void performStagger();

	public Axis getPrimaryXAxis();

	public Axis getPrimaryYAxis();
}
