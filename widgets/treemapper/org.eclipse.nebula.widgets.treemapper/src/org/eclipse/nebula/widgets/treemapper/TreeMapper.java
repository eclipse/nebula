/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
*
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/

package org.eclipse.nebula.widgets.treemapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.treemapper.internal.LinkFigure;
import org.eclipse.nebula.widgets.treemapper.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDragSourceEffect;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A TreeMapper is a composite viewer the creates 2 {@link TreeViewer} (left and right)
 * and an area to display mappings between tree nodes.
 * It relies on a {@link ISemanticTreeMapperSupport} to create your business mapping objects,
 * and to resolve the bounds of a mapping object to object that are provided in the trees.
 *
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 * @since 0.1.0
 * @noextend This class is not intended to be subclassed by clients.
 *
 * @param <M> The type of the business <b>M<b>apping object
 * @param <L> The type of the left bound of the mapping (as provided by <b>L</b>eft {@link ITreeContentProvider})
 * @param <R> The type of the left bound of the mapping (as provided by <b>R</>ight {@link ITreeContentProvider})
 */
public class TreeMapper<M, L, R> implements ISelectionProvider {

	private SashForm control;
	private TreeMapperUIConfigProvider uiConfig;

	private TreeViewer leftTreeViewer;
	private TreeViewer rightTreeViewer;
	private TreeItem leftTopItem;
	private TreeItem rightTopItem;

	private Canvas linkCanvas;
	private LightweightSystem linkSystem;
	private Figure linkRootFigure;
	private boolean canvasNeedRedraw;

	private List<M> mappings;
	private Map<LinkFigure, M> figuresToMappings;
	private Map<M, LinkFigure> mappingsToFigures;
	private LinkFigure selectedFigure;
	private M selectedMapping;
	private ISemanticTreeMapperSupport<M, L, R> semanticSupport;
	private IFigure warningFigure;


	public TreeMapper(Composite parent, ISemanticTreeMapperSupport<M, L, R> semanticSupport, TreeMapperUIConfigProvider uiConfig) {
		this.uiConfig = uiConfig;
		this.semanticSupport = semanticSupport;
		control = new SashForm(parent, SWT.HORIZONTAL);
		control.setLayout(new FillLayout());
		// left
		leftTreeViewer = new TreeViewer(control);
		//center
		linkCanvas = new Canvas(control, SWT.NONE);
		linkCanvas.setLayout(new FillLayout());
		linkCanvas.setBackground(ColorConstants.white);
		linkSystem = new LightweightSystem(linkCanvas);
		linkRootFigure = new Figure();
		linkRootFigure.setLayoutManager(new XYLayout());
		linkSystem.setContents(linkRootFigure);
		// right
		rightTreeViewer = new TreeViewer(control);

		figuresToMappings = new HashMap<>();
		mappingsToFigures = new HashMap<>();

		// Resize
		ControlListener resizeListener = new ControlListener() {
			public void controlResized(ControlEvent e) {
				canvasNeedRedraw = true;
			}
			public void controlMoved(ControlEvent e) {
				canvasNeedRedraw = true;
			}
		};
		leftTreeViewer.getTree().addControlListener(resizeListener);
		rightTreeViewer.getTree().addControlListener(resizeListener);
		linkCanvas.addControlListener(resizeListener);
		// Scroll
		leftTreeViewer.getTree().addListener(SWT.Paint, e -> {
			if (canvasNeedRedraw || leftTreeViewer.getTree().getTopItem() != leftTopItem) {
				leftTopItem = leftTreeViewer.getTree().getTopItem();
				redrawMappings();
				canvasNeedRedraw = false;
			}
		});
		rightTreeViewer.getTree().addListener(SWT.Paint, e -> {
			if (canvasNeedRedraw || rightTreeViewer.getTree().getTopItem() != rightTopItem) {
				rightTopItem = rightTreeViewer.getTree().getTopItem();
				redrawMappings();
				canvasNeedRedraw = false;
			}
		});
		// Expand
		TreeListener treeListener = new TreeListener() {
			public void treeExpanded(TreeEvent e) {
				canvasNeedRedraw = true;
			}
			public void treeCollapsed(TreeEvent e) {
				canvasNeedRedraw = true;
			}
		};
		leftTreeViewer.getTree().addTreeListener(treeListener);
		rightTreeViewer.getTree().addTreeListener(treeListener);

		control.setWeights(this.uiConfig.getControlWeights());

		if (this.uiConfig.isDndEnabled()) {
			bindTreeForDND(leftTreeViewer, rightTreeViewer, SWT.LEFT_TO_RIGHT);
			bindTreeForDND(rightTreeViewer, leftTreeViewer, SWT.RIGHT_TO_LEFT);
		}
	}

