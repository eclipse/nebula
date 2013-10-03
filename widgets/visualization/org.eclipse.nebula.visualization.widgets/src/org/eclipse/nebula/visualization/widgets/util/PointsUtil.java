/*************************************************************************
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *************************************************************************/
 package org.eclipse.nebula.visualization.widgets.util;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class can be used to manimuplate points.
 * @author Kai Meyer, Xihui Chen
 *
 */
public final class PointsUtil {
	
	/**
	 * Private constructor, to avoid instantiation.
	 */
	private PointsUtil() {}
	
	/**
	 * Rotates the given {@link Point} with the given angle relative to the rotation point.
	 * Converts the given point to a {@link PrecisionPoint} and calls {@link #doRotate(PrecisionPoint, double, PrecisionPoint)}.
	 * @param point The {@link Point} to rotate
	 * @param angle The angle to rotate (in Degrees)
	 * @param rotationPoint The rotation point
	 * @return The rotated Point
	 */
	public static PrecisionPoint rotate(final Point point, final double angle, final Point rotationPoint) {
		PrecisionPoint pPoint = point instanceof PrecisionPoint ? (PrecisionPoint) point : new PrecisionPoint(point);
		PrecisionPoint pRotationPoint = rotationPoint instanceof PrecisionPoint ? (PrecisionPoint) rotationPoint : new PrecisionPoint(rotationPoint);
		
		return doRotate(pPoint, angle, pRotationPoint);
	}
	
	/**
	 * Rotates the given {@link Point} with the given angle relative to the rotation point.
	 * @param point The {@link Point} to rotate
	 * @param angle The angle to rotate (in Degrees)
	 * @param rotationPoint The rotation point
	 * @return The rotated Point
	 */
	public static PrecisionPoint doRotate(final PrecisionPoint point, final double angle, final PrecisionPoint rotationPoint) {
		assert point!=null : "Precondition violated: point!=null";
		assert rotationPoint!=null : "Precondition violated: rotationPoint!=null";
		double trueAngle = Math.toRadians(angle);
		double sin = Math.sin(trueAngle);
		double cos = Math.cos(trueAngle);
		
		//Relative coordinates to the rotation point
		double relX = point.preciseX()-rotationPoint.preciseX();
		double relY = point.preciseY()-rotationPoint.preciseY();
		
		double temp = relX * cos - relY * sin;

		double y = relX * sin + relY * cos;
		double x = temp;
		
		return new PrecisionPoint(x+rotationPoint.x,y+rotationPoint.y);
	}
	
	/**
	 * Rotates all points.
	 * 
	 * @param points The PoinList, which points should be rotated
	 * @param angle
	 *            The angle to rotate
	 * @return The rotated PointList
	 */
	public static final PointList rotatePoints(final PointList points, final double angle) {		
		Rectangle pointBounds = points.getBounds();
		Point rotationPoint = pointBounds.getCenter();
		PointList newPoints =  rotatePoints(points, angle, rotationPoint);
		Rectangle newPointBounds = newPoints.getBounds();
		if (!rotationPoint.equals(newPointBounds.getCenter())) {
			Dimension difference = rotationPoint.getCopy().getDifference(
					newPointBounds.getCenter());
			newPoints.translate(difference.width, difference.height);
		}
		return newPoints;
	}
	
	/**
	 * Rotates all points.
	 * 
	 * @param points The PoinList, which points should be rotated
	 * @param angle
	 *            The angle to rotate
	 * @return The rotated PointList
	 */
	public static final PointList rotatePoints(final PointList points, final double angle, final Point center) {		
		PointList newPoints = new PointList();

		for (int i = 0; i < points.size(); i++) {
			newPoints.addPoint(PointsUtil.rotate(points.getPoint(i), angle,
					center));
		}
		
		return newPoints;
	}
	
	
	/**Flip point horizontally from center point.
	 * @param point the point to be flipped.
	 * @param center the center point.
	 * @return the point after flipping.
	 */
	public static final Point flipPointHorizontally(final Point point, final int center){	
		int newX = 2*center-point.x;
		return new Point(newX, point.y);
	}
	
