/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import javax.xml.stream.events.EntityReference;

/**
 * Default implementation of {@link EntityReplacer} that knows about the most common HTML entities.
 */
public class DefaultEntityReplacer implements EntityReplacer {

	@Override
	public String getEntityReferenceValue(EntityReference reference) {
		String entity = reference.getName();
		String value = null;
		switch (entity) {
			case "quot":
				value = "\\u0022";
				break;
			case "amp":
				value = "\u0026";
				break;
			case "apos":
				value = "\u0027";
				break;
			case "lt":
				value = "\u003c";
				break;
			case "gt":
				value = "\u003e";
				break;
			case "nbsp":
				value = "\u00a0";
				break;
			case "iexcl":
				value = "\u00a1";
				break;
			case "cent":
				value = "\u00a2";
				break;
			case "pound":
				value = "\u00a3";
				break;
			case "curren":
				value = "\u00a4";
				break;
			case "yen":
				value = "\u00a5";
				break;
			case "brvbar":
				value = "\u00a6";
				break;
			case "sect":
				value = "\u00a7";
				break;
			case "uml":
				value = "\u00a8";
				break;
			case "copy":
				value = "\u00a9";
				break;
			case "ordf":
				value = "\u00aa";
				break;
			case "laquo":
				value = "\u00ab";
				break;
			case "not":
				value = "\u00ac";
				break;
			case "shy":
				value = "\u00ad";
				break;
			case "reg":
				value = "\u00ae";
				break;
			case "macr":
				value = "\u00af";
				break;
			case "deg":
				value = "\u00b0";
				break;
			case "plusmn":
				value = "\u00b1";
				break;
			case "sup2":
				value = "\u00b2";
				break;
			case "sup3":
				value = "\u00b3";
				break;
			case "acute":
				value = "\u00b4";
				break;
			case "micro":
				value = "\u00b5";
				break;
			case "para":
				value = "\u00b6";
				break;
			case "middot":
				value = "\u00b7";
				break;
			case "cedil":
				value = "\u00b8";
				break;
			case "sup1":
				value = "\u00b9";
				break;
			case "ordm":
				value = "\u00ba";
				break;
			case "raquo":
				value = "\u00bb";
				break;
			case "frac14":
				value = "\u00bc";
				break;
			case "frac12":
				value = "\u00bd";
				break;
			case "frac34":
				value = "\u00be";
				break;
			case "iquest":
				value = "\u00bf";
				break;
			case "Agrave":
				value = "\u00c0";
				break;
			case "Aacute":
				value = "\u00c1";
				break;
			case "Acirc":
				value = "\u00c2";
				break;
			case "Atilde":
				value = "\u00c3";
				break;
			case "Auml":
				value = "\u00c4";
				break;
			case "Aring":
				value = "\u00c5";
				break;
			case "AElig":
				value = "\u00c6";
				break;
			case "Ccedil":
				value = "\u00c7";
				break;
			case "Egrave":
				value = "\u00c8";
				break;
			case "Eacute":
				value = "\u00c9";
				break;
			case "Ecirc":
				value = "\u00ca";
				break;
			case "Euml":
				value = "\u00cb";
				break;
			case "Igrave":
				value = "\u00cc";
				break;
			case "Iacute":
				value = "\u00cd";
				break;
			case "Icirc":
				value = "\u00ce";
				break;
			case "Iuml":
				value = "\u00cf";
				break;
			case "ETH":
				value = "\u00d0";
				break;
			case "Ntilde":
				value = "\u00d1";
				break;
			case "Ograve":
				value = "\u00d2";
				break;
			case "Oacute":
				value = "\u00d3";
				break;
			case "Ocirc":
				value = "\u00d4";
				break;
			case "Otilde":
				value = "\u00d5";
				break;
			case "Ouml":
				value = "\u00d6";
				break;
			case "times":
				value = "\u00d7";
				break;
			case "Oslash":
				value = "\u00d8";
				break;
			case "Ugrave":
				value = "\u00d9";
				break;
			case "Uacute":
				value = "\u00da";
				break;
			case "Ucirc":
				value = "\u00db";
				break;
			case "Uuml":
				value = "\u00dc";
				break;
			case "Yacute":
				value = "\u00dd";
				break;
			case "THORN":
				value = "\u00de";
				break;
			case "szlig":
				value = "\u00df";
				break;
			case "agrave":
				value = "\u00e0";
				break;
			case "aacute":
				value = "\u00e1";
				break;
			case "acirc":
				value = "\u00e2";
				break;
			case "atilde":
				value = "\u00e3";
				break;
			case "auml":
				value = "\u00e4";
				break;
			case "aring":
				value = "\u00e5";
				break;
			case "aelig":
				value = "\u00e6";
				break;
			case "ccedil":
				value = "\u00e7";
				break;
			case "egrave":
				value = "\u00e8";
				break;
			case "eacute":
				value = "\u00e9";
				break;
			case "ecirc":
				value = "\u00ea";
				break;
			case "euml":
				value = "\u00eb";
				break;
			case "igrave":
				value = "\u00ec";
				break;
			case "iacute":
				value = "\u00ed";
				break;
			case "icirc":
				value = "\u00ee";
				break;
			case "iuml":
				value = "\u00ef";
				break;
			case "ntilde":
				value = "\u00f1";
				break;
			case "ograve":
				value = "\u00f2";
				break;
			case "oacute":
				value = "\u00f3";
				break;
			case "ocirc":
				value = "\u00f4";
				break;
			case "otilde":
				value = "\u00f5";
				break;
			case "ouml":
				value = "\u00f6";
				break;
			case "divide":
				value = "\u00f7";
				break;
			case "oslash":
				value = "\u00f8";
				break;
			case "ugrave":
				value = "\u00f9";
				break;
			case "uacute":
				value = "\u00fa";
				break;
			case "ucirc":
				value = "\u00fb";
				break;
			case "uuml":
				value = "\u00fc";
				break;
			case "yacute":
				value = "\u00fd";
				break;
			case "thorn":
				value = "\u00fe";
				break;
			case "yuml":
				value = "\u00ff";
				break;
			case "OElig":
				value = "\u0152";
				break;
			case "oelig":
				value = "\u0153";
				break;
			case "Scaron":
				value = "\u0160";
				break;
			case "scaron":
				value = "\u0161";
				break;
			case "Yuml":
				value = "\u0178";
				break;
			case "fnof":
				value = "\u0192";
				break;
			case "circ":
				value = "\u02c6";
				break;
			case "tilde":
				value = "\u02dc";
				break;
			case "Alpha":
				value = "\u0391";
				break;
			case "Beta":
				value = "\u0392";
				break;
			case "Gamma":
				value = "\u0393";
				break;
			case "Delta":
				value = "\u0394";
				break;
			case "Epsilon":
				value = "\u0395";
				break;
			case "Zeta":
				value = "\u0396";
				break;
			case "Eta":
				value = "\u0397";
				break;
			case "Theta":
				value = "\u0398";
				break;
			case "Iota":
				value = "\u0399";
				break;
			case "Kappa":
				value = "\u039a";
				break;
			case "Lambda":
				value = "\u039b";
				break;
			case "Mu":
				value = "\u039c";
				break;
			case "Nu":
				value = "\u039d";
				break;
			case "Xi":
				value = "\u039e";
				break;
			case "Omicron":
				value = "\u039f";
				break;
			case "Pi":
				value = "\u03a0";
				break;
			case "Rho":
				value = "\u03a1";
				break;
			case "Sigma":
				value = "\u03a3";
				break;
			case "Tau":
				value = "\u03a4";
				break;
			case "Upsilon":
				value = "\u03a5";
				break;
			case "Phi":
				value = "\u03a6";
				break;
			case "Chi":
				value = "\u03a7";
				break;
			case "Psi":
				value = "\u03a8";
				break;
			case "Omega":
				value = "\u03a9";
				break;
			case "alpha":
				value = "\u03b1";
				break;
			case "beta":
				value = "\u03b2";
				break;
			case "gamma":
				value = "\u03b3";
				break;
			case "delta":
				value = "\u03b4";
				break;
			case "epsilon":
				value = "\u03b5";
				break;
			case "zeta":
				value = "\u03b6";
				break;
			case "eta":
				value = "\u03b7";
				break;
			case "theta":
				value = "\u03b8";
				break;
			case "iota":
				value = "\u03b9";
				break;
			case "kappa":
				value = "\u03ba";
				break;
			case "lambda":
				value = "\u03bb";
				break;
			case "mu":
				value = "\u03bc";
				break;
			case "nu":
				value = "\u03bd";
				break;
			case "xi":
				value = "\u03be";
				break;
			case "omicron":
				value = "\u03bf";
				break;
			case "pi":
				value = "\u03c0";
				break;
			case "rho":
				value = "\u03c1";
				break;
			case "sigmaf":
				value = "\u03c2";
				break;
			case "sigma":
				value = "\u03c3";
				break;
			case "tau":
				value = "\u03c4";
				break;
			case "upsilon":
				value = "\u03c5";
				break;
			case "phi":
				value = "\u03c6";
				break;
			case "chi":
				value = "\u03c7";
				break;
			case "psi":
				value = "\u03c8";
				break;
			case "omega":
				value = "\u03c9";
				break;
			case "thetasym":
				value = "\u03d1";
				break;
			case "upsih":
				value = "\u03d2";
				break;
			case "piv":
				value = "\u03d6";
				break;
			case "ndash":
				value = "\u2013";
				break;
			case "mdash":
				value = "\u2014";
				break;
			case "lsquo":
				value = "\u2018";
				break;
			case "rsquo":
				value = "\u2019";
				break;
			case "sbquo":
				value = "\u201a";
				break;
			case "ldquo":
				value = "\u201c";
				break;
			case "rdquo":
				value = "\u201d";
				break;
			case "bdquo":
				value = "\u201e";
				break;
			case "dagger":
				value = "\u2020";
				break;
			case "Dagger":
				value = "\u2021";
				break;
			case "bull":
				value = "\u2022";
				break;
			case "hellip":
				value = "\u2026";
				break;
			case "permil":
				value = "\u2030";
				break;
			case "prime":
				value = "\u2032";
				break;
			case "Prime":
				value = "\u2033";
				break;
			case "lsaquo":
				value = "\u2039";
				break;
			case "rsaquo":
				value = "\u203a";
				break;
			case "oline":
				value = "\u203e";
				break;
			case "frasl":
				value = "\u2044";
				break;
			case "euro":
				value = "\u20ac";
				break;
			case "image":
				value = "\u2111";
				break;
			case "weierp":
				value = "\u2118";
				break;
			case "real":
				value = "\u211c";
				break;
			case "trade":
				value = "\u2122";
				break;
			case "alefsym":
				value = "\u2135";
				break;
			case "larr":
				value = "\u2190";
				break;
			case "uarr":
				value = "\u2191";
				break;
			case "rarr":
				value = "\u2192";
				break;
			case "darr":
				value = "\u2193";
				break;
			case "harr":
				value = "\u2194";
				break;
			case "crarr":
				value = "\u21b5";
				break;
			case "lArr":
				value = "\u21d0";
				break;
			case "uArr":
				value = "\u21d1";
				break;
			case "rArr":
				value = "\u21d2";
				break;
			case "dArr":
				value = "\u21d3";
				break;
			case "hArr":
				value = "\u21d4";
				break;
			case "forall":
				value = "\u2200";
				break;
			case "part":
				value = "\u2202";
				break;
			case "exist":
				value = "\u2203";
				break;
			case "empty":
				value = "\u2205";
				break;
			case "nabla":
				value = "\u2207";
				break;
			case "isin":
				value = "\u2208";
				break;
			case "notin":
				value = "\u2209";
				break;
			case "ni":
				value = "\u220b";
				break;
			case "prod":
				value = "\u220f";
				break;
			case "sum":
				value = "\u2211";
				break;
			case "minus":
				value = "\u2212";
				break;
			case "lowast":
				value = "\u2217";
				break;
			case "radic":
				value = "\u221a";
				break;
			case "prop":
				value = "\u221d";
				break;
			case "infin":
				value = "\u221e";
				break;
			case "ang":
				value = "\u2220";
				break;
			case "and":
				value = "\u2227";
				break;
			case "or":
				value = "\u2228";
				break;
			case "cap":
				value = "\u2229";
				break;
			case "cup":
				value = "\u222a";
				break;
			case "int":
				value = "\u222b";
				break;
			case "there4":
				value = "\u2234";
				break;
			case "sim":
				value = "\u223c";
				break;
			case "cong":
				value = "\u2245";
				break;
			case "asymp":
				value = "\u2248";
				break;
			case "ne":
				value = "\u2260";
				break;
			case "equiv":
				value = "\u2261";
				break;
			case "le":
				value = "\u2264";
				break;
			case "ge":
				value = "\u2265";
				break;
			case "sub":
				value = "\u2282";
				break;
			case "sup":
				value = "\u2283";
				break;
			case "nsub":
				value = "\u2284";
				break;
			case "sube":
				value = "\u2286";
				break;
			case "supe":
				value = "\u2287";
				break;
			case "oplus":
				value = "\u2295";
				break;
			case "otimes":
				value = "\u2297";
				break;
			case "perp":
				value = "\u22a5";
				break;
			case "sdot":
				value = "\u22c5";
				break;
			case "lceil":
				value = "\u2308";
				break;
			case "rceil":
				value = "\u2309";
				break;
			case "lfloor":
				value = "\u230a";
				break;
			case "rfloor":
				value = "\u230b";
				break;
			case "lang":
				value = "\u2329";
				break;
			case "rang":
				value = "\u232a";
				break;
			case "loz":
				value = "\u25ca";
				break;
			case "spades":
				value = "\u2660";
				break;
			case "clubs":
				value = "\u2663";
				break;
			case "hearts":
				value = "\u2665";
				break;
			case "diams":
				value = "\u2666";
				break;

			default:
				value = "\u0026" + entity + ";";
				break;
		}
		return value;
	}

}
