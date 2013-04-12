package org.eclipse.e4.tools.outline;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
/**
 * Default SWT/JFace implementation of an outline. Clients should override. 
 * 
 * @author Christophe Bouhier
 */
public class SWTOutlinePart implements ISelectionChangedListener {

	private TreeViewer treeViewer;

	@Inject
	private ESelectionService selectionService;

	@PostConstruct
	public void postConstruct(Composite parent) {
		createControl(parent);
	}

	protected void createControl(Composite parent) {
		treeViewer = new TreeViewer(parent, getTreeStyle());
		treeViewer.addSelectionChangedListener(this);
	}

	/**
	 * A hint for the styles to use while constructing the TreeViewer.
	 * <p>
	 * Subclasses may override.
	 * </p>
	 * 
	 * @return the tree styles to use. By default, SWT.MULTI | SWT.H_SCROLL |
	 *         SWT.V_SCROLL
	 * @since 3.6
	 */
	protected int getTreeStyle() {
		return SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
	}

	@Focus
	public void onFocus() {
		treeViewer.getTree().setFocus();
	}

	@Inject
	public void setSelection(
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
		if (selection != null) {
			StructuredSelection structuredSelection = new StructuredSelection(
					selection);
			this.setInternalSelection(structuredSelection);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			// We could check, if the tree is multi, and set the selection
			// accordingly.
			Object object = ((IStructuredSelection) selection)
					.getFirstElement();
//			System.out.println("Outline, setting selection: " + object);
//			selectionService.setSelection(object);
		}
	}

	/*
	 * (non-Javadoc) Method declared on ISelectionProvider.
	 */
	private void setInternalSelection(ISelection selection) {
		if (treeViewer != null) {
			treeViewer.setSelection(selection);
		}
	}

	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}
}