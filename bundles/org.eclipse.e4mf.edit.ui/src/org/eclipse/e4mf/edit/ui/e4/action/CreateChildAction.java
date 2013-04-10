/**
 * Copyright (c) 2002-2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.e4mf.edit.ui.e4.action;


import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;


/**
 * A child creation action is implemented by creating a {@link
 * CreateChildCommand}.
 */
public class CreateChildAction extends StaticSelectionCommandAction
{
  /**
   * This describes the child to be created.
   */
  protected Object descriptor;
  
  /**
   * This constructs an instance of an action that uses the given editing domain to create a child
   * specified by <code>descriptor</code> for the single object in the
   * <code>selection</code>.
   * @since 2.4.0
   */
  public CreateChildAction(EditingDomain editingDomain, ISelection selection, Object descriptor)
  {
    super(editingDomain);
    this.descriptor = descriptor;
    configureAction(selection);
  }

  /**
   * This creates the command for {@link
   * StaticSelectionCommandAction#createActionCommand}.
   */
  @Override
  protected Command createActionCommand(EditingDomain editingDomain, Collection<?> collection)
  {
    if (collection.size() == 1)
    {
      Object owner = collection.iterator().next();
      return CreateChildCommand.create(editingDomain, owner,
                                       descriptor, collection);
    }
    return UnexecutableCommand.INSTANCE;
  }
}
