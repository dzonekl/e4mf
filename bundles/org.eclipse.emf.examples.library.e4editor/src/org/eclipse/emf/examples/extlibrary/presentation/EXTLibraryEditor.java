package org.eclipse.emf.examples.extlibrary.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.outline.IOutlinePartProvider;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4mf.common.ui.ViewerPane;
import org.eclipse.e4mf.common.ui.viewer.IViewerProvider;
import org.eclipse.e4mf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.e4mf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.e4mf.edit.ui.dnd.LocalTransfer;
import org.eclipse.e4mf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.e4mf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.e4mf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.examples.extlibrary.handlers.HandlerSupport;
import org.eclipse.emf.examples.extlibrary.provider.EXTLibraryItemProviderAdapterFactory;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.osgi.service.event.Event;

/**
 * This is an example of a EXTLibrary model editor. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
@SuppressWarnings("restriction")
public class EXTLibraryEditor implements IEditingDomainProvider,
		ISelectionProvider, IMenuListener, IViewerProvider, IAdaptable {

	/**
	 * The filters for file extensions supported by the editor. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final List<String> FILE_EXTENSION_FILTERS = prefixExtensions(
			EXTLibraryModelWizard.FILE_EXTENSIONS, "*."); //$NON-NLS-1$

	/**
	 * Returns a new unmodifiable list containing prefixed versions of the
	 * extensions in the given list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	private static List<String> prefixExtensions(List<String> extensions,
			String prefix) {
		List<String> result = new ArrayList<String>();
		for (String extension : extensions) {
			result.add(prefix + extension);
		}
		return Collections.unmodifiableList(result);
	}

	@Inject
	private MDirtyable dirtyable;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell activeShell;

	@Inject
	private HandlerSupport support;

	@Inject
	private ESelectionService selectionService;

	/**
	 * This keeps track of the editing domain that is used to track all changes
	 * to the model. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected AdapterFactoryEditingDomain editingDomain;

	/**
	 * This is the one adapter factory used for providing views of the model.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ComposedAdapterFactory adapterFactory;

	// /**
	// * This is the content outline page. <!-- begin-user-doc --> <!--
	// * end-user-doc -->
	// *
	// * @generated
	// */
	// protected IContentOutlinePage contentOutlinePage;

	/**
	 * This is a kludge... <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected IStatusLineManager contentOutlineStatusLineManager;

	/**
	 * This is the content outline page's viewer. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeViewer contentOutlineViewer;

	/**
	 * This is the property sheet page. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	// CB Disable properties.
	// protected List<PropertySheetPage> propertySheetPages = new
	// ArrayList<PropertySheetPage>();

	/**
	 * This is the viewer that shadows the selection in the content outline. The
	 * parent relation must be correctly defined for this to work. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeViewer selectionViewer;

	/**
	 * This inverts the roll of parent and child in the content provider and
	 * show parents as a tree. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeViewer parentViewer;

	/**
	 * This shows how a tree view works. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeViewer treeViewer;

	/**
	 * This shows how a list view works. A list viewer doesn't support icons.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ListViewer listViewer;

	/**
	 * This shows how a table view works. A table can be used as a list with
	 * icons. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TableViewer tableViewer;

	/**
	 * This shows how a tree view with columns works. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeViewer treeViewerWithColumns;

	/**
	 * This keeps track of the active viewer pane, in the book. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// CB, TODO Migrate the VIewerPane concept.
	protected ViewerPane currentViewerPane;

	/**
	 * This keeps track of the active content viewer, which may be either one of
	 * the viewers in the pages or the content outline viewer. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Viewer currentViewer;

	/**
	 * This listens to which ever viewer is active. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ISelectionChangedListener selectionChangedListener;

	/**
	 * This keeps track of all the
	 * {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are
	 * listening to this editor. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * This keeps track of the selection of the editor as a whole. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ISelection editorSelection = StructuredSelection.EMPTY;

	/**
	 * This listens for when the outline becomes active <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// CB This makes the

	// protected IPartListener partListener = new IPartListener() {
	// public void partActivated(IWorkbenchPart p) {
	// if (p instanceof ContentOutline) {
	// if (((ContentOutline) p).getCurrentPage() == contentOutlinePage) {
	// getActionBarContributor().setActiveEditor(
	// EXTLibraryEditor.this);
	//
	// setCurrentViewer(contentOutlineViewer);
	// }
	// } else if (p instanceof PropertySheet) {
	// if (propertySheetPages.contains(((PropertySheet) p)
	// .getCurrentPage())) {
	// getActionBarContributor().setActiveEditor(
	// EXTLibraryEditor.this);
	// handleActivate();
	// }
	// } else if (p == EXTLibraryEditor.this) {
	// handleActivate();
	// }
	// }
	//
	// public void partBroughtToTop(IWorkbenchPart p) {
	// // Ignore.
	// }
	//
	// public void partClosed(IWorkbenchPart p) {
	// // Ignore.
	// }
	//
	// public void partDeactivated(IWorkbenchPart p) {
	// // Ignore.
	// }
	//
	// public void partOpened(IWorkbenchPart p) {
	// // Ignore.
	// }
	// };

	/**
	 * Resources that have been removed since last activation. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection<Resource> removedResources = new ArrayList<Resource>();

	/**
	 * Resources that have been changed since last activation. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection<Resource> changedResources = new ArrayList<Resource>();

	/**
	 * Resources that have been saved. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	protected Collection<Resource> savedResources = new ArrayList<Resource>();

	/**
	 * Map to store the diagnostic associated with a resource. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<Resource, Diagnostic>();

	/**
	 * Controls whether the problem indication should be updated. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected boolean updateProblemIndication = true;

	/**
	 * Adapter used to update the problem indication when resources are demanded
	 * loaded. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EContentAdapter problemIndicationAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification notification) {
			if (notification.getNotifier() instanceof Resource) {
				switch (notification.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
				case Resource.RESOURCE__ERRORS:
				case Resource.RESOURCE__WARNINGS: {
					Resource resource = (Resource) notification.getNotifier();
					Diagnostic diagnostic = analyzeResourceProblems(resource,
							null);
					if (diagnostic.getSeverity() != Diagnostic.OK) {
						resourceToDiagnosticMap.put(resource, diagnostic);
					} else {
						resourceToDiagnosticMap.remove(resource);
					}

					if (updateProblemIndication) {
						activeShell.getDisplay().asyncExec(new Runnable() {
							public void run() {
								updateProblemIndication();
							}
						});
					}
					break;
				}
				}
			} else {
				super.notifyChanged(notification);
			}
		}

		@Override
		protected void setTarget(Resource target) {
			basicSetTarget(target);
		}

		@Override
		protected void unsetTarget(Resource target) {
			basicUnsetTarget(target);
			resourceToDiagnosticMap.remove(target);
			if (updateProblemIndication) {
				activeShell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						updateProblemIndication();
					}
				});
			}
		}
	};

	private String inputURI;

	/**
	 * Our container composite for our views.
	 */
	private Composite container;

	public Composite getContainer() {
		return container;
	}

	/**
	 * Handles activation of the editor or it's associated views. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// CB, whenever activated we execute this stuff.
	// It's to check if the resources have changed.
	protected void handleActivate() {
		// Recompute the read only state.
		//
		if (editingDomain.getResourceToReadOnlyMap() != null) {
			editingDomain.getResourceToReadOnlyMap().clear();

			// Refresh any actions that may become enabled or disabled.
			//
			setSelection(getSelection());
		}

		if (!removedResources.isEmpty()) {
			if (handleDirtyConflict()) {

				// CB Get the MPart, the EPartService and call
				// ePartService.hidePart(mPart, true);
				// getSite().getPage().closeEditor(EXTLibraryEditor.this,
				// false);

			} else {
				removedResources.clear();
				changedResources.clear();
				savedResources.clear();
			}
		} else if (!changedResources.isEmpty()) {
			changedResources.removeAll(savedResources);
			handleChangedResources();
			changedResources.clear();
			savedResources.clear();
		}
	}

	/**
	 * Handles what to do with changed resources on activation. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void handleChangedResources() {
		if (!changedResources.isEmpty()
				&& (!internalIsDirty() || handleDirtyConflict())) {
			if (internalIsDirty()) {
				changedResources.addAll(editingDomain.getResourceSet()
						.getResources());
			}
			editingDomain.getCommandStack().flush();

			updateProblemIndication = false;
			for (Resource resource : changedResources) {
				if (resource.isLoaded()) {
					resource.unload();
					try {
						resource.load(Collections.EMPTY_MAP);
					} catch (IOException exception) {
						if (!resourceToDiagnosticMap.containsKey(resource)) {
							resourceToDiagnosticMap
									.put(resource,
											analyzeResourceProblems(resource,
													exception));
						}
					}
				}
			}

			if (AdapterFactoryEditingDomain.isStale(editorSelection)) {
				setSelection(StructuredSelection.EMPTY);
			}

			updateProblemIndication = true;
			updateProblemIndication();
		}
	}

	/**
	 * Updates the problems indication with the information described in the
	 * specified diagnostic. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void updateProblemIndication() {
		if (updateProblemIndication) {
			BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK,
					"org.eclipse.emf.examples.library.editor", //$NON-NLS-1$
					0, null, new Object[] { editingDomain.getResourceSet() });
			for (Diagnostic childDiagnostic : resourceToDiagnosticMap.values()) {
				if (childDiagnostic.getSeverity() != Diagnostic.OK) {
					diagnostic.add(childDiagnostic);
				}
			}

			// CB TODO, Rework to show the problemEditorPart
			// int lastEditorPage = getPageCount() - 1;
			// if (lastEditorPage >= 0
			// && getEditor(lastEditorPage) instanceof ProblemEditorPart) {
			// ((ProblemEditorPart) getEditor(lastEditorPage))
			// .setDiagnostic(diagnostic);
			// if (diagnostic.getSeverity() != Diagnostic.OK) {
			// setActivePage(lastEditorPage);
			// }
			// } else if (diagnostic.getSeverity() != Diagnostic.OK) {
			// ProblemEditorPart problemEditorPart = new ProblemEditorPart();
			// problemEditorPart.setDiagnostic(diagnostic);
			// try {
			// addPage(++lastEditorPage, problemEditorPart,
			// getEditorInput());
			// setPageText(lastEditorPage, problemEditorPart.getPartName());
			// setActivePage(lastEditorPage);
			// showTabs();
			// } catch (PartInitException exception) {
			// EXTLibraryEditorPlugin.INSTANCE.log(exception);
			// }
			// }
		}
	}

	/**
	 * Shows a dialog that asks if conflicting changes should be discarded. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected boolean handleDirtyConflict() {
		return MessageDialog.openQuestion(activeShell,
				getString("_UI_FileConflict_label"), //$NON-NLS-1$
				getString("_WARN_FileConflict")); //$NON-NLS-1$
	}

	/**
	 * This creates a model editor. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public EXTLibraryEditor() {
		super();
		initializeEditingDomain();
	}

	/**
	 * This sets up the editing domain for the model editor. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		//
		adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory
				.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory
				.addAdapterFactory(new EXTLibraryItemProviderAdapterFactory());
		adapterFactory
				.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		// Create the command stack that will notify this editor as commands are
		// executed.
		//
		BasicCommandStack commandStack = new BasicCommandStack();

		// Add a listener to set the most recent command's affected objects to
		// be the selection of the viewer with focus.
		//
		commandStack.addCommandStackListener(new CommandStackListener() {
			public void commandStackChanged(final EventObject event) {
				activeShell.getDisplay().asyncExec(new Runnable() {
					public void run() {

						// Whenever we edit something on the stack, we mark
						// ourselves dirty.
						dirtyable.setDirty(true);

						// firePropertyChange(IEditorPart.PROP_DIRTY);
						// Try to select the affected objects.
						//
						Command mostRecentCommand = ((CommandStack) event
								.getSource()).getMostRecentCommand();
						if (mostRecentCommand != null) {
							setSelectionToViewer(mostRecentCommand
									.getAffectedObjects());
						}

						// CB Todo refresh properties. (In e4, this is perhaps
						// done differently.
						// for (Iterator<PropertySheetPage> i =
						// propertySheetPages
						// .iterator(); i.hasNext();) {
						// PropertySheetPage propertySheetPage = i.next();
						// if (propertySheetPage.getControl().isDisposed()) {
						// i.remove();
						// } else {
						// propertySheetPage.refresh();
						// }
						// }
					}
				});
			}
		});

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory,
				commandStack, new HashMap<Resource, Boolean>());
	}

	/**
	 * This is here for the listener to be able to call it. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	// @Override
	// protected void firePropertyChange(int action) {
	// super.firePropertyChange(action);
	// }

	/**
	 * This sets the selection into whichever viewer is active. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;
		// Make sure it's okay.
		//
		if (theSelection != null && !theSelection.isEmpty()) {
			Runnable runnable = new Runnable() {
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					//
					if (currentViewer != null) {
						currentViewer.setSelection(new StructuredSelection(
								theSelection.toArray()), true);
					}
				}
			};
			activeShell.getDisplay().asyncExec(runnable);
		}
	}

	/**
	 * This returns the editing domain as required by the
	 * {@link IEditingDomainProvider} interface. This is important for
	 * implementing the static methods of {@link AdapterFactoryEditingDomain}
	 * and for supporting {@link org.eclipse.e4mf.edit.ui.action.CommandAction}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	public class ReverseAdapterFactoryContentProvider extends
			AdapterFactoryContentProvider {
		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public ReverseAdapterFactoryContentProvider(
				AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		@Override
		public Object[] getElements(Object object) {
			Object parent = super.getParent(object);
			return (parent == null ? Collections.EMPTY_SET : Collections
					.singleton(parent)).toArray();
		}

		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		@Override
		public Object[] getChildren(Object object) {
			Object parent = super.getParent(object);
			return (parent == null ? Collections.EMPTY_SET : Collections
					.singleton(parent)).toArray();
		}

		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		@Override
		public boolean hasChildren(Object object) {
			Object parent = super.getParent(object);
			return parent != null;
		}

		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		@Override
		public Object getParent(Object object) {
			return null;
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setCurrentViewerPane(ViewerPane viewerPane) {
		if (currentViewerPane != viewerPane) {
			if (currentViewerPane != null) {
				currentViewerPane.showFocus(false);
			}
			currentViewerPane = viewerPane;
		}
		setCurrentViewer(currentViewerPane.getViewer());
	}

	/**
	 * This makes sure that one content viewer, either for the current page or
	 * the outline view, if it has focus, is the current one. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setCurrentViewer(Viewer viewer) {
		// If it is changing...
		//
		if (currentViewer != viewer) {
			if (selectionChangedListener == null) {
				// Create the listener on demand.
				//
				selectionChangedListener = new ISelectionChangedListener() {
					// This just notifies those things that are affected by the
					// section.
					//
					public void selectionChanged(
							SelectionChangedEvent selectionChangedEvent) {
						setSelection(selectionChangedEvent.getSelection());
					}
				};
			}

			// Stop listening to the old one.
			//
			if (currentViewer != null) {
				currentViewer
						.removeSelectionChangedListener(selectionChangedListener);
			}

			// Start listening to the new one.
			//
			if (viewer != null) {
				viewer.addSelectionChangedListener(selectionChangedListener);
			}

			// Remember it.
			//
			currentViewer = viewer;

			// Set the editors selection based on the current viewer's
			// selection.
			//
			setSelection(currentViewer == null ? StructuredSelection.EMPTY
					: currentViewer.getSelection());
		}
	}

	/**
	 * This returns the viewer as required by the {@link IViewerProvider}
	 * interface. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Viewer getViewer() {
		return currentViewer;
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well
	 * registering the menu for extension. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */

	// CB Revise for e4
	protected void createContextMenuFor(StructuredViewer viewer) {
		MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
		contextMenu.add(new Separator("additions")); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this);
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// CB TODO
		// getSite().registerContextMenu(contextMenu,
		// new UnwrappingSelectionProvider(viewer));

		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer(),
				FileTransfer.getInstance() };
		viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(
				viewer));
		viewer.addDropSupport(dndOperations, transfers,
				new EditingDomainViewerDropAdapter(editingDomain, viewer));
	}

	/**
	 * This is the method called to load a resource into the editing domain's
	 * resource set based on the editor's input. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public void createModel() {

		// CB Our file location, is a platform URI?
		// URI resourceURI = EditUIUtil.getURI(getEditorInput());

		URI resourceURI = URI.createFileURI(inputURI);
		Exception exception = null;
		Resource resource = null;
		try {
			// Load the resource through the editing domain.
			//
			resource = editingDomain.getResourceSet().getResource(resourceURI,
					true);
		} catch (Exception e) {
			exception = e;
			resource = editingDomain.getResourceSet().getResource(resourceURI,
					false);
		}

		Diagnostic diagnostic = analyzeResourceProblems(resource, exception);
		if (diagnostic.getSeverity() != Diagnostic.OK) {
			resourceToDiagnosticMap.put(resource,
					analyzeResourceProblems(resource, exception));
		}
		editingDomain.getResourceSet().eAdapters()
				.add(problemIndicationAdapter);
	}

	/**
	 * Returns a diagnostic describing the errors and warnings listed in the
	 * resource and the specified exception (if any). <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Diagnostic analyzeResourceProblems(Resource resource,
			Exception exception) {
		if (!resource.getErrors().isEmpty()
				|| !resource.getWarnings().isEmpty()) {
			BasicDiagnostic basicDiagnostic = new BasicDiagnostic(
					Diagnostic.ERROR,
					"org.eclipse.emf.examples.library.editor", //$NON-NLS-1$
					0, getString(
							"_UI_CreateModelError_message", resource.getURI()), //$NON-NLS-1$
					new Object[] { exception == null ? (Object) resource
							: exception });
			basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
			return basicDiagnostic;
		} else if (exception != null) {
			return new BasicDiagnostic(Diagnostic.ERROR,
					"org.eclipse.emf.examples.library.editor", //$NON-NLS-1$
					0, getString(
							"_UI_CreateModelError_message", resource.getURI()), //$NON-NLS-1$
					new Object[] { exception });
		} else {
			return Diagnostic.OK_INSTANCE;
		}
	}

	/**
	 * This is the method used by the framework to install your own controls.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// Not an Interface but called from init.
	public void createPages() {
		// Creates the model from the editor input
		//
		createModel();

		// Only creates the other pages if there is something that can be edited
		//
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()) {
			// Create a page for the selection tree view.
			//
			{

				Tree tree = new Tree(this.getContainer(), SWT.MULTI);

				// CB, Likely needs some layout stuff.

				selectionViewer = new TreeViewer(tree);
				selectionViewer
						.setContentProvider(new AdapterFactoryContentProvider(
								adapterFactory));

				selectionViewer
						.setLabelProvider(new AdapterFactoryLabelProvider(
								adapterFactory));
				selectionViewer.setInput(editingDomain.getResourceSet());
				selectionViewer.setSelection(new StructuredSelection(
						editingDomain.getResourceSet().getResources().get(0)),
						true);
				new AdapterFactoryTreeEditor(selectionViewer.getTree(),
						adapterFactory);

				createContextMenuFor(selectionViewer);

				setCurrentViewer(selectionViewer);

				//
				// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
				// EXTLibraryEditor.this) {
				// @Override
				// public Viewer createViewer(Composite composite) {
				// Tree tree = new Tree(composite, SWT.MULTI);
				// TreeViewer newTreeViewer = new TreeViewer(tree);
				// return newTreeViewer;
				// }
				//
				// @Override
				// public void requestActivation() {
				// super.requestActivation();
				// setCurrentViewerPane(this);
				// }
				// };
				// viewerPane.createControl(getContainer());
				//
				// selectionViewer = (TreeViewer) viewerPane.getViewer();
				// selectionViewer
				// .setContentProvider(new AdapterFactoryContentProvider(
				// adapterFactory));
				//
				// selectionViewer
				// .setLabelProvider(new AdapterFactoryLabelProvider(
				// adapterFactory));
				// selectionViewer.setInput(editingDomain.getResourceSet());
				// selectionViewer.setSelection(new StructuredSelection(
				// editingDomain.getResourceSet().getResources().get(0)),
				// true);
				// viewerPane.setTitle(editingDomain.getResourceSet());
				//
				// new AdapterFactoryTreeEditor(selectionViewer.getTree(),
				// adapterFactory);
				//
				// createContextMenuFor(selectionViewer);
				// int pageIndex = addPage(viewerPane.getControl());
				//				setPageText(pageIndex, getString("_UI_SelectionPage_label")); //$NON-NLS-1$
			}

			// // Create a page for the parent tree view.
			// //
			// {
			// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
			// EXTLibraryEditor.this) {
			// @Override
			// public Viewer createViewer(Composite composite) {
			// Tree tree = new Tree(composite, SWT.MULTI);
			// TreeViewer newTreeViewer = new TreeViewer(tree);
			// return newTreeViewer;
			// }
			//
			// @Override
			// public void requestActivation() {
			// super.requestActivation();
			// setCurrentViewerPane(this);
			// }
			// };
			// viewerPane.createControl(getContainer());
			//
			// parentViewer = (TreeViewer) viewerPane.getViewer();
			// parentViewer.setAutoExpandLevel(30);
			// parentViewer
			// .setContentProvider(new ReverseAdapterFactoryContentProvider(
			// adapterFactory));
			// parentViewer.setLabelProvider(new AdapterFactoryLabelProvider(
			// adapterFactory));
			//
			// createContextMenuFor(parentViewer);
			// int pageIndex = addPage(viewerPane.getControl());
			//				setPageText(pageIndex, getString("_UI_ParentPage_label")); //$NON-NLS-1$
			// }
			//
			// // This is the page for the list viewer
			// //
			// {
			// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
			// EXTLibraryEditor.this) {
			// @Override
			// public Viewer createViewer(Composite composite) {
			// return new ListViewer(composite);
			// }
			//
			// @Override
			// public void requestActivation() {
			// super.requestActivation();
			// setCurrentViewerPane(this);
			// }
			// };
			// viewerPane.createControl(getContainer());
			// listViewer = (ListViewer) viewerPane.getViewer();
			// listViewer
			// .setContentProvider(new AdapterFactoryContentProvider(
			// adapterFactory));
			// listViewer.setLabelProvider(new AdapterFactoryLabelProvider(
			// adapterFactory));
			//
			// createContextMenuFor(listViewer);
			// int pageIndex = addPage(viewerPane.getControl());
			//				setPageText(pageIndex, getString("_UI_ListPage_label")); //$NON-NLS-1$
			// }
			//
			// // This is the page for the tree viewer
			// //
			// {
			// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
			// EXTLibraryEditor.this) {
			// @Override
			// public Viewer createViewer(Composite composite) {
			// return new TreeViewer(composite);
			// }
			//
			// @Override
			// public void requestActivation() {
			// super.requestActivation();
			// setCurrentViewerPane(this);
			// }
			// };
			// viewerPane.createControl(getContainer());
			// treeViewer = (TreeViewer) viewerPane.getViewer();
			// treeViewer
			// .setContentProvider(new AdapterFactoryContentProvider(
			// adapterFactory));
			// treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(
			// adapterFactory));
			//
			// new AdapterFactoryTreeEditor(treeViewer.getTree(),
			// adapterFactory);
			//
			// createContextMenuFor(treeViewer);
			// int pageIndex = addPage(viewerPane.getControl());
			//				setPageText(pageIndex, getString("_UI_TreePage_label")); //$NON-NLS-1$
			// }
			//
			// // This is the page for the table viewer.
			// //
			// {
			// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
			// EXTLibraryEditor.this) {
			// @Override
			// public Viewer createViewer(Composite composite) {
			// return new TableViewer(composite);
			// }
			//
			// @Override
			// public void requestActivation() {
			// super.requestActivation();
			// setCurrentViewerPane(this);
			// }
			// };
			// viewerPane.createControl(getContainer());
			// tableViewer = (TableViewer) viewerPane.getViewer();
			//
			// Table table = tableViewer.getTable();
			// TableLayout layout = new TableLayout();
			// table.setLayout(layout);
			// table.setHeaderVisible(true);
			// table.setLinesVisible(true);
			//
			// TableColumn objectColumn = new TableColumn(table, SWT.NONE);
			// layout.addColumnData(new ColumnWeightData(3, 100, true));
			//				objectColumn.setText(getString("_UI_ObjectColumn_label")); //$NON-NLS-1$
			// objectColumn.setResizable(true);
			//
			// TableColumn selfColumn = new TableColumn(table, SWT.NONE);
			// layout.addColumnData(new ColumnWeightData(2, 100, true));
			//				selfColumn.setText(getString("_UI_SelfColumn_label")); //$NON-NLS-1$
			// selfColumn.setResizable(true);
			//
			//				tableViewer.setColumnProperties(new String[] { "a", "b" }); //$NON-NLS-1$ //$NON-NLS-2$
			// tableViewer
			// .setContentProvider(new AdapterFactoryContentProvider(
			// adapterFactory));
			// tableViewer.setLabelProvider(new AdapterFactoryLabelProvider(
			// adapterFactory));
			//
			// createContextMenuFor(tableViewer);
			// int pageIndex = addPage(viewerPane.getControl());
			//				setPageText(pageIndex, getString("_UI_TablePage_label")); //$NON-NLS-1$
			// }
			//
			// // This is the page for the table tree viewer.
			// //
			// {
			// ViewerPane viewerPane = new ViewerPane(getSite().getPage(),
			// EXTLibraryEditor.this) {
			// @Override
			// public Viewer createViewer(Composite composite) {
			// return new TreeViewer(composite);
			// }
			//
			// @Override
			// public void requestActivation() {
			// super.requestActivation();
			// setCurrentViewerPane(this);
			// }
			// };
			// viewerPane.createControl(getContainer());
			//
			// treeViewerWithColumns = (TreeViewer) viewerPane.getViewer();
			//
			// Tree tree = treeViewerWithColumns.getTree();
			// tree.setLayoutData(new FillLayout());
			// tree.setHeaderVisible(true);
			// tree.setLinesVisible(true);
			//
			// TreeColumn objectColumn = new TreeColumn(tree, SWT.NONE);
			//				objectColumn.setText(getString("_UI_ObjectColumn_label")); //$NON-NLS-1$
			// objectColumn.setResizable(true);
			// objectColumn.setWidth(250);
			//
			// TreeColumn selfColumn = new TreeColumn(tree, SWT.NONE);
			//				selfColumn.setText(getString("_UI_SelfColumn_label")); //$NON-NLS-1$
			// selfColumn.setResizable(true);
			// selfColumn.setWidth(200);
			//
			// treeViewerWithColumns.setColumnProperties(new String[] {
			//						"a", "b" }); //$NON-NLS-1$ //$NON-NLS-2$
			// treeViewerWithColumns
			// .setContentProvider(new AdapterFactoryContentProvider(
			// adapterFactory));
			// treeViewerWithColumns
			// .setLabelProvider(new AdapterFactoryLabelProvider(
			// adapterFactory));
			//
			// createContextMenuFor(treeViewerWithColumns);
			// int pageIndex = addPage(viewerPane.getControl());
			// setPageText(pageIndex,
			//						getString("_UI_TreeWithColumnsPage_label")); //$NON-NLS-1$
			// }
			//
			// getSite().getShell().getDisplay().asyncExec(new Runnable() {
			// public void run() {
			// setActivePage(0);
			// }
			// });
		}
		//
		// // Ensures that this editor will only display the page's tab
		// // area if there are more than one page
		// //
		// getContainer().addControlListener(new ControlAdapter() {
		// boolean guard = false;
		//
		// @Override
		// public void controlResized(ControlEvent event) {
		// if (!guard) {
		// guard = true;
		// hideTabs();
		// guard = false;
		// }
		// }
		// });
		//
		// getSite().getShell().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// updateProblemIndication();
		// }
		// });
	}

	/**
	 * If there is just one page in the multi-page editor part, this hides the
	 * single tab at the bottom. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void hideTabs() {

		// CB TODO, we don't have tabs.
		// if (getPageCount() <= 1) {
		//			setPageText(0, ""); //$NON-NLS-1$
		// if (getContainer() instanceof CTabFolder) {
		// ((CTabFolder) getContainer()).setTabHeight(1);
		// Point point = getContainer().getSize();
		// getContainer().setSize(point.x, point.y + 6);
		// }
		// }
	}

	/**
	 * If there is more than one page in the multi-page editor part, this shows
	 * the tabs at the bottom. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void showTabs() {

		// CB TODO, we don't have tabs.
		// if (getPageCount() > 1) {
		//			setPageText(0, getString("_UI_SelectionPage_label")); //$NON-NLS-1$
		// if (getContainer() instanceof CTabFolder) {
		// ((CTabFolder) getContainer()).setTabHeight(SWT.DEFAULT);
		// Point point = getContainer().getSize();
		// getContainer().setSize(point.x, point.y - 6);
		// }
		// }
	}

	/**
	 * This is used to track the active viewer. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	// @Override
	// protected void pageChange(int pageIndex) {
	// super.pageChange(pageIndex);
	//
	// if (contentOutlinePage != null) {
	// handleContentOutlineSelection(contentOutlinePage.getSelection());
	// }
	// }

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class key) {
		if (key.equals(IOutlinePartProvider.class)) {
			return showOutlineView() ? new IOutlinePartProvider() {
				public Class<?> getOutlineClass() {
					return EXTLibraryContentOutline.class;
				}
			} : null;
		}

		// The e4 Adapter service, will deal with a non-adaptables.
		return null;
		// else if (key.equals(IPropertySheetPage.class)) {
		// return getPropertySheetPage();
		// }
	}

	/**
	 * This accesses a cached version of the content outliner. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated //
	 */
	// public IContentOutlinePage getContentOutlinePage() {
	// if (contentOutlinePage == null) {
	// // The content outline is just a tree.
	// //
	// class MyContentOutlinePage extends ContentOutlinePage {
	// @Override
	// public void createControl(Composite parent) {
	// super.createControl(parent);
	// contentOutlineViewer = getTreeViewer();
	// contentOutlineViewer.addSelectionChangedListener(this);
	//
	// // Set up the tree viewer.
	// //
	// contentOutlineViewer
	// .setContentProvider(new AdapterFactoryContentProvider(
	// adapterFactory));
	// contentOutlineViewer
	// .setLabelProvider(new AdapterFactoryLabelProvider(
	// adapterFactory));
	// contentOutlineViewer.setInput(editingDomain
	// .getResourceSet());
	//
	// // Make sure our popups work.
	// //
	// createContextMenuFor(contentOutlineViewer);
	//
	// if (!editingDomain.getResourceSet().getResources()
	// .isEmpty()) {
	// // Select the root object in the view.
	// //
	// contentOutlineViewer
	// .setSelection(new StructuredSelection(
	// editingDomain.getResourceSet()
	// .getResources().get(0)), true);
	// }
	// }
	//
	// @Override
	// public void makeContributions(IMenuManager menuManager,
	// IToolBarManager toolBarManager,
	// IStatusLineManager statusLineManager) {
	// super.makeContributions(menuManager, toolBarManager,
	// statusLineManager);
	// contentOutlineStatusLineManager = statusLineManager;
	// }
	//
	// @Override
	// public void setActionBars(IActionBars actionBars) {
	// super.setActionBars(actionBars);
	// getActionBarContributor().shareGlobalActions(this,
	// actionBars);
	// }
	// }
	//
	// contentOutlinePage = new MyContentOutlinePage();
	//
	// // Listen to selection so that we can handle it is a special way.
	// //
	// contentOutlinePage
	// .addSelectionChangedListener(new ISelectionChangedListener() {
	// // This ensures that we handle selections correctly.
	// //
	// public void selectionChanged(SelectionChangedEvent event) {
	// handleContentOutlineSelection(event.getSelection());
	// }
	// });
	// }
	//
	// return contentOutlinePage;
	// }

	/**
	 * This accesses a cached version of the property sheet. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// Part support for property sheet page.
	// public IPropertySheetPage getPropertySheetPage() {
	// PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage(
	// editingDomain) {
	// @Override
	// public void setSelectionToViewer(List<?> selection) {
	// EXTLibraryEditor.this.setSelectionToViewer(selection);
	// EXTLibraryEditor.this.setFocus();
	// }
	//
	// @Override
	// public void setActionBars(IActionBars actionBars) {
	// super.setActionBars(actionBars);
	// getActionBarContributor().shareGlobalActions(this, actionBars);
	// }
	// };
	// propertySheetPage
	// .setPropertySourceProvider(new AdapterFactoryContentProvider(
	// adapterFactory));
	// propertySheetPages.add(propertySheetPage);
	//
	// return propertySheetPage;
	// }

	/**
	 * This deals with how we want selection in the outliner to affect the other
	 * views. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void handleContentOutlineSelection(ISelection selection) {
		if (currentViewerPane != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Iterator<?> selectedElements = ((IStructuredSelection) selection)
					.iterator();
			if (selectedElements.hasNext()) {
				// Get the first selected element.
				//
				Object selectedElement = selectedElements.next();

				// If it's the selection viewer, then we want it to select the
				// same selection as this selection.
				//
				if (currentViewerPane.getViewer() == selectionViewer) {
					ArrayList<Object> selectionList = new ArrayList<Object>();
					selectionList.add(selectedElement);
					while (selectedElements.hasNext()) {
						selectionList.add(selectedElements.next());
					}

					// Set the selection to the widget.
					//
					selectionViewer.setSelection(new StructuredSelection(
							selectionList));
				} else {
					// Set the input to the widget.
					//
					if (currentViewerPane.getViewer().getInput() != selectedElement) {
						currentViewerPane.getViewer().setInput(selectedElement);
						currentViewerPane.setTitle(selectedElement);
					}
				}
			}
		}
	}

	/**
	 * This is for implementing {@link IEditorPart} and simply tests the command
	 * stack. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	// @Override
	// public boolean isDirty() {
	// return ((BasicCommandStack) editingDomain.getCommandStack())
	// .isSaveNeeded();
	// }

	/**
	 * CB As we are not queried for dirty state, we should force it whener the
	 * command stack changes. Parked here for the moment.
	 * 
	 * @return
	 */
	public boolean internalIsDirty() {
		return ((BasicCommandStack) editingDomain.getCommandStack())
				.isSaveNeeded();
	}

	@Persist
	public void doSave(IProgressMonitor progressMonitor) {
		// Save only resources that have actually changed.
		//
		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
				Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		saveOptions.put(Resource.OPTION_LINE_DELIMITER,
				Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

		// Do the work within an operation because this is a long running
		// activity that modifies the workbench.
		//
		IRunnableWithProgress operation = new IRunnableWithProgress() {
			// This is the method that gets invoked when the operation runs.
			//
			public void run(IProgressMonitor monitor) {
				// Save the resources to the file system.
				//
				boolean first = true;
				for (Resource resource : editingDomain.getResourceSet()
						.getResources()) {
					if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
							&& !editingDomain.isReadOnly(resource)) {
						try {
							long timeStamp = resource.getTimeStamp();
							resource.save(saveOptions);
							if (resource.getTimeStamp() != timeStamp) {
								savedResources.add(resource);
							}
						} catch (Exception exception) {
							resourceToDiagnosticMap
									.put(resource,
											analyzeResourceProblems(resource,
													exception));
						}
						first = false;
					}
				}
			}
		};

		updateProblemIndication = false;
		try {
			// This runs the options, and shows progress.
			new ProgressMonitorDialog(activeShell).run(true, false, operation);

			// Refresh the necessary state.
			//
			((BasicCommandStack) editingDomain.getCommandStack()).saveIsDone();
			// firePropertyChange(IEditorPart.PROP_DIRTY);

			// CB We are not dirty anymore.
			dirtyable.setDirty(false);
		} catch (Exception exception) {
			// Something went wrong that shouldn't.
			//
			EXTLibraryEditorPlugin.INSTANCE.log(exception);
		}
		updateProblemIndication = true;
		updateProblemIndication();
	}

	/**
	 * This returns whether something has been persisted to the URI of the
	 * specified resource. The implementation uses the URI converter from the
	 * editor's resource set to try to open an input stream. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected boolean isPersisted(Resource resource) {
		boolean result = false;
		try {
			InputStream stream = editingDomain.getResourceSet()
					.getURIConverter().createInputStream(resource.getURI());
			if (stream != null) {
				result = true;
				stream.close();
			}
		} catch (IOException e) {
			// Ignore
		}
		return result;
	}

	/**
	 * This always returns true because it is not currently supported. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	// @Override
	// public boolean isSaveAsAllowed() {
	// return true;
	// }

	/**
	 * This also changes the editor's input. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Persist
	public void doSaveAs() {
		String[] filters = FILE_EXTENSION_FILTERS
				.toArray(new String[FILE_EXTENSION_FILTERS.size()]);
		String[] files = support.openFilePathDialog(activeShell, SWT.SAVE,
				filters);

		if (files.length > 0) {
			@SuppressWarnings("unused")
			URI uri = URI.createFileURI(files[0]);
			// CB TODO Migrate
			// doSaveAs(uri, new URIEditorInput(uri));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// TODO
	// protected void doSaveAs(URI uri, IEditorInput editorInput) {
	// (editingDomain.getResourceSet().getResources().get(0)).setURI(uri);
	//
	// setInputWithNotify(editorInput);
	// setPartName(editorInput.getName());
	// IProgressMonitor progressMonitor = getActionBars()
	// .getStatusLineManager() != null ? getActionBars()
	// .getStatusLineManager().getProgressMonitor()
	// : new NullProgressMonitor();
	//
	// doSave(progressMonitor);
	// }

	/**
	 * This is called during startup. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	// CB input is handled differently.
	// @Override
	// public void init(IEditorSite site, IEditorInput editorInput) {
	// setSite(site);
	// setInputWithNotify(editorInput);
	// setPartName(editorInput.getName());
	// site.setSelectionProvider(this);
	// site.getPage().addPartListener(partListener);
	// }

	@PostConstruct
	public void init(MApplication application, MInputPart inputPart,
			Composite parent) {
		inputURI = inputPart.getInputURI();
		this.container = parent;
		createPages();

		// Make editing domain and adapter factory available in the application
		// context.
		// The context could also be set on the part. Other parts could obtain
		// it using the
		// ActivePart injection, and pass on relevant context variables to a
		// newly created child context.
		IEclipseContext context = application.getContext();
		context.set(EditingDomain.class, this.getEditingDomain());
		context.set(AdapterFactory.class, this.getAdapterFactory());

	}

	@Inject
	@Optional
	public void partActivation(
			@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event,
			MApplication application) {

		MPart part = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
		IEclipseContext appContext = application.getContext();
		if (part.getElementId().equals(HandlerSupport.EDITOR_ID)) {
			appContext.set("EMF_Editor_Active", HandlerSupport.EDITOR_ID);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Focus
	public void setFocus() {

		// TODO, Viewer pane not supported yet, fix the pane first.
		if (currentViewerPane != null) {
			currentViewerPane.setFocus();
		}
		// else {

		// getControl(getActivePage()).setFocus();

		// }
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
	 * return this editor's overall selection. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public ISelection getSelection() {
		return editorSelection;
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
	 * set this editor's overall selection. Calling this result will notify the
	 * listeners. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSelection(ISelection selection) {
		editorSelection = selection;

		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}

		if (selection instanceof IStructuredSelection) {
			selectionService.setSelection(((IStructuredSelection) selection)
					.getFirstElement());
		}

		// CB Fix See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=332499
		// setStatusLineManager(selection);
	}

	// CB TODO
	// public void setStatusLineManager(ISelection selection) {
	// IStatusLineManager statusLineManager = currentViewer != null
	// && currentViewer == contentOutlineViewer ?
	// contentOutlineStatusLineManager
	// : getActionBars().getStatusLineManager();
	//
	// if (statusLineManager != null) {
	// if (selection instanceof IStructuredSelection) {
	// Collection<?> collection = ((IStructuredSelection) selection)
	// .toList();
	// switch (collection.size()) {
	// case 0: {
	// statusLineManager
	//							.setMessage(getString("_UI_NoObjectSelected")); //$NON-NLS-1$
	// break;
	// }
	// case 1: {
	// String text = new AdapterFactoryItemDelegator(
	// adapterFactory).getText(collection.iterator()
	// .next());
	// statusLineManager.setMessage(getString(
	//							"_UI_SingleObjectSelected", text)); //$NON-NLS-1$
	// break;
	// }
	// default: {
	// statusLineManager
	// .setMessage(getString(
	//									"_UI_MultiObjectSelected", Integer.toString(collection.size()))); //$NON-NLS-1$
	// break;
	// }
	// }
	// } else {
	//				statusLineManager.setMessage(""); //$NON-NLS-1$
	// }
	// }
	// }

	/**
	 * This looks up a string in the plugin's plugin.properties file. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static String getString(String key) {
		return EXTLibraryEditorPlugin.INSTANCE.getString(key);
	}

	/**
	 * This looks up a string in plugin.properties, making a substitution. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static String getString(String key, Object s1) {
		return EXTLibraryEditorPlugin.INSTANCE.getString(key,
				new Object[] { s1 });
	}

	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help
	 * fill the context menus with contributions from the Edit menu. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void menuAboutToShow(IMenuManager menuManager) {

		// CB TODO migrate ActionBar
		// ((IMenuListener) getEditorSite().getActionBarContributor())
		// .menuAboutToShow(menuManager);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	// CB TODO
	// public EditingDomainActionBarContributor getActionBarContributor() {
	// return (EditingDomainActionBarContributor) getEditorSite()
	// .getActionBarContributor();
	// }

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	// CB TODO
	// public IActionBars getActionBars() {
	// return getActionBarContributor().getActionBars();
	// }

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public AdapterFactory getAdapterFactory() {
		return adapterFactory;
	}

	// CB changed, check later if needed.
	@PreDestroy
	public void dispose() {
		updateProblemIndication = false;

		// getSite().getPage().removePartListener(partListener);

		adapterFactory.dispose();

		// CB TODO, Migrate.
		// if (getActionBarContributor().getActiveEditor() == this) {
		// getActionBarContributor().setActiveEditor(null);
		// }

		// for (PropertySheetPage propertySheetPage : propertySheetPages) {
		// propertySheetPage.dispose();
		// }
		//
		// if (contentOutlinePage != null) {
		// contentOutlinePage.dispose();
		// }

		// super.dispose();
	}

	/**
	 * Returns whether the outline view should be presented to the user. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected boolean showOutlineView() {
		return true;
	}
}