	/**
	 * Set the content providers for both trees.
	 * Both tree provides MUST HAVE their {@link ITreeContentProvider#getParent(Object)} method implemeneted.
	 * @param leftContentProvider An {@link ITreeContentProvider} that node are instances of the <b>L<b> type parameter.
	 * @param rightTreeContentProvider An {@link ITreeContentProvider} that node are instances of the <b>R<b> type parameter.
	 */
	public void setContentProviders(ITreeContentProvider leftContentProvider, ITreeContentProvider rightTreeContentProvider) {
		leftTreeViewer.setContentProvider(leftContentProvider);
		rightTreeViewer.setContentProvider(rightTreeContentProvider);
	}

	public void setLabelProviders(IBaseLabelProvider leftLabelProvider, IBaseLabelProvider rightLabelProvider) {
		leftTreeViewer.setLabelProvider(leftLabelProvider);
		rightTreeViewer.setLabelProvider(rightLabelProvider);
	}

	/**
	 * Sets the input of the widget.
	 * @param leftTreeInput The input for left {@link TreeViewer}
	 * @param rightTreeInput The input for right {@link TreeViewer}
	 * @param mappings The list containing the mapping. It will be used as a working copy and
	 * then MODIFIED by the tree mapper. If you don't want to pass a modifiable list, then pass
	 * a copy of the default mapping list, and prefer using {@link TreeMapper}{@link #addNewMappingListener(INewMappingListener)}
	 * and {@link INewMappingListener} to track the creation of mapping.
	 */
	public void setInput(Object leftTreeInput, Object rightTreeInput, List<M> mappings) {
		clearFigures();
		if (leftTreeInput != null) {
			leftTreeViewer.setInput(leftTreeInput);
		}
		if (rightTreeInput != null) {
			rightTreeViewer.setInput(rightTreeInput);
		}
		if (mappings != null) {
			this.mappings = mappings;
			canvasNeedRedraw = true;
		} else {
			this.mappings = new ArrayList<>();
		}
		// Synchronize tree and viewers for mappings:
		// Expand left and right items of mappings, and then restore
		// tree to previous state
		for (M mapping : this.mappings) {
			L leftItem = semanticSupport.resolveLeftItem(mapping);
			leftTreeViewer.expandToLevel(leftItem, 0);
			R rightItem = semanticSupport.resolveRightItem(mapping);
			rightTreeViewer.expandToLevel(rightItem, 0);
		}

	}

	/**
	 * DO NOT USE IN CODE. Prefer setting "canvasNeedsRedraw" field to true to
	 * avoid useless operations.
	 * @param mappings
	 */
	private void redrawMappings() {
		if (this.mappings == null) {
			return;
		}

		boolean everythingOK = true;
		for (M mapping : this.mappings) {
			everythingOK &= drawMapping(mapping);
			if (mapping == selectedMapping) {
				LinkFigure newSelectedFigure = mappingsToFigures.get(mapping);
				applySelectedMappingFeedback(newSelectedFigure);
				selectedFigure = newSelectedFigure;
			}
		}
		if (everythingOK && warningFigure != null) {
			linkRootFigure.remove(warningFigure);
			warningFigure = null;
		} else if (!everythingOK && warningFigure == null) {
			warningFigure = createWarningFigure();
			linkRootFigure.add(warningFigure, new Rectangle(5, 5, SWT.DEFAULT, SWT.DEFAULT));
		}
	}

