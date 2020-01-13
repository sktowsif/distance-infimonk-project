package com.project.distance.di

import com.project.distance.BuildConfig
import com.project.distance.data.LocationAPI
import com.project.distance.data.MapDataSource
import com.project.distance.data.MapRepository
import com.project.distance.data.MapViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {
    single { createWebService<LocationAPI>(BuildConfig.BASE_API_URL) }
}

val repositoryModule = module {
    single<MapDataSource> { MapRepository(get()) }
}

val viewModelModule = module {
    viewModel { MapViewModel(get(), get()) }
}

inline fun <reified T> createWebService(url: String, gsonEnable: Boolean = true): T {
    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)

    if (gsonEnable) retrofit.addConverterFactory(GsonConverterFactory.create())
    return retrofit.build().create(T::class.java)
}