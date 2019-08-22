package com.drians.footballclub.teams

import com.drians.footballclub.model.Team

interface TeamsView {
    fun showLoading()
    fun hideLoading()
    fun showTeamList(data: List<Team>)
}