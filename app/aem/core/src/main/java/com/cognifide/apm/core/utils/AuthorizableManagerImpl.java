/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.utils;

import com.cognifide.apm.api.actions.AuthorizableManager;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.exceptions.AuthorizableNotFoundException;
import com.cognifide.apm.core.utils.mocks.MockGroup;
import com.cognifide.apm.core.utils.mocks.MockPrincipal;
import com.cognifide.apm.core.utils.mocks.MockUser;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

public class AuthorizableManagerImpl implements AuthorizableManager {

  private final UserManager userManager;

  private final Map<String, Authorizable> existingAuthorizables = new HashMap<>();

  private final Set<String> removedAuthorizables = new HashSet<>();

  public AuthorizableManagerImpl(UserManager userManager) {
    this.userManager = userManager;
  }

  @Override
  public Authorizable getAuthorizableIfExists(String id) throws RepositoryException {
    if (checkIfRemoved(id)) {
      return null;
    }
    Authorizable authorizable = existingAuthorizables.get(id);

    if (authorizable == null) {
      authorizable = userManager.getAuthorizable(id);
    }

    return authorizable;
  }

  @Override
  public Authorizable getAuthorizable(String id) throws ActionExecutionException, RepositoryException, AuthorizableNotFoundException {
    return getAuthorizable(Authorizable.class, id);
  }

  @Override
  public void markAuthorizableAsRemoved(Authorizable authorizable)
      throws RepositoryException {
    removedAuthorizables.add(authorizable.getID());
  }

  @Override
  public Group getGroupIfExists(String id) throws RepositoryException, ActionExecutionException {
    return getAuthorizableIfExists(Group.class, id);
  }

  @Override
  public Group getGroup(String id) throws ActionExecutionException, RepositoryException, AuthorizableNotFoundException {
    return getAuthorizable(Group.class, id);
  }

  @Override
  public Group createGroup(String id, Principal namePrincipal, String path) throws RepositoryException {
    Group group = userManager.createGroup(id, namePrincipal, path);
    existingAuthorizables.put(id, group);
    removedAuthorizables.remove(id);
    return group;
  }

  @Override
  public Group createMockGroup(String id) {
    Group group = new MockGroup(id);
    existingAuthorizables.put(id, group);
    removedAuthorizables.remove(id);
    return group;
  }

  @Override
  public void removeGroup(Group group) throws RepositoryException {
    existingAuthorizables.remove(group.getID());
    group.remove();
  }

  @Override
  public User getUserIfExists(String id) throws ActionExecutionException, RepositoryException {
    return getAuthorizableIfExists(User.class, id);
  }

  @Override
  public User getUser(String id) throws ActionExecutionException, RepositoryException, AuthorizableNotFoundException {
    return getAuthorizable(User.class, id);
  }

  @Override
  public User createUser(String id, String password, Principal namePrincipal, String path) throws RepositoryException {
    User user = userManager.createUser(id, password, namePrincipal, path);
    existingAuthorizables.put(id, user);
    removedAuthorizables.remove(id);
    return user;
  }

  @Override
  public User createSystemUser(String id, String path) throws RepositoryException {
    User user = userManager.createSystemUser(id, path);
    existingAuthorizables.put(id, user);
    removedAuthorizables.remove(id);
    return user;
  }

  @Override
  public User createMockUser(String id) {
    User user = new MockUser(id);
    existingAuthorizables.put(id, user);
    removedAuthorizables.remove(id);
    return user;
  }

  @Override
  public void removeUser(User user) throws RepositoryException {
    Iterator<Group> groups = user.memberOf();
    while (groups.hasNext()) {
      groups.next().removeMember(user);
    }
    existingAuthorizables.remove(user.getID());
    user.remove();
  }

  @Override
  public Principal createMockPrincipal(String name) {
    return new MockPrincipal(name);
  }

  private <T extends Authorizable> T getAuthorizableIfExists(Class<T> authorizableClass, String id)
      throws ActionExecutionException, RepositoryException {
    if (checkIfRemoved(id)) {
      return null;
    }

    Authorizable authorizable = existingAuthorizables.get(id);

    if (authorizable == null) {
      authorizable = userManager.getAuthorizable(id);
    }

    if (authorizable == null) {
      return null;
    }

    if (!authorizableClass.isInstance(authorizable)) {
      throw new ActionExecutionException(
          String.format("Authorizable with id %s exists but is a %s", id, authorizableClass.getSimpleName()));
    }

    existingAuthorizables.put(id, authorizable);
    return authorizableClass.cast(authorizable);
  }

  private <T extends Authorizable> T getAuthorizable(Class<T> authorizableClass, String id)
      throws ActionExecutionException, RepositoryException, AuthorizableNotFoundException {
    if (checkIfRemoved(id)) {
      throw new AuthorizableNotFoundException(
          String.format("%s with id %s not found", authorizableClass.getSimpleName(), id));
    }

    Authorizable authorizable = existingAuthorizables.get(id);

    if (authorizable == null) {
      authorizable = userManager.getAuthorizable(id);
    }

    if (authorizable == null) {
      throw new AuthorizableNotFoundException(
          String.format("%s with id %s not found", authorizableClass.getSimpleName(), id));
    }

    if (!authorizableClass.isInstance(authorizable)) {
      throw new ActionExecutionException(
          String.format("Authorizable with id %s exists but is a %s", id, authorizableClass.getSimpleName()));
    }

    existingAuthorizables.put(id, authorizable);
    return authorizableClass.cast(authorizable);
  }

  private boolean checkIfRemoved(String id) {
    return removedAuthorizables.contains(id);
  }
}
