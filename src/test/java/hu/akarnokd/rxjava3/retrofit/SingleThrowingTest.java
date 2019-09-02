/*
 * Copyright (C) 2015 Square, Inc.
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

import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.*;
import org.junit.rules.TestRule;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.*;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import okhttp3.mockwebserver.*;
import retrofit2.*;
import retrofit2.http.GET;

public final class SingleThrowingTest {
  @Rule public final MockWebServer server = new MockWebServer();
  @Rule public final TestRule resetRule = new RxJavaPluginsResetRule();
  @Rule public final RecordingSingleObserver.Rule subscriberRule = new RecordingSingleObserver.Rule();

  interface Service {
    @GET("/") Single<String> body();
    @GET("/") Single<Response<String>> response();
    @GET("/") Single<Result<String>> result();
  }

  private Service service;

  @Before public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new StringConverterFactory())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build();
    service = retrofit.create(Service.class);
  }

  @Test public void bodyThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<String> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override public void onSuccess(String value) {
        throw e;
      }
    });

    assertThat(throwableRef.get().getCause()).isSameAs(e);
  }

  @Test public void bodyThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse().setResponseCode(404));

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<String> observer = subscriberRule.create();
    final AtomicReference<Throwable> errorRef = new AtomicReference<>();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override public void onError(Throwable throwable) {
        if (!errorRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
        throw e;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(errorRef.get(), e);
  }

  @Test public void responseThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<Response<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override public void onSuccess(Response<String> value) {
        throw e;
      }
    });

    assertThat(throwableRef.get().getCause()).isSameAs(e);
  }

  @Test public void responseThrowingInOnErrorDeliveredToPlugin() {
      MockResponse mockResponse = new MockResponse();
      mockResponse.socketPolicy(DISCONNECT_AFTER_REQUEST);
      server.enqueue(mockResponse);

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<Response<String>> observer = subscriberRule.create();
    final AtomicReference<Throwable> errorRef = new AtomicReference<>();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override public void onError(Throwable throwable) {
        if (!errorRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
        throw e;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(errorRef.get(), e);
  }

  @Test public void resultThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override public void onSuccess(Result<String> value) {
        throw e;
      }
    });

    assertThat(throwableRef.get().getCause()).isSameAs(e);
  }

  @Ignore("Single's contract is onNext|onError so we have no way of triggering this case")
  @Test public void resultThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingSingleObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException first = new RuntimeException();
    final RuntimeException second = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override public void onSuccess(Result<String> value) {
        // The only way to trigger onError for Result is if onSuccess throws.
        throw first;
      }

      @Override public void onError(Throwable throwable) {
        throw second;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(first, second);
  }

  private static abstract class ForwardingObserver<T> implements SingleObserver<T> {
    private final SingleObserver<T> delegate;

    ForwardingObserver(SingleObserver<T> delegate) {
      this.delegate = delegate;
    }

    @Override public void onSubscribe(Disposable disposable) {
      delegate.onSubscribe(disposable);
    }

    @Override public void onSuccess(T value) {
      delegate.onSuccess(value);
    }

    @Override public void onError(Throwable throwable) {
      delegate.onError(throwable);
    }
  }
}