	/**Flip point vertically from center point.
	 * @param point the point to be flipped.
	 * @param center the center point.
	 * @return the point after flipping.
	 */
	public static final Point flipPointVertically(final Point point, final int center){
		int newY = 2*center-point.y;
		return new Point(point.x, newY);
	}
	
	/**Flip points horizontally.
	 * @param points the points to be flipped.
	 * @return the flipped points.
	 */
	public static final PointList flipPointsHorizontally(PointList points){
		
		int centerX = points.getBounds().x + points.getBounds().width/2;
		
		return flipPointsHorizontally(points, centerX);
		
	}
	
	/**Flip points horizontally.
	 * @param points the points to be flipped.
	 * @param centerX the center X position
	 * @return the flipped points.
	 */
	public static final PointList flipPointsHorizontally(PointList points, int centerX){	
		
		PointList newPointList = new PointList();
		
		for (int i = 0; i < points.size(); i++) {
			newPointList.addPoint(flipPointHorizontally(points.getPoint(i), centerX));
		}
		
		return newPointList;
		
	}
	
	/**Flip points vertically.
	 * @param points the points to be flipped.
	 * @return the flipped points.
	 */
	public static final PointList flipPointsVertically(PointList points){
		
		int centerY = points.getBounds().y + points.getBounds().height/2;
		
		return flipPointsVertically(points, centerY);
		
	}
	
	
	/**Flip points vertically.
	 * @param points the points to be flipped.
	 * @param centerY the center Y position.
	 * @return the flipped points.
	 */
	public static final PointList flipPointsVertically(PointList points, int centerY){
				
		PointList newPointList = new PointList();
		
		for (int i = 0; i < points.size(); i++) {
			newPointList.addPoint(flipPointVertically(points.getPoint(i), centerY));
		}
		
		return newPointList;
		
	}
	
	/**Scale the geometry size of a pointlist.
	 * @param points points to be scaled.
	 * @param widthRatio width scale ratio.
	 * @param heightRatio height scale ratio.
	 */
	public static final void scalePoints(PointList points,
			double widthRatio, double heightRatio){
		Point p0 = points.getBounds().getLocation();
		for(int i=0; i<points.size(); i++){
			Point p=points.getPoint(i);
			p.x=(int) ((p.x-p0.x)*widthRatio) + p0.x;
			p.y=(int) ((p.y-p0.y)*heightRatio) + p0.y;
			points.setPoint(p,i);
		}	
	}

	/**Scale the bound size of a point list.
	 * @param points the points to be scaled.
	 * @param width the new width.
	 * @param height the new height
	 * @return the points after scaled. If no scale is needed, return the input points.
	 */
	public static PointList scalePointsBySize(final PointList points, final int width, final int height){
		int targetW = Math.max(1, width);
		int targetH = Math.max(1, height);
		double oldW = points.getBounds().width;
		double oldH = points.getBounds().height;
		double topLeftX = points.getBounds().x;
		double topLeftY = points.getBounds().y;

		if (oldW != targetW || oldH != targetH) {
			PointList newPoints = new PointList();
			for (int i = 0; i < points.size(); i++) {
				int x = points.getPoint(i).x;
				int y = points.getPoint(i).y;

				Point newPoint = new Point(x, y);
				if (oldW > 0 && oldH > 0) {
					double oldRelX = (x - topLeftX) / oldW;
					double oldRelY = (y - topLeftY) / oldH;

					double newX = topLeftX + (oldRelX * targetW);
					double newY = topLeftY + (oldRelY * targetH);
					int roundedX = (int) Math.round(newX);
					int roundedY = (int) Math.round(newY);
					newPoint = new Point(roundedX, roundedY);
				}

				newPoints.addPoint(newPoint);
			}
			return newPoints;
		}
		return points;
	}
	
}
