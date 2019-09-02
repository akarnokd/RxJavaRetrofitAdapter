/*
 * Copyright (C) 2018 Square, Inc.
 *
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
 */
package hu.akarnokd.rxjava3.retrofit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.*;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public final class CancelDisposeTest {
  @Rule public final MockWebServer server = new MockWebServer();

  interface Service {
    @GET("/") Observable<String> go();
  }

  private final OkHttpClient client = new OkHttpClient();
  private Service service;

  @Before public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new StringConverterFactory())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
        .callFactory(client)
        .build();
    service = retrofit.create(Service.class);
  }

  @Test public void disposeCancelsCall() {
    Disposable disposable = service.go().subscribe();
    List<Call> calls = client.dispatcher().runningCalls();
    assertEquals(1, calls.size());
    disposable.dispose();
    assertTrue(calls.get(0).isCanceled());
  }

  @Test public void disposeBeforeEnqueueDoesNotEnqueue() {
    service.go().test(true);
    List<Call> calls = client.dispatcher().runningCalls();
    assertEquals(0, calls.size());
  }

  @Test public void cancelDoesNotDispose() {
    Disposable disposable = service.go().subscribe();
    List<Call> calls = client.dispatcher().runningCalls();
    assertEquals(1, calls.size());
    calls.get(0).cancel();
    assertFalse(disposable.isDisposed());
  }
}

