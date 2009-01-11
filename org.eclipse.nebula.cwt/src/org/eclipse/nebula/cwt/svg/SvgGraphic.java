package org.eclipse.nebula.cwt.svg;

import org.eclipse.nebula.cwt.svg.SvgPaint.PaintType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;


public abstract class SvgGraphic extends SvgElement {

	String title;
	String description;
	SvgFill fill;
	SvgStroke stroke;
	SvgTransform transform;
	
	SvgGraphic(SvgContainer container, String id) {
		super(container, id);
		fill = new SvgFill(this);
		stroke = new SvgStroke(this);
	}

	public abstract void apply(GC gc);
	
	public String getDescription() {
		return (description == null) ? null : description;
	}
	
	SvgFill getFill() {
		SvgFill df = new SvgFill(this);
		for(SvgElement el : getAncestry(this)) {
			if(el instanceof SvgGraphic) {
				SvgFill tmp = ((SvgGraphic) el).fill;
				if(tmp.type != null) {
					df.type = tmp.type;
				}
				if(tmp.color != null) {
					df.color = tmp.color;
				}
				if(tmp.linkId != null) {
					df.linkId = tmp.linkId;
				}
				if(tmp.opacity != null) {
					df.opacity = tmp.opacity;
				}
				if(tmp.rule != null) {
					df.rule = tmp.rule;
				}
			}
		}
		if(df.type == null) {
			df.type = PaintType.Color;
		}
		if(df.color == null) {
			df.color = 0;
		}
		if(df.opacity == null) {
			df.opacity = 1f;
		}
		if(df.rule == null) {
			df.rule = SWT.FILL_EVEN_ODD;
		}
		return df;
	}

	SvgStroke getStroke() {
		SvgStroke ds = new SvgStroke(this);
		for(SvgElement el : getAncestry(this)) {
			if(el instanceof SvgGraphic) {
				SvgStroke tmp = ((SvgGraphic) el).stroke;
				if(tmp.type != null) {
					ds.type = tmp.type;
				}
				if(tmp.color != null) {
					ds.color = tmp.color;
				}
				if(tmp.linkId != null) {
					ds.linkId = tmp.linkId;
				}
				if(tmp.opacity != null) {
					ds.opacity = tmp.opacity;
				}
				if(tmp.width != null) {
					ds.width = tmp.width;
				}
				if(tmp.lineCap != null) {
					ds.lineCap = tmp.lineCap;
				}
				if(tmp.lineJoin != null) {
					ds.lineJoin = tmp.lineJoin;
				}
			}
		}
		if(ds.type == null) {
			ds.type = PaintType.None;
		}
		if(ds.type != PaintType.None) {
			if(ds.color == null) {
				ds.color = 0;
			}
			if(ds.opacity == null) {
				ds.opacity = 1f;
			}
			if(ds.width == null) {
				ds.width = 1f;
			}
			if(ds.lineCap == null) {
				ds.lineCap = SWT.CAP_FLAT;
			}
			if(ds.lineJoin == null) {
				ds.lineJoin = SWT.JOIN_MITER;
			}
		}
		return ds;
	}
	
	Transform getTransform(GC gc) {
		Transform t = new Transform(gc.getDevice());
		gc.getTransform(t);
		for(SvgElement el : getAncestry(this)) {
			if(el instanceof SvgFragment) {
				SvgTransform st = ((SvgFragment) el).boundsTransform;
				if(!st.isIdentity()) {
					Transform tmp = new Transform(gc.getDevice());
					tmp.setElements(st.data[0], st.data[1], st.data[2], st.data[3], st.data[4], st.data[5]);
					t.multiply(tmp);
					tmp.dispose();
				}
			} else if(el instanceof SvgGraphic) {
				SvgTransform st = ((SvgGraphic) el).transform;
				while(st != null) {
					if(!st.isIdentity()) {
						Transform tmp = new Transform(gc.getDevice());
						tmp.setElements(st.data[0], st.data[1], st.data[2], st.data[3], st.data[4], st.data[5]);
						t.multiply(tmp);
						tmp.dispose();
					}
					st = st.next;
				}
			}
		}
		return t;
	}
	
	public String getTitle() {
		return (title == null) ? null : title;
	}

}
