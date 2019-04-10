/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.servlets;

import static com.cognifide.cq.cqsm.core.utils.ErrorMessage.errorMessage;
import static com.cognifide.cq.cqsm.core.utils.ErrorMessage.errorMessageBuilder;
import static com.cognifide.cq.cqsm.core.utils.ServletUtils.writeJson;
import static com.cognifide.cq.cqsm.core.utils.SuccessMessage.successMessage;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.utils.ErrorMessage.ErrorMessageBuilder;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

@SlingServlet(paths = {"/bin/cqsm/validate"})
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Validation Servlet"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class ScriptValidationServlet extends SlingAllMethodsServlet {

  @Reference
  private transient ScriptManager scriptManager;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    final String content = request.getParameter("content");
    if (StringUtils.isEmpty(content)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      writeJson(response, errorMessage("Script content is required"));
      return;
    }

    try {
      final Progress progress = scriptManager.evaluate(content, Mode.VALIDATION, request.getResourceResolver());
      if (progress.isSuccess()) {
        writeJson(response, successMessage("Script passes validation"));
      } else {
        String lastError = progress.getLastError().getLastMessageText();
        ErrorMessageBuilder errorMessageBuilder = errorMessageBuilder("Script does not pass validation");
        if (StringUtils.isNotBlank(lastError)) {
          errorMessageBuilder.addError(lastError);
        }

        writeJson(response, errorMessageBuilder.build());
      }
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      writeJson(response, errorMessage("Script cannot be validated because of error: " + e.getMessage()));
    }
  }
}