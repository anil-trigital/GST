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
package com.gst.batch.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gst.batch.domain.BatchRequest;
import com.gst.batch.domain.BatchResponse;
import com.gst.batch.serialization.BatchRequestJsonHelper;
import com.gst.batch.service.BatchApiService;
import com.gst.infrastructure.core.serialization.ToApiJsonSerializer;
import com.gst.infrastructure.security.service.PlatformSecurityContext;

/**
 * Provides a REST resource for Batch Requests. This class acts as a proxy to
 * {@link com.gst.batch.service.BatchApiService} and de-serializes the
 * incoming JSON string to a list of
 * {@link com.gst.batch.domain .BatchRequest} type. This list is
 * forwarded to BatchApiService which finally returns a list of
 * {@link com.gst.batch.domain.BatchResponse} type which is then
 * serialized into JSON response by this Resource class.
 * 
 * @author Rishabh Shukla
 * 
 * @see com.gst.batch.service.BatchApiService
 * @see com.gst.batch.domain.BatchRequest
 * @see com.gst.batch.domain.BatchResponse
 */
@Path("/batches")
@Component
@Scope("singleton")
public class BatchApiResource {

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BatchResponse> toApiJsonSerializer;
    private final BatchApiService service;
    private final BatchRequestJsonHelper batchRequestJsonHelper;

    /**
     * Constructs a 'BatchApiService' with context, toApiJsonSerializer, service
     * and batchRequestJsonHelper.
     * 
     * @param context
     * @param toApiJsonSerializer
     * @param service
     * @param batchRequestJsonHelper
     */
    @Autowired
    public BatchApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer<BatchResponse> toApiJsonSerializer,
            final BatchApiService service, final BatchRequestJsonHelper batchRequestJsonHelper) {

        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.service = service;
        this.batchRequestJsonHelper = batchRequestJsonHelper;
    }

    /**
     * Rest assured POST method to get {@link BatchRequest} and returns back the
     * consolidated {@link BatchResponse}
     * 
     * @param jsonRequestString
     * @param enclosingTransaction
     * @param uriInfo
     * @return serialized JSON
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String handleBatchRequests(@DefaultValue("false") @QueryParam("enclosingTransaction") final boolean enclosingTransaction,
            final String jsonRequestString, @Context UriInfo uriInfo) {

        // Handles user authentication
        this.context.authenticatedUser();

        // Converts request array into BatchRequest List
        final List<BatchRequest> requestList = this.batchRequestJsonHelper.extractList(jsonRequestString);

        // Gets back the consolidated BatchResponse from BatchApiservice
        List<BatchResponse> result = new ArrayList<>();

        // If the request is to be handled as a Transaction. All requests will
        // be rolled back on error
        if (enclosingTransaction) {
            result = service.handleBatchRequestsWithEnclosingTransaction(requestList, uriInfo);
        } else {
            result = service.handleBatchRequestsWithoutEnclosingTransaction(requestList, uriInfo);
        }

        return this.toApiJsonSerializer.serialize(result);

    }
}
