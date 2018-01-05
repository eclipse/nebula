/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.loginDialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Instances of this class are Login Dialog box, which is composed of
 * <p>
 * <dl>
 * <dt><b>A login</b></dt>
 * <dt><b>A password</b></dt>
 * <dt><b>An image</b></dt>
 * <dd>(optional)</dd>
 * <dt><b>A description</b></dt>
 * <dd>(optional)</dd>
 * <dt><b>A checkbox "remember the password"</b></dt>
 * <dd>(optional)</dd>
 * </dl>
 * </p>
 */
public class LoginDialog {
	private Image image;
	private String description;
	private String login;
	private String password;
	private List<String> autorizedLogin;
	private boolean displayRememberPassword;
	private boolean rememberPassword;
	private LoginDialogVerifier verifier;

	private Shell shell;
	private boolean returnedValue;
	private Button buttonOk;

	/**
	 * Constructor
	 */
	public LoginDialog() {
		this.displayRememberPassword = true;
	}

	/**
	 * Open the Login box
	 * 
	 * @return <code>true</code> if the authentication is OK, <code>false</code>
	 *         if the user pressed on cancel.
	 */
	public boolean open() {
		if (this.verifier == null) {
			throw new IllegalArgumentException("Please set a verifier before opening the dialog box");
		}

		buildDialog();
		openShell();

		return this.returnedValue;
	}

	/**
	 * Build the dialog box
	 */
	private void buildDialog() {
		buildShell();
		buildImage();
		buildDescription();
		buildLogin();
		buildPassword();
		if (this.displayRememberPassword) {
			buildRememberPassword();
		}
		buildButtons();
	}

	/**
	 * Build the shell
	 */
	private void buildShell() {
		this.shell = new Shell(SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER);
		this.shell.setText(ResourceManager.getLabel(ResourceManager.LOGIN));
		this.shell.setLayout(new GridLayout(4, false));
	}

