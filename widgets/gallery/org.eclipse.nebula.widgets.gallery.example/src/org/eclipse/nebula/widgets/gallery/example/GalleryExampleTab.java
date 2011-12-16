/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery.example;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.animation.ScrollingSmoother;
import org.eclipse.nebula.animation.movement.BounceOut;
import org.eclipse.nebula.animation.movement.ElasticOut;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.nebula.widgets.gallery.AbstractGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.AbstractGridGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.examples.ExamplesView;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

/**
 * Demonstrates the Gallery widget.
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class GalleryExampleTab extends AbstractExampleTab {

	class WidgetParamSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			recreateExample();
		}

	}

	class GroupRendererParamSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			if (g != null) {
				g.setGroupRenderer(getGroupRenderer());
			}
		}

	}

	class ItemRendererParamSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			if (g != null) {
				g.setItemRenderer(getItemRenderer());
			}
		}

	}

	class ContentParamSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			clearAndPopulateGallery(g);
		}

	}

	WidgetParamSelectionListener widgetParamSelectionListener = new WidgetParamSelectionListener();
	GroupRendererParamSelectionListener groupParamSelectionListener = new GroupRendererParamSelectionListener();
	ItemRendererParamSelectionListener itemRendererParamSelectionListener = new ItemRendererParamSelectionListener();
	ContentParamSelectionListener contentParamSelectionListener = new ContentParamSelectionListener();

	Image womanImage = null;
	Image bgImage = null;
	Image eclipseImage = null;

	Gallery g = null;
	ScrollingSmoother scrollingSmoother;

	// Style options

	Button bMulti = null;

	Button bHScroll = null;

	Button bVScroll = null;

	// Animation options
	Button bAnimation = null;

	// Data options
	Button bGroupImage = null;
	Button bGroupDescription = null;
	Button bItemDescription = null;

	// Size options
	Scale scale = null;

	Scale itemWidthScale = null;

	Scale itemHeightScale = null;

	Scale marginsScale = null;

	DefaultGalleryItemRenderer itemRenderer = null;

	AbstractGridGroupRenderer groupRenderer = null;

	private Button bLayoutAutoMargin;

	private Button bLayoutAlwaysExpanded;

	private Combo cAnimationMovement;

	private Spinner sAnimationDuration;

	private Button bLayoutLowQualityOnAction;

	private Button bItemDropShadow;
	private Spinner sItemDropShadowSize;
	private Button bItemLabel;
	private Combo cItemRenderer;
	private Button bDecoratorLeft;
	private Button bDecoratorUp;
	private Button bDecoratorRight;
	private Button bDecoratorDown;
	private Spinner sDecoratorNumber;
	private Combo cGroupRenderer;

	public Control createControl(Composite parent) {
		int style = SWT.NONE;

		if (bMulti.getSelection())
			style |= SWT.MULTI;

		if (bHScroll.getSelection())
			style |= SWT.H_SCROLL;

		if (bVScroll.getSelection())
			style |= SWT.V_SCROLL;

		g = new Gallery(parent, style);
		scrollingSmoother = new ScrollingSmoother(g, new ExpoOut());
		scrollingSmoother.smoothControl(bAnimation.getSelection());

		if (groupRenderer != null) {
			groupRenderer.dispose();
		}
		groupRenderer = getGroupRenderer();
		g.setGroupRenderer(groupRenderer);

		if (itemRenderer != null) {
			itemRenderer.dispose();
		}
		g.setItemRenderer(getItemRenderer());

		// Create item image
		if (womanImage == null) {
			womanImage = ExamplesView.getImage("icons/woman3.png");
		}
		if (bgImage == null) {
			bgImage = ExamplesView.getImage("icons/background_small.png");
		}

		if (eclipseImage == null) {
			eclipseImage = ExamplesView.getImage("icons/eclipse.png");
		}

		g.setLowQualityOnUserAction(bLayoutLowQualityOnAction.getSelection());
		// Add items.
		this.clearAndPopulateGallery(g);

		return g;
	}

	private AbstractGalleryItemRenderer getItemRenderer() {
		AbstractGalleryItemRenderer result = null;
		
		if (cItemRenderer.getSelectionIndex() == 0) {
			DefaultGalleryItemRenderer renderer = new DefaultGalleryItemRenderer();
			renderer.setShowLabels(bItemLabel.getSelection());
			renderer.setDropShadowsSize(sItemDropShadowSize.getSelection());
			renderer.setDropShadows(bItemDropShadow.getSelection());
			result= renderer;
		} else {
			ListItemRenderer renderer = new ListItemRenderer();
			renderer.setShowLabels(bItemLabel.getSelection());
			renderer.setDropShadowsSize(sItemDropShadowSize.getSelection());
			renderer.setDropShadows(bItemDropShadow.getSelection());
			result= renderer;
		}
		
		return result;

	}

	private AbstractGridGroupRenderer getGroupRenderer() {

		AbstractGridGroupRenderer result = null;
		if (cGroupRenderer.getSelectionIndex() == 0) {
			DefaultGalleryGroupRenderer groupRenderer = new DefaultGalleryGroupRenderer();

			if (bAnimation.getSelection()) {
				// Animation
				groupRenderer.setAnimation(true);

				// Movement
				IMovement m = null;
				switch (cAnimationMovement.getSelectionIndex()) {
				case 1:
					m = new BounceOut();
					break;
				case 2:
					m = new ElasticOut();
					break;
				case 3:
					m = new LinearInOut();
					break;
				default:
					m = new ExpoOut();
					break;
				}
				groupRenderer.setAnimationCloseMovement(m);
				groupRenderer.setAnimationOpenMovement(m);

				// Length
				groupRenderer.setAnimationLength(sAnimationDuration
						.getSelection());
			} else {
				groupRenderer.setAnimation(false);
			}
			result = groupRenderer;
		} else {
			NoGroupRenderer groupRenderer = new NoGroupRenderer();
			result = groupRenderer;
		}
		
		result.setItemWidth(this.itemWidthScale.getSelection());
		result.setItemHeight(this.itemHeightScale.getSelection());
		result.setMinMargin(this.marginsScale.getSelection());

		result.setAutoMargin(bLayoutAutoMargin.getSelection());
		result.setAlwaysExpanded(bLayoutAlwaysExpanded.getSelection());

		scrollingSmoother.smoothControl(bAnimation.getSelection());

		return result;

	}

	private void clearAndPopulateGallery(Gallery g) {
		g.removeAll();

		if ((g.getStyle() & SWT.VIRTUAL) == 0) {
			this.populateGalleryWithGroups(g);
		} else {
			// Virtual mode.
			// TODO: Virtual mode example
		}

	}

	/**
	 * Add 10 groups containing 10 to 100 items each.
	 * 
	 * @param g
	 */
	private void populateGalleryWithGroups(Gallery g) {
		for (int i = 0; i < 10; i++) {
			GalleryItem gi1 = new GalleryItem(g, SWT.None);
			gi1.setText("Group " + i + ".jpg");

			if (bGroupImage.getSelection()) {
				gi1.setImage(womanImage);
			}

			if (bGroupDescription.getSelection()) {
				gi1.setText(1, "Group description");
			}

			if (i % 2 == 0) {
				gi1.setExpanded(true);
			}

			for (int j = 0; j < (10 * (i + 1)); j++) {
				GalleryItem gi2 = new GalleryItem(gi1, SWT.None);
				if (j % 2 == 0) {
					gi2.setImage(womanImage);
				} else {
					gi2.setImage(bgImage);
				}
				gi2.setText("Eclipse " + i + " " + j + ".jpg");
				if (bItemDescription.getSelection()) {
					gi2.setText(1, "Image description");
				}

				if (bDecoratorLeft.getSelection()) {
					gi2.setData(DefaultGalleryItemRenderer.OVERLAY_TOP_LEFT,
							getDecoratorImage(eclipseImage, sDecoratorNumber
									.getSelection()));
				}
				if (bDecoratorUp.getSelection()) {
					gi2.setData(DefaultGalleryItemRenderer.OVERLAY_TOP_RIGHT,
							getDecoratorImage(eclipseImage, sDecoratorNumber
									.getSelection()));
				}
				if (bDecoratorRight.getSelection()) {
					gi2.setData(
							DefaultGalleryItemRenderer.OVERLAY_BOTTOM_RIGHT,
							getDecoratorImage(eclipseImage, sDecoratorNumber
									.getSelection()));
				}
				if (bDecoratorDown.getSelection()) {
					gi2.setData(DefaultGalleryItemRenderer.OVERLAY_BOTTOM_LEFT,
							getDecoratorImage(eclipseImage, sDecoratorNumber
									.getSelection()));
				}
			}
		}
	}

	private Object getDecoratorImage(Image img, int nb) {
		switch (nb) {
		case 0:
			return null;

		case 1:
			return img;

		default:
			Image[] result = new Image[nb];
			for (int i = 0; i < nb; i++) {
				result[i] = img;
			}
			return result;
		}

	}

	public String[] createLinks() {
		String[] links = new String[4];

		links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/gallery/gallery.php\">Gallery Home Page</a>";

		links[1] = "<a href=\"http://www.eclipse.org/nebula/snippets.php#Gallery\">Snippets</a>";

		links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=Gallery&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";

		links[3] = "<a href=\"http://www.eclipse.org/projects/project-plan.php?projectid=technology.nebula\">Projet plan</a>";

		return links;
	}

	private Button createButton(Composite parent, int style, String text,
			boolean selected, boolean createExampleOnChange) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setSelection(selected);
		if (createExampleOnChange) {
			button.addSelectionListener(new WidgetParamSelectionListener());
		}
		return button;
	}

	private Group createEmptyGroup(Composite parent, String text) {
		Group styleGroup = new Group(parent, SWT.NONE);
		styleGroup.setText(text);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = SWT.FILL;
		styleGroup.setLayoutData(gd);

		return styleGroup;
	}

	private void createStyleGroup(Composite parent) {
		Group styleGroup = createEmptyGroup(parent, "Style");
		styleGroup.setLayout(new RowLayout());

		bMulti = createButton(styleGroup, SWT.CHECK, "SWT.MULTI", false, true);
		bVScroll = createButton(styleGroup, SWT.RADIO, "SWT.V_SCROLL", true,
				true);
		bHScroll = createButton(styleGroup, SWT.RADIO, "SWT.H_SCROLL", false,
				true);
	}

	private void createAnimationGroup(Composite parent) {
		Group animationGroup = createEmptyGroup(parent, "Animation");
		animationGroup.setLayout(new RowLayout());

		bAnimation = createButton(animationGroup, SWT.CHECK, "Animations",
				false, false);
		bAnimation.addSelectionListener(groupParamSelectionListener);

		cAnimationMovement = new Combo(animationGroup, SWT.READ_ONLY);
		cAnimationMovement.setItems(new String[] { "ExpoOut", "BounceOut",
				"ElasticOut", "LinearInOut" });
		cAnimationMovement.setText("ExpoOut");
		cAnimationMovement.addSelectionListener(groupParamSelectionListener);

		sAnimationDuration = new Spinner(animationGroup, SWT.NONE);
		sAnimationDuration.setMinimum(250);
		sAnimationDuration.setMaximum(5000);
		sAnimationDuration.setIncrement(100);
		sAnimationDuration.setSelection(500);
		sAnimationDuration.addSelectionListener(groupParamSelectionListener);
	}

	private void createDataGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Data");
		dataGroup.setLayout(new RowLayout());

		bGroupImage = createButton(dataGroup, SWT.CHECK, "Group image", false,
				true);
		bGroupDescription = createButton(dataGroup, SWT.CHECK,
				"Group descriptions", false, true);
		bItemDescription = createButton(dataGroup, SWT.CHECK,
				"Item descriptions", false, true);
	}

	private void createLayoutGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Layout");
		dataGroup.setLayout(new RowLayout());

		bLayoutAutoMargin = createButton(dataGroup, SWT.CHECK, "Auto Margins",
				false, true);
		bLayoutAlwaysExpanded = createButton(dataGroup, SWT.CHECK,
				"Always expanded", false, true);

		bLayoutLowQualityOnAction = createButton(dataGroup, SWT.CHECK,
				"Low quality on user action", false, true);

	}

	private void createDecoratorsGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Decorators");
		dataGroup.setLayout(new RowLayout());

		sDecoratorNumber = new Spinner(dataGroup, SWT.NONE);
		sDecoratorNumber.setMinimum(1);
		sDecoratorNumber.setMaximum(5);
		sDecoratorNumber.setIncrement(1);
		sDecoratorNumber.setSelection(1);
		sDecoratorNumber.addSelectionListener(contentParamSelectionListener);

		bDecoratorLeft = createButton(dataGroup, SWT.CHECK, "Top Left", false,
				false);
		bDecoratorLeft.addSelectionListener(contentParamSelectionListener);
		bDecoratorUp = createButton(dataGroup, SWT.CHECK, "Top Right", false,
				false);
		bDecoratorUp.addSelectionListener(contentParamSelectionListener);
		bDecoratorRight = createButton(dataGroup, SWT.CHECK, "Bottom Right",
				false, false);
		bDecoratorRight.addSelectionListener(contentParamSelectionListener);
		bDecoratorDown = createButton(dataGroup, SWT.CHECK, "Bottom Left",
				false, false);
		bDecoratorDown.addSelectionListener(contentParamSelectionListener);
	}

	private void createItemParametersGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Item parameters");
		dataGroup.setLayout(new RowLayout());

		cItemRenderer = new Combo(dataGroup, SWT.READ_ONLY);
		cItemRenderer.setItems(new String[] { "Icon", "List" });
		cItemRenderer.setText("Icon");
		cItemRenderer.addSelectionListener(itemRendererParamSelectionListener);

		bItemDropShadow = createButton(dataGroup, SWT.CHECK, "Drop shadow",
				false, true);

		sItemDropShadowSize = new Spinner(dataGroup, SWT.NONE);
		sItemDropShadowSize.setMinimum(0);
		sItemDropShadowSize.setMaximum(20);
		sItemDropShadowSize.setIncrement(1);
		sItemDropShadowSize.setSelection(5);
		sItemDropShadowSize
				.addSelectionListener(itemRendererParamSelectionListener);

		bItemLabel = createButton(dataGroup, SWT.CHECK, "Display labels",
				false, true);
	}

	private void createGroupParametersGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Group parameters");
		GridLayoutFactory.swtDefaults().margins(3, 3).numColumns(3).applyTo(
				dataGroup);

		cGroupRenderer = new Combo(dataGroup, SWT.READ_ONLY);
		cGroupRenderer
				.setItems(new String[] { "Show groups", "Hide groups" });
		cGroupRenderer.setText("Show groups");
		cGroupRenderer.addSelectionListener(groupParamSelectionListener);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		cGroupRenderer.setLayoutData(gridData);
		
		// Scale : set item size
		scale = createScale(dataGroup, "Item size", 16, 512, 16, 64);
		scale.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				if (g != null) {
					groupRenderer.setItemSize(scale.getSelection(), scale
							.getSelection());
					itemWidthScale.setSelection(scale.getSelection());
					itemHeightScale.setSelection(scale.getSelection());
					g.setGroupRenderer(groupRenderer);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

		});

		// Scale : set item width
		this.itemWidthScale = createScale(dataGroup, "Item width", 16, 512, 16,
				64);
		itemWidthScale.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				if (g != null) {
					groupRenderer.setItemWidth(itemWidthScale.getSelection());
					g.setGroupRenderer(groupRenderer);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

		});

		// Scale : set item height
		this.itemHeightScale = createScale(dataGroup, "Item height", 16, 512,
				16, 64);
		itemHeightScale.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				if (g != null) {
					groupRenderer.setItemHeight(itemHeightScale.getSelection());
					g.setGroupRenderer(groupRenderer);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

		});

		// Scale : set margins size
		this.marginsScale = createScale(dataGroup, "Margins", 0, 128, 16, 10);
		marginsScale.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if (g != null) {
					groupRenderer.setMinMargin(marginsScale.getSelection());
					g.setGroupRenderer(groupRenderer);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

	}

	public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(
				parent);
		createStyleGroup(parent);
		createAnimationGroup(parent);
		createDataGroup(parent);
		createDecoratorsGroup(parent);
		createLayoutGroup(parent);
		createGroupParametersGroup(parent);
		createItemParametersGroup(parent);

		Button b = new Button(parent, SWT.NONE);
		b.setText("deselectAll");
		b.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				g.deselectAll();
			}

		});

	}

	private Scale createScale(Composite parent, String text, int min, int max,
			int increment, int value) {
		GridData gridData = new GridData();

		Label l = new Label(parent, SWT.NONE);
		l.setText(text);
		gridData.horizontalSpan = 1;
		l.setLayoutData(gridData);

		Scale scale = new Scale(parent, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		scale.setLayoutData(gridData);

		scale.setMaximum(max);
		scale.setMinimum(min);
		scale.setPageIncrement(increment);
		scale.setSelection(value);

		return scale;
	}
}
