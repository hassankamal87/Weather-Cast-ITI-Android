package com.example.noaa.model

import android.util.Log
import com.data.source.FakeLocalSource
import com.data.source.FakeLocationClient
import com.data.source.FakeRemoteSource
import com.data.source.FakeSettingSharedPreferences
import com.example.noaa.homeactivity.view.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class RepoTest {
    private val place1: Place = Place(0, "giza", 31.2125, 30.2121)
    private val place2: Place = Place(1, "cairo", 30.2125, 29.2121)
    private val place3: Place = Place(2, "giza", 32.2125, 28.2121)

    private val alarm1: AlarmItem = AlarmItem(0, "no", 0.0, 0.0, "aa")
    private val alarm2: AlarmItem = AlarmItem(1, "al", 2.0, 5.0, "ss")
    private val alarm3: AlarmItem = AlarmItem(2, "no", 1.0, 1.0, "ss")

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

    private val coordinate: Coordinate = Coordinate(0.0,0.0)

    private lateinit var fakeLocalSource: FakeLocalSource
    private lateinit var fakeRemoteSource: FakeRemoteSource
    private lateinit var fakeSettingSharedPref: FakeSettingSharedPreferences
    private lateinit var fakeLocationClient: FakeLocationClient
    private lateinit var repo: RepoInterface

    @Before
    fun setUp() {
        fakeLocalSource = FakeLocalSource(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
        fakeRemoteSource = FakeRemoteSource(weatherResponse)
        fakeSettingSharedPref = FakeSettingSharedPreferences("", 0.0f)
        fakeLocationClient = FakeLocationClient(coordinate)

        repo = Repo.getInstance(
            fakeRemoteSource,
            fakeLocationClient,
            fakeLocalSource,
            fakeSettingSharedPref
        )
    }


    @Test
    fun getWeatherDataTest()= runTest{
        //when
        val result = repo.getWeatherResponse(coordinate, "en")

        //then
        result.collectLatest {
            if(it.isSuccessful){
                assertEquals(weatherResponse, it.body())
            }
        }
    }


    @Test
    fun getCurrentLocationTest() = runTest{
        //when
        val result = repo.getCurrentLocation()

        //then
        result.collectLatest {
            assertEquals(coordinate, it)
        }

    }


    @Test
    fun deletePlaceFromFavTest() = runTest{
        //when
        repo.deletePlaceFromFav(place1)

        var result : Boolean? = null
        repo.getAllFavouritePlaces().collectLatest {
            result = it.contains(place1)
        }

        //then
        assertEquals(false, result)

    }


    @Test
    fun insertPlaceToFavTest() = runTest{
        //when
        repo.insertPlaceToFav(place1)
        var result: Boolean? = null
        repo.getAllFavouritePlaces().collectLatest {
            result = it.contains(place1)
        }

        //then
        assertEquals(true, result)
        repo.deletePlaceFromFav(place1)

    }


    @Test
    fun getAllFavPlacesTest() = runTest {
        //when
        repo.insertPlaceToFav(place1)
        repo.insertPlaceToFav(place2)
        repo.insertPlaceToFav(place3)
        val result = repo.getAllFavouritePlaces()

        //then
        result.collectLatest {
            assertEquals(listOf(place1, place2, place3), it)
        }
    }


    @Test
    fun insertCashedDataTest()= runTest{
        //when
        repo.insertCashedData(weatherResponse)
        val result = repo.getCashedData()

        //then
        result?.collectLatest {
            assertEquals(weatherResponse, it)
        }
        repo.deleteCashedData()
    }


    @Test
    fun deleteCashedDataTest()= runTest{
        //when
        repo.deleteCashedData()
        val result = repo.getCashedData()

        //then
        result?.collectLatest {
            assertNull(it)
        }
    }


    @Test
    fun getCashedDataTest() = runTest {
        //when
        val result = repo.getCashedData()

        result?.collectLatest {
            assertEquals(weatherResponse, it)
        }
    }


    @Test
    fun getAllAlarmsTest() = runTest {
        //when
        repo.insertAlarm(alarm1)
        repo.insertAlarm(alarm2)
        repo.insertAlarm(alarm3)
        val result = repo.getAllAlarms()

        //then
        result.collectLatest {
            assertEquals(listOf(alarm1, alarm2, alarm3), it)
        }
    }


    @Test
    fun insertAlarmToDatabaseTest() = runTest{
        //when
        repo.insertAlarm(alarm1)
        var result: Boolean? = null
        repo.getAllAlarms().collectLatest {
            result = it.contains(alarm1)
        }

        //then
        assertEquals(true, result)
        repo.deleteAlarm(alarm1)
    }


    @Test
    fun deleteAlarmFromDatabaseTest() = runTest{
        //when
        repo.deleteAlarm(alarm1)

        var result : Boolean? = null
        repo.getAllAlarms().collectLatest {
            result = it.contains(alarm1)
        }

        //then
        assertEquals(false, result)

    }

    @Test
    fun writeStringToSPTest(){
        repo.writeStringToSettingSP("val1", "hassan")

        val result = repo.readStringFromSettingSP("val1")

        assertEquals("hassan",result)
    }

    @Test
    fun writeFloatToSPTest(){
        repo.writeFloatToSettingSP("val1", 2.2f)

        val result = repo.readFloatFromSettingSP("val2")

        assertEquals(2.2f,result)
    }

}