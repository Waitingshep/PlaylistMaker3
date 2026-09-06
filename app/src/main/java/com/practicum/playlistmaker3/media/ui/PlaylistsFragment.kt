package com.practicum.playlistmaker3.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.playlist.ui.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyImage: ImageView
    private lateinit var emptyText: TextView
    private lateinit var createButton: Button
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.playlistsRecyclerView)
        emptyImage = view.findViewById(R.id.emptyPlaylistImage)
        emptyText = view.findViewById(R.id.emptyPlaylistText)
        createButton = view.findViewById(R.id.createPlaylistButton)

        setupRecyclerView()
        observeViewModel()

        createButton.setOnClickListener {
            (parentFragment as? MediaFragment)?.navigateToCreatePlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.layoutManager = layoutManager

        adapter = PlaylistAdapter(emptyList()) { playlist ->
            // TODO: переход на экран плейлиста
        }
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Empty -> {
                    recyclerView.visibility = View.GONE
                    emptyImage.visibility = View.VISIBLE
                    emptyText.visibility = View.VISIBLE
                    createButton.visibility = View.VISIBLE
                }
                is PlaylistsState.Content -> {
                    recyclerView.visibility = View.VISIBLE
                    emptyImage.visibility = View.GONE
                    emptyText.visibility = View.GONE
                    createButton.visibility = View.VISIBLE
                    adapter.updatePlaylists(state.playlists)
                }
            }
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}