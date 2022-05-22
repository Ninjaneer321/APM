/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.main.actions.createauthorizable;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.main.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAuthorizable implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateAuthorizable.class);

  private final String path;

  private final String id;

  private final String password;

  private final String externalId;

  private final Boolean ignoreIfExists;

  private final CreateAuthorizableStrategy createStrategy;

  public CreateAuthorizable(String id, String password, String path, String externalId,
      Boolean ignoreIfExists, CreateAuthorizableStrategy createStrategy) {
    this.id = id;
    this.password = password;
    this.path = path;
    this.externalId = externalId;
    this.ignoreIfExists = ignoreIfExists;
    this.createStrategy = createStrategy;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context, true);
  }

  @Override
  public ActionResult execute(Context context) {
    return process(context, false);
  }

  public ActionResult process(Context context, boolean simulate) {
    ActionResult actionResult = context.createActionResult();
    try {
      Authorizable authorizable = context.getAuthorizableManager().getAuthorizableIfExists(id);
      LOGGER.info("Creating authorizable with id = " + id);
      if (authorizable != null) {
        logMessage(actionResult, authorizable);
      } else {
        authorizable = createStrategy.create(id, password, path, externalId, context, actionResult, simulate);
      }
      context.setCurrentAuthorizable(authorizable);
    } catch (RepositoryException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private void logMessage(ActionResult actionResult, Authorizable authorizable) throws RepositoryException {
    if (!ignoreIfExists) {
      if (authorizable instanceof Group) {
        actionResult.logError(authorizableExists(authorizable.getID(), "Group"));
      } else {
        actionResult.logError(authorizableExists(authorizable.getID(), "User"));
      }
    } else {
      if (authorizable instanceof Group) {
        actionResult.changeStatus(Status.SKIPPED, authorizableExists(authorizable.getID(), "Group"));
      } else {
        actionResult.changeStatus(Status.SKIPPED, authorizableExists(authorizable.getID(), "User"));
      }
    }
  }

  private static String authorizableExists(String authorizableId, String type) {
    return "Authorizable with id: " + authorizableId + " already exists, and is a " + type;
  }
}
