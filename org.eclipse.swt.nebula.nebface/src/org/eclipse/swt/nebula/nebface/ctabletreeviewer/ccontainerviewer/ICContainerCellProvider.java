/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/
package org.eclipse.swt.nebula.nebface.ctabletreeviewer.ccontainerviewer;


/**
 * <p>
 * NOTE:  THIS VIEWER AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p>
 */
public interface ICContainerCellProvider {

	public abstract Class[] getCellClasses(Object element);

}
