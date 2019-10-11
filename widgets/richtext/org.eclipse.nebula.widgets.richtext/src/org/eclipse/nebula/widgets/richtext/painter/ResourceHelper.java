/*****************************************************************************
 * Copyright (c) 2015, 2019 CEA LIST.
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

//TODO javadoc

public final class ResourceHelper {

	private ResourceHelper() {
	}

	public static Color getColor(String rgbString) {
		if (!JFaceResources.getColorRegistry().hasValueFor(rgbString)) {
			if (rgbString.startsWith("#")) {
				// decode hex to rgb
				Integer intval = Integer.decode(rgbString);
		        int i = intval.intValue();
		        RGB rgb = new RGB((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
		        JFaceResources.getColorRegistry().put(rgbString, rgb);
			} else {
				// rgb string in format rgb(r, g, b)
				String rgbValues = rgbString.substring(rgbString.indexOf('(') + 1, rgbString.lastIndexOf(')'));
				String[] values = rgbValues.split(",");
				try {
					int red = Integer.valueOf(values[0].trim());
					int green = Integer.valueOf(values[1].trim());
					int blue = Integer.valueOf(values[2].trim());
					
					JFaceResources.getColorRegistry().put(rgbString, new RGB(red, green, blue));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		return JFaceResources.getColorRegistry().get(rgbString);
	}

	public static Font getFont(FontData... fontDatas) {
		StringBuilder keyBuilder = new StringBuilder();
		for (FontData fontData : fontDatas) {
			keyBuilder.append(fontData.toString());
		}
		String key = keyBuilder.toString();

		if (!JFaceResources.getFontRegistry().hasValueFor(key)) {
			JFaceResources.getFontRegistry().put(key, fontDatas);
		}
		return JFaceResources.getFont(key);
	}

	public static Font getFont(Font currentFont, String name, Integer size) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			if (name != null) {
				data.setName(name);
			}
			if (size != null) {
				data.setHeight(size);
			}
		}

		return getFont(fontData);
	}

	public static Font getBoldFont(Font currentFont) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			data.setStyle(data.getStyle() | SWT.BOLD);
		}

		return getFont(fontData);
	}

	public static Font getItalicFont(Font currentFont) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			data.setStyle(data.getStyle() | SWT.ITALIC);
		}

		return getFont(fontData);
	}

	public static String ltrim(String s) {
		int i = 0;
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		return s.substring(i);
	}

	public static String rtrim(String s) {
		int i = s.length() - 1;
		while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}

	private static Path tempDir = null;

	public static URL getRichTextResource(final String resource) {
		if (tempDir == null) {
			extractResources();
		}

		Finder finder = new Finder(resource);
		if (tempDir != null) {
			try {
				Files.walkFileTree(tempDir, finder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return finder.result;
	}
	
	private static void extractResources() {
		if (tempDir == null) {
			URL jarURL = ResourceHelper.class.getProtectionDomain().getCodeSource().getLocation();
			File jarFileReference = null;
			if (jarURL.getProtocol().equals("file")) {
				try {
					String decodedPath = URLDecoder.decode(jarURL.getPath(), "UTF-8");
					jarFileReference = new File(decodedPath);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			else {
				// temporary download of jar file
				// necessary to be able to unzip the resources
				try {
					final Path jar = Files.createTempFile("richtext", ".jar");
					Files.copy(jarURL.openStream(), jar, StandardCopyOption.REPLACE_EXISTING);
					jarFileReference = jar.toFile();
					
					// delete the temporary file
					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							try {
								Files.delete(jar);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (jarFileReference != null) {
				try (JarFile jarFile = new JarFile(jarFileReference)) {
					String unpackDirectory = System.getProperty(RichTextEditor.JAR_UNPACK_LOCATION_PROPERTY);
					// create the directory to unzip to
					tempDir = (unpackDirectory == null)
							? Files.createTempDirectory("richtext")
							: Files.createDirectories(Paths.get(unpackDirectory));

					// only register the hook to delete the temp directory after shutdown if
					// a temporary directory was used
					if (unpackDirectory == null) {
						Runtime.getRuntime().addShutdownHook(new Thread() {
							@Override
							public void run() {
								try {
									Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
										@Override
										public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
											Files.delete(file);
											return FileVisitResult.CONTINUE;
										}

										@Override
										public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
											Files.delete(dir);
											return FileVisitResult.CONTINUE;
										}

									});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}

					Enumeration<JarEntry> entries = jarFile.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if (name.startsWith("org/eclipse/nebula/widgets/richtext/resources")) {
							File file = new File(tempDir.toAbsolutePath() + File.separator + name);
							if (!file.exists()) {
								if (entry.isDirectory()) {
									file.mkdirs();
								} else {
									try (InputStream is = jarFile.getInputStream(entry);
											OutputStream os = new FileOutputStream(file)) {
										while (is.available() > 0) {
											os.write(is.read());
										}
									}
								}
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class Finder extends SimpleFileVisitor<Path> {
		
		private URL result;
		private String resource;
		
		Finder(String resource) {
			this.resource = resource;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (file.endsWith(resource)) {
				this.result = file.toFile().toURI().toURL();
				return FileVisitResult.TERMINATE;
			}
			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if (dir.endsWith(resource)) {
				this.result = dir.toFile().toURI().toURL();
				return FileVisitResult.TERMINATE;
			}
			return FileVisitResult.CONTINUE;
		}

	}
}
