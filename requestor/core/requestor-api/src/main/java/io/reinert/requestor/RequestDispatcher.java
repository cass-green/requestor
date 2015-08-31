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
package io.reinert.requestor;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.gwt.core.client.Callback;

import io.reinert.requestor.auth.Auth;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher {

    private final ResponseProcessor processor;
    private final DeferredFactory deferredFactory;

    public RequestDispatcher(ResponseProcessor processor, DeferredFactory deferredFactory) {
        this.processor = processor;
        this.deferredFactory = deferredFactory;
    }

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * The success result must be a instance of #resultType.
     * <p/>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link Deferred#notifyDownload(RequestProgress)} and
     * {@link Deferred#notifyUpload(RequestProgress)}.
     * <p/>
     * All possible exceptions should be caught and sent to {@link Deferred#reject(RequestException)}
     * wrapped in a {@link RequestException} or any of its children. This will avoid breaking code flow when some
     * exception occurs.
     *
     * @param request           The request to be sent
     * @param deferred          The deferred to resolve or reject when completed
     * @param resolveType       The class of the expected result in the promise
     * @param parametrizedType  The class of the parametrized type if a collection is expected as result
     * @param <D>               The expected type of the promise
     */
    protected abstract <D> void send(PreparedRequest request, final Deferred<D> deferred, Class<D> resolveType,
                                     @Nullable Class<?> parametrizedType);

    /**
     * Sends the request and return an instance of {@link Promise} expecting a sole result.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param <T>           The expected type in response payload
     *
     * @return              The promise for the dispatched request
     */
    @SuppressWarnings("unchecked")
    public <T> Promise<T> dispatch(SerializedRequest request, Class<T> resultType) {
        final Deferred<T> deferred = deferredFactory.getDeferred();

        final Auth auth = request.getAuth();
        auth.setDispatcher(this);
        auth.auth(new PreparedRequestImpl(this, request, deferred, resultType, null));

        return deferred.getPromise();
    }

    /**
     * Sends the request and return an instance of {@link Promise} expecting a collection result.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param containerType The class instance of the container type which will hold the values
     * @param <T>           The expected type in response payload
     * @param <C>           The collection type to hold the values
     *
     * @return              The promise for the dispatched request
     */
    @SuppressWarnings("unchecked")
    public <T, C extends Collection> Promise<Collection<T>> dispatch(SerializedRequest request, Class<T> resultType,
                                                                     Class<C> containerType) {
        final Deferred<Collection<T>> deferred = deferredFactory.getDeferred();

        final Auth auth = request.getAuth();
        auth.setDispatcher(this);
        auth.auth(new PreparedRequestImpl(this, request, deferred, containerType, resultType));

        return deferred.getPromise();
    }

    /**
     * Sends the request with the respective callback.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param callback      The callback to be executed when done
     * @param <T>           The expected type in response payload
     */
    @SuppressWarnings("unchecked")
    public <T> void dispatch(SerializedRequest request, Class<T> resultType, Callback<T, Throwable> callback) {
        final Deferred<T> deferred = new CallbackDeferred<T>(callback);

        final Auth auth = request.getAuth();
        auth.setDispatcher(this);
        auth.auth(new PreparedRequestImpl(this, request, deferred, resultType, null));
    }

    /**
     * Sends the request and return an instance of {@link Promise} expecting a collection result.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param containerType The class instance of the container type which will hold the values
     * @param callback      The callback to be executed when done
     * @param <T>           The expected type in response payload
     * @param <C>           The collection type to hold the values
     */
    @SuppressWarnings("unchecked")
    public <T, C extends Collection> void dispatch(SerializedRequest request, Class<T> resultType,
                                                   Class<C> containerType,
                                                   Callback<Collection<T>, Throwable> callback) {
        final Deferred<Collection<T>> deferred = new CallbackDeferred<Collection<T>>(callback);

        final Auth auth = request.getAuth();
        auth.setDispatcher(this);
        auth.auth(new PreparedRequestImpl(this, request, deferred, containerType, resultType));
    }

    /**
     * Evaluates the response and resolves the deferred.
     * This method must be called by implementations after the response is received.
     *
     * @param request           Dispatched request
     * @param deferred          Promise to be resolved
     * @param resolveType       Class of the expected type in the promise
     * @param parametrizedType  Class of the parametrized type if the promise expects a collection
     * @param response          The response received from the request
     * @param <D>               Type of the deferred
     */
    @SuppressWarnings("unchecked")
    protected <D> void evalResponse(Request request, Deferred<D> deferred, Class<D> resolveType,
                                    Class<?> parametrizedType, RawResponse response) {
        if (resolveType == RawResponse.class) {
            deferred.resolve((Response<D>) ResponseImpl.fromRawResponse(response));
            return;
        }

        if (parametrizedType != null) {
            processor.process(request, response, parametrizedType, (Class<Collection>) resolveType,
                    (Deferred<Collection>) deferred);
            return;
        }

        processor.process(request, response, resolveType, deferred);
    }
}
