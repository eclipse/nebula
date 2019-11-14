/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph;

import org.eclipse.osgi.util.NLS;

/**
 * Externalized Strings
 * 
 * @author Kay Kasemir
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.nebula.visualization.xygraph.messages"; //$NON-NLS-1$
	public static String Annotation_Add;
	public static String Annotation_ChangeFont;
	public static String Annotation_Color;
	public static String Annotation_ColorFromYAxis;
	public static String Annotation_Cursor;
	public static String Annotation_DefaultNameFmt;
	public static String Annotation_Font;
	public static String Annotation_Name;
	public static String Annotation_NameTT;
	public static String Annotation_ShowInfo;
	public static String Annotation_ShowName;
	public static String Annotation_ShowPosition;
	public static String Annotation_Snap;
	public static String Annotation_SnapTT;
	public static String Annotation_SystemDefault;
	public static String Annotation_Trace;
	public static String Annotation_TraceSnapTT;
	public static String Annotation_XAxis;
	public static String Annotation_XAxisSnapTT;
	public static String Annotation_YAxis;
	public static String Annotation_YAxisSnapTT;
	public static String PointBar;
	public static String PointCircle;
	public static String PointCross;
	public static String PointDiamond;
	public static String PointFilledCircle;
	public static String PointFilledDiamond;
	public static String PointFilledSquare;
	public static String PointFilledTriangle;
	public static String PointNone;
	public static String PointPoint;
	public static String PointSquare;
	public static String PointTriangle;
	public static String ProintCross2;
	public static String TraceArea;
	public static String TraceLineArea;
	public static String TraceBar;
	public static String TraceDash;
	public static String TraceDashDot;
	public static String TraceDashDotDot;
	public static String TraceDot;
	public static String TracePoint;
	public static String TraceSolid;
	public static String TraceStepHoriz;
	public static String TraceStepVert;
	public static String Zoom_Horiz;
	public static String Zoom_In;
	public static String Zoom_InHoriz;
	public static String Zoom_InVert;
	public static String Zoom_None;
	public static String Zoom_Out;
	public static String Zoom_OutHoriz;
	public static String Zoom_OutVert;
	public static String Zoom_Pan;
	public static String Zoom_Rubberband;
	public static String Zoom_Dynamic;
	public static String Zoom_Vert;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instantiation
	}
}
