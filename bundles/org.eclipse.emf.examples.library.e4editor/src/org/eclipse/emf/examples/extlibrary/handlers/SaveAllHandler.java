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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
public class SaveAllHandler {
	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute(
	EPartService ePartService) throws InvocationTargetException,
			InterruptedException {
		ePartService.saveAll(false);
	}
}
