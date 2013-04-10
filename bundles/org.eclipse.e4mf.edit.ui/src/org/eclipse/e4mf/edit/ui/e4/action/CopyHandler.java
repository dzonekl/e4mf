package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;

public class CopyHandler extends EditingDomainHandler {

	public Command createCommand(Collection<?> selection) {
		return CopyToClipboardCommand.create(editingDomain, selection);
	}
}