	/**
	 * @return a newly created figure to alert the end-user of an inconsistency in the widget
	 */
	private IFigure createWarningFigure() {
		Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		ImageFigure res = new ImageFigure(image);
		res.setPreferredSize(10, 10);
		Label label = new Label(Messages.widgetInconsistency);
		res.setToolTip(label);
		return res;
	}

	/**
	 * @param sourceTreeViewer
	 * @param targetTreeViewer
	 * @param direction
	 */
	private void bindTreeForDND(final TreeViewer sourceTreeViewer, final TreeViewer targetTreeViewer, final int direction) {
		final LocalSelectionTransfer sourceTransfer = LocalSelectionTransfer.getTransfer();
		final LocalSelectionTransfer targetTransfer = LocalSelectionTransfer.getTransfer();
		sourceTreeViewer.addDragSupport(DND.DROP_LINK, new Transfer[] { sourceTransfer }, new TreeDragSourceEffect(sourceTreeViewer.getTree()) {
			@Override
			public void dragStart(DragSourceEvent event) {
				event.doit = !sourceTreeViewer.getSelection().isEmpty();
			}
		});
		targetTreeViewer.addDropSupport(DND.DROP_LINK, new Transfer[] { targetTransfer }, new TreeDropTargetEffect(targetTreeViewer.getTree()) {
			@Override
			public void dragEnter(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
				event.detail = DND.DROP_LINK;
				super.dragEnter(event);
			}

			@Override
			public void drop(DropTargetEvent event) {
				performMappingByDrop(sourceTreeViewer, sourceTreeViewer.getSelection(), targetTreeViewer, (TreeItem) getItem(event.x, event.y), direction);
			}
		});
	}

	/**
	 * @param targetTreeViewer
	 * @param data
	 * @param widget
	 */
	@SuppressWarnings("unchecked")
	protected void performMappingByDrop(TreeViewer sourceTreeViewer, ISelection sourceData, TreeViewer targetTreeViewer, TreeItem targetTreeItem, int direction) {
		Object resolvedTargetItem = resolveTreeViewerItem(targetTreeViewer, targetTreeItem);
		for (Object sourceItem : ((IStructuredSelection)sourceData).toList()) {
			if (direction == SWT.LEFT_TO_RIGHT) {
				createMapping((L)sourceItem, (R)resolvedTargetItem);
			} else if (direction == SWT.RIGHT_TO_LEFT) {
				createMapping((L)resolvedTargetItem, (R)sourceItem);
			}
		}
	}

	/**
	 * @param leftItem
	 * @param resolvedTargetItem
	 */
	private void createMapping(L leftItem, R rightItem) {
		M newMapping = semanticSupport.createSemanticMappingObject(leftItem, rightItem);
		if (newMapping != null) {
			mappings.add(newMapping);
			refresh();
			drawMapping(newMapping);
			for (INewMappingListener<M> listener : creationListeners) {
				listener.mappingCreated(newMapping);
			}
		}
	}

