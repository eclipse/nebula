package org.eclipse.nebula.cwt.test;

import java.awt.Robot;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.nebula.cwt.v.VControl;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VTracker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractVTestCase extends TestCase {

	private Thread t = new Thread("Test - " + getName()) {
		@Override
		public void run() {
			try {
				runTest();
			} catch (Throwable e) {
				if (exception == null) {
					exception = e;
				}
			} finally {
				testing = false;
				if (display != null && !display.isDisposed()) {
					display.wake();
				}
			}
		}
	};

	private Point defaultSize;

	private int delay = 100;

	private Throwable exception = null;

	private int stateMask = 0;

	private Shell shell;
	private Display display;

	private String capturePath;
	private int captureFormat = SWT.IMAGE_PNG;

	private Object tmpObj;

	private Set<Character> keyDownChars = new HashSet<Character>();
	private Set<Integer> keyDownMods = new HashSet<Integer>();
	private Set<Integer> mouseDowns = new HashSet<Integer>();

	boolean testing = false;

	protected void assertUserConfirm(final String message) {
		syncExec(new Runnable() {
			public void run() {
				MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO);
				mb.setMessage(message);
				mb.setText("User Confirmation");
				tmpObj = mb.open();
			}
		});
		assertTrue(message, tmpObj.equals(SWT.YES));
	}

	public void asyncExec(Runnable runnable) {
		display.asyncExec(runnable);
	}

	public void capture(Control control) {
		capture(control, null);
	}

	public void capture(final Control control, final String suffix) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.getParent().toDisplay(control.getLocation());
				Point size = control.getSize();
				capture(new Rectangle(location.x, location.y, size.x, size.y), suffix);
			}
		});
	}

	public void capture(int x, int y, int width, int height) {
		capture(x, y, width, height, null);
	}

	public void capture(int x, int y, int width, int height, String suffix) {
		capture(new Rectangle(x, y, width, height), suffix);
	}

	private void capture(Rectangle bounds, String suffix) {
		GC gc = new GC(display);
		Image image = new Image(display, bounds);
		gc.copyArea(image, bounds.x, bounds.y);
		gc.dispose();

		ImageData[] da = new ImageData[] { image.getImageData() };
		image.dispose();

		ImageLoader il = new ImageLoader();
		il.data = da;

		StringBuilder sb = new StringBuilder();
		if (capturePath != null && capturePath.length() > 0) {
			sb.append(capturePath);
		} else {
			sb.append(System.getProperty("user.home"));
		}

		File path = new File(sb.toString());
		if (!path.exists()) {
			path.mkdirs();
		}

		sb.append(File.separator);
		sb.append(getName());
		if (suffix != null && suffix.length() > 0) {
			sb.append("-").append(suffix);
		}
		switch (captureFormat) {
		case SWT.IMAGE_BMP:
			sb.append(".bmp");
			break;
		case SWT.IMAGE_GIF:
			sb.append(".gif");
			break;
		case SWT.IMAGE_ICO:
			sb.append(".ico");
			break;
		case SWT.IMAGE_JPEG:
			sb.append(".jpg");
			break;
		case SWT.IMAGE_PNG:
			sb.append(".png");
			break;
		case SWT.IMAGE_TIFF:
			sb.append(".tiff");
			break;
		default:
			captureFormat = SWT.IMAGE_PNG;
			sb.append(".png");
			break;
		}

		il.save(sb.toString(), captureFormat);
	}

	public void capture(final VControl control) {
		capture(control, null);
	}

	public void capture(final VControl control, final String suffix) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.toDisplay(control.getLocation());
				Point size = control.getSize();
				capture(new Rectangle(location.x, location.y, size.x, size.y), suffix);
			}
		});
	}

	public void captureScreen() {
		captureScreen(null);
	}

	public void captureScreen(final String suffix) {
		syncExec(new Runnable() {
			public void run() {
				capture(display.getBounds(), suffix);
			}
		});
	}

	public void captureShell() {
		captureShell(null);
	}

	public void captureShell(final String suffix) {
		syncExec(new Runnable() {
			public void run() {
				capture(shell.getBounds(), suffix);
			}
		});
	}

	public void click() {
		click(1);
	}

	public void click(Control control) {
		click(control, 1);
	}

	public void click(Control control, int button) {
		moveTo(control);
		click(button);
	}

	public void click(int button) {
		final int oldDelay = delay;
		delay = 0;
		mouseDown(button);
		delay = oldDelay;
		processEvents();
		mouseUp(button);
		processEvents();
	}

	public void click(VControl control) {
		click(control, 1);
	}

	public void click(VControl control, int button) {
		moveTo(control);
		click(button);
	}

	public void doubleClick() {
		click();
		pause(display.getDoubleClickTime() / 2);
		click();
	}

	public void doubleClick(VControl control) {
		moveTo(control);
		doubleClick();
	}

	public int getDelay() {
		return delay;
	}

	public Display getDisplay() {
		return display;
	}

	public Control getFocusControl() {
		synchronized (this) {
			syncExec(new Runnable() {
				public void run() {
					tmpObj = display.getFocusControl();
				}
			});
			return (Control) tmpObj;
		}
	}

	public VPanel getPanel(final Control control) {
		final ArrayList<VPanel> result = new ArrayList<VPanel>();
		syncExec(new Runnable() {
			public void run() {
				Object o = control.getData("cwt_vcontrol");
				if (o instanceof VPanel)
					result.add((VPanel) o);
				else
					result.add(null);
			}
		});

		return result.get(0);

	}

	public Composite getComposite(final VPanel panel) {

		final ArrayList<Composite> result = new ArrayList<Composite>();
		syncExec(new Runnable() {
			public void run() {
				result.add(panel.getComposite());
			}
		});

		return result.get(0);
	}

	protected Shell getShell() {
		return shell;
	}

	public boolean hasFocus(Control control) {
		synchronized (this) {
			processEvents();
			syncExec(new Runnable() {
				public void run() {
					tmpObj = display.getFocusControl();
				}
			});
			return tmpObj == control;
		}
	}

	public boolean hasFocus(VControl control) {
		processEvents();
		return control == VTracker.getFocusControl();
	}

	public boolean hasFocus(VNative<? extends Control> vnative) {
		return hasFocus(vnative.getControl());
	}

	public void keyDown(char character) {
		keyDownChars.add(character);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = character;
		display.post(event);
		pause(delay);
	}

	public void keyDown(int keyCode) {
		keyDownMods.add(keyCode);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.keyCode = keyCode;
		display.post(event);
		pause(delay);
	}

	public void keyPress(char character) {
		final int oldDelay = delay;
		delay = 0;
		keyDown(character);
		delay = oldDelay;
		processEvents();
		keyUp(character);
		processEvents();
	}

	public void keyPress(int keyCode) {
		final int oldDelay = delay;
		delay = 0;
		keyDown(keyCode);
		delay = oldDelay;
		processEvents();
		keyUp(keyCode);
		processEvents();
	}

	public void keyPress(char character, int... keyCodes) {
		for (int keyCode : keyCodes) {
			keyDown(keyCode);
		}
		keyDown(character);
		keyUp(character);
		for (int keyCode : keyCodes) {
			keyUp(keyCode);
		}
	}

	public void keyUp(char character) {
		keyDownChars.remove(character);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.character = character;
		display.post(event);
		pause(delay);
	}

	public void keyUp(int keyCode) {
		keyDownMods.remove(keyCode);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.keyCode = keyCode;
		display.post(event);
		pause(delay);
	}

	public void layoutShell() {
		syncExec(new Runnable() {
			public void run() {
				if (defaultSize != null) {
					shell.setSize(defaultSize);
				} else if (shell.getChildren().length > 0) {
					shell.pack();
				}
				Point size = shell.getSize();
				Rectangle screen = display.getMonitors()[0].getBounds();
				shell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
			}
		});
	}

	public void mouseDown() {
		mouseDown(1);
	}

	public void mouseDown(Control control) {
		mouseDown(control, 1);
	}

	public void mouseDown(Control control, int button) {
		moveTo(control);
		mouseDown(control);
	}

	public void mouseDown(int button) {
		mouseDowns.add(button);
		Event event = new Event();
		event.type = SWT.MouseDown;
		event.button = button;
		// event.stateMask = stateMask;
		// event.data = VTracker.getActiveControl();
		display.post(event);
		pause(delay);
	}

	public void mouseDown(VControl control) {
		mouseDown(control, 1);
	}

	public void mouseDown(VControl control, int button) {
		moveTo(control);
		mouseDown(button);
	}

	public void mouseUp() {
		mouseUp(1);
	}

	public void mouseUp(int button) {
		mouseDowns.remove(button);
		Event event = new Event();
		event.type = SWT.MouseUp;
		event.button = button;
		// event.stateMask = stateMask;
		// event.data = VTracker.getActiveControl();
		display.post(event);
		pause(delay);
	}

	public void mouseWheel(int count) {
		// Event event = new Event();
		// event.type = SWT.MouseWheel;
		// event.data = VTracker.getActiveControl();
		// event.detail = SWT.SCROLL_LINE;
		// event.x = x;
		// event.y = y;
		// event.count = count;
		// display.post(event);
		try {
			Robot robot = new Robot();
			robot.mouseWheel(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pause(delay);
	}

	public void move(final int x, final int y) {
		display.syncExec(new Runnable() {
			public void run() {
				Point point = display.getCursorLocation();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = point.x + x;
				event.y = point.y + y;
				display.post(event);

				event.x += 1;
				display.post(event);
			}
		});
		pause(delay);
	}

	public void moveTo(final Control control) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.getParent().toDisplay(control.getLocation());
				Point size = control.getSize();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = location.x + (size.x / 2) - 1;
				event.y = location.y + (size.y / 2);
				display.post(event);

				event.x += 1;
				display.post(event);
			}
		});
		pause(delay);
	}

	public void moveTo(final int x, final int y) {
		display.syncExec(new Runnable() {
			public void run() {
				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = x;
				event.y = y;
				display.post(event);

				event.x += 1;
				display.post(event);
			}
		});
		pause(delay);
	}

	public void moveX(final int x) {
		display.syncExec(new Runnable() {
			public void run() {
				Point location = getDisplay().getCursorLocation();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = location.x + x;
				event.y = location.y;
				display.post(event);

				event.x += 1;
				display.post(event);
			}
		});
		pause(delay);
	}

	public void moveY(final int y) {
		display.syncExec(new Runnable() {
			public void run() {
				Point location = getDisplay().getCursorLocation();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = location.x;
				event.y = location.y + y;
				display.post(event);

				event.x += 1;
				display.post(event);
			}
		});
		pause(delay);
	}

	public void moveTo(final VControl control) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.toDisplay(control.getLocation());
				Point size = control.getSize();

				int multiplier = (control.getShell().getStyle() & SWT.LEFT_TO_RIGHT) == SWT.LEFT_TO_RIGHT ? 1 : -1;

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				event.x = location.x + ((size.x / 2) * multiplier) - 1;
				event.y = location.y + (size.y / 2);
				display.post(event);
				processEvents();
				event.x += 1;
				display.post(event);
			}
		});
		processEvents();
		pause(delay);
	}

	public void moveToEdge(final Control control, final int edge) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.toDisplay(control.getLocation());
				Point size = control.getSize();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				if ((edge & SWT.LEFT) != 0) {
					event.x = location.x;
				} else if ((edge & SWT.RIGHT) != 0) {
					event.x = location.x + size.x - 1;
				} else {
					event.x = location.x + (size.x / 2) - 1;
				}
				if ((edge & SWT.TOP) != 0) {
					event.y = location.y;
				} else if ((edge & SWT.BOTTOM) != 0) {
					event.y = location.y + size.y - 1;
				} else {
					event.y = location.y + (size.y / 2) - 1;
				}
				display.post(event);
				processEvents();
				event.x += 1;
				display.post(event);
			}
		});
		processEvents();
		pause(delay);
	}

	public void moveToEdge(final VControl control, final int edge) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.toDisplay(control.getLocation());
				Point size = control.getSize();

				Event event = new Event();
				event.type = SWT.MouseMove;
				event.stateMask = stateMask;
				if ((edge & SWT.LEFT) != 0) {
					event.x = location.x;
				} else if ((edge & SWT.RIGHT) != 0) {
					event.x = location.x + size.x - 1;
				} else {
					event.x = location.x + (size.x / 2) - 1;
				}
				if ((edge & SWT.TOP) != 0) {
					event.y = location.y;
				} else if ((edge & SWT.BOTTOM) != 0) {
					event.y = location.y + size.y - 1;
				} else {
					event.y = location.y + (size.y / 2) - 1;
				}
				display.post(event);
				processEvents();
				event.x += 1;
				display.post(event);
			}
		});
		processEvents();
		pause(delay);
	}

	public void moveTo(final VControl control, final int step) {
		syncExec(new Runnable() {
			public void run() {
				Point location = control.toDisplay(control.getLocation());
				Point size = control.getSize();
				Point start = display.getCursorLocation();
				Point end = new Point(location.x + (size.x / 2), location.y + (size.y / 2));
				int x = start.x;
				int y = start.y;

				while (x < end.x || y < end.y) {
					if (x < end.x) {
						x += step;
					}
					if (y < end.y) {
						y += step;
					}
					Event event = new Event();
					event.type = SWT.MouseMove;
					event.stateMask = stateMask;
					event.x = x;
					event.y = y;
					display.post(event);
					processEvents();
					pause(10);
				}
			}
		});
		pause(delay);
	}

	public void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public void processEvents() {
		syncExec(new Runnable() {
			public void run() {
				while (display.readAndDispatch()) {
				}
			}
		});
	}

	public void redraw() {
		redraw(shell);
	}

	public void redraw(final Control control) {
		syncExec(new Runnable() {
			public void run() {
				control.redraw();
			}
		});
	}

	public void releaseAllEvents() {
		processEvents();
		delay = 0;
		for (Integer button : mouseDowns) {
			mouseUp(button);
		}
		for (Integer keyCode : keyDownMods) {
			keyUp(keyCode);
		}
		for (Character character : keyDownChars) {
			keyUp(character);
		}
		processEvents();
	}

	public void runBare() throws Throwable {
		final String name = getName();

		display = Display.getDefault();

		display.syncExec(new Runnable() {

			public void run() {
				shell = createShell();
				shell.setText(name);
				shell.setLayout(new FillLayout());

				try {
					setUp();
				} catch (Exception e) {
					exception = e;
				}

				layoutShell();
				shell.open();

				pause(500);
				processEvents();

				testing = true;

				if (name.endsWith("_Sync")) {
					t.run();
				} else {
					t.start();
				}

				while (testing && !shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}

				try {
					tearDown();
				} catch (Exception e) {
					exception = e;
				}

				display.dispose();
				display = null;

			}
		});

		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * Creates the {@link Shell}, override for a special shell.
	 * 
	 * @return
	 */
	public Shell createShell() {
		return new Shell(display);
	}

	public void setCaptureFormat(int format) {
		switch (captureFormat) {
		case SWT.IMAGE_BMP:
		case SWT.IMAGE_GIF:
		case SWT.IMAGE_ICO:
		case SWT.IMAGE_JPEG:
		case SWT.IMAGE_PNG:
		case SWT.IMAGE_TIFF:
			captureFormat = format;
			break;
		default:
			captureFormat = SWT.IMAGE_PNG;
		}
	}

	public void setCapturePath(String path) {
		capturePath = path;
	}

	public void setDefaultShellSize(Point size) {
		defaultSize = size;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	@Override
	protected void setUp() throws Exception {
		String name = getName();
		Method method = null;

		// TODO: pick a format and stick to it :)
		try {
			String setup = "setup" + name.substring(4);
			method = getClass().getMethod(setup, new Class[0]);
		} catch (NoSuchMethodException e) {
			// nothing to do
		}

		if (method == null) {
			try {
				String setup = Character.toLowerCase(name.charAt(4)) + name.substring(5) + "Setup";
				method = getClass().getMethod(setup, new Class[0]);
			} catch (NoSuchMethodException e) {
				// nothing to do
			}
		}

		if (method == null) {
			try {
				String[] sa = name.split("_");
				String s = sa[sa.length - 1];
				method = getClass().getMethod("setUp" + Integer.valueOf(s), new Class[0]);
			} catch (NoSuchMethodException e) {
				// nothing to do
			} catch (NumberFormatException e) {
				// nothing to do
			}
		}

		if (method != null) {
			method.invoke(this, new Object[0]);
		}
	}

	public void syncExec(Runnable runnable) {
		display.syncExec(runnable);
	}

	@Override
	protected void tearDown() throws Exception {
		releaseAllEvents();
		super.tearDown();
	}

}
