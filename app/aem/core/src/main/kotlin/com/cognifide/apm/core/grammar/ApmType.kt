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

package com.cognifide.apm.core.grammar

abstract class ApmType(val argument: Any? = null) {
    open val integer: Int?
        get() = null
    open val string: String?
        get() = null
    open val list: List<String>?
        get() = null
    open val nestedList: List<List<String>>?
        get() = null
}

abstract class ApmValue(arg: Any? = null) : ApmType(arg)

data class ApmInteger(val value: Int) : ApmValue(value) {
    override val integer: Int
        get() = value

    override fun toString(): String {
        return value.toString()
    }
}

data class ApmString(val value: String) : ApmValue(value) {
    override val string: String
        get() = value

    override fun toString(): String {
        return "\"$value\""
    }
}

data class ApmList(val value: List<String>) : ApmType(value) {
    override val list: List<String>
        get() = value

    override fun toString(): String {
        return value.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
    }

}

data class ApmNestedList(val value: List<List<String>>) : ApmType(value) {
    override val nestedList: List<List<String>>
        get() = value

    override fun toString(): String {
        return value.joinToString(prefix = "[", postfix = "]") { item ->
            item.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
        }
    }

}

class ApmEmpty : ApmType()
