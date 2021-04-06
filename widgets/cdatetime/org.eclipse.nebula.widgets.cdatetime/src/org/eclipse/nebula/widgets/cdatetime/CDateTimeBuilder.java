/****************************************************************************
 * Copyright (c) 2008 - 2019 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *    Stefan Nöbauer - https://bugs.eclipse.org/bugs/show_bug.cgi?id=548149
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;

/**
 * Contains factory methods for the {@link CDateTime} widget.
 */
public class CDateTimeBuilder {

	/**
	 * @return a compact version of the widget.
	 */
	public static CDateTimeBuilder getCompact() {
		CDateTimeBuilder builder = new CDateTimeBuilder();
		builder.setHeader(Header.MonthPrev(), Header.DateNow(),
				Header.MonthNext(),
				Header.Month().align(SWT.RIGHT, SWT.FILL, true), Header.Year(),
				Header.Time());
		builder.setBody(Body.Days().spacedAt(1).compact(), Body.Months(),
				Body.Years(), Body.Time());
		return builder;
	}

	/**
	 * @return the standard version of the widget.
	 */
	public static CDateTimeBuilder getStandard() {
		CDateTimeBuilder builder = new CDateTimeBuilder();
		builder.setHeader(Header.MonthPrev(), Header.MonthNext(),
				Header.Month().align(SWT.LEFT, SWT.FILL, true),
				Header.Year().align(SWT.RIGHT, SWT.FILL, false),
				Header.YearPrev(), Header.YearNext());
		builder.setBody(Body.Days().spacedAt(1), Body.Months(), Body.Years(),
				Body.Time().newColumn());
		builder.setFooter(Footer.VerboseToday());
		return builder;
	}

	private Header[] headers = new Header[0];
	private Body[] bodies = new Body[0];
	private Footer[] footers = new Footer[0];

	private List<Header> activeHeaders = new ArrayList<>();
	private List<Body> activeBodies = new ArrayList<>();
	private List<Footer> activeFooters = new ArrayList<>();

	private int headerAlignment;
	private boolean headerEqualColumns;

	private int footerAlignment;
	private boolean footerEqualColumns;
	
	private Calendar minDate;
	private Calendar maxDate;

	public List<Body> getBodies() {
		return activeBodies;
	}

	public int getFooterAlignment() {
		return footerAlignment;
	}

	public boolean getFooterEqualColumns() {
		return footerEqualColumns;
	}

	public List<Footer> getFooters() {
		return activeFooters;
	}

	public int getHeaderAlignment() {
		return headerAlignment;
	}

	public boolean getHeaderEqualColumns() {
		return headerEqualColumns;
	}

	public List<Header> getHeaders() {
		return activeHeaders;
	}

	public boolean hasBody() {
		return !activeBodies.isEmpty();
	}

	public boolean hasBody(int type) {
		for (Body body : activeBodies) {
			if (body.type == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFooter() {
		return !activeFooters.isEmpty();
	}

	public boolean hasFooter(int type) {
		for (Footer footer : activeFooters) {
			if (footer.type == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasHeader() {
		return !activeHeaders.isEmpty();
	}

	public boolean hasHeader(int type) {
		for (Header header : activeHeaders) {
			if (header.type == type) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Returns a clone of the minDate or <code>null</code>.
	 */
	public Calendar getMinDate() {
		if(minDate == null) {
			return null;
		}
		return (Calendar) minDate.clone();
	}
	
	/**
	 * @return Returns a clone of the maxDate or <code>null</code>.
	 */
	public Calendar getMaxDate() {
		if(maxDate == null) {
			return null;
		}
		return  (Calendar) maxDate.clone();
	}
	
	public void setBody(Body... attrs) {
		this.bodies = attrs;
	}

	public void setFields(int[] calendarFields) {
		activeHeaders.clear();
		activeHeaders = new ArrayList<>();
		activeBodies.clear();
		activeBodies = new ArrayList<>();
		activeFooters.clear();
		activeFooters = new ArrayList<>();

		boolean found = false;
		for (Body a : bodies) {
			found = false;
			for (int cf : calendarFields) {
				for (int f : a.fields) {
					if (f == cf) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (found) {
				activeBodies.add(a);
			}
		}
		if (activeBodies.size() > 1 || activeBodies.get(0).type == Body.YEARS) {
			for (Header a : headers) {
				if (activeBodies.size() > 1 || a.type == Header.YEAR_NEXT
						|| a.type == Header.YEAR_PREV) {
					found = false;
					for (int cf : calendarFields) {
						for (int f : a.fields) {
							if (f == cf) {
								found = true;
								break;
							}
						}
						if (found) {
							break;
						}
					}
					if (found) {
						activeHeaders.add(a);
					}
				}
			}
		}
		if (activeBodies.size() > 1) {
			for (Footer a : footers) {
				found = false;
				for (int cf : calendarFields) {
					for (int f : a.fields) {
						if (f == cf) {
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
				if (found) {
					activeFooters.add(a);
				}
			}
		}
	}

	public void setFooter(Footer... attrs) {
		setFooter(SWT.FILL, true, attrs);
	}

	public void setFooter(int alignment, boolean equalColumns,
			Footer... attrs) {
		footerAlignment = alignment;
		footerEqualColumns = equalColumns;
		this.footers = attrs;
	}

	public void setHeader(Header... attrs) {
		setHeader(SWT.FILL, false, attrs);
	}

	public void setHeader(int alignment, boolean equalColumns,
			Header... attrs) {
		headerAlignment = alignment;
		headerEqualColumns = equalColumns;
		this.headers = attrs;
	}
	
	/**
	 * Sets a minimum date for the date picker. This date is exclusive.
	 * 
	 * @param minDate minimum date or <code>null</code> for no limit.
	 * @return CDateTimeBuilder instance
	 * 
	 * @since 1.4.0
	 */
	public CDateTimeBuilder setMinDate(Calendar minDate) {
		this.minDate = minDate;
		return this;
	}
	
	/**
	 * Sets a maximum date for the date picker. This date is inclusive.
	 * 
	 * @param maxDate maximum date or <code>null</code> for no limit.
	 * @return CDateTimeBuilder instance
	 * 
	 * @since 1.4.0
	 */
	public CDateTimeBuilder setMaxDate(Calendar maxDate) {
		this.maxDate = maxDate;
		return this;
	}
}
