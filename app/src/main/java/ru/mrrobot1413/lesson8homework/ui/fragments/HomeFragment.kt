package ru.mrrobot1413.lesson8homework.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.oshi.libsearchtoolbar.SearchAnimationToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.databinding.FragmentHomeBinding
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class HomeFragment : Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener {

    private val adapter by lazy {
        MoviesAdapter(mutableListOf()) { movie: Movie, holder: RelativeLayout ->
            (activity as MovieClickListener).openDetailsFragment(movie, holder)
        }
    }

    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private var moviesPage = 1
    lateinit var binding: FragmentHomeBinding
    var parcelable: Parcelable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        moviesViewModel.movies.observe(viewLifecycleOwner, {
            binding.recyclerView.visibility = View.VISIBLE
            adapter.setMovies(it)
            deleteNoConnectionSign()
            moviesViewModel.saveAll(it)
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(viewLifecycleOwner, {
            showSnackbar(it)
            moviesViewModel.selectAll()
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.getPopularMovies(moviesPage)

        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState?.getParcelable<Parcelable>("key") != null) {
            parcelable = savedInstanceState.getParcelable("key")!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("key", linearLayoutManager.onSaveInstanceState())
    }

    override fun onResume() {
        super.onResume()
        (activity as MovieClickListener).showBottomNav()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun showTopRatedMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getTopRatedMovies(1)
    }

    private fun showPopularMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(1)
    }

    private fun showSearchView() {
        toolbar.onSearchIconClick()
    }

    private fun showSnackbar(text: String) {
        view?.let {
            Snackbar.make(it, text, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry)) {
                moviesViewModel.getPopularMovies(
                    moviesPage
                )
            }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent)).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> showSearchView()
            R.id.popular -> showPopularMovies()
            R.id.top_rated -> showTopRatedMovies()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFields() {
        linearLayoutManager =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 3)
            } else {
                GridLayoutManager(requireContext(), 2)
            }

        initRecycler()

        deleteNoConnectionSign()

        binding.toolbar.setOnSearchQueryChangedListener(this)

        binding.refreshLayout.setOnRefreshListener {
            moviesViewModel.getPopularMovies(
                moviesPage
            )
            binding.refreshLayout.isRefreshing = false
        }


        binding.refreshLayout.isEnabled = true
    }

    private fun showNoConnectionSign() {
        binding.txtNoConnection.visibility = View.VISIBLE
        binding.errorIcon.visibility = View.VISIBLE
    }

    private fun deleteNoConnectionSign() {
        binding.txtNoConnection.visibility = View.GONE
        binding.errorIcon.visibility = View.GONE
    }

    private fun attachPopularMoviesOnScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = linearLayoutManager.itemCount
                val visibleItemCount = linearLayoutManager.childCount
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    recyclerView.removeOnScrollListener(this)
                    moviesPage++
                    moviesViewModel.getPopularMovies(moviesPage)
                    attachPopularMoviesOnScrollListener()
                }
            }
        })
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = linearLayoutManager

        binding.recyclerView.adapter = adapter

        attachPopularMoviesOnScrollListener()
    }

    override fun onSearchCollapsed() {

    }

    override fun onSearchQueryChanged(query: String?) {
        if (query != null) {
            if (query.length >= 2) {
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.setMoviesFromMenu(mutableListOf())
                    moviesViewModel.searchMovie(1, query)
                }, 1000)
            }
        }
    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {
        val view = activity?.currentFocus
        if (view != null) {
            val im: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}