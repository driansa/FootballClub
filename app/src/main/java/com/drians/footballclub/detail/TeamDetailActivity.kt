package com.drians.footballclub.detail

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.*
import com.drians.footballclub.R
import com.drians.footballclub.api.ApiRepository
import com.drians.footballclub.database.database
import com.drians.footballclub.model.Favorite
import com.drians.footballclub.model.Team
import com.drians.footballclub.util.invisible
import com.drians.footballclub.util.visible
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class TeamDetailActivity : AppCompatActivity(), TeamDetailView {

    private lateinit var presenter: TeamDetailPresenter
    private lateinit var teams: Team
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private var menuItem: Menu? = null
    private var isFavorite: Boolean = false
    private lateinit var id: String

    private lateinit var teamBadge: ImageView
    private lateinit var teamName: TextView
    private lateinit var teamFormedYear: TextView
    private lateinit var teamStadium: TextView
    private lateinit var teamDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Team Detail Activity UI
        linearLayout {
            lparams(width = matchParent, height = wrapContent)
            orientation = LinearLayout.VERTICAL
            backgroundColor = Color.WHITE

            swipeRefresh = swipeRefreshLayout {
                setColorSchemeResources(
                    R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
                )

                scrollView {
                    isVerticalScrollBarEnabled = false

                    relativeLayout {
                        lparams(width = matchParent, height = wrapContent)

                        linearLayout {
                            lparams(matchParent, wrapContent)
                            padding = dip(10)
                            orientation = LinearLayout.VERTICAL
                            gravity = Gravity.CENTER_HORIZONTAL

                            teamBadge = imageView {}.lparams(dip(96), dip(96)){
                                topMargin = dip(18)
                                bottomMargin = dip(18)
                            }

                            teamName = textView {
                                this.gravity = Gravity.CENTER
                                textSize = 20f
                                textColorResource = R.color.colorAccent
                            }.lparams { topMargin = dip(5) }

                            teamFormedYear = textView { this.gravity = Gravity.CENTER }

                            teamStadium = textView {
                                this.gravity = Gravity.CENTER
                                textColorResource = R.color.colorPrimary
                            }

                            teamDescription = textView { }.lparams { topMargin = dip(24) }
                        }
                        progressBar = progressBar {}.lparams { centerHorizontally() }
                    }
                }
            }
        }

        supportActionBar?.title = "Team Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getStringExtra("id")

        favoriteState()
        presenter = TeamDetailPresenter(this, ApiRepository(), Gson())
        presenter.getTeamDetail(id)
        swipeRefresh.onRefresh { presenter.getTeamDetail(id) }
    }

    // fungsi bantuan untuk mengecek status apakah sebuah tim sudah masuk ke dalam database atau belum
    private fun favoriteState(){
        database.use {
            val result = select(Favorite.TABLE_FAVORITE)
                .whereArgs("(TEAM_ID = {id})", "id" to id)
            val favorite = result.parseList(classParser<Favorite>())
            if (favorite.isNotEmpty()) isFavorite = true
        }
    }

    override fun showLoading() {
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.invisible()
    }

    override fun showTeamDetail(data: List<Team>) {
        teams = Team(
            data[0].teamId,
            data[0].teamName, data[0].teamBadge
        )
        swipeRefresh.isRefreshing = false

        Picasso.get().load(data[0].teamBadge).into(teamBadge)
        teamName.text = data[0].teamName
        teamFormedYear.text = data[0].teamFormedYear
        teamStadium.text = data[0].teamStadium
        teamDescription.text = data[0].teamDescription
    }

    // fungsi untuk memasukkan data ke dalam database
    private fun addToFavorite(){
        try {
            database.use {
                insert(
                    Favorite.TABLE_FAVORITE,
                    Favorite.TEAM_ID to teams.teamId,
                    Favorite.TEAM_NAME to teams.teamName,
                    Favorite.TEAM_BADGE to teams.teamBadge)
            }
            swipeRefresh.snackbar("Added to favorite").show()
        } catch (e: SQLiteConstraintException){
            swipeRefresh.snackbar(e.localizedMessage).show()
        }
    }

    // fungsi menghapus data dari database favorite
    private fun removeFromFavorite(){
        try {
            database.use {
                delete(Favorite.TABLE_FAVORITE, "(TEAM_ID = {id})",
                    "id" to id)
            }
            swipeRefresh.snackbar("Removed to favorite").show()
        } catch (e: SQLiteConstraintException){
            swipeRefresh.snackbar(e.localizedMessage).show()
        }
    }

    // fungsi untuk mengatur ikon pada tombol Favorite
    private fun setFavorite() {
        if (isFavorite)
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites)
        else
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.add_to_favorite -> {
                if (isFavorite) removeFromFavorite() else addToFavorite()

                isFavorite = !isFavorite
                setFavorite()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}