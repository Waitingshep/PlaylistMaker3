package com.practicum.playlistmaker3.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker3.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emptyImage = view.findViewById<ImageView>(R.id.emptyFavoritesImage)
        val emptyText = view.findViewById<TextView>(R.id.emptyFavoritesText)

        // Показываем заглушку
        emptyImage.visibility = View.VISIBLE
        emptyText.visibility = View.VISIBLE
    }
}