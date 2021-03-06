package org.eclipse.emf.examples.extlibrary.presentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.helpers.E4ToolsHelper;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuFactoryImpl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.e4mf.edit.ui.e4.action.CreateChildAction;
import org.eclipse.e4mf.edit.ui.e4.action.CreateSiblingAction;
import org.eclipse.e4mf.edit.ui.e4.action.EditingDomainContribution;
import org.eclipse.e4mf.edit.ui.e4.action.StaticSelectionCommandAction;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

@SuppressWarnings("restriction")
/**
 * An auto created and inject service which 
 * Produces menu's based on the selections in the EMF Editor. 
 *  
 * @author Christophe Bouhier
 */
@Singleton
public class EditorSupport extends ContextFunction implements
		ISelectionListener {

	private static final String[] FILE_EXTENSION_FILTERS = EXTLibraryEditor.FILE_EXTENSION_FILTERS
			.toArray(new String[0]);

	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(EditorSupport.class, context);
	}

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	private EPartService partService;

	@Inject
	private ESelectionService selectionService;

	@Inject
	@Optional
	private EditingDomain editingDomain;

	@Inject
	@Optional
	private EditingDomainContribution editingDomainContribution;

	@Inject
	private E4ToolsHelper helpMeWith;

	/**
	 * A Map of Actions with no specific implementation, but acts as a marker
	 * for injection in objects contributed to MMenuElement's
	 */
	public static class ActionMap extends HashMap<String, IAction> {
		private static final long serialVersionUID = -2678074897005203160L;
	}

	protected MMenu createChildMenuManager;

	protected MMenu createSiblingMenuManager;

	public MMenu getCreateChildMenuManager() {
		if (createChildMenuManager == null) {
			createChildMenuManager = MMenuFactory.INSTANCE.createMenu();
			createChildMenuManager.setLabel(EXTLibraryEditorPlugin.INSTANCE
					.getString("_UI_CreateChild_menu_item"));//$NON-NLS-1$
			createChildMenuManager
					.setElementId(EditorIdentities.CHILD_CREATE_ID);

		}

		depopulateManager(createChildMenuManager);
		populateManager(createChildMenuManager, createChildActionsMap);

		return createChildMenuManager;
	}

	public MMenu getCreateSiblingMenuManager() {
		if (createSiblingMenuManager == null) {
			createSiblingMenuManager = MMenuFactory.INSTANCE.createMenu();
			createSiblingMenuManager.setLabel(EXTLibraryEditorPlugin.INSTANCE
					.getString("_UI_CreateSibling_menu_item"));//$NON-NLS-1$
			createSiblingMenuManager
					.setElementId(EditorIdentities.SIBLING_CREATE_ID);
		}
		depopulateManager(createSiblingMenuManager);
		populateManager(createSiblingMenuManager, createSiblingActionsMap);

		return createSiblingMenuManager;
	}

	protected ActionMap createChildActionsMap = new ActionMap();

	protected ActionMap createSiblingActionsMap = new ActionMap();

	private boolean emfEditorActive;

	@Inject
	@Optional
	public void partActivation(
			@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event,
			MApplication application) {

		MPart part = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);

		if (part.getElementId().equals(EditorIdentities.EDITOR_ID)) {

			if (!emfEditorActive) {
				emfEditorActive = true;

				System.out.println("EMF Editor support activated: "
						+ this.hashCode());

				// Fill the context menu with some entries.
				populateContextMenu(part);

				// Selection updates.
				selectionService.addSelectionListener(this);
				// Force a selection update so the actions are created.
				Object selectionObject = selectionService.getSelection();
				selectionChanged(part, selectionObject);
			} else {
				System.out.println("EMF Editor part active: lifecycle update: "
						+ event);
			}
		} else {
			if (emfEditorActive) {
				System.out.println("EMF Editor part de-activated");
				selectionService.removeSelectionListener(this);
				emfEditorActive = false;
			}

			// super.deactivate();
		}
	}

	private void populateContextMenu(MPart part) {

		// Re-create the menu each time, as clearing is not supported.
		MPopupMenu pum = helpMeWith.findPopupMenu(part,
				EditorIdentities.EDITOR_POPUP_ID);

		if (pum != null) {
			part.getMenus().remove(pum);
		}

		pum = createContectMenu();
		part.getMenus().add(pum);

		if (pum != null) {
			if (createChildMenuManager != null) {
				pum.getChildren().add(
						helpMeWith.cloneMenu(createChildMenuManager));
			}
			if (createSiblingMenuManager != null) {
				pum.getChildren().add(
						helpMeWith.cloneMenu(createSiblingMenuManager));
			}
			// populateManager(pum, createChildActionsMap);
			// populateManager(pum, createSiblingActionsMap);
			addGlobalHandlers(pum);
		}

		// Update the registration to the EMenuService, as we are
		// otherwise not known.
		Object contribution = part.getObject();
		if (contribution instanceof EXTLibraryEditor) {
			((EXTLibraryEditor) contribution).updateContextMenuRegistration();
		}

	}

	private void addGlobalHandlers(MMenu menu) {

		// Find the menu entries for Refresh, Show Properties etc....
		// Costly, has we walk the full hierarchy, does are in a fragment.
		MUIElement trimmedWindow = modelService.find(
				EditorIdentities.MAIN_WINDOW_ID, application);

		if (trimmedWindow instanceof MTrimmedWindow) {
			if (editingDomainContribution != null) {
				editingDomainContribution.addGlobalHandlers(
						(MTrimmedWindow) trimmedWindow, menu);
			}
			System.out.println("Adding EMF Editor global handlers");
			menu.getChildren().addAll(
					helpMeWith.locateGlobalHandlers(
							EditorIdentities.GLOBAL_HANDLERS,
							(MTrimmedWindow) trimmedWindow));
		}
	}

	public void selectionChanged(MPart part, Object object) {

		// super.selectionChanged(part, object);

		// Wrap our object in a selection duh.... our EMF Infra requires a
		// selection.
		if (object == null) {
			return;
		}

		StructuredSelection selection = new StructuredSelection(object);

		// Query the new selection for appropriate new child/sibling descriptors
		//
		Collection<?> newChildDescriptors = null;
		Collection<?> newSiblingDescriptors = null;

		if (editingDomain != null) {

			newChildDescriptors = editingDomain.getNewChildDescriptors(object,
					null);
			newSiblingDescriptors = editingDomain.getNewChildDescriptors(null,
					object);

			// Generate actions for selection; populate and redraw the menus.
			generateCreateChildActions(newChildDescriptors, selection);
			generateCreateSiblingActions(newSiblingDescriptors, selection);

			// create or update our Menu Manger.
			getCreateChildMenuManager();
			System.out.println("EMF Editor selection changed child menu size: "
					+ createChildMenuManager.getChildren().size());
			getCreateSiblingMenuManager();
			System.out
					.println("EMF Editor selection changed sibling menu size: "
							+ createSiblingMenuManager.getChildren().size());

			populateContextMenu(part);
		}
	}

	private void depopulateManager(MMenu menuManager) {
		menuManager.getChildren().clear();
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
	public void populateManager(MMenu manager, ActionMap actionMap) {
		if (actionMap != null) {
			for (Entry<String, IAction> entry : actionMap.entrySet()) {
				addMenuEntry(manager, entry);
			}
		}
	}

	protected void addMenuEntry(MMenu manager, Entry<String, IAction> entry) {

		IAction action = entry.getValue(); 
		
		MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE
				.createDirectMenuItem();
		dynamicItem.setLabel(entry.getValue().getText());
		
		// We need a URI for the image. 
		ImageDescriptor imageDescriptor = action.getImageDescriptor();
		
//		dynamicItem.setIconURI()
		dynamicItem.setContributorURI(EditorIdentities.PLUGIN_ID);
		dynamicItem
				.setContributionURI(EditorIdentities.CREATION_CONTRIBUTION_URI);
		manager.getChildren().add(dynamicItem);
	}

	protected void generateCreateChildActions(Collection<?> descriptors,
			ISelection selection) {
		createChildActionsMap.clear();

		if (descriptors != null) {
			for (Object descriptor : descriptors) {
				createChildActionsMap.put(descriptor.toString(),
						new CreateChildAction(editingDomain, selection,
								descriptor));
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
				createSiblingActionsMap.put(descriptor.toString(),
						new CreateSiblingAction(editingDomain, selection,
								descriptor));
			}
		}
	}

	public boolean openEditor(URI inputURI) {
		return openEditor(inputURI.toFileString());
	}

	public boolean openEditor(String filePath) {
		MPartStack stack = (MPartStack) modelService.find(
				EditorIdentities.STACK_POSITION_ID, application);

		// Create the stack if it doesn't exist.
		if (stack == null) {
			MPartStack editorStack = MBasicFactory.INSTANCE.createPartStack();
			editorStack.setElementId(EditorIdentities.STACK_POSITION_ID);
			MUIElement parent = modelService.find(
					EditorIdentities.STACK_PARENT_ID, application);
			if (parent instanceof MPartSashContainer) {
				((MPartSashContainer) parent).getChildren().add(editorStack);
			}
		}

		MInputPart part = MBasicFactory.INSTANCE.createInputPart();

		part.setElementId(EditorIdentities.EDITOR_ID);

		// Pointing to the contributing class
		part.setContributionURI(EditorIdentities.EDITOR_CONTRIBUTION_URI);

		// As file String.
		part.setInputURI(URI.createFileURI(filePath).toFileString());

		// The icon.
		part.setIconURI("platform:/plugin/org.eclipse.emf.examples.library.e4editor/icons/full/obj16/EXTLibraryModelFile.gif");

		part.setLabel(filePath);

		// part.setTooltip(input.getTooltip());
		part.setCloseable(true);

		MPopupMenu createContectMenu = createContectMenu();
		part.getMenus().add(createContectMenu);

		// Add the part to the stack.
		stack.getChildren().add(part);
		MPart showPart = partService.showPart(part,
				EPartService.PartState.ACTIVATE);
		return showPart != null;
	}

	private MPopupMenu createContectMenu() {
		// Add a menu programmatically, which we bind to a control with the
		// EMenuService,
		// and to which we contribute, whenever the part becomes active.
		MPopupMenu menu = MenuFactoryImpl.eINSTANCE.createPopupMenu();
		menu.setElementId(EditorIdentities.EDITOR_POPUP_ID);
		return menu;
	}

	public String[] openFilePathDialog(Shell shell, int style,
			String[] fileExtensionFilters) {
		return openFilePathDialog(shell, style, fileExtensionFilters,
				(style & SWT.OPEN) != 0, (style & SWT.OPEN) != 0,
				(style & SWT.SAVE) != 0);
	}

	public String[] openFilePathDialog(Shell shell, int style,
			String[] fileExtensionFilters, boolean includeGroupFilter,
			boolean includeAllFilter, boolean addExtension) {
		FileDialog fileDialog = new FileDialog(shell, style);
		if (fileExtensionFilters == null) {
			fileExtensionFilters = FILE_EXTENSION_FILTERS;
		}

		// If requested, augment the file extension filters by adding a group of
		// all the other filters (*.ext1;*.ext2;...)
		// at the beginning and/or an all files wildcard (*.*) at the end.
		//
		includeGroupFilter &= fileExtensionFilters.length > 1;
		int offset = includeGroupFilter ? 1 : 0;

		if (includeGroupFilter || includeAllFilter) {
			int size = fileExtensionFilters.length + offset
					+ (includeAllFilter ? 1 : 0);
			String[] allFilters = new String[size];
			StringBuilder group = includeGroupFilter ? new StringBuilder()
					: null;

			for (int i = 0; i < fileExtensionFilters.length; i++) {
				if (includeGroupFilter) {
					if (i != 0) {
						group.append(';');
					}
					group.append(fileExtensionFilters[i]);
				}
				allFilters[i + offset] = fileExtensionFilters[i];
			}

			if (includeGroupFilter) {
				allFilters[0] = group.toString();
			}
			if (includeAllFilter) {
				allFilters[allFilters.length - 1] = "*.*"; //$NON-NLS-1$
			}

			fileDialog.setFilterExtensions(allFilters);
		} else {
			fileDialog.setFilterExtensions(fileExtensionFilters);
		}
		fileDialog.open();

		String[] filenames = fileDialog.getFileNames();
		String[] result = new String[filenames.length];
		String path = fileDialog.getFilterPath() + File.separator;
		String extension = null;

		// If extension adding requested, get the dotted extension corresponding
		// to the selected filter.
		//
		if (addExtension) {
			int i = fileDialog.getFilterIndex();
			if (i != -1
					&& (!includeAllFilter || i != fileExtensionFilters.length)) {
				i = includeGroupFilter && i == 0 ? 0 : i - offset;
				String filter = fileExtensionFilters[i];
				int dot = filter.lastIndexOf('.');
				if (dot == 1 && filter.charAt(0) == '*') {
					extension = filter.substring(dot);
				}
			}
		}

		// Build the result by adding the selected path and, if needed,
		// auto-appending the extension.
		//
		for (int i = 0; i < filenames.length; i++) {
			String filename = path + filenames[i];
			if (extension != null) {
				int dot = filename.lastIndexOf('.');
				if (dot == -1
						|| !Arrays.asList(fileExtensionFilters).contains(
								"*" + filename.substring(dot))) //$NON-NLS-1$
				{
					filename += extension;
				}
			}
			result[i] = filename;
		}
		return result;
	}
}
