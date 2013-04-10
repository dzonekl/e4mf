package org.eclipse.e4mf.edit.ui.e4.action;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.viewers.StructuredSelection;

@SuppressWarnings("restriction")
public class EditingDomainContribution {

	private Object activeContribution;

	/**
	 * This is the action used to implement copy.
	 */
	protected CopyAction copyAction;

	public void init() {
		copyAction = createCopyAction();
	}

	public void setActiveContribution(Object contribution) {
		if (contribution instanceof IEditingDomainProvider) {
			activeContribution = contribution;
			activate();
		}
	}

	private void activate() {
//		selectionService.addSelectionListener(this);
		copyAction.setActiveContribution(activeContribution);
	}

	public void deactivate() {
//		selectionService.removeSelectionListener(this);
	}

	public void selectionChanged(MPart part, Object selection) {
		if (selection != null) {
			copyAction.updateSelection(new StructuredSelection(selection));
		}
	}

	/**
	 * Returns the action used to implement copy.
	 * 
	 * @see #copyAction
	 * @since 2.6
	 */
	protected CopyAction createCopyAction() {
		return new CopyAction();
	}
}
