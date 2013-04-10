package org.eclipse.emf.examples.extlibrary.presentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.e4mf.edit.ui.e4.action.CreateChildAction;
import org.eclipse.e4mf.edit.ui.e4.action.CreateSiblingAction;
import org.eclipse.e4mf.edit.ui.e4.action.EditingDomainContribution;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.examples.extlibrary.handlers.HandlerSupport;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.service.event.Event;

@SuppressWarnings("restriction")
public class EditorContribution extends EditingDomainContribution implements ISelectionListener {

	/** The ID of the child create menu */
	public static final String CHILD_CREATE_ID = "org.eclipse.emf.examples.library.e4editor.menu.child.create";

	/** The ID of the sibling create menu */
	public static final String SIBLING_CREATE_ID = "org.eclipse.emf.examples.library.e4editor.menu.sibling.create";

	@Inject
	private ESelectionService selectionService;

	private EXTLibraryEditor contributionPart;

	@SuppressWarnings("unused")
	private MPart part;

	@PostConstruct
	public void init() {
		super.init();
	}
	
	/**
	 * A Map of Actions with no specific implementation, but acts as a marker
	 * for injection in objects contributed to MMenuElement's
	 */
	public static class ActionMap extends HashMap<String, IAction> {
		private static final long serialVersionUID = -2678074897005203160L;
	}

	@Inject
	@Optional
	public void partActivation(
			@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event,
			MApplication application) {
		MPart part = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
		this.part = part;
		if (part.getElementId().equals(HandlerSupport.EDITOR_ID)) {
			Object contribution = part.getObject();
			if (contribution instanceof EXTLibraryEditor) {
				contributionPart = (EXTLibraryEditor) contribution;
				super.setActiveContribution(contributionPart);
			}
			selectionService.addSelectionListener(this);
			Object selectionObject = selectionService.getSelection();
			selectionChanged(part, selectionObject);
		} else {
			selectionService.removeSelectionListener(this);
			super.deactivate();
		}
	}

