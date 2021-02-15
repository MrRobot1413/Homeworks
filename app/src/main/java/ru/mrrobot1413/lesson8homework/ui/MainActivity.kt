package ru.mrrobot1413.lesson8homework.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.interfaces.FragmentsClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.ui.fragments.DetailsFragment
import ru.mrrobot1413.lesson8homework.ui.fragments.FavoriteListFragment
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class MainActivity : AppCompatActivity(), FragmentsClickListener {

    private lateinit var recyclerView: RecyclerView
    private val moviesList = DataStorage.moviesList
    private lateinit var adapter: MoviesAdapter
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false
    private lateinit var moviesViewModel: MoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initRecycler()
        initBottomNav()
    }

    private fun initViewModel(){
        moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
        moviesViewModel.init()

        moviesViewModel.getMovies().observe(this, Observer {

        })
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = moviesViewModel.getMovies().value?.let {
            MoviesAdapter(it) { movie ->
                openDetailsActivity(movie)
            }
        }!!
        recyclerView.adapter = adapter

    }

    private fun initBottomNav() {
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    changeFocusOnBottomNavToMainActivity()

                    openMainActivity()

                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.page_2 -> {
                    if (!isAddedFragment) {
                        openFavoriteActivity()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFocusOnBottomNavToMainActivity(){
        val menuItem: MenuItem = bottomNav.menu.findItem(R.id.page_1)
        menuItem.isChecked = true
    }

    private fun openMainActivity() {
        isAddedFragment = false

        supportFragmentManager
            .popBackStack()
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true

        replaceFragment(
            DetailsFragment.newInstance(movie),
            R.id.relative
        )
    }

    private fun openFavoriteActivity() {
        isAddedFragment = true

        replaceFragment(
            FavoriteListFragment.newInstance(),
            R.id.container
        )
    }

    private fun replaceFragment(
        fragment: Fragment,
        container: Int
    ) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(container, fragment, fragment.tag)
            .commit()
    }

    override fun onClick(movie: Movie) {
        openDetailsActivity(movie)
    }

    override fun onBackPressed() {
        if (isAddedFragment) {
            isAddedFragment = false

            supportFragmentManager
                .popBackStack()

            changeFocusOnBottomNavToMainActivity()
        } else {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
        val builder = MaterialAlertDialogBuilder(this)

        builder.setTitle(R.string.exit_text)
            .setPositiveButton(getString(R.string.confirm_exit)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.cancel_exit)) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }
}

