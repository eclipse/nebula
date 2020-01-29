/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.picture;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.nebula.widgets.picture.forms.FormPictureControl;
import org.eclipse.nebula.widgets.picture.internal.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Picture Control gives you the capability to display an image picture in a SWT
 * Label and change it with "Modify" Link. This class is abstract must be
 * implemented to override methodes whicg create SWT {@link Label}, SWT
 * {@link Composite} and Link according if you use only SWT (see
 * {@link PictureControl}) or SWT Form Toolkit (see {@link FormPictureControl}.
 *
 * @param <T>
 *            the "Modify" Link control used to upload a new image picture.
 */
public abstract class AbstractPictureControl<T extends Control> extends
		Composite implements PropertyChangeListener {

	/** Bundle name constant */
	public static final String BUNDLE_NAME = "org.eclipse.nebula.widgets.picture.resources"; //$NON-NLS-1$

	/** Resources constants */
	private static final String PICTURE_CONTROL_DELETE = "PictureControl.delete";
	private static final String PICTURE_CONTROL_MODIFY = "PictureControl.modify";
	private static final String PICTURE_CONTROL_FILEDIALOG_TEXT = "PictureControl.fileDialog.text";

	public static final String IMAGE_BYTEARRAY_PROPERTY = "imageByteArray";

	private static final String[] DEFAULT_EXTENSIONS;

	static {
		DEFAULT_EXTENSIONS = ImageFilterExtension.createFilterExtension(true,
				ImageFilterExtension.values());
	}

	/** Picture label which host the picture image **/
	private Label pictureLabel;
	/** "Modify" image link **/
	private T modifyImageLink;
	/** "Delete" image link **/
	private T deleteImageLink;
	/** Current picture image byte array **/
	private byte[] imageByteArray;
	/** Current resized picture image **/
	private Image resizedPictureImage;
	/** The maximum width of the picture image **/
	private Integer maxImageWidth = 96;
	/** The minimum height of the picture image **/
	private Integer maxImageHeight = 96;

	/** Resources bundle */
	protected ResourceBundle resources;

	/** Default image picture to display when none picture is displayed **/
	private Image defaultImage;

	private String[] extensions;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/** The SWT GridData of the picture label **/
	private GridData pictureLabelImageGridData;

	private MenuItem deleteItem;

	/**
	 * Constructor for {@link AbstractPictureControl} with default SWT styles.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 */
	public AbstractPictureControl(Composite parent) {
		this(parent, SWT.NONE, SWT.BORDER | SWT.CENTER, SWT.NONE, true);
	}

	/**
	 * Constructor for {@link AbstractPictureControl} with given SWT style .
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 *
	 * @param compositeStyle
	 *            SWT style of the SWT Composite which host Label+Link controls.
	 * @param labelStyle
	 *            SWT style of the Label control.
	 * @param linkStyle
	 *            SWT style of the Link control.
	 */
	public AbstractPictureControl(Composite parent, int compositeStyle,
			int labelStyle, int linkStyle) {
		this(parent, compositeStyle, labelStyle, linkStyle, true);
	}

	/**
	 * Constructor for {@link AbstractPictureControl} with given SWT styles.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 *
	 * @param compositeStyle
	 *            SWT style of the SWT Composite which host Label+Link controls.
	 * @param labelStyle
	 *            SWT style of the Label control.
	 * @param linkStyle
	 *            SWT style of the Link control.
	 * @param createUI
	 *            true if UI must be created and false otherwise.
	 */
	protected AbstractPictureControl(Composite parent, int compositeStyle,
			int labelStyle, int linkStyle, boolean createUI) {
		super(parent, compositeStyle);
		setLocale(Locale.getDefault());
		setFilterExtensions(DEFAULT_EXTENSIONS);
		if (createUI) {
			createUI(labelStyle, linkStyle);
		}
	}

	/**
	 * Create the UI picture control composed with Label (for host the image
	 * picture)
	 *
	 * @param labelStyle
	 *            the SWT label style.
	 * @param linkStyle
	 *            the link style.
	 */
	protected Composite createUI(int labelStyle, int linkStyle) {
		// Layout of the global COmposite
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginTop = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.setLayout(layout);

		// Create internal composite
		Composite parent = createComposite(this, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		GridData g = new GridData();
		g.verticalAlignment = SWT.TOP;
		parent.setLayoutData(g);

		// Create Label to host the image picture.
		this.pictureLabel = createLabelImage(parent, labelStyle);
		// Create the link to "Modify" the image
		this.modifyImageLink = createModifyLink(parent, linkStyle);
		this.deleteImageLink = createDeleteLink(parent, linkStyle);
		setDeleteLinkEnabled(false);
		return parent;
	}

	/**
	 * Create the SWT {@link Label} to host the image picture.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @return
	 */
	protected Label createLabelImage(Composite parent, int style) {
		// Create a Label
		Label label = createLabel(parent, style);
		// Create SWT GridData. the size is managed with maxImageWidth and
		// maxImageHeight
		pictureLabelImageGridData = new GridData();
		pictureLabelImageGridData.horizontalAlignment = SWT.CENTER;
		pictureLabelImageGridData.verticalAlignment = SWT.CENTER;
		pictureLabelImageGridData.horizontalSpan = 2;
		label.setLayoutData(pictureLabelImageGridData);
		setMaxImageWidth(maxImageWidth);
		setMaxImageHeight(maxImageHeight);

		// Create a menu with "Delete", "Modify" Item.
		Menu menu = createMenu(label);
		if (menu != null) {
			label.setMenu(menu);
		}
		return label;
	}

	/**
	 * Create the menu with "Delete", "Modify" Item.
	 *
	 * @param parent
	 * @return
	 */
	protected Menu createMenu(Control parent) {
		Menu menu = new Menu(parent);

		// "Delete" menu item.
		deleteItem = new MenuItem(menu, SWT.NONE);
		deleteItem.setText(resources.getString(PICTURE_CONTROL_DELETE));
		deleteItem.addListener(SWT.Selection, e -> {
			// Delete the image.
			AbstractPictureControl.this.handleDeleteImage();
		});

		// "Modify" menu item.
		final MenuItem modifyItem = new MenuItem(menu, SWT.NONE);
		modifyItem.setText(resources.getString(PICTURE_CONTROL_MODIFY));
		modifyItem.addListener(SWT.Selection, e -> {
			// Modify the image.
			AbstractPictureControl.this.handleModifyImage();
		});
		return menu;
	}

	/**
	 * Create the "Modify" Link to open Explorer File to select a new image.
	 *
	 * @param parent
	 * @param style
	 * @return
	 */
	private T createModifyLink(Composite parent, int style) {
		T modifyImageLink = createLink(parent, style);
		GridData gridData = new GridData(GridData.CENTER, GridData.CENTER,
				true, false);
		modifyImageLink.setLayoutData(gridData);
		setLinkText(modifyImageLink,
				resources.getString(PICTURE_CONTROL_MODIFY));
		addModifyImageHandler(modifyImageLink);
		return modifyImageLink;
	}

	/**
	 * Create the "Delete" Link to delete the current image.
	 *
	 * @param parent
	 * @param style
	 * @return
	 */
	private T createDeleteLink(Composite parent, int style) {
		T deleteImageLink = createLink(parent, style);
		GridData gridData = new GridData(GridData.CENTER, GridData.CENTER,
				true, false);
		deleteImageLink.setLayoutData(gridData);
		setLinkText(deleteImageLink,
				resources.getString(PICTURE_CONTROL_DELETE));
		addDeleteImageHandler(deleteImageLink);
		return deleteImageLink;
	}

	/**
	 * Set the text of the "Modify" Link.
	 *
	 * @param text
	 */
	public void setModifyImageLinkText(String text) {
		setLinkText(getModifyImageLink(), text);
	}

	/**
	 * Set the text of the "Modify" Link.
	 *
	 * @param text
	 */
	public void setDeleteImageLinkText(String text) {
		setLinkText(getDeleteImageLink(), text);
	}

	/**
	 * Delete the current image picture.
	 */
	protected void handleDeleteImage() {
		setImageByteArray(null);
	}

	/**
	 * Set the file extensions which the dialog will use to filter the files it
	 * shows to the argument, which may be null.
	 * <p>
	 * The strings are platform specific. For example, on some platforms, an
	 * extension filter string is typically of the form "*.extension", where
	 * "*.*" matches all files. For filters with multiple extensions, use
	 * semicolon as a separator, e.g. "*.jpg;*.png".
	 * </p>
	 *
	 * @param extensions
	 *            the file extension filter
	 *
	 * @see #setFilterNames to specify the user-friendly names corresponding to
	 *      the extensions
	 */
	public void setFilterExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	/**
	 * Open the Explorer File to select a new image.
	 */
	protected void handleModifyImage() {
		FileDialog fd = new FileDialog(this.getShell(), getFileDialogStyle());
		configure(fd);
		String selected = fd.open();
		if (selected != null && selected.length() > 0) {
			File f = new File(selected);
			try {
				FileInputStream in = new FileInputStream(f);
				setImageStream(in);
			} catch (Throwable e) {
				handleError(e);
			}
		}
	}

	/**
	 * Configure the {@link FileDialog} to set the file extension, the text,
	 * etc. This method can be override to custome the configuration.
	 *
	 * @param fd
	 */
	protected void configure(FileDialog fd) {
		fd.setText(resources.getString(PICTURE_CONTROL_FILEDIALOG_TEXT));
		if (extensions != null) {
			fd.setFilterExtensions(extensions);
		}
	}

	/**
	 * Returns the {@link FileDialog}SWT style. This method can be override if
	 * the SWT style should be customized.
	 *
	 * @return
	 */
	protected int getFileDialogStyle() {
		return SWT.SHELL_TRIM | SWT.SINGLE | SWT.APPLICATION_MODAL;
	}

	/**
	 * Handle error when file selected cannot be loaded as Image.
	 *
	 * @param e
	 */
	protected void handleError(Throwable e) {
		e.printStackTrace();
	}

	/**
	 * Returns the picture label which hosts the picture image.
	 *
	 * @return
	 */
	public Label getPictureLabel() {
		return pictureLabel;
	}

	/**
	 * Returns the "Modify" Link control used to open Explorer files to change
	 * image.
	 *
	 * @return
	 */
	public T getModifyImageLink() {
		return modifyImageLink;
	}

	/**
	 * Returns the "Delete" Link control used to open Explorer files to change
	 * image.
	 *
	 * @return
	 */
	public T getDeleteImageLink() {
		return deleteImageLink;
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void propertyChange(PropertyChangeEvent arg0) {

	}

	/**
	 * Set the current {@link InputStream} of the image picture. null is
	 * accepted to delete the image.
	 *
	 * @param stream
	 * @throws IOException
	 */
	public void setImageStream(InputStream stream) throws IOException {
		if (stream == null) {
			setImageByteArray(null);
		} else {
			setImageByteArray(IOUtils.toByteArray(stream));
		}
	}

	/**
	 * Returns the {@link InputStream} of the image picture and null if none
	 * picture was setted.
	 *
	 * @return
	 */
	public InputStream getImageStream() {
		byte[] imageByteArray = getImageByteArray();
		if (imageByteArray == null) {
			return null;
		}
		return new ByteArrayInputStream(imageByteArray);
	}

	/**
	 * Set the current byte array of the image picture. null is accepted to
	 * delete the image.
	 *
	 * @param imageByteArray
	 */
	public void setImageByteArray(byte[] imageByteArray) {
		byte[] oldImageByteArray = this.imageByteArray;
		this.imageByteArray = imageByteArray;
		// Dispose the old image.
		disposePictureImage();

		if (imageByteArray != null) {
			// byte array is not null.
			// Create ImageData
			ImageData imageData = new ImageData(new ByteArrayInputStream(
					imageByteArray));
			// Resize image if needed.
			final ImageData resizedImageData = getResizedImageData(imageData);
			this.resizedPictureImage = new Image(super.getDisplay(),
					resizedImageData);
			// Set the new image
			pictureLabel.setImage(resizedPictureImage);
			setDeleteLinkEnabled(true);
		} else {
			// byte array is null
			// default image was defined, set as the image.
			pictureLabel.setImage(defaultImage);
			setDeleteLinkEnabled(false);
		}
		propertyChangeSupport.firePropertyChange(IMAGE_BYTEARRAY_PROPERTY,
				oldImageByteArray, imageByteArray);
	}

	/**
	 * Returns the resized {@link ImageData}. This method can be override if
	 * scale logic doen't please you.
	 *
	 * @param imageData
	 * @return
	 */
	protected ImageData getResizedImageData(ImageData imageData) {
		int height = imageData.height;
		int width = imageData.width;
		if (height <= maxImageHeight) {
			return imageData;
		}
		int newHeight = maxImageHeight;
		float w = width;
		float h = height;
		float nw = (w / h) * maxImageHeight;
		int newWidth = (int) nw;
		return imageData.scaledTo(newWidth, newHeight);
	}

	/**
	 * Returns the byte array of the image picture and null if none picture was
	 * setted.
	 *
	 * @return
	 */
	public byte[] getImageByteArray() {
		return imageByteArray;
	}

	/**
	 * Sets a new locale to use for picture controle. Locale will choose the
	 * well resources bundle.
	 *
	 * @param locale
	 *            new locale (must not be null)
	 */
	public void setLocale(Locale locale) {
		checkWidget();
		if (locale == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);

		// Loads the resources
		resources = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	/**
	 * Set the maximum height of the image.
	 *
	 * @param maxImageHeight
	 */
	public void setMaxImageHeight(Integer maxImageHeight) {
		this.maxImageHeight = maxImageHeight;
		if (maxImageHeight != null) {
			pictureLabelImageGridData.heightHint = maxImageHeight;
		} else {
			pictureLabelImageGridData.heightHint = SWT.DEFAULT;
		}
	}

	/**
	 * Returns the maximum height of the image.
	 *
	 * @param maxImageHeight
	 *
	 * @return
	 */
	public Integer getMaxImageHeight() {
		return maxImageHeight;
	}

	/**
	 * Set the maximum width of the image.
	 *
	 * @param maxImageWidth
	 */
	public void setMaxImageWidth(Integer maxImageWidth) {
		this.maxImageWidth = maxImageWidth;
		if (maxImageWidth != null) {
			pictureLabelImageGridData.widthHint = maxImageWidth;
		} else {
			pictureLabelImageGridData.widthHint = SWT.DEFAULT;
		}
	}

	/**
	 * Returns the maximum width of the image.
	 *
	 * @param maxImageWidth
	 *
	 * @return
	 */
	public Integer getMaxImageWidth() {
		return maxImageWidth;
	}

	/**
	 * Set the default image for the picture. The default image doesn't store
	 * the input stream of the image in this control. It is used just to display
	 * an "empty" picture and set the maximum/minimum width of the picture
	 * Label.
	 *
	 * @param defaultImage
	 */
	public void setDefaultImage(Image defaultImage) {
		this.defaultImage = defaultImage;
		ImageData imageData = defaultImage.getImageData();
		setMaxImageHeight(imageData.height);
		setMaxImageWidth(imageData.width);
		setImageByteArray(null);
	}

	private void setDeleteLinkEnabled(boolean enabled) {
		deleteImageLink.setEnabled(enabled);
		if (deleteItem != null) {
			deleteItem.setEnabled(enabled);
		}
	}

	/**
	 * Dispose the current image if needed.s
	 */
	private void disposePictureImage() {
		if (this.resizedPictureImage != null
				&& !resizedPictureImage.isDisposed()) {
			this.resizedPictureImage.dispose();
		}
	}

	@Override
	public void dispose() {
		disposePictureImage();
		super.dispose();
	}

	/**
	 * Create a SWT {@link Label}.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @return
	 */
	protected abstract Label createLabel(Composite parent, int style);

	/**
	 * Create a SWT control for the "Modify" Link.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @return
	 */
	protected abstract T createLink(Composite parent, int style);

	/**
	 * Create a SWT {@link Composite}.
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @return
	 */
	protected abstract Composite createComposite(Composite parent, int style);

	/**
	 * Set the text of a Link control.
	 *
	 * @param modifyImageLink
	 * @param text
	 */
	protected abstract void setLinkText(T link, String text);

	/**
	 * Add the handler to open Explorer files to the Link control.
	 *
	 * @param modifyImageLink
	 */
	protected abstract void addModifyImageHandler(T modifyImageLink);

	/**
	 * Add the handler to delete the image to the Link control.
	 *
	 * @param modifyImageLink
	 */
	protected abstract void addDeleteImageHandler(T deleteImageLink);

}