	/**
	 * Draw a mapping and returns whether the operation is successful or not.
	 * If not, a message is logged to help in debugging.
	 * @param leftItem
	 * @param rightItem
	 * @return true is successful, false if an issue occured
	 */
	private boolean drawMapping(final M mapping) {
		LinkFigure previousFigure = mappingsToFigures.get(mapping);
		if (previousFigure != null) {
			previousFigure.deleteFromParent();
			mappingsToFigures.remove(mapping);
			figuresToMappings.remove(previousFigure);
		}

		final LinkFigure arrowFigure = new LinkFigure(linkRootFigure);

        {
            int leftHeaderOffset = 0;
            if (leftTreeViewer.getTree().getHeaderVisible()) {
                leftHeaderOffset = leftTreeViewer.getTree().getHeaderHeight();
            }
    
			boolean leftItemVisible = true;
			TreeItem leftTreeItem = (TreeItem) leftTreeViewer.testFindItem(semanticSupport.resolveLeftItem(mapping));
			if (leftTreeItem == null) {
				if (semanticSupport.signalOnMissingItem()) {
				    Policy.getLog().log(
						new Status(IStatus.ERROR,
								"org.eclipse.nebula.widgets.treemapper",
								"Could not find left entry of mapping " + mapping.toString() + " in left treeViewer."));
					return false;
				}
				return true;
			}
			TreeItem lastVisibleLeftTreeItem = leftTreeItem;
			while (leftTreeItem.getParentItem() != null) {
				if (!leftTreeItem.getParentItem().getExpanded()) {
					lastVisibleLeftTreeItem = leftTreeItem.getParentItem();
					leftItemVisible = false;
				}
				leftTreeItem = leftTreeItem.getParentItem();
			}
			arrowFigure.setLeftPoint(0, leftHeaderOffset + lastVisibleLeftTreeItem.getBounds().y + lastVisibleLeftTreeItem.getBounds().height / 2);
			arrowFigure.setLeftMappingVisible(leftItemVisible);
		}

		{
            int rightHeaderOffset = 0;
            if (rightTreeViewer.getTree().getHeaderVisible()) {
                rightHeaderOffset = rightTreeViewer.getTree().getHeaderHeight();
            }
            
			boolean rightItemVisible = true;
			TreeItem rightTreeItem = (TreeItem) rightTreeViewer.testFindItem(semanticSupport.resolveRightItem(mapping));
			if (rightTreeItem == null) {
				if (semanticSupport.signalOnMissingItem()) {
					Policy.getLog().log(
							new Status(IStatus.ERROR,
									"org.eclipse.nebula.widgets.treemapper",
									"Could not find right entry of mapping " + mapping.toString() + " in right treeViewer."));
					return false;
				}
				return true;
			}
			TreeItem lastVisibleRightTreeItem = rightTreeItem;
			while (rightTreeItem.getParentItem() != null) {
				if (!rightTreeItem.getParentItem().getExpanded()) {
					lastVisibleRightTreeItem = rightTreeItem.getParentItem();
					rightItemVisible = false;
				}
				rightTreeItem = rightTreeItem.getParentItem();
			}
			arrowFigure.setRightPoint(linkRootFigure.getBounds().width, rightHeaderOffset + lastVisibleRightTreeItem.getBounds().y + rightTreeItem.getBounds().height / 2);
			arrowFigure.setRightMappingVisible(rightItemVisible);
		}

		arrowFigure.setLineWidth(uiConfig.getDefaultArrowWidth());
		arrowFigure.seLineColor(uiConfig.getDefaultMappingColor());
		arrowFigure.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent me) {
				fireMappingSelection(mapping, arrowFigure);
			}
			public void mouseReleased(MouseEvent me) {
			}

