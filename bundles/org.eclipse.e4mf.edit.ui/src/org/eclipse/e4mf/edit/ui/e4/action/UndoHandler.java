package org.eclipse.e4mf.edit.ui.e4.action;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4mf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;

@SuppressWarnings("restriction")
public class UndoHandler {

	private static final String UNDO_COMMAND_ID = "org.eclipse.ui.edit.undo";

	@Inject
	@Optional
	protected EditingDomain domain;

	@Inject
	@Optional
	private ECommandService commandService;

	@CanExecute
	public boolean canExecute() {

//		org.eclipse.core.commands.Command command = commandService
//				.getCommand(UNDO_COMMAND_ID);

		boolean result = domain.getCommandStack().canUndo();

		String newText = null;
		String newDescription = null;

		Command undoCommand = domain.getCommandStack().getUndoCommand();
		if (undoCommand != null && undoCommand.getLabel() != null) {
			newText = EMFEditUIPlugin.INSTANCE.getString("_UI_Undo_menu_item",
					new Object[] { undoCommand.getLabel() });
			// setText();
		} else {
			newText = EMFEditUIPlugin.INSTANCE.getString("_UI_Undo_menu_item",
					new Object[] { "" });
			// setText();
		}

		if (undoCommand != null && undoCommand.getDescription() != null) {
			newDescription = EMFEditUIPlugin.INSTANCE.getString(
					"_UI_Undo_menu_item_description",
					new Object[] { undoCommand.getDescription() });
			// setDescription();
		} else {
			newDescription = EMFEditUIPlugin.INSTANCE
					.getString("_UI_Undo_menu_item_simple_description");
			// setDescription();
		}

		System.out.println("Undo Handler: debug: " + newText + newDescription);

		return result;
	}

	@Execute
	public void execute() {
		domain.getCommandStack().undo();
	}

}
