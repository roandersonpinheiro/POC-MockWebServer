package com.roanderson.mockwebserver.data.di

import android.util.Log
import com.roanderson.mockwebserver.data.NullToEmptyStringAdapter
import com.roanderson.mockwebserver.data.services.GamesApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object DataModule {
    private const val BASE_URL =
        "https://www.freetogame.com/api/"

    private const val BASE_URL2 =
        "https://imdb-api.com/en/API/Search/"
    private const val OK_HTTP = "Ok Http"

    fun load() {
        loadKoinModules(networkModule())
    }


    private fun networkModule(): Module {
        return module {

            single {
                createOkHttpClient()
            }

            single {
                Moshi.Builder().add(NullToEmptyStringAdapter()).add(KotlinJsonAdapterFactory())
                    .build()
            }

            single {
                createService<GamesApiService>(BASE_URL, get(), get())
            }

            single {
                createService<GamesApiService>(BASE_URL2, get(), get())
            }
        }
    }

    private fun createOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor {
            Log.e(OK_HTTP, it)
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private inline fun <reified T> createService(
        url: String,
        client: OkHttpClient,
        factory: Moshi,
    ): T {
        return Retrofit.Builder()
            //.baseUrl(BASE_URL)
            .baseUrl(url)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .build()
            .create(T::class.java)
    }
}