package com.drians.footballclub.detail

import com.drians.footballclub.api.ApiRepository
import com.drians.footballclub.api.TheSportDBApi
import com.drians.footballclub.model.TeamResponse
import com.drians.footballclub.util.CoroutineContextProvider
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TeamDetailPresenter(private val view: TeamDetailView,
                          private val apiRepository: ApiRepository,
                          private val gson: Gson,
                          private val context: CoroutineContextProvider = CoroutineContextProvider()) {

    fun getTeamDetail(teamId: String) {
        view.showLoading()

        GlobalScope.launch(context.main){
            val data = gson.fromJson(apiRepository
                .doRequestAsync(TheSportDBApi.getTeamDetail(teamId)).await(),
                TeamResponse::class.java)

            view.showTeamDetail(data.teams)
            view.hideLoading()
        }

    }
}