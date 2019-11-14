/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Baha El-Kassaby - initial commit
 *     Matthew Gerring - implementation
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

/**
 * Interface used to get the information text of a label
 *
 * @author Matthew Gerring
 * @author Baha El-Kassaby
 *
 */
public interface IAnnotationLabelProvider {

	/**
	 * Return a string to be used on the annotation label.
	 * 
	 * @param xValue
	 * @param yValue
	 * @return null to use normal labelling, "" to have no label, or a string to
	 *         be the label.
	 */
	public String getInfoText(double xValue, double yValue, boolean showName, boolean showSample, boolean showPosition);

}
