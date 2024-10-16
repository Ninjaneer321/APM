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
package com.cognifide.apm.main.actions.removeparentsgroups;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.main.actions.removechildrengroups.ClearFromGroupDetacher;

public class RemoveParentsGroups implements Action {

  @Override
  public ActionResult simulate(Context context) {
    ClearFromGroupDetacher detacher = new ClearFromGroupDetacher(context, true);
    return detacher.detachAuthorizableFromParents();
  }

  @Override
  public ActionResult execute(Context context) {
    ClearFromGroupDetacher detacher = new ClearFromGroupDetacher(context, false);
    return detacher.detachAuthorizableFromParents();
  }
}
