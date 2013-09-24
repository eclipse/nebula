package org.eclipse.nebula.paperclips.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.nebula.paperclips.core.CompositeEntry;
import org.eclipse.nebula.paperclips.core.CompositePiece;
import org.eclipse.nebula.paperclips.core.LayerEntry;
import org.eclipse.nebula.paperclips.core.LayerEntryIterator;
import org.eclipse.nebula.paperclips.core.LayerPrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class LayerIterator implements PrintIterator {
	LayerEntryIterator[] entries;

	public LayerIterator(LayerPrint print, Device device, GC gc) {
		entries = new LayerEntryIteratorImpl[print.getEntries().length];
		LayerEntry[] e = print.getEntries();
		for (int i = 0; i < entries.length; i++) {
			entries[i] = e[i].iterator(device, gc);
		}
	}

	public LayerIterator(LayerIterator that) {
		this.entries = (LayerEntryIterator[]) that.entries.clone();
		for (int i = 0; i < entries.length; i++)
			if (entries[i].getTarget().hasNext())
				entries[i] = entries[i].copy();
	}

	public boolean hasNext() {
		for (int i = 0; i < entries.length; i++)
			if (entries[i].getTarget().hasNext())
				return true;
		return false;
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content"); //$NON-NLS-1$

		PrintPiece[] pieces = nextPieces(width, height);
		if (pieces == null)
			return null;

		CompositeEntry[] entries = new CompositeEntry[pieces.length];
		for (int i = 0; i < entries.length; i++) {
			PrintPiece piece = pieces[i];
			int offset = getHorzAlignmentOffset(this.entries[i].getAlignment(),
					piece.getSize().x, width);
			entries[i] = new CompositeEntry(piece, new Point(offset, 0));
		}
		return new CompositePiece(entries);
	}

	private PrintPiece[] nextPieces(int width, int height) {
		LayerEntryIteratorImpl[] entries = (LayerEntryIteratorImpl[]) this.entries
				.clone();

		List pieces = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			LayerEntryIteratorImpl entry = entries[i];
			if (entry.target.hasNext()) {
				PrintPiece piece = PaperClips.next(entry.target, width, height);

				if (piece == null) {
					for (Iterator iter = pieces.iterator(); iter.hasNext();)
						((PrintPiece) iter.next()).dispose();
					return null;
				}
				pieces.add(piece);
			}
		}

		// Replace instance entries with the entries that were just consumed.
		this.entries = entries;

		return (PrintPiece[]) pieces.toArray(new PrintPiece[pieces.size()]);
	}

	private int getHorzAlignmentOffset(int alignment, int pieceWidth,
			int totalWidth) {
		int offset = 0;
		switch (alignment) {
		case SWT.CENTER:
			offset = (totalWidth - pieceWidth) / 2;
			break;
		case SWT.RIGHT:
			offset = totalWidth - pieceWidth;
			break;
		}
		return offset;
	}

	Point computeSize(PrintSizeStrategy strategy) {
		Point size = new Point(0, 0);
		for (int i = 0; i < entries.length; i++) {
			LayerEntryIterator entry = entries[i];
			Point entrySize = strategy.computeSize(entry.getTarget());
			size.x = Math.max(size.x, entrySize.x);
			size.y = Math.max(size.y, entrySize.y);
		}
		return size;
	}

	public Point minimumSize() {
		return computeSize(PrintSizeStrategy.MINIMUM);
	}

	public Point preferredSize() {
		return computeSize(PrintSizeStrategy.PREFERRED);
	}

	public PrintIterator copy() {
		return new LayerIterator(this);
	}
}