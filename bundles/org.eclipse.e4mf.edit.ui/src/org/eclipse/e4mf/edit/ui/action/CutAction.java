/**
 * Copyright (c) 2002-2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.e4mf.edit.ui.action;


import java.util.Collection;

import org.eclipse.e4mf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CutToClipboardCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;


/**
 * A cut action is implemented by creating a {@link CutToClipboardCommand}.
 */
public class CutAction extends CommandActionHandler
{
  public CutAction(EditingDomain domain)
  {
    super(domain, EMFEditUIPlugin.INSTANCE.getString("_UI_Cut_menu_item"));
  }

  public CutAction()
  {
    super(null, EMFEditUIPlugin.INSTANCE.getString("_UI_Cut_menu_item"));
  }

  @Override
  public Command createCommand(Collection<?> selection)
  {
    return CutToClipboardCommand.create(domain, selection);
  }

  /**
   * @deprecated As of EMF 2.1.0, replaced by {@link #setActiveWorkbenchPart}.
   */
  @Deprecated
  public void setActiveEditor(IEditorPart editorPart)
  {
    setActiveWorkbenchPart(editorPart);
  }

  /**
   * @since 2.1.0
   */
  public void setActiveWorkbenchPart(IWorkbenchPart workbenchPart)
  {
    if (workbenchPart instanceof IEditingDomainProvider)
    {
      domain = ((IEditingDomainProvider)workbenchPart).getEditingDomain();
    }
  }
}
