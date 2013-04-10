/**
 * Copyright (c) 2002-2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;

/**
 * A sibling creation action is implemented by creating a
 * {@link CreateChildCommand}.
 */
public class CreateSiblingAction extends StaticSelectionCommandAction {
	/**
	 * This describes the sibling to be created.
	 */
	protected Object descriptor;

	/**
	 * This constructs an instance of an action that uses the given editing
	 * domain to create a sibling specified by <code>descriptor</code>.
	 * 
	 * @since 2.4.0
	 */
	public CreateSiblingAction(EditingDomain editingDomain,
			ISelection selection, Object descriptor) {
		super(editingDomain);
		this.descriptor = descriptor;
		configureAction(selection);
	}

	/**
	 * This creates the command for
	 * {@link StaticSelectionCommandAction#createActionCommand}.
	 */
	@Override
	protected Command createActionCommand(EditingDomain editingDomain,
			Collection<?> collection) {
		if (collection.size() == 1) {
			return CreateChildCommand.create(editingDomain, null, descriptor,
					collection);
		}
		return UnexecutableCommand.INSTANCE;
	}
}
