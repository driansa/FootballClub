package com.drians.footballclub.detail

import com.drians.footballclub.TestContextProvider
import com.drians.footballclub.api.ApiRepository
import com.drians.footballclub.model.Team
import com.drians.footballclub.model.TeamResponse
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class TeamDetailPresenterTest {

    @Mock
    private lateinit var view: TeamDetailView

    @Mock
    private lateinit var gson: Gson

    @Mock
    private lateinit var apiRepository: ApiRepository

    @Mock
    private lateinit var apiResponse: Deferred<String>

    private lateinit var presenter: TeamDetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = TeamDetailPresenter(view, apiRepository, gson, TestContextProvider())
    }

    @Test
    fun getTeamDetail() {
        val teams: MutableList<Team> = mutableListOf()
        val response = TeamResponse(teams)
        val id = "1234"

        runBlocking {
            Mockito.`when`(apiRepository.doRequestAsync(ArgumentMatchers.anyString())).thenReturn(apiResponse)
            Mockito.`when`(apiResponse.await()).thenReturn("")
            Mockito.`when`(gson.fromJson("", TeamResponse::class.java)).thenReturn(response)

            presenter.getTeamDetail(id)

            Mockito.verify(view).showLoading()
            Mockito.verify(view).showTeamDetail(teams)
            Mockito.verify(view).hideLoading()
        }
    }
}