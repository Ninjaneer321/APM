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
package com.cognifide.apm.api.actions;

import com.cognifide.apm.api.status.Status;
import java.util.List;

public interface ActionResult {

  void logMessage(String message);

  void logWarning(String warning);

  void logError(String error);

  void changeStatus(Status status, String message);

  List<Message> getMessages();

  void setAuthorizable(String authorizable);

  ActionResult merge(ActionResult... actionResult);

  ActionResult merge(List<ActionResult> actionResult);

  String getAuthorizable();

  Status getStatus();
}
