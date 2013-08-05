package org.eclipse.nebula.paperclips.core;

/**
 * 
 * 
 */
public interface LayerEntryIterator {

	PrintIterator getTarget();

	int getAlignment();

	LayerEntryIterator copy();
}