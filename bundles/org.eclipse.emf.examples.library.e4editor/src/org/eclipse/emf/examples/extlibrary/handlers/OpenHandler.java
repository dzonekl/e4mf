/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class OpenHandler {

	@Inject
	private HandlerSupport support;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		String[] filePaths = support.openFilePathDialog(shell, SWT.OPEN, null);
		if (filePaths.length > 0) {
			support.openEditor(filePaths[0]);
		}
	}
}
