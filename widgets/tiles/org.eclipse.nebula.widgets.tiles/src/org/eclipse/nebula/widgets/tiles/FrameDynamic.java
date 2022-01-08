/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Fabian Prasser - Initial API and implementation
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/

package org.eclipse.nebula.widgets.tiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Color;

/**
 * This class implements a dynamic frame that implements a transition between two static frames
 *
 * @author Fabian Prasser
 *
 * @param <T>
 */
class FrameDynamic<T> extends Frame<T>{

	/** Source frame*/
	private final FrameStatic<T>          source;
	/** Target frame*/
	private final FrameStatic<T>          target;

	/** Layout data*/
	private final int                           defaultWidthSource;
	/** Layout data*/
	private final int                           defaultHeightSource;
	/** Layout data*/
	private final int                           defaultWidthTarget;
	/** Layout data*/
	private final int                           defaultHeightTarget;

	/** Element to tile*/
	private final Map<T, Tile<T>>               targetMap = new HashMap<>();
	/** Element to tile*/
	private final Map<T, Tile<T>>               sourceMap = new HashMap<>();
	/** List of elements*/
	private final List<T>                       list      = new ArrayList<>();
	/** List of tiles*/
	private final List<Tile<T>>                 rendered  = new ArrayList<>();
	/** Background*/
	private Color                         background;
	/** Transition function*/
	private final FrameTransitionFunction transition;

	/** Timer*/
	protected int                         time      = 0;
	/** Timestamp*/
	private long                          previous  = 0;

	/**
	 * Creates a new instance
	 * @param tiles
	 * @param source
	 * @param target
	 */
	FrameDynamic(final Tiles<T> tiles, final FrameStatic<T> source, final FrameStatic<T> target) {

		super(tiles);

		this.transition = new FrameTransitionFunction(1,0.25,0,0.75); //(1,0.25,0,0.75)
		this.source = source;
		this.target = target;

		this.defaultWidthSource = source.getTiles() != null && !source.getTiles().isEmpty() ? source.getTiles().get(0).width : 0;
		this.defaultHeightSource = source.getTiles() != null && !source.getTiles().isEmpty() ? source.getTiles().get(0).height : 0;
		this.defaultWidthTarget = target.getTiles() != null && !target.getTiles().isEmpty() ? target.getTiles().get(0).width : 0;
		this.defaultHeightTarget = target.getTiles() != null && !target.getTiles().isEmpty() ? target.getTiles().get(0).height : 0;

		// Create list and map
		final Set<T> all = new HashSet<>();
		for (final Tile<T> t : target.getTiles()) {
			targetMap.put(t.item, t);
			if (all.add(t.item)) {
				list.add(t.item);
			}
		}
		for (final Tile<T> t : source.getTiles()) {
			sourceMap.put(t.item, t);
			if (all.add(t.item)) {
				list.add(t.item);
			}
		}

		// Use all decorators in source
		this.source.getDecoratorLineWidth().use();
		this.source.getDecoratorLineStyle().use();
		this.source.getDecoratorForegroundColor().use();
		this.source.getDecoratorBackgroundColor().use();
		this.source.getDecoratorLineColor().use();
		this.source.getDecoratorLabel().use();

		// Use all decorators in target
		this.target.getDecoratorLineWidth().use();
		this.target.getDecoratorLineStyle().use();
		this.target.getDecoratorForegroundColor().use();
		this.target.getDecoratorBackgroundColor().use();
		this.target.getDecoratorLineColor().use();
		this.target.getDecoratorLabel().use();

		// Update
		this.update();
	}

	@Override
	public void dispose() {

		// Dispose in source
		this.source.getDecoratorLineWidth().free();
		this.source.getDecoratorLineStyle().free();
		this.source.getDecoratorForegroundColor().free();
		this.source.getDecoratorBackgroundColor().free();
		this.source.getDecoratorLineColor().free();
		this.source.getDecoratorLabel().free();
		this.source.dispose();

		// Dispose in target
		this.target.getDecoratorLineWidth().free();
		this.target.getDecoratorLineStyle().free();
		this.target.getDecoratorForegroundColor().free();
		this.target.getDecoratorBackgroundColor().free();
		this.target.getDecoratorLineColor().free();
		this.target.getDecoratorLabel().free();
		this.target.dispose();

	}

	@Override
	public Color getBackground() {
		return background;
	}

	/**
	 * Returns the source
	 * @return
	 */
	public FrameStatic<T> getSource() {
		return source;
	}