	/**
	 * Build the image on top of the login box. If no image has been set, create
	 * a default image
	 */
	private void buildImage() {
		final Canvas canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1);
		gridData.widthHint = 400;
		gridData.heightHint = 60;
		canvas.setLayoutData(gridData);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				e.gc.drawImage(LoginDialog.this.image == null ? createDefaultImage(e.width, e.height) : LoginDialog.this.image, 0, 0);
			}
		});

	}

	/**
	 * Create a default image. It is a port of the image used by the Login Box
	 * in the project SwingX
	 * 
	 * @param w width
	 * @param h height
	 * @return a default image (blue wave)
	 */
	private Image createDefaultImage(final int w, final int h) {
		final Display display = Display.getCurrent();
		final Color backgroundColor = new Color(display, 49, 121, 242);
		final Color gradientColor1 = new Color(display, 155, 185, 245);
		final Color gradientColor2 = new Color(display, 53, 123, 242);

		final Image img = new Image(display, w, h);
		final GC gc = new GC(img);
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setBackground(backgroundColor);
		gc.fillRectangle(0, 0, w, h);

		final Path curveShape = new Path(display);
		curveShape.moveTo(0, h * .6f);
		curveShape.cubicTo(w * .167f, h * 1.2f, w * .667f, h * -.5f, w, h * .75f);
		curveShape.lineTo(w, h);
		curveShape.lineTo(0, h);
		curveShape.lineTo(0, h * .8f);
		curveShape.close();

		final Pattern pattern = new Pattern(display, 0, 0, 1, h * 1.2f, gradientColor1, gradientColor2);
		gc.setBackgroundPattern(pattern);
		gc.fillPath(curveShape);

		final Font font = new Font(display, "Arial Bold", 30, SWT.NONE);
		gc.setFont(font);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		final Point textSize = gc.stringExtent(ResourceManager.getLabel(ResourceManager.LOGIN));
		gc.drawString(ResourceManager.getLabel(ResourceManager.LOGIN), (int) (w * .05f), (h - textSize.y) / 2, true);

		font.dispose();
		curveShape.dispose();
		pattern.dispose();
		backgroundColor.dispose();
		gradientColor1.dispose();
		gradientColor2.dispose();
		gc.dispose();
		return img;
	}

	/**
	 * Build the description part of the box
	 */
	private void buildDescription() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 4, 1);
		gridData.verticalIndent = 5;
		gridData.horizontalIndent = 5;
		label.setLayoutData(gridData);
		final Font bold = SWTGraphicUtil.buildFontFrom(label, SWT.BOLD);
		label.setFont(bold);
		SWTGraphicUtil.addDisposer(label, bold);

		if (this.description == null || this.description.trim().equals("")) {
			label.setText(" ");
		} else {
			label.setText(this.description);
		}
	}

	/**
	 * Build the login part of the box
	 */
	private void buildLogin() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.END, GridData.END, false, false, 1, 1);
		gridData.horizontalIndent = 35;
		gridData.verticalIndent = 15;
		label.setLayoutData(gridData);
		label.setText(ResourceManager.getLabel(ResourceManager.NAME));

		if (this.autorizedLogin != null && !this.autorizedLogin.isEmpty()) {
			// Combo
			buildLoginCombo();
		} else {
			// Text
			buildLoginText();
		}

	}

	private void buildLoginCombo() {
		final Combo combo = new Combo(this.shell, SWT.BORDER | SWT.READ_ONLY);

		combo.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 3, 1));
		for (final String loginToAdd : this.autorizedLogin) {
			combo.add(loginToAdd);
		}
		combo.setText(this.login == null ? "" : this.login);
		combo.setFocus();
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				LoginDialog.this.login = combo.getText();
				changeButtonOkState();
			}
		});
	}

	private void buildLoginText() {
		final Text text = new Text(this.shell, SWT.BORDER);
		text.setText(this.login == null ? "" : this.login);
		text.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 3, 1));
		text.setFocus();
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				LoginDialog.this.login = text.getText();
				changeButtonOkState();
			}
		});
	}

	/**
	 * Build the password part of the box
	 */
	private void buildPassword() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
		gridData.horizontalIndent = 35;
		label.setLayoutData(gridData);
		label.setText(ResourceManager.getLabel(ResourceManager.PASSWORD));

		final Text text = new Text(this.shell, SWT.PASSWORD | SWT.BORDER);
		text.setText(this.password == null ? "" : this.password);
		text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				LoginDialog.this.password = text.getText();
				changeButtonOkState();
			}
		});
	}

	/**
	 * Enable/Disable the button when the login and the password is empty (or
	 * not)
	 */
	private void changeButtonOkState() {
		final boolean loginEntered = this.login != null && !this.login.trim().equals("");
		final boolean passwordEntered = this.password != null && !this.password.trim().equals("");
		this.buttonOk.setEnabled(loginEntered && passwordEntered);
	}

	/**
	 * Build the "remember password" part of the box
	 */
	private void buildRememberPassword() {
		final Button checkbox = new Button(this.shell, SWT.CHECK);
		final GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 4, 1);
		gridData.horizontalIndent = 35;
		checkbox.setLayoutData(gridData);
		checkbox.setText(ResourceManager.getLabel(ResourceManager.REMEMBER_PASSWORD));
		checkbox.setSelection(this.rememberPassword);
	}

	/**
	 * Build the buttons
	 */
	private void buildButtons() {
		buildOkButton();
		buildCancelButton();
	}

	private void buildOkButton() {
		this.buttonOk = new Button(this.shell, SWT.PUSH);
		final GridData gdOk = new GridData(GridData.END, GridData.CENTER, true, false, 3, 1);
		gdOk.verticalIndent = 60;
		gdOk.minimumWidth = 80;
		this.buttonOk.setLayoutData(gdOk);
		this.buttonOk.setText(ResourceManager.getLabel(ResourceManager.OK));
		this.buttonOk.setEnabled(false);

		this.buttonOk.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				try {
					LoginDialog.this.verifier.authenticate(LoginDialog.this.login, LoginDialog.this.password);
					LoginDialog.this.returnedValue = true;
					LoginDialog.this.shell.dispose();
				} catch (final Exception e) {
					Dialog.error(ResourceManager.getLabel(ResourceManager.LOGIN_FAILED), e.getMessage());
					for (final Control control : LoginDialog.this.shell.getChildren()) {
						if (control instanceof Text || control instanceof Combo) {
							control.setFocus();
							break;
						}
					}
				}
			}
		});
	}

	private void buildCancelButton() {
		final Button buttonCancel = new Button(this.shell, SWT.PUSH);
		final GridData gdCancel = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gdCancel.widthHint = 80;
		gdCancel.verticalIndent = 60;
		buttonCancel.setLayoutData(gdCancel);
		buttonCancel.setText(ResourceManager.getLabel(ResourceManager.CANCEL));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				LoginDialog.this.returnedValue = false;
				LoginDialog.this.shell.dispose();
			}
		});
	}

	/**
	 * Open the shell
	 */
	private void openShell() {
		this.shell.setDefaultButton(this.buttonOk);
		this.shell.pack();
		this.shell.open();
		SWTGraphicUtil.centerShell(this.shell);
		while (!this.shell.isDisposed()) {
			if (!this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}
	}

	// ------------- Getters & Setters
	/**
	 * @return the image
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return this.login == null ? null : this.login.trim();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password == null ? null : this.password.trim();
	}

	/**
	 * @return the list of autorized logins
	 */
	public List<String> getAutorizedLogin() {
		return this.autorizedLogin;
	}

	/**
	 * @return <code>true</code> if the checkbox "remember the password" is
	 *         displayed, <code>false</code> otherwise
	 */
	public boolean isDisplayRememberPassword() {
		return this.displayRememberPassword;
	}

	/**
	 * @return <code>true</code> if the checkbox "remember the password" is
	 *         checked, <code>false</code> otherwise
	 */
	public boolean isRememberPassword() {
		return this.rememberPassword;
	}

	/**
	 * @return the verifier associated to this box
	 */
	public LoginDialogVerifier getVerifier() {
		return this.verifier;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(final Image image) {
		this.image = image;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @param autorizedLogin the list of autorized logins to set
	 */
	public void setAutorizedLogin(final List<String> autorizedLogin) {
		this.autorizedLogin = autorizedLogin;
	}

	/**
	 * @param autorizedLogin the list of autorized logins to set
	 */
	public void setAutorizedLogin(final String... autorizedLogin) {
		this.autorizedLogin = Arrays.asList(autorizedLogin);
	}

	/**
	 * @param displayRememberPassword if <code>true</code>, the checkbox
	 *            "remember the password" is displayed
	 */
	public void setDisplayRememberPassword(final boolean displayRememberPassword) {
		this.displayRememberPassword = displayRememberPassword;
	}

	/**
	 * @param rememberPassword if <code>true</code>, the checkbox
	 *            "remember the password" is selected
	 */
	public void setRememberPassword(final boolean rememberPassword) {
		this.rememberPassword = rememberPassword;
	}

	/**
	 * @param verifier the verifier to set
	 */
	public void setVerifier(final LoginDialogVerifier verifier) {
		this.verifier = verifier;
	}

}
