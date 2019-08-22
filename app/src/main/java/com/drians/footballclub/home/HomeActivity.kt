package com.drians.footballclub.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import com.drians.footballclub.R
import com.drians.footballclub.R.id.*
import com.drians.footballclub.teams.FavoriteTeamsFragment
import com.drians.footballclub.teams.TeamsFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.design.bottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var mBottomNavigationView: BottomNavigationView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)

        relativeLayout {
            frameLayout {
                id = main_container
            }.lparams(matchParent, matchParent) {
                above(bottom_layout)
            }

            linearLayout {
                id = bottom_layout
                orientation = LinearLayout.VERTICAL

                view {
                    backgroundResource = R.drawable.shadow
                }.lparams(matchParent, dip(4))

                mBottomNavigationView = bottomNavigationView {
                    id = bottom_navigation
                    itemBackgroundResource = android.R.color.white
                    itemIconTintList = resources.getColorStateList(R.drawable.nav_item_color_state)
                    itemTextColor = resources.getColorStateList(R.drawable.nav_item_color_state)
                    inflateMenu(R.menu.bottom_navigation_menu)
                }.lparams(matchParent, wrapContent)
            }.lparams(matchParent, wrapContent) {
                alignParentBottom()
            }
        }

        mBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                teams -> {
                    loadTeamsFragment(savedInstanceState)
                }
                favorites -> {
                    loadFavoritesFragment(savedInstanceState)
                }
            }
            true
        }
        mBottomNavigationView.selectedItemId = teams
    }

    private fun loadTeamsFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(main_container, TeamsFragment(), TeamsFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadFavoritesFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(main_container, FavoriteTeamsFragment(), FavoriteTeamsFragment::class.java.simpleName)
                .commit()
        }
    }
}