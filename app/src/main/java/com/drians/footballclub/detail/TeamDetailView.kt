package com.drians.footballclub.detail

import com.drians.footballclub.model.Team

interface TeamDetailView {
    fun showLoading()
    fun hideLoading()
    fun showTeamDetail(data: List<Team>)
}