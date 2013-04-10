/**
 * Copyright (c) 2002-2006 IBM Corporation and others.
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

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4mf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

/**
 * A copy action is implemented by creating a {@link CopyToClipboardCommand}.
 */
public class CopyAction extends CommandActionHandler {

	@CanExecute
	public boolean canExecute() {
		return this.isEnabled();
	}

	@Execute
	public void execute() {
		this.run();
	}

	public CopyAction(EditingDomain domain) {
		super(domain, EMFEditUIPlugin.INSTANCE.getString("_UI_Copy_menu_item"));
	}

	public CopyAction() {
		super(null, EMFEditUIPlugin.INSTANCE.getString("_UI_Copy_menu_item"));
	}

	@Override
	public Command createCommand(Collection<?> selection) {
		return CopyToClipboardCommand.create(domain, selection);
	}

	/**
	 * @since 2.1.0
	 */
	public void setActiveContribution(Object contribution) {
		if (contribution instanceof IEditingDomainProvider) {
			domain = ((IEditingDomainProvider) contribution).getEditingDomain();
		}
	}
}
