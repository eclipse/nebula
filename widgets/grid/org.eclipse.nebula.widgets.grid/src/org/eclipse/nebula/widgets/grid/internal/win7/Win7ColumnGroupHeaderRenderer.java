package org.eclipse.nebula.widgets.grid.internal.win7;

import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.internal.DefaultColumnGroupHeaderRenderer;
import org.eclipse.nebula.widgets.grid.internal.ExpandToggleRenderer;
import org.eclipse.nebula.widgets.grid.internal.TextUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

/**
 * Column group renderer which emulates a default Win7 L&F
 */
public class Win7ColumnGroupHeaderRenderer extends DefaultColumnGroupHeaderRenderer {
	int leftMargin = 6;

	int rightMargin = 6;

	int topMargin = 3;

	int bottomMargin = 3;

	int imageSpacing = 3;

	private ExpandToggleRenderer toggleRenderer = new ExpandToggleRenderer();

	private TextLayout textLayout;

	private Win7PaletteProvider palette;

	/**
	 * @param palette
	 */
	public Win7ColumnGroupHeaderRenderer(Win7PaletteProvider palette) {
		this.palette = palette;
	}

	/**
	 * {@inheritDoc}
	 */
	public void paint(GC gc, Object value) {
		GridColumnGroup group = (GridColumnGroup) value;

		// set the font to be used to display the text.
		gc.setFont(group.getHeaderFont());

		Win7ColumnHeaderUtil.drawColumn(gc, getBounds(), palette, isHover(), isSelected(), isMouseDown());

		int x = leftMargin;

		if (group.getImage() != null) {
			gc.drawImage(group.getImage(), getBounds().x + x, getBounds().y + topMargin);
			x = group.getImage().getBounds().width + imageSpacing;
		}

		int width = getBounds().width - x - rightMargin;
		if ((group.getStyle() & SWT.TOGGLE) != 0) {
			width -= toggleRenderer.getSize().x;
		}

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		if (!isWordWrap()) {
			gc.drawString(TextUtils.getShortString(gc, group.getText(), width), getBounds().x + x,
					getBounds().y + topMargin, true);
		} else {
			getTextLayout(gc, group);
			textLayout.setWidth(width < 1 ? 1 : width);
			textLayout.setText(group.getText());

			if (group.getParent().isAutoHeight()) {
				group.getParent().recalculateHeader();
			}

			textLayout.draw(gc, getBounds().x + x, getBounds().y + topMargin);
		}

		if ((group.getStyle() & SWT.TOGGLE) != 0) {
			toggleRenderer.setHover(isHover() && getHoverDetail().equals("toggle"));
			toggleRenderer.setExpanded(group.getExpanded());
			toggleRenderer.setBounds(getToggleBounds());
			toggleRenderer.paint(gc, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDisplay(Display display) {
		super.setDisplay(display);
		toggleRenderer.setDisplay(display);
		palette.initializePalette(getDisplay(), Win7PaletteProvider.NORMAL_GRID_COLUMN_HEADER);
		palette.initializePalette(getDisplay(), Win7PaletteProvider.HOVER_GRID_COLUMN_HEADER);
	}

	private void getTextLayout(GC gc, GridColumnGroup group)
    {
        if (textLayout == null)
        {
            textLayout = new TextLayout(gc.getDevice());
            textLayout.setFont(gc.getFont());
            group.getParent().addListener(SWT.Dispose, e ->
                    textLayout.dispose()
            );
        }
}}