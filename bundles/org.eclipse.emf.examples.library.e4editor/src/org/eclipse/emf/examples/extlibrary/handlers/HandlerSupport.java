package org.eclipse.emf.examples.extlibrary.handlers;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.examples.extlibrary.presentation.EXTLibraryEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
/**
 * @author Christophe Bouhier
 */
public class HandlerSupport extends ContextFunction {

	/**
	 * The parent ID of the part stack, used to re-create the stack if it's
	 * closed
	 */
	static final String STACK_PARENT = "org.eclipse.emf.examples.library.e4editor.partsashcontainer.main";

	/** The ID of the part stack which will be inserted */
	static final String STACK_POSITION = "org.eclipse.emf.examples.library.e4editor.partstack.editor.stack";

	/** The ID of the editor part which is created */
	public static final String EDITOR_ID = "org.eclipse.emf.examples.library.e4editor.partstack.editor";

	/** The URI for the contributed Editor */
	static final String EDITOR_CONTRIBUTION_URI = "bundleclass://org.eclipse.emf.examples.library.e4editor/org.eclipse.emf.examples.extlibrary.presentation.EXTLibraryEditor";

	private static final String[] FILE_EXTENSION_FILTERS = EXTLibraryEditor.FILE_EXTENSION_FILTERS
			.toArray(new String[0]);

	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(HandlerSupport.class, context);
	}

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	private EPartService partService;
	
//	@Inject
//	private ECommandService commandService;
	

	public boolean openEditor(URI inputURI) {
		return openEditor(inputURI.toFileString());
	}

	public boolean openEditor(String filePath) {
		MPartStack stack = (MPartStack) modelService.find(STACK_POSITION,
				application);

		// Create the stack if it doesn't exist.
		if (stack == null) {
			MPartStack editorStack = MBasicFactory.INSTANCE.createPartStack();
			editorStack.setElementId(STACK_POSITION);
			MUIElement parent = modelService.find(STACK_PARENT, application);
			if (parent instanceof MPartSashContainer) {
				((MPartSashContainer) parent).getChildren().add(editorStack);
			}
		}

		MInputPart part = MBasicFactory.INSTANCE.createInputPart();

		part.setElementId(EDITOR_ID);

		// Pointing to the contributing class
		part.setContributionURI(EDITOR_CONTRIBUTION_URI);

		// As file String.
		part.setInputURI(URI.createFileURI(filePath).toFileString());

		part.setIconURI("platform:/plugin/org.eclipse.emf.examples.library.e4editor/icons/full/obj16/EXTLibraryModelFile.gif");

		part.setLabel(filePath);
		// part.setTooltip(input.getTooltip());
		part.setCloseable(true);

		stack.getChildren().add(part);
		MPart showPart = partService.showPart(part,
				EPartService.PartState.ACTIVATE);
		return showPart != null;
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
