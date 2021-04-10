package ru.mrrobot1413.lesson8homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.FavoriteAdapter
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.interfaces.FragmentsClickListener

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noMoviesSign: TextView

    companion object {
        //todo переписать под получение данных из ViewModel
        fun newInstance(): FavoriteListFragment {
            val args = Bundle()

            val fragment = FavoriteListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(//todo переписать под конструктор
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields(view)
        initRecycler()
    }

    private fun initFields(view: View) {//todo избыточный метод
        noMoviesSign = view.findViewById(R.id.txt_no_movie)
        noMoviesSign.visibility = View.VISIBLE
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun initRecycler() {//todo избыточный метод
        recyclerView.layoutManager = LinearLayoutManager(context)//todo вынести в разметку
        recyclerView.adapter = FavoriteAdapter(DataStorage.favoriteList, noMoviesSign) {
            (activity as? FragmentsClickListener)?.onClick(it)
        }
    }
}