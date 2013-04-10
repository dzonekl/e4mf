package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;

@SuppressWarnings("restriction")
public abstract class EditingDomainHandler {
	private Command c;

	protected EditingDomain editingDomain;

	@CanExecute
	public boolean canExecute(
			@Named(IServiceConstants.ACTIVE_SELECTION) Object selection,
			IEclipseContext context) {
		editingDomain = context.get(EditingDomain.class);

		if (editingDomain == null) {
			return false;
		}

		List<Object> collection = new ArrayList<Object>();
		collection.add(selection);
		c = createCommand(collection);
		return c.canExecute();
	}

	@Execute
	public void execute() {
		editingDomain.getCommandStack().execute(c);
	}

	public abstract Command createCommand(Collection<?> selection);
}