	/**
	 * This is the menu manager into which menu contribution items should be
	 * added for CreateChild actions. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	protected MMenu createChildMenuManager;

	/**
	 * This is the menu manager into which menu contribution items should be
	 * added for CreateSibling actions. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected MMenu createSiblingMenuManager;

	protected ActionMap createChildActionsMap  = new ActionMap();

	protected ActionMap createSiblingActionsMap = new ActionMap();

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {

		// CB TODO, Doesn't clean.
		items.remove(createChildMenuManager);
		items.remove(createSiblingMenuManager);
	}

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {

		createChildMenuManager = MMenuFactory.INSTANCE.createMenu();
		createChildMenuManager.setLabel(EXTLibraryEditorPlugin.INSTANCE
				.getString("_UI_CreateChild_menu_item"));//$NON-NLS-1$
		createChildMenuManager.setElementId(CHILD_CREATE_ID);

		//		submenuManager = new MenuManager(EXTLibraryEditorPlugin.INSTANCE.getString("_UI_CreateChild_menu_item")); //$NON-NLS-1$
		populateManager(createChildMenuManager, createChildActionsMap, null);
		items.add(createChildMenuManager);

		//		menuManager.insertBefore("edit", submenuManager); //$NON-NLS-1$

		createSiblingMenuManager = MMenuFactory.INSTANCE.createMenu();
		createSiblingMenuManager.setLabel(EXTLibraryEditorPlugin.INSTANCE
				.getString("_UI_CreateSibling_menu_item"));//$NON-NLS-1$
		createSiblingMenuManager.setElementId(SIBLING_CREATE_ID);

		//		submenuManager = new MenuManager(EXTLibraryEditorPlugin.INSTANCE.getString("_UI_CreateSibling_menu_item")); //$NON-NLS-1$
		populateManager(createSiblingMenuManager, createSiblingActionsMap, null);
		//		menuManager.insertBefore("edit", submenuManager); //$NON-NLS-1$
		items.add(createSiblingMenuManager);
	}

	public void selectionChanged(MPart part, Object object) {
		
		super.selectionChanged(part, object);
		
		// Wrap our object in a selection duh.... our EMF Infra requires a
		// selection.
		if (object == null) {
			return;
		}

		StructuredSelection selection = new StructuredSelection(object);

		// Remove any menu items for old selection.
		//
		// if (createChildMenuManager != null) {
		// depopulateManager(createChildMenuManager, createChildActions);
		// }
		// if (createSiblingMenuManager != null) {
		// depopulateManager(createSiblingMenuManager, createSiblingActions);
		// }

		// Query the new selection for appropriate new child/sibling descriptors
		//
		Collection<?> newChildDescriptors = null;
		Collection<?> newSiblingDescriptors = null;

		if (contributionPart instanceof IEditingDomainProvider) {
			EditingDomain domain = ((IEditingDomainProvider) contributionPart)
					.getEditingDomain();

			newChildDescriptors = domain.getNewChildDescriptors(object, null);
			newSiblingDescriptors = domain.getNewChildDescriptors(null, object);

			// Generate actions for selection; populate and redraw the menus.
			//
			generateCreateChildActions(newChildDescriptors, selection);

			generateCreateSiblingActions(newSiblingDescriptors, selection);

			if (createChildMenuManager != null) {
				populateManager(createChildMenuManager, createChildActionsMap,
						null);
				// createChildMenuManager.update(true);
			}
			if (createSiblingMenuManager != null) {
				populateManager(createSiblingMenuManager, createSiblingActionsMap,
						null);
				// createSiblingMenuManager.update(true);
			}
		}
	}

	/**
	 * This populates the specified <code>manager</code> with
	 * {@link org.eclipse.jface.action.ActionContributionItem}s based on the
	 * {@link org.eclipse.jface.action.IAction}s contained in the
	 * <code>actions</code> collection, by inserting them before the specified
	 * contribution item <code>contributionID</code>. If
	 * <code>contributionID</code> is <code>null</code>, they are simply added.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void populateManager(MMenu manager,
			ActionMap actionMap, String contributionID) {
		if (actionMap != null) {
			for (Entry<String, IAction> entry : actionMap.entrySet()) {
				if (contributionID != null) {
					// manager.getChildren().add(e)
					// manager.insertBefore(contributionID, action);
				} else {
					// manager.add(action);
					addMenuEntry(manager,entry);
				}

			}
		}
	}

	protected void addMenuEntry(MMenu manager, Entry<String, IAction> entry) {

		MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE
				.createDirectMenuItem();
		dynamicItem.setLabel(entry.getValue().getText());
		dynamicItem
				.setContributorURI("platform:/plugin/org.eclipse.emf.example.library.e4.editor");
		dynamicItem
				.setContributionURI("bundleclass://org.eclipse.emf.examples.library.e4editor/org.eclipse.emf.examples.extlibrary.presentation.ActionDelegate");
		manager.getChildren().add(dynamicItem);
	}

	protected void generateCreateChildActions(Collection<?> descriptors,
			ISelection selection) {
		createChildActionsMap.clear();

		if (descriptors != null) {
			for (Object descriptor : descriptors) {
				createChildActionsMap.put(
						descriptor.toString(),
						new CreateChildAction(contributionPart
								.getEditingDomain(), selection, descriptor));
			}
		}
	}

	/**
	 * This generates a
	 * {@link org.eclipse.e4mf.edit.ui.action.CreateSiblingAction} for each
	 * object in <code>descriptors</code>, and returns the collection of these
	 * actions. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void generateCreateSiblingActions(Collection<?> descriptors,
			ISelection selection) {
		createSiblingActionsMap.clear();
		if (descriptors != null) {
			for (Object descriptor : descriptors) {
				createSiblingActionsMap.put(
						descriptor.toString(),
						new CreateSiblingAction(contributionPart
								.getEditingDomain(), selection, descriptor));
			}
		}
	}

}
