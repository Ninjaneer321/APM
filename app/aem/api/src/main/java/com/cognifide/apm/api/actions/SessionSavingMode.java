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

public enum SessionSavingMode {

  EVERY_ACTION("EVERY-ACTION"), SINGLE("SINGLE"), ON_DEMAND("ON-DEMAND"), NEVER("NEVER");

  private final String mode;

  SessionSavingMode(String mode) {
    this.mode = mode;
  }

  public static SessionSavingMode valueOfMode(String mode) {
    for (SessionSavingMode savingMode : values()) {
      if (savingMode.getMode().equals(mode)) {
        return savingMode;
      }
    }
    throw new IllegalArgumentException("No enum const for given mode: " + mode
        + ", possible modes: EVERY-ACTION, SINGLE, ON-DEMAND, NEVER.");
  }

  public static SessionSavingMode getDefaultMode() {
    return EVERY_ACTION;
  }

  public String getMode() {
    return mode;
  }
}
