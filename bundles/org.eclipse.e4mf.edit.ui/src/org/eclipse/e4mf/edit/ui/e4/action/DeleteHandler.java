package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;

public class DeleteHandler extends EditingDomainHandler {

	@Inject
	@Optional
	private EditingDomainContribution contribution;

	public Command createCommand(Collection<?> selection) {
		
		boolean removeAllReferences = contribution != null ? contribution
				.removeAllReferencesOnDelete() : false;
				
		return removeAllReferences ? DeleteCommand.create(editingDomain,
				selection) : RemoveCommand.create(editingDomain, selection);
	}
}
