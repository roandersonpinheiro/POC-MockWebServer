package com.roanderson.mockwebserver.data

import com.roanderson.mockwebserver.data.services.GamesApiService
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

@RunWith(JUnit4::class)
class GameApiServiceTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var service: GamesApiService

    @Before
    fun createService() {
        val factory = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .build()
            .create(GamesApiService::class.java)
    }

    @Test
    fun `should return correct endpoint games`() {
        runBlocking {
            mockWebServer.enqueue(MockResponse().setBody("[]"))
            service.getGames()
            val request = mockWebServer.takeRequest()
            assertEquals(request.path, "/games")
        }
    }

    @Test
    fun `should return correct endpoint games by id`() {
        runBlocking {
            mockWebServer.enqueue(MockResponse().setBody("[]"))
            service.getGameById(1000)
            val request = mockWebServer.takeRequest()
            assertEquals(request.path, "/games?id=1000")
        }
    }


    @Test
    fun `should return games list on success`() = runBlocking {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("games/games_success.json").content)
        mockWebServer.enqueue(response)

        // Act
        val actualResponse = service.getGames()

        // Assert
        assertEquals(374, actualResponse.body()?.size)
    }


    @Test
    fun `should return a game when searching for the id in case of success`() = runBlocking {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("games/game_success_by_id.json").content)
        mockWebServer.enqueue(response)

        // Act
        val actualResponse = service.getGameById(1)

        // Assert
        assertEquals(1, actualResponse.body()?.size)
    }


    @Test
    fun ` should return a 404 not found if it doesn't find the game`() = runBlocking {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setBody(MockResponseFileReader("games/game_404_not_found.json").content)
        mockWebServer.enqueue(response)

        val actualResponse = service.getGameById(1)

        assertEquals(
            actualResponse.body(), null
        )
        assertEquals(
            actualResponse.code(), HttpURLConnection.HTTP_NOT_FOUND
        )
        assertEquals(
            actualResponse.errorBody()?.string(),
            MockResponseFileReader("games/game_404_not_found.json").content
        )
    }


    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

}