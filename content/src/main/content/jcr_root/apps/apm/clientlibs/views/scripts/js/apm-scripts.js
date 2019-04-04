/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Cognifide Limited
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
(function (window, $) {

  const ERROR_STATUS = 'ERROR',
      WARNING_STATUS = 'WARNING',
      SUCCESS_STATUS = 'SUCCESS';

  let utilMessenger = $(window).adaptTo('foundation-util-messenger'),
      uiHelper = $(window).adaptTo('foundation-ui');

  $(document).on('foundation-contentloaded', function () {
    utilMessenger.promptAll();
  });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.activecondition', {
        name: 'is-not-folder',
        handler: function (name, el, config, collection, selections) {
          return !isFolder(selections);
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.activecondition', {
        name: 'is-available',
        handler: function (name, el, config, collection, selections) {
          if (isFolder(selections)) {
            return false;
          }

          el.disabled = isScriptInvalidOrNonExecutable(selections);
          return true;
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.dryrun',
        handler: function (name, el, config, collection, selections) {
          const selected = selections[0].attributes['data-path'].value;
          runOnAuthor(selected, 'DRY_RUN');
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonauthor',
        handler: function (name, el, config, collection, selections) {
          const selected = selections[0].attributes['data-path'].value;
          runOnAuthor(selected, 'RUN');
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonpublish',
        handler: function (name, el, config, collection, selections) {
          const selected = selections[0].attributes['data-path'].value;
          runOnPublish(selected);
        }
      });

  function runOnAuthor(scriptPath, mode) {
    $.ajax({
      type: 'POST',
      url: '/bin/cqsm/run-background?file=' + scriptPath + '&mode=' + mode,
      dataType: 'json',
      success: function (data) {
        const jobId = data.id;
        const jobMessage = data.message;
        checkStatus(jobId, jobMessage, mode);
      }
    });
  }

  function runOnPublish(fileName) {
    $.ajax({
      type: 'GET',
      url: '/bin/cqsm/replicate?run=publish&fileName=' + fileName,
      dataType: 'json',
      success: function (data) {
        console.log('publish response: ' + JSON.stringify(data));
        uiHelper.notify('info', 'Run on publish executed successfully', 'info');
      },
      error: function (data) {
        console.log('publish  response: ' + JSON.stringify(data));
        uiHelper.notify('error', 'Run on publish wasn\'t executed successfully: '
            + data.responseJSON.message, 'error');
      }
    });
  }

  function checkStatus(jobId, jobMessage, mode) {
    $.ajax({
      type: 'GET',
      url: '/bin/cqsm/run-background?id=' + jobId,
      dataType: 'json',
      success: function (data) {
        if (data.type === 'running') {
          setTimeout(function () {
            checkStatus(jobId, jobMessage, mode)
          }, 1000);
        } else if (data.type === 'finished') {
          let status = getResponseStatus(data);
          showMessageOnFinished(mode, status);
          reloadPage();
        } else if (data.type === 'unknown') {
          showMessageOnUnknown(mode, jobMessage);
          reloadPage();
        }
      }
    });
  }

  function showMessageOnFinished(mode, status) {
    let title;

    switch (mode) {
      case 'DRY_RUN':
        title = 'Dry Run';
        break;
      case 'RUN':
        title = 'Run on author';
        break;
    }

    switch (status) {
      case ERROR_STATUS:
        utilMessenger.put('error', title + ' executed with errors', 'error');
        break;
      case WARNING_STATUS:
        utilMessenger.put('warning', title + ' executed with warnings', 'notice');
        break;
      case SUCCESS_STATUS:
        utilMessenger.put('success', title + ' executed successfully', 'success');
        break;
    }
  }

  function showMessageOnUnknown(mode, jobMessage) {
    switch (mode) {
      case 'DRY_RUN':
        utilMessenger.put('error', 'Dry Run wasn\'t executed successfully: ' + jobMessage, 'error');
        break;
      case 'RUN':
        utilMessenger.put('error', 'Run on author wasn\'t executed successfully: ' + jobMessage, 'error');
        break;
    }
  }

  function reloadPage() {
    setTimeout(function () {
      location.reload()
    }, 500);
  }

  function getResponseStatus(data) {
    let statuses = new Set(data.entries.map(function(entry) { return entry.status; })),
        result;
    if (statuses.has(ERROR_STATUS)) {
      result = ERROR_STATUS;
    } else if (statuses.has(WARNING_STATUS)) {
      result = WARNING_STATUS;
    } else {
      result = SUCCESS_STATUS;
    }
    return result;
  }

  function isFolder(selections) {
    return selections[0].items._container.innerHTML.includes('folder');
  }

  function isScriptInvalidOrNonExecutable(selections) {
    return selections[0].items._container.innerHTML.includes('script-is-invalid');
  }

})(window, jQuery);