	/**
	 * Returns the target
	 * @return
	 */
	public FrameStatic<T> getTarget() {
		return target;
	}

	@Override
	public List<Tile<T>> getTiles() {
		return rendered;
	}

	/**
	 * Tick
	 */
	public void tick(){
		if (previous == 0) {
			previous = System.currentTimeMillis();
		} else {
			time += System.currentTimeMillis() - previous;
			if (time > tiles.getAnimationSettings().getDuration()) {
				tiles.removeFrame();
			}
		}
	}

	@Override
	public void update() {

		// Blend factor
		final double duration = tiles.getAnimationSettings().getDuration();
		double factor = time / duration;
		factor = factor <= 1.0d ? factor : 1.0d;
		factor = transition.bezier(factor, duration);

		// TODO
		background = target.getBackground();

		// Prepare
		rendered.clear();

		// Render each tile
		for (final T element : list) {
			rendered.add(blend(sourceMap.get(element), targetMap.get(element), factor));
		}
	}

	/**
	 * Merges two tiles
	 * @param frame
	 * @param source
	 * @param target
	 * @return
	 */
	private Tile<T> blend(final Tile<T> source, final Tile<T> target, final double factor) {

		double sourcex, sourcey, sourcewidth, sourceheight;
		double targetx, targety, targetwidth, targetheight;

		String label;
		int lineWidth, lineStyle;
		Color lineColor, foregroundColor, backgroundColor;
		T element;


		// Prepare target bounds
		if (target != null) {
			targetwidth = target.width;
			targetheight = target.height;
			targetx = target.x;
			targety = target.y;
		} else {
			targetwidth = defaultWidthTarget;
			targetheight = defaultHeightTarget;
			targetx = -defaultWidthTarget;
			targety = -defaultHeightTarget;
		}

		// Prepare source bounds
		if (source != null) {
			sourcewidth = source.width;
			sourceheight = source.height;
			sourcex = source.x;
			sourcey = source.y;
		} else {
			sourcewidth = defaultWidthSource;
			sourceheight = defaultHeightSource;
			sourcex = - defaultWidthSource;
			sourcey = -defaultHeightSource;
		}

		// Prepare other properties
		if (target != null) {
			element = target.item;
			lineWidth = target.lineWidth;
			lineStyle = target.lineStyle;
			lineColor = target.lineColor;
			foregroundColor = target.foregroundColor;
			backgroundColor = target.backgroundColor;
			label = target.label;
		} else {
			element = source.item;
			lineWidth = source.lineWidth;
			lineStyle = source.lineStyle;
			lineColor = source.lineColor;
			foregroundColor = source.foregroundColor;
			backgroundColor = source.backgroundColor;
			label = source.label;
		}


		// Blend
		if (sourcex != targetx) {
			targetx = sourcex * (1.0d - factor) + targetx * factor;
		}

		if (sourcey != targety) {
			targety = sourcey * (1.0d - factor) + targety * factor;
		}

		if (sourcewidth != targetwidth) {
			targetwidth = sourcewidth * (1.0d - factor) + targetwidth * factor;
		}

		if (sourceheight != targetheight) {
			targetheight = sourceheight * (1.0d - factor) + targetheight * factor;
		}

		// Return
		return new Tile<>( element, (int)targetx, (int)targety, (int)targetwidth, (int)targetheight,
				label, lineWidth, lineStyle, lineColor, foregroundColor, backgroundColor);
	}

	@Override
	protected Comparator<T> getComparator() {
		return target.getComparator();
	}

	@Override
	protected DecoratorColor<T> getDecoratorBackgroundColor() {
		return target.getDecoratorBackgroundColor();
	}

	@Override
	protected DecoratorColor<T> getDecoratorForegroundColor() {
		return target.getDecoratorForegroundColor();
	}

	@Override
	protected DecoratorString<T> getDecoratorLabel() {
		return target.getDecoratorLabel();
	}

	@Override
	protected DecoratorColor<T> getDecoratorLineColor() {
		return target.getDecoratorLineColor();
	}

	@Override
	protected DecoratorInteger<T> getDecoratorLineStyle() {
		return target.getDecoratorLineStyle();
	}

	@Override
	protected DecoratorInteger<T> getDecoratorLineWidth() {
		return target.getDecoratorLineWidth();
	}

	@Override
	protected Filter<T> getFilter() {
		return target.getFilter();
	}

	@Override
	protected List<T> getItems() {
		return new ArrayList<>(target.getItems());
	}
}
