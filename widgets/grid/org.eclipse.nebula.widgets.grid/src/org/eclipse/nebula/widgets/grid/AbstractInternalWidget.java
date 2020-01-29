/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.grid;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * Base implementation of IRenderer and IInternalWidget. Provides management of
 * a few values. 
 * 
 * @see AbstractRenderer
 * @author chris.gross@us.ibm.com
 */
public abstract class AbstractInternalWidget extends AbstractRenderer implements IInternalWidget
{

    String hoverDetail = "";

    /**
     * @return the hoverDetail
     */
    public String getHoverDetail()
    {
        return hoverDetail;
    }

    /**
     * @param hoverDetail the hoverDetail to set
     */
    public void setHoverDetail(String hoverDetail)
    {
        this.hoverDetail = hoverDetail;
    }

}
