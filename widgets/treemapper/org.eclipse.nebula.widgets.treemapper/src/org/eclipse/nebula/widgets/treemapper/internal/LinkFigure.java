/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
*
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.internal;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 */
public class LinkFigure {
	
	private IFigure parent;
	
	private Polyline leftLine;
	private Polyline rightLine;
	private Point leftPoint;
	private Point rightPoint;
	
	public LinkFigure(IFigure parent) {
		leftLine = new Polyline();
		leftLine.setAntialias(SWT.ON);
		rightLine = new Polyline();
		rightLine.setAntialias(SWT.ON);
		parent.add(leftLine);
		parent.add(rightLine);
		this.parent = parent;
	}

	/**
	 * @param i
	 * @param j 
	 */
	public void setLeftPoint(int x, int y) {
		leftPoint = new Point(x, y);
		updateFigures();
	}
	
	/**
	 * @param i
	 * @param j 
	 */
	public void setRightPoint(int x, int y) {
		rightPoint = new Point(x, y);
		updateFigures();
	}

	/**
	 * @param leftItemVisible
	 */
	public void setLeftMappingVisible(boolean leftItemVisible) {
		if (leftItemVisible) {
			leftLine.setLineStyle(SWT.LINE_SOLID);
		} else {
			leftLine.setLineStyle(SWT.LINE_DASH);
		}
	}

	/**
	 * 
	 */
	private void updateFigures() {
		if (leftPoint != null && rightPoint != null) {
			Point middlePoint = new Rectangle(leftPoint, rightPoint).getCenter();
			{
				PointList leftPointList = new PointList(2);
				leftPointList.addPoint(leftPoint);
				leftPointList.addPoint(middlePoint);
				leftLine.setPoints(leftPointList);
			}
			{
				PointList rightPointList = new PointList(2);
				rightPointList.addPoint(middlePoint);
				rightPointList.addPoint(rightPoint);
				rightLine.setPoints(rightPointList);
			}
		}
		
	}

	/**
	 * @param rightItemVisible
	 */
	public void setRightMappingVisible(boolean rightItemVisible) {
		if (rightItemVisible) {
			rightLine.setLineStyle(SWT.LINE_SOLID);
		} else {
			rightLine.setLineStyle(SWT.LINE_DASH);
		}
	}

	/**
	 * @param lineWidth
	 */
	public void setLineWidth(int lineWidth) {
		leftLine.setLineWidth(lineWidth);
		rightLine.setLineWidth(lineWidth);
	}

	/**
	 * 
	 */
	public void deleteFromParent() {
		parent.remove(leftLine);
		parent.remove(rightLine);
	}

	/**
	 * @param color
	 */
	public void seLineColor(Color color) {
		leftLine.setForegroundColor(color);
		rightLine.setForegroundColor(color);
	}

	/**
	 * @param mouseListener
	 */
	public void addMouseListener(MouseListener mouseListener) {
		leftLine.addMouseListener(mouseListener);
		rightLine.addMouseListener(mouseListener);
	}

	/**
	 * @param mouseMotionListener
	 */
	public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
		leftLine.addMouseMotionListener(mouseMotionListener);
		rightLine.addMouseMotionListener(mouseMotionListener);
	}

}
