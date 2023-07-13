package com.example.noaa.favourite.viewmodel


import app.cash.turbine.test
import com.data.source.FakeRepo
import com.example.noaa.model.MainDispatcherRule
import com.example.noaa.model.Current
import com.example.noaa.model.Place
import com.example.noaa.model.RepoInterface
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavouriteViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val place1: Place = Place(0, "giza", 31.2125, 30.2121)
    private val place2: Place = Place(1, "cairo", 30.2125, 29.2121)
    private val place3: Place = Place(2, "giza", 32.2125, 28.2121)

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

    private lateinit var repo: RepoInterface
    private lateinit var favouriteViewModel: FavouriteViewModel


    @Before
    fun setUp() {

        repo = FakeRepo(weather = weatherResponse, stringReadValue = "")
        favouriteViewModel = FavouriteViewModel(repo)
    }



    @Test
    fun insertPlaceToFavTest() = runBlocking{
        //when
        favouriteViewModel.insertPlaceToFav(place1)
        favouriteViewModel.getAllFavouritePlaces()
        var result: List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        assertTrue(result.contains(place1))
    }



    @Test
    fun deletePlaceFromFavTest() = runBlocking{
        //when
        favouriteViewModel.insertPlaceToFav(place1)
        favouriteViewModel.deletePlaceFromFav(place1)
        favouriteViewModel.getAllFavouritePlaces()
        var result : List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        assertFalse(result.contains(place1))
    }

    @Test
    fun getAllPlacesTest()= runBlocking{
        //when
        favouriteViewModel.insertPlaceToFav(place1)
        favouriteViewModel.insertPlaceToFav(place2)
        favouriteViewModel.insertPlaceToFav(place3)
        favouriteViewModel.getAllFavouritePlaces()

        var result: List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        assertEquals(listOf(place1, place2, place3), result)
    }
    

}