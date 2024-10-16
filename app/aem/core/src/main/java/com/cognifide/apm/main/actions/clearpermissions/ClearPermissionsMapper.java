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
package com.cognifide.apm.main.actions.clearpermissions;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "CLEAR-PERMISSIONS", group = ActionGroup.CORE)
public final class ClearPermissionsMapper {

  public static final String STRICT_PATH = "STRICT-PATH";
  public static final String STRICT_PATH_DESC = "remove permissions only for given path, permissions on descendants stay as they were";
  public static final String REFERENCE = "Delete every permission applied for current authorizable starting from specified path down to each descendants.";

  @Mapping(
      examples = {
          "CLEAR-PERMISSIONS '/'",
          "CLEAR-PERMISSIONS '/' --" + STRICT_PATH
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "path", description = "e.g.: '/content/dam'") String path,
      @Flag(value = STRICT_PATH, description = STRICT_PATH_DESC) boolean strictPath) {
    if (strictPath) {
      return new RemoveAll(path);
    } else {
      return new Purge(path);
    }
  }

}
