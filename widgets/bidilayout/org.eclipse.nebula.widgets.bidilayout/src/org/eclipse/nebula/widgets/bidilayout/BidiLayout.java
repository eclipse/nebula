/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.bidilayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BidiSegmentEvent;
import org.eclipse.swt.custom.BidiSegmentListener;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.MovementListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.BidiUtil;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

@SuppressWarnings("restriction")
public class BidiLayout extends Composite {

	public static final int LANG_ARABIC = 0x01;
	public static final int LANG_HEBREW = 0x0d;
	public static final int LANG_ENGLISH = 0x09;

	private static final char LRO = '\u202d';
	private static final char RLO = '\u202e';
	private static final char PDF = '\u202c';
	static final String eolStr = "\r\n";



	Listener listener;
	StyledText styledText;
	int bidiLangCode = 0;
	int nonBidiLangCode = 0;
	boolean isPushMode;
	boolean isWidgetReversed;
	boolean isAutoPush;
	int indxPushSegmentStart = -1;
	int lengthPushSegment = 0;
	Caret defaultCaret = null;
	MenuItem rtlMenuItem;
	MenuItem autopushMenuItem;
	char[] arrOfIgnoredChars = null;
	private int prevLength = 0;


	public BidiLayout(Composite parent, int style){
		super(parent, 0);
		this.setLayout(new FillLayout());
		styledText = new StyledText(this, style);

		addBidiSegmentListener();
		addListeners();
		styledText.setMenu(createContextMenu());
	}

	public void setBidiLang (int lang) {
		bidiLangCode = lang;
	}
	public void setNonBidiLang (int lang) {
		nonBidiLangCode = lang;
	}

	public void setArrOfIgnoredChars(char[] arr){
		arrOfIgnoredChars = arr.clone();
	}

