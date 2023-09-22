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
package com.cognifide.apm.core.endpoints.dto

import com.cognifide.apm.api.scripts.Script
import org.apache.commons.io.FilenameUtils
import java.util.*

class ScriptDto(script: Script) {

    val name: String = FilenameUtils.getName(script.path)
    val path: String = script.path
    val author: String? = script.author
    val launchEnabled: Boolean = script.isLaunchEnabled
    val launchMode: String = script.launchMode.name.lowercase()
    val lastModified: Date? = script.lastModified
    val valid: Boolean = script.isValid

}