/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Image;

public class AdvancedTooltip {

    private Image  _image;
    private String _title;
    private String _content;
    private Image  _helpImage;
    private String _helpText;
    private int    _extraTextSpacing;

    /**
     * Creates a new Advanced Tooltip.
     * 
     * @param title Header
     * @param content Content
     */
    public AdvancedTooltip(final String title, final String content) {
        _title = title;
        _content = content;
    }

    /**
     * Creates a new Advanced Tooltip.
     * 
     * @param title Header
     * @param content Content
     * @param image Image
     */
    public AdvancedTooltip(final String title, final String content, final Image image) {
        _title = title;
        _content = content;
        _image = image;
    }

    /**
     * Creates a new Advanced Tooltip with help image and text.
     * 
     * @param title Header
     * @param content Content
     * @param image Image
     * @param helpImage Help Image
     * @param helpText Help Text
     */
    public AdvancedTooltip(final String title, final String content, final Image image, final Image helpImage, final String helpText) {
        _title = title;
        _content = content;
        _image = image;
        _helpImage = helpImage;
        _helpText = helpText;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(final Image image) {
        _image = image;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(final String content) {
        _content = content;
    }

    public Image getHelpImage() {
        return _helpImage;
    }

    public void setHelpImage(final Image helpImage) {
        _helpImage = helpImage;
    }

    public String getHelpText() {
        return _helpText;
    }

    public void setHelpText(final String helpText) {
        _helpText = helpText;
    }

    public int getExtraTextSpacing() {
        return _extraTextSpacing;
    }

    public void setExtraTextSpacing(final int spacing) {
        _extraTextSpacing = spacing;
    }

}
