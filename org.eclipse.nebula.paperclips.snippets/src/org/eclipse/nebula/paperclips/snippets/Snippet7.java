/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.page.PageNumberPageDecoration;
import org.eclipse.nebula.paperclips.core.page.PagePrint;
import org.eclipse.nebula.paperclips.core.page.SimplePageDecoration;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * Demonstrate use of PrintPreview control.
 * 
 * @author Matthew
 */
public class Snippet7 {
	public static Print createPrint() {
		DefaultGridLook look = new DefaultGridLook();
		look.setCellSpacing(5, 2);
		GridPrint grid = new GridPrint("p:g, d:g", look);

		String text = "The quick brown fox jumps over the lazy dog.";
		for (int i = 0; i < 500; i++)
			grid.add(new TextPrint(text));

		PagePrint page = new PagePrint(grid);
		page.setHeader(new SimplePageDecoration(new TextPrint("Snippet7.java",
				SWT.CENTER)));
		page.setFooter(new PageNumberPageDecoration(SWT.CENTER));
		page.setHeaderGap(5);
		page.setFooterGap(5);

		return page;
	}

	public static class UI {
		final Display display;

		PrintJob printJob;

		Shell shell;
		Button previousPage;
		Label pageNumber;
		Button nextPage;

		ScrolledComposite scroll;
		PrintPreview preview;

		double[] scrollingPosition;

		public UI(Display display) {
			this.display = display;
		}

