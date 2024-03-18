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
package com.cognifide.apm.core.services.async;

import com.cognifide.apm.api.services.ExecutionResult;
import java.util.List;

public abstract class ExecutionStatus {

  private final String status;

  private ExecutionStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public static class RunningExecution extends ExecutionStatus {

    public RunningExecution() {
      super("running");
    }
  }

  public static class UnknownExecution extends ExecutionStatus {

    public UnknownExecution() {
      super("unknown");
    }
  }

  protected static class FinishedExecutionStatus extends ExecutionStatus {

    private final String path;

    private final long timestamp;

    private final String formattedDate;

    private final List<ExecutionResult.Entry> entries;

    public FinishedExecutionStatus(String path, long timestamp, String formattedDate, List<ExecutionResult.Entry> entries) {
      super("finished");
      this.path = path;
      this.timestamp = timestamp;
      this.formattedDate = formattedDate;
      this.entries = entries;
    }

    public String getPath() {
      return path;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public String getFormattedDate() {
      return formattedDate;
    }

    public List<ExecutionResult.Entry> getEntries() {
      return entries;
    }
  }

  public static class FinishedSuccessfulExecution extends FinishedExecutionStatus {

    public FinishedSuccessfulExecution(String path, long timestamp, String formattedDate, List<ExecutionResult.Entry> entries) {
      super(path, timestamp, formattedDate, entries);
    }
  }

  public static class FinishedFailedExecution extends FinishedExecutionStatus {

    private final ExecutionResult.Entry error;

    public FinishedFailedExecution(String path, long timestamp, String formattedDate, List<ExecutionResult.Entry> entries, ExecutionResult.Entry error) {
      super(path, timestamp, formattedDate, entries);
      this.error = error;
    }

    public ExecutionResult.Entry getError() {
      return error;
    }
  }
}