			public void mouseDoubleClicked(MouseEvent me) {
				//if (arrowFigure.)
			}
		});
		arrowFigure.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent me) {
			}

			public void mouseEntered(MouseEvent me) {
				fireMouseEntered(mapping, arrowFigure);
			}

			public void mouseExited(MouseEvent me) {
				fireMouseExited(mapping, arrowFigure);
			}

			public void mouseHover(MouseEvent me) {
			}

			public void mouseMoved(MouseEvent me) {
			}

		});
		// store it
		figuresToMappings.put(arrowFigure, mapping);
		mappingsToFigures.put(mapping, arrowFigure);

		return true;
	}

	/**
	 * @param treeViewer
	 * @param treeItem
	 * @return
	 */
	private Object resolveTreeViewerItem(TreeViewer treeViewer, TreeItem treeItem) {
		ITreeContentProvider contentProvider = (ITreeContentProvider) treeViewer.getContentProvider();
		List<Integer> locations = new ArrayList<>();
		TreeItem parentTreeItem = treeItem.getParentItem();
		while (parentTreeItem != null) {
			int index = Arrays.asList(parentTreeItem.getItems()).indexOf(treeItem);
			locations.add(index);
			treeItem = parentTreeItem;
			parentTreeItem = treeItem.getParentItem();
		}
		// root
		if (treeItem != null) {
			int rootIndex = Arrays.asList(treeViewer.getTree().getItems()).indexOf(treeItem);
			locations.add(rootIndex);
		}
		Collections.reverse(locations);
		Object current = contentProvider.getElements(treeViewer.getInput())[locations.get(0)];
		locations.remove(0);
		for (int index : locations) {
			current = contentProvider.getChildren(current)[index];
		}
		return current;
	}

	/**
	 * @return
	 */
	public SashForm getControl() {
		return control;
	}


	//
	// Selection management
	//

	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private IStructuredSelection currentSelection = new StructuredSelection();

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public IStructuredSelection getSelection() {
		return currentSelection;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	public void setSelection(ISelection selection) {
		IStructuredSelection strSelection = (IStructuredSelection)selection;
		if (strSelection.isEmpty()) {
			currentSelection = new StructuredSelection();
			fireMouseExited(selectedMapping, mappingsToFigures.get(selectedMapping));
		} else {
			M mapping = (M) strSelection.getFirstElement();
			fireMappingSelection(mapping, mappingsToFigures.get(mapping));
		}
	}

	/**
	 * @param mapping
	 * @param arrowFigure
	 */
	protected void fireMappingSelection(M mapping, LinkFigure arrowFigure) {
		if (selectedFigure != null) {
			applyDefaultMappingStyle(selectedFigure);
		}
		applySelectedMappingFeedback(arrowFigure);
		selectedFigure = arrowFigure;
		selectedMapping = mapping;
		currentSelection = new StructuredSelection(selectedMapping);
		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, currentSelection));
		}
	}

	/**
	 * Select no item
	 */
	private void unselect() {
		selectedMapping = null;
		selectedFigure = null;
		currentSelection = new StructuredSelection();
		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, currentSelection));
		}
	}


	//
	// Creation management
	//

	private List<INewMappingListener<M>> creationListeners = new ArrayList<>();

	/**
	 * @param iNewMappingListener
	 */
	public void addNewMappingListener(INewMappingListener<M> listener) {
		this.creationListeners.add(listener);
	}

	/**
	 *
	 */
	private void applyDefaultMappingStyle(LinkFigure figure) {
		figure.seLineColor(uiConfig.getDefaultMappingColor());
		figure.setLineWidth(uiConfig.getDefaultArrowWidth());
	}

	/**
	 * @param arrowFigure
	 */
	private void applySelectedMappingFeedback(LinkFigure arrowFigure) {
		arrowFigure.seLineColor(uiConfig.getSelectedMappingColor());
		arrowFigure.setLineWidth(uiConfig.getHoverArrowWidth());
	}

	/**
	 * @param mapping
	 * @param arrowFigure
	 */
	protected void fireMouseExited(M mapping, LinkFigure arrowFigure) {
		if (arrowFigure != selectedFigure) {
			applyDefaultMappingStyle(arrowFigure);
		}
	}

	/**
	 * @param mapping
	 * @param arrowFigure
	 */
	protected void fireMouseEntered(M mapping, LinkFigure arrowFigure) {
		if (arrowFigure != selectedFigure) {
			arrowFigure.setLineWidth(uiConfig.getHoverArrowWidth());
		}
	}

	/**
	 * @return
	 */
	public TreeViewer getLeftTreeViewer() {
		return leftTreeViewer;
	}

	/**
	 * @return
	 */
	public TreeViewer getRightTreeViewer() {
		return rightTreeViewer;
	}


	/**
	 * Refresh the widget by resetting the setInput value
	 */
	public void refresh() {
		setInput(leftTreeViewer.getInput(), rightTreeViewer.getInput(), mappings);
		if (!mappings.contains(selectedMapping)) {
			unselect();
		}
		leftTreeViewer.refresh();
		rightTreeViewer.refresh();
		canvasNeedRedraw = true;
		control.layout(true);
	}
	
	/**
	 * Force canvas update and redraw
	 */
	public void updateCanvas() {
		canvasNeedRedraw = true;
		redrawMappings();
	}

	/**
	 *
	 */
	private void clearFigures() {
		for (Entry<M, LinkFigure> entry : mappingsToFigures.entrySet()) {
			entry.getValue().deleteFromParent();
		}
		mappingsToFigures.clear();
		figuresToMappings.clear();
	}

}
