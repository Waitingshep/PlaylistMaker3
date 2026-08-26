package com.practicum.playlistmaker3.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.search.ui.TrackAdapter
import com.practicum.playlistmaker3.search.ui.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyImage: ImageView
    private lateinit var emptyText: TextView
    private lateinit var trackAdapter: TrackAdapter

    private var clickDebounceJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        emptyImage = view.findViewById(R.id.emptyFavoritesImage)
        emptyText = view.findViewById(R.id.emptyFavoritesText)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { trackUi ->
            openPlayerFragmentWithDebounce(trackUi)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = trackAdapter
    }

    private fun openPlayerFragmentWithDebounce(trackUi: TrackUi) {
        clickDebounceJob?.cancel()
        clickDebounceJob = lifecycleScope.launch {
            delay(300L)

            val bundle = Bundle().apply {
                putParcelable("track", trackUi)
            }

            parentFragment?.findNavController()?.navigate(
                R.id.action_mediaFragment_to_playerFragment,
                bundle
            ) ?: run {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_mediaFragment_to_playerFragment, bundle)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> {
                    recyclerView.visibility = View.GONE
                    emptyImage.visibility = View.VISIBLE
                    emptyText.visibility = View.VISIBLE
                }
                is FavoritesState.Content -> {
                    recyclerView.visibility = View.VISIBLE
                    emptyImage.visibility = View.GONE
                    emptyText.visibility = View.GONE
                    trackAdapter.updateTracks(state.tracks)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clickDebounceJob?.cancel()
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}