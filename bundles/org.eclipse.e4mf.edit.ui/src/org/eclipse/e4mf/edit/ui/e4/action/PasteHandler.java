package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;

public class PasteHandler extends EditingDomainHandler {

	@Override
	public Command createCommand(Collection<?> selection) {
		if (selection.size() == 1) {
			return PasteFromClipboardCommand.create(editingDomain, selection
					.iterator().next(), null);
		} else {
			return UnexecutableCommand.INSTANCE;
		}
	}

}