	private void addListeners() {
		listener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MouseDown: handleMouseDown(event); break;
				case SWT.KeyDown: handleKeyDown(event); break;
				}

			}
		};
		addAndReorderListener(SWT.KeyDown, listener);
		styledText.addListener(SWT.MouseDown, listener);
		addAndReorderListener(SWT.Show, listener);

		styledText.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if ((keyEvent.keyCode == 'u') &&
						((keyEvent.stateMask & SWT.CTRL) != 0)){
					setPush(true);
					keyEvent.doit = false;
				}else if ((keyEvent.keyCode == 'o') &&
						((keyEvent.stateMask & SWT.CTRL) != 0)) {
					setPush(false);
					keyEvent.doit = false;
				} else if ((keyEvent.keyCode == 't') &&
						((keyEvent.stateMask & SWT.CTRL) != 0)){
					switchAutoPush();
					keyEvent.doit = false;
				} else if (isPushMode && (keyEvent.keyCode == SWT.HOME || keyEvent.keyCode==SWT.END)){
					int carPos = getCaretOffset();
					setPush(false);
					setCaretOffset(carPos);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	public void addAndReorderListener(int eventType, Listener listener){
		//have to 'reorder' listeners in eventTable. BidiLayout's listener should come first (before StyledText's one)
		Listener[] listeners = styledText.getListeners(eventType);
		Listener styledTextListener = null;
		for (Listener listener2 : listeners) {
			if (listener2.getClass().getSimpleName().startsWith("StyledText")){
				styledTextListener = listener2;
				break;
			}
		}
		if (styledTextListener != null){
			styledText.removeListener(eventType, styledTextListener);
		}
		styledText.addListener(eventType, listener);
		if (styledTextListener != null){
			styledText.addListener(eventType, styledTextListener);
		}
	}

	private Menu createContextMenu(){
		Menu menu;
		if (styledText.getMenu() == null) {
			menu = new Menu(styledText);
		} else {
			menu = styledText.getMenu();
		}

		MenuItem copy = new MenuItem(menu, SWT.CASCADE);
		copy.setText("&Copy"); //$NON-NLS-1$
		copy.setAccelerator(SWT.CTRL+'c');
		copy.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				copy();
				e.doit = false;
			}
		});
		MenuItem cut = new MenuItem(menu, SWT.CASCADE);
		cut.setText("Cu&t"); //$NON-NLS-1$
		cut.setAccelerator(SWT.CTRL+'x');
		cut.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				cut();
				e.doit = false;
			}
		});
		MenuItem paste = new MenuItem(menu, SWT.CASCADE);
		paste.setText("&Paste"); //$NON-NLS-1$
		paste.setAccelerator(SWT.CTRL+'v');
		paste.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				paste();
				e.doit = false;
			}
		});

		rtlMenuItem = new MenuItem(menu, SWT.CHECK);
		rtlMenuItem.setText("Right to Left Reading Order"); //$NON-NLS-1$
		rtlMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT);
		rtlMenuItem.setData("#PopupMenu");
		rtlMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				e = createEventForSwithchDir(e);
				handleKeyDown(e);
				styledText.notifyListeners(SWT.KeyUp, e);
				rtlMenuItem.setSelection(isWidgetReversed);
			}
			private Event createEventForSwithchDir(Event e) {
				e.widget = styledText;
				e.keyCode = SWT.SHIFT;
				if (isWidgetReversed) {
					e.keyLocation = SWT.LEFT;
				} else {
					e.keyLocation = SWT.RIGHT;
				}
				e.stateMask = SWT.CTRL;
				return e;
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		autopushMenuItem = new MenuItem(menu, SWT.CHECK);
		autopushMenuItem.setText("AutoPush [Ctrl+T]");
		autopushMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				switchAutoPush();
			}
		});
		final MenuItem pushon = new MenuItem(menu, SWT.CASCADE/*SWT.CHECK*/);
		pushon.setText("Push On [Ctrl+U]");
		pushon.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				setPush(true);
			}
		});

		final MenuItem pushoff = new MenuItem(menu, SWT.CASCADE/*SWT.CHECK*/);
		pushoff.setText("Push Off [Ctrl+O]");
		pushoff.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				setPush(false);
			}
		});

		return menu;
	}

	public void copy() {
		styledText.copy();
	}

	public void cut() {
		styledText.cut();
	}
	public void paste() {
		styledText.paste();
	}
	protected void handleMouseDown (Event event){
		if (isPushMode && !isCaretInsidePushSegment()){
			int caretPos = styledText.getCaretOffset();
			setPush(false);
			styledText.setCaretOffset(getUpdatedCaret(caretPos));
		}
	}
	protected void handleKeyDown(Event event) {
		if (event.keyCode == SWT.ARROW_RIGHT && isPushMode){
			if (!isWidgetReversed && isCursorAtStartPushSegemet()) {
				setPush(false);
			} else if (isWidgetReversed && isCursorAtEndPushSegemet()){
				int newCaretPos = indxPushSegmentStart;
				setPush(false);
				styledText.setCaretOffset (newCaretPos);
				styledText.invokeAction(ST.COLUMN_NEXT);
			} else {
				event.keyCode = SWT.ARROW_LEFT;
			}
		} else if (event.keyCode == SWT.ARROW_LEFT && isPushMode){
			if (!isWidgetReversed && isCursorAtEndPushSegemet()){
				int newCaretPos = indxPushSegmentStart;
				setPush(false);
				styledText.setCaretOffset (newCaretPos);
				styledText.invokeAction(ST.COLUMN_NEXT);
			} else if (isWidgetReversed && isCursorAtStartPushSegemet()){
				setPush(false);
				styledText.invokeAction(ST.COLUMN_NEXT);
			} else {
				event.keyCode = SWT.ARROW_RIGHT;
			}
		}else if (isPushMode && (event.keyCode == SWT.HOME || event.keyCode==SWT.END)){
			setPush(false);
			styledText.notifyListeners(SWT.KeyDown, event);
		} else if (event.keyCode == SWT.DEL && isPushMode){
			if (!isCursorAtEndPushSegemet()){
				lengthPushSegment--;
			}else {
				event.doit = false;
				event.type = SWT.None;
			}
		} else if (event.keyCode == SWT.BS && isPushMode){
			if (!isCursorAtStartPushSegemet()){
				lengthPushSegment --;
			}else {
				setPush(false);
			}
		}else if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL && event.keyCode == SWT.SHIFT && BidiUtil.isBidiPlatform()){
			if ((!isWidgetReversed && event.keyLocation == SWT.RIGHT) ||
					(isWidgetReversed && event.keyLocation == SWT.LEFT)){
				switchWidgetDir();
				styledText.notifyListeners(SWT.KeyDown, event);
			}
		} else if (isPushMode && (event.character == SWT.CR) ) {
			setPush(false);
			styledText.notifyListeners(SWT.KeyDown, event);
		} else {
			if (isAutoPush && event.character >= ' ' && event.character != SWT.DEL){
				handleAutoPush();
			}
			if ((arrOfIgnoredChars != null) && ((new String(arrOfIgnoredChars)).indexOf(event.character) != -1)){
				event.doit = false;
				event.type = SWT.None;
				return;
			}
		}
	}

	private boolean isCursorAtStartPushSegemet() {
		int caretOffset = styledText.getCaretOffset();
		if (isCaretAtTheLAstLine()) {
			caretOffset --;
		}
		//pushSegmentStart was stored when string had similar to widget orientation, therefore it needs to be 'mirrored' for comparison
		int mirroredBoundPosition = calculateMirroredPushSegmentStart();
		if (mirroredBoundPosition == (caretOffset-1)) {
			return true;
		}
		return false;
	}
	private boolean isCursorAtEndPushSegemet() {
		int caretOffset = styledText.getCaretOffset();
		if (isCaretAtTheLAstLine()) {
			caretOffset --;
		}
		//pushSegmentStart was stored when string had similar to widget orientation, therefore it needs to be 'mirrored' for comparison
		int mirroredBoundPosition = calculateMirroredPushSegmentEnd();

		if (mirroredBoundPosition == caretOffset) {
			return true;
		}
		return false;
	}
	private int calculateMirroredCaretPosition(int caretPos) {
		LineIndx lineIndx = new LineIndx(styledText.getText(),styledText.getCaretOffset());
		return lineIndx.getEndIndx() - (caretPos - lineIndx.getStartIndx());
	}
	private int calculateMirroredPushSegmentEnd() {
		return calculateMirroredCaretPosition(indxPushSegmentStart);
	}
	private int calculateMirroredPushSegmentStart() {
		return calculateMirroredPushSegmentEnd() - lengthPushSegment;
	}

	private boolean isCaretAtTheLAstLine(){
		LineIndx lineIndx = new LineIndx(styledText.getText(),styledText.getCaretOffset());
		if (lineIndx.getEndIndx() == styledText.getText().length()-1) {
			return true;
		}
		return false;
	}

	private boolean isCaretInsidePushSegment(){
		int mirroredPushSegmentEnd = calculateMirroredPushSegmentEnd();
		int mirroredPushSegmentStart = calculateMirroredPushSegmentStart();
		int caretPos = styledText.getCaretOffset();
		if ((caretPos >= mirroredPushSegmentStart) && (caretPos <= mirroredPushSegmentEnd)) {
			return true;
		}
		return false;
	}

	protected void switchAutoPush() {
		isAutoPush = !isAutoPush;
		autopushMenuItem.setSelection(isAutoPush);
	}
	public boolean isPushMode(){
		return isPushMode;
	}
	public void switchWidgetDir() {
		switchWidgetDir(true);
	}
	public void switchWidgetDir(boolean forceSringReverse) {
		isWidgetReversed = !isWidgetReversed;
		int carPos = styledText.getCaretOffset();
		if (forceSringReverse) {
			styledText.setText(reverseStr(styledText.getText()));
		}
		styledText.setCaretOffset (carPos);
		rtlMenuItem.setSelection(isWidgetReversed);

		if (isPushMode) {
			setPush(false);
		}
		if (isWidgetReversed) {
			setBidiKeyboardLanguage();
		} else {
			setNonBidiKeyboardLanguage();
		}
	}

	public void setPush(boolean pushOn) {
		if (isPushMode == pushOn) {
			return;
		}
		isPushMode = pushOn;
		if (pushOn) {
			startPushMode();
		}
		else {
			endPushMode();
			indxPushSegmentStart = -1;
			lengthPushSegment = 0;
		}
	}

	private void endPushMode() {
		styledText.setCaret(defaultCaret);
		styledText.setCaret(defaultCaret);
		styledText.setText(reverseStr(styledText.getText()));
		styledText.setCaretOffset (indxPushSegmentStart + lengthPushSegment);
		if (!isWidgetReversed) {
			setNonBidiKeyboardLanguage();
		} else {
			setBidiKeyboardLanguage();
		}

	}

	public static String reverseStr (String str) {
		String resultStr = "";
		String orgStr = new String(str);
		int i=-1;
		while ((i = orgStr.indexOf(eolStr)) != -1){
			StringBuffer sb = new StringBuffer(orgStr.substring(0,i));
			resultStr += sb.reverse() + eolStr;
			orgStr = orgStr.substring(i + eolStr.length());
		}
		if (orgStr.length()>0){
			StringBuffer sb = new StringBuffer(orgStr);
			resultStr += sb.reverse();
		}
		return resultStr;
	}

	public void addBidiSegmentListener() {
		styledText.addBidiSegmentListener(new BidiSegmentListener() {
			@Override
			public void lineGetSegments(BidiSegmentEvent event) {
				int length = event.lineText.length();
				if ((isPushMode && !isWidgetReversed) ||
						(!isPushMode && isWidgetReversed)){
					event.segments = new int[] { 0};
					event.segmentsChars = new char[] { RLO};
				} else {
					event.segments = new int[] { 0, length };
					event.segmentsChars = new char[] { LRO, PDF };
				}
				if (isPushMode && (indxPushSegmentStart != -1) && (prevLength< length)) {
					lengthPushSegment++;
				}
				prevLength  = length;
			}
		});

	}
	protected void startPushMode() {
		final Image image = new Image (styledText.getDisplay(), 20, 20);
		GC gc = new GC (image);
		gc.setBackground (styledText.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillRectangle (0, 0, 20, 20);
		gc.setForeground (styledText.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setLineAttributes(new LineAttributes(2));
		gc.drawLine (0, 13, gc.getFontMetrics().getAverageCharWidth(), 13);
		gc.dispose ();
		defaultCaret = styledText.getCaret();
		Caret cc = new Caret(styledText, 0);
		cc.setImage(image);
		styledText.setCaret(cc);
		int carOffset = styledText.getCaretOffset();


		String str = reverseStr(styledText.getText());
		styledText.setText(str);
		indxPushSegmentStart = carOffset;
		styledText.setCaretOffset(getUpdatedCaret(carOffset));
		if (!isWidgetReversed) {
			setBidiKeyboardLanguage();
		} else {
			setNonBidiKeyboardLanguage();
		}
	}
	private int getUpdatedCaret(int carOffset) {
		String str = styledText.getText();
		LineIndx lineIndx = new LineIndx(str, carOffset);
		int starIndx = lineIndx.getStartIndx();
		int endIndx = lineIndx.getEndIndx();
		if (endIndx == str.length()-1) {
			carOffset --;
		}
		return starIndx + (endIndx - carOffset);
	}
	private void handleAutoPush(){
		if ((isPushMode && !isWidgetReversed && (BidiUtil.getKeyboardLanguage() == BidiUtil.KEYBOARD_NON_BIDI))||
				(isPushMode && isWidgetReversed && (BidiUtil.getKeyboardLanguage() == BidiUtil.KEYBOARD_BIDI))) {
			setPush(false);
		} else if ((!isPushMode && !isWidgetReversed && (BidiUtil.getKeyboardLanguage() == BidiUtil.KEYBOARD_BIDI)) ||
				(!isPushMode && isWidgetReversed && (BidiUtil.getKeyboardLanguage() == BidiUtil.KEYBOARD_NON_BIDI))) {
			setPush(true);
		}
	}

	public boolean isWidgetReversed() {
		return isWidgetReversed;
	}


	private void setBidiKeyboardLanguage(){
		if (bidiLangCode == 0) {
			BidiUtil.setKeyboardLanguage(BidiUtil.KEYBOARD_BIDI);
		} else {
			setSpecificKeyboardLanguage(bidiLangCode);
		}
	}

	private void setNonBidiKeyboardLanguage(){
		if (nonBidiLangCode == 0) {
			BidiUtil.setKeyboardLanguage(BidiUtil.KEYBOARD_NON_BIDI);
		} else {
			setSpecificKeyboardLanguage(nonBidiLangCode);
		}
	}

	public static boolean setSpecificKeyboardLanguage(int langCode){
		int currentLang = OS.PRIMARYLANGID(OS.LOWORD(OS.GetKeyboardLayout(0)));
		if (currentLang == langCode) {
			return true;
		}
		int [] list = getKeyboardLanguageList();
		for (int element : list) {
			if (langCode == OS.PRIMARYLANGID(OS.LOWORD(element))) {
				OS.ActivateKeyboardLayout(element, 0);
				return true;
			}
		}
		return false;
	}

	public void setText(String text){
		if (isWidgetReversed) {
			text = reverseStr(text);
		}
		styledText.setText(text);
	}

	public String getText(){
		return styledText.getText();
	}
	public void addSelectionListener(SelectionListener listener) {
		styledText.addSelectionListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener) {
		styledText.removeSelectionListener(listener);
	}
	public void addLineBackgroundListener(LineBackgroundListener listener) {
		styledText.addLineBackgroundListener(listener);
	}
	public void removeLineBackgroundListener(LineBackgroundListener listener) {
		styledText.removeLineBackgroundListener(listener);
	}
	public void addVerifyListener(VerifyListener listener){
		styledText.addVerifyListener(listener);
	}
	public void removeVerifyListener(VerifyListener listener){
		styledText.removeVerifyListener(listener);
	}
	public void addWordMovementListener(MovementListener listener){
		styledText.addWordMovementListener(listener);
	}
	public void removeWordMovementListener(MovementListener listener){
		styledText.removeWordMovementListener(listener);
	}
	public void addModifyListener(ModifyListener modifyListener) {
		styledText.addModifyListener(modifyListener);
	}
	public void removeModifyListener(ModifyListener modifyListener) {
		styledText.removeModifyListener(modifyListener);
	}
	@Override
	public void redraw() {
		styledText.redraw();
	}
	public int getCharCount() {
		return styledText.getCharCount();
	}
	public Point getLocationAtOffset(int offset){
		return styledText.getLocationAtOffset(offset);
	}
	public int getLineHeight(){
		return styledText.getLineHeight();
	}
	public int getLineHeight(int lineIndex) {
		return styledText.getLineHeight(lineIndex);
	}
	@Override
	public void redraw(int x, int y, int width, int height, boolean all){
		styledText.redraw(x,y,width,height,all);
	}
	public void redrawRange(int start, int length, boolean clearBackground) {
		styledText.redrawRange(start, length, clearBackground);
	}
	public int getTopIndex(){
		return styledText.getTopIndex();
	}
	public StyledText getStyledText (){
		return styledText;
	}
	public boolean getBlockSelection() {
		return styledText.getBlockSelection();
	}
	public Rectangle getBlockSelectionBounds() {
		return styledText.getBlockSelectionBounds();
	}
	public void setTopIndex(int topIndex) {
		styledText.setTopIndex(topIndex);
	}
	public boolean getEditable(){
		return styledText.getEditable();
	}
	public int styledText(){
		return styledText.getHorizontalPixel();
	}
	public int getLineAtOffset(int offset){
		return styledText.getLineAtOffset(offset);
	}
	public int getHorizontalPixel(){
		return styledText.getHorizontalPixel();
	}
	public int getHorizontalIndex(){
		return styledText.getHorizontalIndex();
	}
	public void setHorizontalIndex(int offset) {
		styledText.setHorizontalIndex(offset);
	}
	public Point getSelection(){
		return styledText.getSelection();
	}
	public void setSelection(int start) {
		styledText.setSelection(start);
	}
	public void setSelection(int start, int end) {
		styledText.setSelection(start, end);
	}
	public void setSelectionRange(int start, int length) {
		styledText.setSelectionRange(start, length);
	}
	public void selectAll(){
		styledText.selectAll();
	}
	public void setStyleRange(StyleRange range) {
		styledText.setStyleRange(range);
	}
	public void setStyleRanges(StyleRange[] ranges) {
		styledText.setStyleRanges(ranges);
	}
	public void showSelection() {
		styledText.showSelection();
	}
	public int getSelectionCount() {
		return styledText.getSelectionCount();
	}
	public Point getSelectionRange(){
		return styledText.getSelectionRange();
	}
	public int[] getSelectionRanges() {
		return styledText.getSelectionRanges();
	}
	public int getTabs() {
		return styledText.getTabs();
	}
	public Rectangle getTextBounds(int start, int end) {
		return styledText.getTextBounds(start, end);
	}
	public int getTopPixel() {
		return styledText.getTopPixel();
	}
	public int getCaretOffset() {
		return styledText.getCaretOffset();
	}
	public void invokeAction(int action){
		styledText.invokeAction(action);
	}
	public Runnable print(Printer printer, StyledTextPrintOptions options) {
		return styledText.print(printer, options);
	}
	public void replaceStyleRanges(int start, int length, StyleRange[] ranges) {
		styledText.replaceStyleRanges(start, length, ranges);
	}
	public void setBlockSelectionBounds(int x, int y, int width, int height) {
		styledText.setBlockSelectionBounds(x, y, width, height);
	}
	public void setBlockSelectionBounds(Rectangle rect) {
		styledText.setBlockSelectionBounds(rect);
	}
	public void setCaretOffset(int offset) {
		styledText.setCaretOffset(offset);
	}
	public void setContent(StyledTextContent newContent) {
		styledText.setContent(newContent);
	}
	public void setDoubleClickEnabled(boolean enable) {
		styledText.setDoubleClickEnabled(enable);
	}
	public void setEditable(boolean editable) {
		if (editable) {
			styledText.setBackground(null);
		} else {
			styledText.setBackground(styledText.getParent().getBackground());
		}
		styledText.setEditable(editable);
	}
	@Override
	public void setEnabled (boolean enabled){
		if (enabled) {
			styledText.setBackground(null);
		} else {
			styledText.setBackground(styledText.getParent().getBackground());
		}
		styledText.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public void setBackground (Color color) {
		styledText.setBackground(color);
	}
	//this method was copied from BidiUtil (since it isn't public)
	static int [] getKeyboardLanguageList() {
		int maxSize = 10;
		int [] tempList = new int [maxSize];
		int size = OS.GetKeyboardLayoutList(maxSize, tempList);
		int [] list = new int [size];
		System.arraycopy(tempList, 0, list, 0, size);
		return list;
	}

	private class LineIndx {
		private int startIndx = -1;
		private int endIndx = -1;

		LineIndx (String str, int indx){
			startIndx = str.substring(0, indx).lastIndexOf(eolStr);
			if (startIndx == -1) {
				startIndx = 0;
			} else {
				startIndx += eolStr.length();
			}
			endIndx = str.substring(startIndx).indexOf(eolStr);
			if (endIndx == -1) {
				endIndx = str.substring(startIndx).length()-1;
			}
			endIndx += startIndx;
		}

		public int getStartIndx(){
			return startIndx;
		}

		public int getEndIndx(){
			return endIndx;
		}

	}
}
