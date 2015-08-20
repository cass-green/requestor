/*
 * Copyright 2015 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.auth;

import io.reinert.requestor.PreparedRequest;
import io.reinert.requestor.RequestDispatcher;

/**
 * Abstraction for HTTP Authentication/Authorization methods.
 *
 * @author Danilo Reinert
 */
public abstract class AbstractAuth implements Auth {

    private RequestDispatcher dispatcher;

    public abstract void auth(PreparedRequest preparedRequest);

    public RequestDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}