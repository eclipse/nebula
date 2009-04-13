package org.eclipse.nebula.jface.viewer.radiogroup;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;

public class RadioGroupViewerSupport {
	/**
	 * Binds the viewer to the specified input, using the specified label
	 * property to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static void bind(RadioGroupViewer viewer, IObservableList input,
			IValueProperty labelProperty) {
		ObservableListContentProvider contentProvider = new ObservableListContentProvider(
				new RadioGroupViewerUpdater(viewer));
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider(labelProperty
				.observeDetail(contentProvider.getKnownElements())));
		viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * property to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static void bind(RadioGroupViewer viewer, IObservableSet input,
			IValueProperty labelProperty) {
		ObservableSetContentProvider contentProvider = new ObservableSetContentProvider(
				new RadioGroupViewerUpdater(viewer));
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider(labelProperty
				.observeDetail(contentProvider.getKnownElements())));
		viewer.setInput(input);
	}
}
