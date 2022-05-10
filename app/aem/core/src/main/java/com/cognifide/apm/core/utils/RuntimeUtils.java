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
package com.cognifide.apm.core.utils;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeUtils {

  public static final String SERVICE_PID = "org.apache.jackrabbit.oak.composite.CompositeNodeStoreService";

  public static boolean determineCompositeNodeStore(ConfigurationAdmin configurationAdmin) {
    boolean result;
    try {
      Configuration configuration = configurationAdmin.getConfiguration(SERVICE_PID, null);
      result = Optional.ofNullable(configuration)
          .map(Configuration::getProperties)
          .map(dict -> (Boolean) dict.get("enabled"))
          .orElse(false);
    } catch (Exception e) {
      result = false;
    }
    return result;
  }

}
