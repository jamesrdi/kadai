/*
 * Copyright [2024] [envite consulting GmbH]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.kadai.spi.task.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.ReviewRequiredProvider;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewRequiredManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRequiredManager.class);

  private final List<ReviewRequiredProvider> reviewRequiredProviders;

  public ReviewRequiredManager(KadaiEngine kadaiEngine) {
    reviewRequiredProviders = SpiLoader.load(ReviewRequiredProvider.class);
    for (ReviewRequiredProvider serviceProvider : reviewRequiredProviders) {
      serviceProvider.initialize(kadaiEngine);
      LOGGER.info(
          "Registered ReviewRequiredProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (reviewRequiredProviders.isEmpty()) {
      LOGGER.info(
          "No ReviewRequiredProvider service provider found. "
              + "Running without any ReviewRequiredProvider implementation.");
    }
  }

  public boolean reviewRequired(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to ReviewRequiredProvider service providers: {}", task);
    }

    return reviewRequiredProviders.stream()
        .anyMatch(serviceProvider -> serviceProvider.reviewRequired(task));
  }
}