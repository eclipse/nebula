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
package org.eclipse.swt.nebula.examples.parts;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.nebula.examples.ExamplesView;
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
	Image eclipseImage = null;
	Image bgImage = null;

	Gallery g = null;

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

	public Control createControl(Composite parent) {
		System.out.println("Create Control");
		int style = SWT.NONE;

		if (bMulti.getSelection())
			style |= SWT.MULTI;

		if (bHScroll.getSelection())
			style |= SWT.H_SCROLL;

		if (bVScroll.getSelection())
			style |= SWT.V_SCROLL;

		g = new Gallery(parent, style);

		if (groupRenderer != null) {
			groupRenderer.dispose();
		}
		groupRenderer = getGroupRenderer();
		g.setGroupRenderer(groupRenderer);

		if (itemRenderer != null) {
			itemRenderer.dispose();
		}
		g.setItemRenderer(getItemRenderer());

		if (bAnimation.getSelection()) {
			new ScrollingSmoother(g, new ExpoOut()).smoothControl(true);
		}

		// Create item image
		if (eclipseImage == null) {
			eclipseImage = ExamplesView.getImage("icons/woman3.png");
		}
		if (bgImage == null) {
			bgImage = ExamplesView.getImage("icons/background.PNG");
		}

		g.setLowQualityOnUserAction(bLayoutLowQualityOnAction.getSelection());
		// Add items.
		this.clearAndPopulateGallery(g);

		return g;
	}

	private AbstractGalleryItemRenderer getItemRenderer() {

		if (cItemRenderer.getSelectionIndex() == 0) {

			DefaultGalleryItemRenderer renderer = new DefaultGalleryItemRenderer();

			renderer.setShowLabels(bItemLabel.getSelection());
			renderer.setDropShadowsSize(sItemDropShadowSize.getSelection());
			renderer.setDropShadows(bItemDropShadow.getSelection());
			return renderer;
		} else {
			ListItemRenderer renderer = new ListItemRenderer();

			renderer.setShowLabels(bItemLabel.getSelection());
			renderer.setDropShadowsSize(sItemDropShadowSize.getSelection());
			renderer.setDropShadows(bItemDropShadow.getSelection());
		
			return renderer;
		}

	}

	private AbstractGridGroupRenderer getGroupRenderer() {
		DefaultGalleryGroupRenderer groupRenderer = new DefaultGalleryGroupRenderer();
		groupRenderer.setItemWidth(this.itemWidthScale.getSelection());
		groupRenderer.setItemHeight(this.itemHeightScale.getSelection());
		groupRenderer.setMinMargin(this.marginsScale.getSelection());

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
			groupRenderer.setAnimationLength(sAnimationDuration.getSelection());
		} else {
			groupRenderer.setAnimation(false);
		}

		groupRenderer.setAutoMargin(bLayoutAutoMargin.getSelection());
		groupRenderer.setAlwaysExpanded(bLayoutAlwaysExpanded.getSelection());

		return groupRenderer;

	}

	private void clearAndPopulateGallery(Gallery g) {
		System.out.println("clearAndPopulateGallery : remove");

		g.removeAll();
		System.out.println("clearAndPopulateGallery : populate");

		if ((g.getStyle() & SWT.VIRTUAL) == 0) {
			this.populateGalleryWithGroups(g);
		} else {
			// Virtual mode.
			// TODO: Virtual mode example
		}
		System.out.println("clearAndPopulateGallery : done");

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
				gi1.setImage(eclipseImage);
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
					gi2.setImage(eclipseImage);
				} else {
					gi2.setImage(bgImage);
				}
				gi2.setText("Eclipse " + i + " " + j + ".jpg");
				if (bItemDescription.getSelection()) {
					gi2.setText(1, "Image description");
				}
			}
		}
	}

	public String[] createLinks() {
		String[] links = new String[3];

		links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/gallery/gallery.php\">Gallery Home Page</a>";

		links[1] = "<a href=\"http://www.eclipse.org/nebula/widgets/gallery/snippets.php\">Snippets</a>";

		links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=Gallery&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";

		return links;
	}

	class ParamSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			recreateExample();
		}

	}

	private Button createButton(Composite parent, int style, String text,
			boolean selected) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setSelection(selected);
		button.addSelectionListener(new ParamSelectionListener());
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

		bMulti = createButton(styleGroup, SWT.CHECK, "SWT.MULTI", false);
		bVScroll = createButton(styleGroup, SWT.RADIO, "SWT.V_SCROLL", true);
		bHScroll = createButton(styleGroup, SWT.RADIO, "SWT.H_SCROLL", false);
	}

	private void createAnimationGroup(Composite parent) {
		Group animationGroup = createEmptyGroup(parent, "Animation");
		animationGroup.setLayout(new RowLayout());

		bAnimation = createButton(animationGroup, SWT.CHECK, "Animations",
				false);

		cAnimationMovement = new Combo(animationGroup, SWT.READ_ONLY);
		cAnimationMovement.setItems(new String[] { "ExpoOut", "BounceOut",
				"ElasticOut", "LinearInOut" });
		cAnimationMovement.setText("ExpoOut");
		cAnimationMovement.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (g != null) {
					g.setGroupRenderer(getGroupRenderer());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		sAnimationDuration = new Spinner(animationGroup, SWT.NONE);
		sAnimationDuration.setMinimum(250);
		sAnimationDuration.setMaximum(5000);
		sAnimationDuration.setIncrement(100);
		sAnimationDuration.setSelection(500);
		sAnimationDuration.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (g != null) {
					g.setGroupRenderer(getGroupRenderer());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createDataGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Data");
		dataGroup.setLayout(new RowLayout());

		bGroupImage = createButton(dataGroup, SWT.CHECK, "Group image", false);
		bGroupDescription = createButton(dataGroup, SWT.CHECK,
				"Group descriptions", false);
		bItemDescription = createButton(dataGroup, SWT.CHECK,
				"Item descriptions", false);
	}

	private void createLayoutGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Layout");
		dataGroup.setLayout(new RowLayout());

		bLayoutAutoMargin = createButton(dataGroup, SWT.CHECK, "Auto Margins",
				false);
		bLayoutAlwaysExpanded = createButton(dataGroup, SWT.CHECK,
				"Always expanded", false);

		bLayoutLowQualityOnAction = createButton(dataGroup, SWT.CHECK,
				"Low quality on user action", false);

	}

	private void createItemParametersGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Item parameters");
		dataGroup.setLayout(new RowLayout());

		cItemRenderer = new Combo(dataGroup, SWT.READ_ONLY);
		cItemRenderer.setItems(new String[] { "Icon", "List" });
		cItemRenderer.setText("Icon");
		cItemRenderer.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (g != null) {
					g.setItemRenderer(getItemRenderer());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		bItemDropShadow = createButton(dataGroup, SWT.CHECK, "Drop shadow",
				false);

		sItemDropShadowSize = new Spinner(dataGroup, SWT.NONE);
		sItemDropShadowSize.setMinimum(0);
		sItemDropShadowSize.setMaximum(20);
		sItemDropShadowSize.setIncrement(1);
		sItemDropShadowSize.setSelection(5);
		sItemDropShadowSize.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (g != null) {
					g.setItemRenderer(getItemRenderer());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		bItemLabel = createButton(dataGroup, SWT.CHECK, "Display labels", false);
	}

	private void createGroupParametersGroup(Composite parent) {
		Group dataGroup = createEmptyGroup(parent, "Group parameters");
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(
				dataGroup);

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
		createLayoutGroup(parent);
		createGroupParametersGroup(parent);
		createItemParametersGroup(parent);

		Button b = new Button(parent, SWT.NONE);
		b.setText("deselectAll");
		b.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

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
