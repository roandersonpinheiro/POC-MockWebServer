package com.roanderson.mockwebserver.data

import com.roanderson.mockwebserver.data.services.FilmIMDbApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@RunWith(JUnit4::class)
class FilmIMDbApiServiceTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var service: FilmIMDbApiService

    @Before
    fun createService() {
        val factory = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .build()
            .create(FilmIMDbApiService::class.java)
    }

    @Test
    fun `should return correct endpoint search movie`() {
        runBlocking {
            mockWebServer.enqueue(MockResponse().setBody(MockResponseFileReader("film/search_film_success.json").content))
            service.searchMovie("inception")
            val request = mockWebServer.takeRequest()
            assertEquals(request.path, "/k_tkkbokr8/inception")
        }
    }

    @Test
    fun `should return an error message when entering an empty name`() {
        runBlocking {
            // Assign
            val response = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockResponseFileReader("film/search_film_empty_name_success.json").content)
            mockWebServer.enqueue(response)

            // Act
            val actualResponse = service.searchMovie("")

            // Assert
            println(actualResponse.body()?.errorMessage)
            assertTrue(actualResponse.body()?.errorMessage?.isNotEmpty() == true)
        }
    }

    @Test
    fun `should return errorMessage Invalid API Key`() {
        runBlocking {
            // Assign
            val response = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockResponseFileReader("film/search_film_invalid_key.json").content)
            mockWebServer.enqueue(response)

            // Act
            val actualResponse = service.searchMovie("")

            // Assert
            assertTrue(actualResponse.body()?.errorMessage?.isNotEmpty() == true)
            assertEquals("Invalid API Key", actualResponse.body()?.errorMessage)
        }
    }

    @Test
    fun `should return a movie list after searching by name`(): Unit = runBlocking {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("film/search_film_success.json").content)
        mockWebServer.enqueue(response)

        // Act
        val actualResponse = service.searchMovie("inception")

        // Assert
        assertNotNull(actualResponse.body()?.results?.size)
        assertEquals("", actualResponse.body()?.errorMessage)
    }


    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

}