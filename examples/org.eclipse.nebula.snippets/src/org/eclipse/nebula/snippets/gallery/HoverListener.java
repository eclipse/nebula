package org.eclipse.nebula.snippets.gallery;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.SetColorEffect;
import org.eclipse.nebula.animation.effects.SetColorEffect.IColoredObject;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

/**
 * This class adds an hover effect to a gallery widget.
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class HoverListener implements MouseMoveListener, MouseTrackListener,
		MouseWheelListener {

	private static final String ANIMATION_DATA = "hoverAnimation"; //$NON-NLS-1$

	private Color backgroundColor = null;
	private Color hoverColor = null;
	GalleryItem current = null;
	Gallery gallery = null;
	int durationIn = 1000;
	int durationOut = 1000;

	/**
	 * This runnable cleans temporary AnimationRunner once the animation is
	 * finished.
	 * 
	 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
	 * 
	 */
	public class AnimationDataCleaner implements Runnable {

		private GalleryItem item;

		public AnimationDataCleaner(GalleryItem item) {
			this.item = item;
		}

		public void run() {
			item.setData(ANIMATION_DATA, null);
		}
	}

	/**
	 * Adapter to set the color of the background of a GalleryItem
	 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
	 *
	 */
	public class GalleryItemBackgroundColorAdapter implements IColoredObject {
		GalleryItem item;

		public GalleryItemBackgroundColorAdapter(GalleryItem item) {
			this.item = item;
		}

		public Color getColor() {
			return item.getBackground();
		}

		public void setColor(Color c) {
			item.setBackground(c);
		}

	}

	/**
	 * Adds hover effect to a Gallery instance.
	 * 
	 * @param gallery
	 * @param background
	 * @param hover
	 */
	public HoverListener(Gallery gallery, Color background, Color hover,
			int durationIn, int durationOut) {
		this.setBackgroundColor(background);
		this.setHoverColor(hover);
		this.durationIn = durationIn;
		this.durationOut = durationOut;
		this.gallery = gallery;
		gallery.addMouseMoveListener(this);
		gallery.addMouseTrackListener(this);
		gallery.addMouseWheelListener(this);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getHoverColor() {
		return hoverColor;
	}

	public void setHoverColor(Color hoverColor) {
		this.hoverColor = hoverColor;
	}

	public void mouseMove(MouseEvent e) {
		updateHover(e);
	}

	public void mouseEnter(MouseEvent e) {
		updateHover(e);
	}

	public void mouseExit(MouseEvent e) {
		animateBackgroundColor(current, backgroundColor, durationOut);
	}

	public void mouseHover(MouseEvent e) {
		// Nothing to do for this event
	}

	/**
	 * Animate the color change using the animation framework of the Gallery widget.
	 * @param item
	 * @param color
	 * @param duration
	 */
	private void animateBackgroundColor(final GalleryItem item, Color color,
			int duration) {
		
		if (item != null) {
			// Cancel any color animation on this item
			Object o = item.getData(ANIMATION_DATA);
			if (o != null && o instanceof AnimationRunner) {
				((AnimationRunner) o).cancel();
			}

			// Get current background color (backgrounfColor is default)
			Color bg = item.getBackground();
			if (bg == null)
				bg = backgroundColor;

			// Start animation
			AnimationRunner animation = new AnimationRunner();
			item.setData(ANIMATION_DATA, animation);
			animation.runEffect(new SetColorEffect(new GalleryItemBackgroundColorAdapter(item), bg, color, duration,
					new ExpoOut(), new AnimationDataCleaner(item),
					new AnimationDataCleaner(item)));
		}

	}

	/**
	 * Change the current hovered item according to mouse position.
	 * 
	 * @param e
	 */
	private void updateHover(MouseEvent e) {
		GalleryItem item = ((Gallery) e.widget).getItem(new Point(e.x, e.y));
		if (item != current) {
			animateBackgroundColor(current, backgroundColor, durationOut);
			animateBackgroundColor(item, hoverColor, durationIn);
			current = item;
		}
	}

	public void mouseScrolled(MouseEvent e) {
		updateHover(e);
	}
}
