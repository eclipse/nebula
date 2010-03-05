/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    public AdvancedTooltip(String title, String content) {
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
    public AdvancedTooltip(String title, String content, Image image) {
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
    public AdvancedTooltip(String title, String content, Image image, Image helpImage, String helpText) {
        _title = title;
        _content = content;
        _image = image;
        _helpImage = helpImage;
        _helpText = helpText;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image image) {
        _image = image;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        _content = content;
    }

    public Image getHelpImage() {
        return _helpImage;
    }

    public void setHelpImage(Image helpImage) {
        _helpImage = helpImage;
    }

    public String getHelpText() {
        return _helpText;
    }

    public void setHelpText(String helpText) {
        _helpText = helpText;
    }

    public int getExtraTextSpacing() {
        return _extraTextSpacing;
    }

    public void setExtraTextSpacing(int spacing) {
        _extraTextSpacing = spacing;
    }

}
