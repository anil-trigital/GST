/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.gst.organisation.provisioning.handler;

import com.gst.commands.annotation.CommandType;
import com.gst.commands.handler.NewCommandSourceHandler;
import com.gst.infrastructure.core.api.JsonCommand;
import com.gst.infrastructure.core.data.CommandProcessingResult;
import com.gst.organisation.provisioning.service.ProvisioningCriteriaWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "PROVISIONCRITERIA", action = "UPDATE")
public class UpdateProvisioningCriteriaRequestCommandHandler implements NewCommandSourceHandler {

    private final ProvisioningCriteriaWritePlatformService provisioningCriteriaWritePlatformService;

    @Autowired
    public UpdateProvisioningCriteriaRequestCommandHandler(final ProvisioningCriteriaWritePlatformService provisioningCriteriaWritePlatformService) {
        this.provisioningCriteriaWritePlatformService = provisioningCriteriaWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
        return this.provisioningCriteriaWritePlatformService.updateProvisioningCriteria(jsonCommand.entityId(), jsonCommand);
    }

}