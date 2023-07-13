package com.example.noaa.details.viewmodel

import app.cash.turbine.test
import com.data.source.FakeRepo
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Current
import com.example.noaa.model.MainDispatcherRule
import com.example.noaa.model.RepoInterface
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.network.ApiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weatherResponse: WeatherResponse = WeatherResponse(
        0,
        Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
        listOf(),
        listOf(),
        0.0,
        0.0,
        "",
        1,
        listOf()
    )

    private val coordinate = Coordinate(0.0, 0.0)

    lateinit var detailsViewModel: DetailsViewModel
    lateinit var repo: RepoInterface

    @Before
    fun setUp() {

        repo = FakeRepo(weather = weatherResponse, stringReadValue = "hassan")
        detailsViewModel = DetailsViewModel(repo)
    }

    @Test
    fun getWeatherDataTest() = runBlocking{
        //when
        detailsViewModel.getWeatherData(coordinate, "en")

        var result: ApiState = ApiState.Loading
        detailsViewModel.weatherResponseStateFlow.test {
            result = this.awaitItem()
        }

        //when
        if(result is ApiState.Success){
            assertEquals(weatherResponse, (result as ApiState.Success).weatherResponse)
        }
    }

    @Test
    fun readStringFromSPTest(){
        val x = detailsViewModel.readStringFromSettingSP("")
        assertEquals("hassan", x)
    }
}