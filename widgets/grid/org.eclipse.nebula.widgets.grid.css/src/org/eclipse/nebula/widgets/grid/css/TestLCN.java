package org.eclipse.nebula.widgets.grid.css;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

public class TestLCN {

	public static void main(final String[] args) throws IOException {
		final Enumeration<URL> e = TestLCN.class.getClassLoader().getResources("org/w3c/dom/css/CSSPrimitiveValue.class");
		Collections.list(e).forEach(System.out::println);
	}

}
