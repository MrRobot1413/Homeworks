package ru.mrrobot1413.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.databinding.FragmentDetailsBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.ui.MainActivity
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel
import java.text.SimpleDateFormat
import java.util.*


class DetailsFragment : Fragment() {

    private lateinit var inviteText: String
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private var binding: FragmentDetailsBinding? = null
    private var isAddedToFavorite = false
    private lateinit var calendar: Calendar
    private var isLiked = false
    private var isToNotify = false
    private var reminder = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding?.root!!
    }

    override fun onResume() {
        super.onResume()
        (activity as MovieClickListener).hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObservers()

        arguments?.getInt(MainActivity.MOVIE)?.let {
            moviesViewModel.getMovieDetails(it)
        }

        (activity as MainActivity?)!!.setSupportActionBar(binding?.toolbar)

        binding?.toolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            moviesViewModel.movieDetailed.collectLatest { movie ->
                setImage(binding?.imageBackdrop!!, ("https://image.tmdb.org/t/p/w342" + movie?.posterPath))

                binding?.collapsingToolbarLayout?.title = movie?.title
                binding?.txtRating?.text = movie?.rating.toString()
                binding?.txtTime?.text = movie?.time.toString()
                binding?.txtDate?.text = movie?.releaseDate
                binding?.txtLanguage?.text = movie?.language
                binding?.txtDescr?.text = movie?.overview

                movie?.id?.let { setOnPlayTrailerBtnClickListener(it) }

                setOnFabClickListener(movie)

                favoriteListViewModel.selectById(movie?.id!!)

                favoriteListViewModel.movieDetailed.collectLatest { result ->
                    if (result != null) {
                        isAddedToFavorite =
                            if (result.liked) {
                                setIconLiked()
                                true
                            } else {
                                setIconUnliked()
                                false
                            }
                        isLiked = result.liked
                        isToNotify = result.isToNotify
                        reminder = result.reminder
                    } else {
                        setIconUnliked()
                    }
                }

                inviteText = getString(R.string.invite_text) + " " + movie?.title
            }

            moviesViewModel.videoKey.collectLatest {
                if (it.isNotEmpty()) {
                    val intentApp = Intent(Intent.ACTION_VIEW,
                        Uri.parse(it))
                    val intentBrowser = Intent(Intent.ACTION_VIEW,
                        Uri.parse(it))
                    try {
                        activity?.startActivity(intentApp)
                    } catch (ex: ActivityNotFoundException) {
                        activity?.startActivity(intentBrowser)
                    }
                }
            }
        }
    }

    private fun setOnFabClickListener(movie: MovieNetwork?) {
        binding?.fabAddToFavorite?.setOnClickListener {
            if (movie != null) {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    val movieToDelete =
                        Movie(
                            movie.id,
                            movie.title,
                            movie.overview,
                            movie.posterPath,
                            movie.rating,
                            movie.releaseDate,
                            movie.time,
                            movie.language,
                            false,
                            isToNotify,
                            reminder
                        )
                    favoriteListViewModel.delete(movieToDelete)

                    setIconUnliked()
                } else {
                    isAddedToFavorite = true
                    val movieToInsert =
                        Movie(
                            movie.id,
                            movie.title,
                            movie.overview,
                            movie.posterPath,
                            movie.rating,
                            movie.releaseDate,
                            movie.time,
                            movie.language,
                            true,
                            isToNotify,
                            reminder
                        )
                    movieToInsert.time = moviesViewModel.movieDetailed.value?.time!!
                    favoriteListViewModel.insert(movieToInsert)

                    setIconLiked()
                }
            }
        }
    }

    private fun setImage(view: ImageView, url: String) {
        context?.let {
            Glide.with(it)
                .load(url)
                .transform(CenterCrop())
                .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_error_24))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding?.progressBar?.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding?.progressBar?.visibility = View.GONE
                        return false
                    }

                })
                .into(view)
        }
    }

    private fun setOnPlayTrailerBtnClickListener(id: Int) {
        binding?.btnPlayTrailer?.setOnClickListener {
            moviesViewModel.getVideos(id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val view = activity?.currentFocus
        if (view != null) {
            val im: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding = null
    }

    private fun setIconLiked() {
        binding?.fabAddToFavorite?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite
            )
        )
    }

    private fun setIconUnliked() {
        binding?.fabAddToFavorite?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite_border
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        if (arguments?.getInt("source") == 3) {
            binding?.toolbar?.inflateMenu(R.menu.watch_later_menu)
        } else {
            binding?.toolbar?.inflateMenu(R.menu.invite_menu)
        }
    }

    private fun sendInviteToWatch() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding?.collapsingToolbarLayout?.title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, inviteText)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun scheduleNotification() {
        calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        val datePickerDialog = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(getString(R.string.select_date))
            .build()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(getString(R.string.select_time))
            .build()

        activity?.supportFragmentManager?.let { fm ->
            datePickerDialog.addOnPositiveButtonClickListener {
                timePicker.show(fm, "timePicker")
            }
            timePicker.addOnPositiveButtonClickListener {
                calendar.timeInMillis = datePickerDialog.selection!!
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)
                calendar.set(Calendar.SECOND, 0)

                val format = SimpleDateFormat("HH:mm dd MMM, yyyy")
                val formatted = format.format(calendar.time)

                moviesViewModel.movieDetailed.value?.let { movie ->
                    moviesViewModel.scheduleNotification(Movie(
                        movie.id,
                        movie.title,
                        movie.overview,
                        movie.posterPath,
                        movie.rating,
                        movie.releaseDate,
                        movie.time,
                        movie.language,
                        isLiked,
                        isToNotify,
                        reminder
                    ),
                        calendar,
                        requireContext(),
                        moviesViewModel.movieDetailed.value!!.id,
                        formatted
                    )
                }
            }
            datePickerDialog.show(fm, "datePicker")
        }
    }

    private fun unscheduleNotification() {
        moviesViewModel.unscheduleNotification(requireContext(), Movie(
            moviesViewModel.movieDetailed.value?.id!!,
            moviesViewModel.movieDetailed.value?.title!!,
            moviesViewModel.movieDetailed.value?.overview!!,
            moviesViewModel.movieDetailed.value?.posterPath,
            moviesViewModel.movieDetailed.value?.rating!!,
            moviesViewModel.movieDetailed.value?.releaseDate!!,
            moviesViewModel.movieDetailed.value?.time!!,
            moviesViewModel.movieDetailed.value?.language!!,
            isLiked,
            isToNotify,
            reminder
        ))
        activity?.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.invite_friend -> sendInviteToWatch()

            R.id.watch_later -> scheduleNotification()

            R.id.watch_later_notif_off -> unscheduleNotification()
        }
        return super.onOptionsItemSelected(item)
    }
}