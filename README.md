# RxJavaRetrofitAdapter
RxJava 3 adapter for Retrofit 2

Port of Retrofit/RxJava2 adapter: https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2

<a href='https://github.com/akarnokd/RxJavaRetrofitAdapter/actions?query=workflow%3A%22Java+CI+with+Gradle%22'><img src='https://github.com/akarnokd/RxJavaRetrofitAdapter/workflows/Java%20CI%20with%20Gradle/badge.svg'></a>
[![codecov.io](http://codecov.io/github/akarnokd/RxJavaRetrofitAdapter/coverage.svg?branch=master)](http://codecov.io/github/akarnokd/RxJavaRetrofitAdapter?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava3-retrofit-adapter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava3-retrofit-adapter)

Retrofit 2: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.squareup.retrofit2/retrofit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.squareup.retrofit2/retrofit)
 
RxJava 3: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava3/rxjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava3/rxjava)


```groovy

dependencies {
    implementation "com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0"
}
```

### Usage

```java
import retrofit2.*;
import hu.akarnokd.rxjava3.retrofit.*;
import io.reactivex.rxjava3.core.*;

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://example.com/")
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .build();

interface MyService {
  @GET("/user")
  Observable<User> getUser();
}

```