		public Shell createShell() {
			printJob = new PrintJob("Snippet7.java", createPrint())
					.setMargins(108); // 1.5"

			shell = new Shell(display);
			shell.setText("Snippet7.java");
			shell.setBounds(100, 100, 800, 600);
			shell.setLayout(new GridLayout(1, false));

			createButtonPanel(shell).setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, false));
			createScrollingPreview(shell).setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true));

			preview.setPrintJob(printJob);
			updatePreviewSize();
			updatePageNumber();

			shell.setVisible(true);

			return shell;
		}

		private Control createButtonPanel(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);

			GridLayout layout = new GridLayout(16, false);
			layout.marginWidth = layout.marginHeight = 0;
			composite.setLayout(layout);

			previousPage = createIconButton(composite, "previous_page.gif",
					"Previous Page", new Listener() {
						public void handleEvent(Event event) {
							setPreviewPageIndex(preview.getPageIndex()
									- preview.getHorizontalPageCount()
									* preview.getVerticalPageCount());
						}
					});

			pageNumber = new Label(composite, SWT.NONE);

			nextPage = createIconButton(composite, "next_page.gif",
					"Next Page", new Listener() {
						public void handleEvent(Event event) {
							setPreviewPageIndex(preview.getPageIndex()
									+ preview.getHorizontalPageCount()
									* preview.getVerticalPageCount());
						}
					});

			createIconButton(composite, "fit_horizontal.png", "Fit Width",
					new Listener() {
						public void handleEvent(Event event) {
							preview.setFitHorizontal(true);
							preview.setFitVertical(false);
							rememberScrollingPosition();
							updatePreviewSize();
							restoreScrollingPosition();
						}
					});

			createIconButton(composite, "fit_vertical.png", "Fit Height",
					new Listener() {
						public void handleEvent(Event event) {
							preview.setFitVertical(true);
							preview.setFitHorizontal(false);
							rememberScrollingPosition();
							updatePreviewSize();
							restoreScrollingPosition();
						}
					});

			createIconButton(composite, "fit_best.png", "Fit Window",
					new Listener() {
						public void handleEvent(Event event) {
							preview.setFitVertical(true);
							preview.setFitHorizontal(true);
							rememberScrollingPosition();
							updatePreviewSize();
							restoreScrollingPosition();
						}
					});

			createIconButton(composite, "zoom_in.gif", "Zoom In",
					new Listener() {
						public void handleEvent(Event event) {
							setPreviewScale(preview.getAbsoluteScale() * 1.1f);
						}
					});

			createIconButton(composite, "zoom_out.gif", "Zoom Out",
					new Listener() {
						public void handleEvent(Event event) {
							setPreviewScale(preview.getAbsoluteScale() / 1.1f);
						}
					});

			createIconButton(composite, "zoom_scale.gif", "Zoom to Scale",
					new Listener() {
						public void handleEvent(Event event) {
							setPreviewScale(1);
						}
					});

			createTextButton(composite, "Port", "Portrait Orientation",
					new Listener() {
						public void handleEvent(Event event) {
							printJob
									.setOrientation(PaperClips.ORIENTATION_PORTRAIT);
							preview.setPrintJob(printJob);

							forgetScrollingPosition();
							updatePreviewSize();
							updatePageNumber();
						}
					});

			createTextButton(composite, "Land", "Landscape Orientation",
					new Listener() {
						public void handleEvent(Event event) {
							printJob
									.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
							preview.setPrintJob(printJob);

							forgetScrollingPosition();
							updatePreviewSize();
							updatePageNumber();
						}
					});

			createIconButton(composite, "print.gif", "Print", new Listener() {
				public void handleEvent(Event event) {
					PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
					PrinterData printerData = dialog.open();
					if (printerData != null) {
						PaperClips.print(printJob, printerData);
						preview.setPrinterData(printerData);
					}
				}
			});

			createLabel(composite, "Horz Pages");
			createPageCountSpinner(composite, new Listener() {
				public void handleEvent(Event event) {
					preview.setHorizontalPageCount(((Spinner) event.widget)
							.getSelection());
					forgetScrollingPosition();
					updatePreviewSize();
					updatePageNumber();
				}
			});

			createLabel(composite, "Vert Pages");
			createPageCountSpinner(composite, new Listener() {
				public void handleEvent(Event event) {
					preview.setVerticalPageCount(((Spinner) event.widget)
							.getSelection());
					forgetScrollingPosition();
					updatePreviewSize();
					updatePageNumber();
				}
			});

			return composite;
		}

		private Control createScrollingPreview(Composite parent) {
			scroll = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL
					| SWT.V_SCROLL);
			scroll.setExpandHorizontal(true);
			scroll.setExpandVertical(true);

			preview = new PrintPreview(scroll, SWT.NONE);
			scroll.setContent(preview);

			scroll.addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event event) {
					Rectangle bounds = scroll.getClientArea();

					scroll.getHorizontalBar().setPageIncrement(
							bounds.width * 2 / 3);
					scroll.getVerticalBar().setPageIncrement(
							bounds.height * 2 / 3);

					if (preview.isFitHorizontal() ^ preview.isFitVertical()) {
						rememberScrollingPosition();
						updatePreviewSize();
						restoreScrollingPosition();
					}
				}
			});

			preview.setFitVertical(true);
			preview.setFitHorizontal(true);

			Listener dragListener = new Listener() {
				private final Point dpi = display.getDPI();
				private boolean scrollable = false;

				private boolean dragging = false;
				private Point dragStartScrollOrigin = null;
				private Point dragStartMouseAnchor = null;

				public void handleEvent(Event event) {
					switch (event.type) {
					case SWT.Resize:
						forgetScrollingPosition();
						Rectangle bounds = scroll.getClientArea();
						Point size = preview.getSize();
						scrollable = size.x > bounds.width
								|| size.y > bounds.height;
						if (!scrollable && dragging)
							endDragging();
						break;
					case SWT.MouseDown:
						forgetScrollingPosition();
						if (scrollable && event.button == 1)
							beginDragging(event);
						break;
					case SWT.MouseMove:
						if (dragging) {
							forgetScrollingPosition();
							Point point = preview.toDisplay(event.x, event.y);
							scroll.setOrigin(dragStartScrollOrigin.x
									+ dragStartMouseAnchor.x - point.x,
									dragStartScrollOrigin.y
											+ dragStartMouseAnchor.y - point.y);
						}
						break;
					case SWT.MouseUp:
						forgetScrollingPosition();
						if (dragging)
							endDragging();
						break;
					case SWT.MouseEnter:
						display.addFilter(SWT.MouseWheel, this);
						break;
					case SWT.MouseWheel:
						if (event.count != 0) {
							if (scrollable
									&& !dragging
									&& (event.stateMask == SWT.NONE || event.stateMask == SWT.SHIFT)) {
								forgetScrollingPosition();
								bounds = scroll.getClientArea();
								size = preview.getSize();
								Point origin = scroll.getOrigin();
								int direction = event.count > 0 ? -1 : 1;
								// Prefer vertical scrolling unless user is
								// pressing Shift
								if (size.y > bounds.height
										&& event.stateMask == SWT.NONE)
									origin.y += direction
											* Math
													.min(dpi.y,
															bounds.height / 4);
								else if (size.x > bounds.width)
									origin.x += direction
											* Math.min(dpi.x, bounds.width / 4);
								scroll.setOrigin(origin);
								event.doit = false;
							} else if (event.stateMask == SWT.CTRL) { // Ctrl+MouseWheel
								// ->
								// zoom
								float scale = preview.getAbsoluteScale();
								setPreviewScale(event.count > 0 ? scale / 1.1f
										: scale * 1.1f);
							}
						}
						break;
					case SWT.MouseExit:
						display.removeFilter(SWT.MouseWheel, this);
						break;
					}
				}

				private void beginDragging(Event event) {
					dragStartScrollOrigin = scroll.getOrigin();
					dragStartMouseAnchor = preview.toDisplay(event.x, event.y);
					dragging = true;
				}

				private void endDragging() {
					dragging = false;
					dragStartMouseAnchor = null;
					dragStartScrollOrigin = null;
				}
			};

			scroll.addListener(SWT.Resize, dragListener);
			preview.addListener(SWT.MouseDown, dragListener);
			preview.addListener(SWT.MouseMove, dragListener);
			preview.addListener(SWT.MouseUp, dragListener);

			// These are for mouse wheel handling
			preview.addListener(SWT.MouseEnter, dragListener);
			preview.addListener(SWT.MouseExit, dragListener);

			return scroll;
		}

		private Button createIconButton(Composite parent, String imageFilename,
				String toolTipText, Listener selectionListener) {
			Button button = createButton(parent, toolTipText, selectionListener);
			button.setImage(createImage(imageFilename));
			return button;
		}

		private Button createTextButton(Composite parent, String text,
				String toolTipText, Listener selectionListener) {
			Button button = createButton(parent, toolTipText, selectionListener);
			button.setText(text);
			return button;
		}

		private Button createButton(Composite parent, String toolTipText,
				Listener selectionListener) {
			Button button = new Button(parent, SWT.PUSH);
			button.setToolTipText(toolTipText);
			button
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
							false));
			button.addListener(SWT.Selection, selectionListener);
			return button;
		}

		private Spinner createPageCountSpinner(Composite parent,
				Listener selectionListener) {
			Spinner spinner = new Spinner(parent, SWT.BORDER);
			spinner.setMinimum(1);
			spinner.setMaximum(99);
			spinner.addListener(SWT.Selection, selectionListener);
			return spinner;
		}

		private void createLabel(Composite parent, String text) {
			new Label(parent, SWT.NONE).setText(text);
		}

		private Image createImage(String filename) {
			final Image image = new Image(display, getClass()
					.getResourceAsStream(filename));

			shell.addListener(SWT.Dispose, new Listener() {
				public void handleEvent(Event event) {
					image.dispose();
				}
			});

			return image;
		}

		private void updatePageNumber() {
			int pageIndex = preview.getPageIndex();
			int pageCount = preview.getPageCount();
			int visiblePageCount = preview.getHorizontalPageCount()
					* preview.getVerticalPageCount();
			String text = (visiblePageCount > 1 ? "Pages " + (pageIndex + 1)
					+ "-" + Math.min(pageCount, pageIndex + visiblePageCount)
					: "Page " + (pageIndex + 1))
					+ " of " + pageCount;
			pageNumber.setText(text);
			previousPage.setEnabled(pageIndex > 0);
			nextPage.setEnabled(pageIndex < pageCount - visiblePageCount);
			shell.layout(new Control[] { pageNumber });
		}

		private void rememberScrollingPosition() {
			Point size = preview.getSize();
			if (size.x == 0 || size.y == 0) {
				forgetScrollingPosition();
			} else if (scrollingPosition == null) {
				Point origin = scroll.getOrigin();
				scrollingPosition = new double[] {
						(double) origin.x / (double) size.x,
						(double) origin.y / (double) size.y };
			}
		}

		private void forgetScrollingPosition() {
			scrollingPosition = null;
		}

		private void restoreScrollingPosition() {
			if (scrollingPosition != null) {
				Point size = preview.getSize();
				scroll.setOrigin((int) Math
						.round(scrollingPosition[0] * size.x), (int) Math
						.round(scrollingPosition[1] * size.y));
			}
		}

		private void updatePreviewSize() {
			Point minSize;
			Rectangle bounds = scroll.getClientArea();
			if (preview.isFitHorizontal()) {
				if (preview.isFitVertical())
					minSize = new Point(0, 0); // Best fit
				else
					minSize = new Point(0, preview.computeSize(bounds.width,
							SWT.DEFAULT).y); // Fit to width
			} else {
				if (preview.isFitVertical())
					minSize = new Point(preview.computeSize(SWT.DEFAULT,
							bounds.height).x, 0); // Fit to height
				else
					minSize = preview.computeSize(SWT.DEFAULT, SWT.DEFAULT); // Custom
				// scale
			}
			scroll.setMinSize(minSize);
		}

		private void setPreviewScale(float scale) {
			preview.setFitVertical(false);
			preview.setFitHorizontal(false);
			preview.setScale(scale);
			rememberScrollingPosition();
			updatePreviewSize();
			restoreScrollingPosition();
		}

		private void setPreviewPageIndex(int pageIndex) {
			preview.setPageIndex(Math.max(Math.min(pageIndex, preview
					.getPageCount() - 1), 0));
			updatePageNumber();
		}
	}

	/**
	 * Executes the snippet.
	 * 
	 * @param args
	 *            command-line args.
	 */
	public static void main(String[] args) {
		final Display display = Display.getDefault();

		Shell shell = new UI(display).createShell();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}
}
