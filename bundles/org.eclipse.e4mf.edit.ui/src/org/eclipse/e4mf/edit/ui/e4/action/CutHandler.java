package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CutToClipboardCommand;

public class CutHandler extends EditingDomainHandler {

	public Command createCommand(Collection<?> selection) {
		return CutToClipboardCommand.create(editingDomain, selection);
	}
}
