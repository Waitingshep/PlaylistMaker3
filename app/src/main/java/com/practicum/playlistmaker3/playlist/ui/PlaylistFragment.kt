package com.practicum.playlistmaker3.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.search.ui.TrackAdapter
import com.practicum.playlistmaker3.search.ui.TrackMapper
import com.practicum.playlistmaker3.search.ui.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var backButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var playlistNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var trackCountTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var infoLayout: View
    private lateinit var shareButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var buttonsLayout: View
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyTracksLayout: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetContainer: View
    private lateinit var overlay: View
    private lateinit var menuOverlay: View
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetContainer: View

    private lateinit var trackAdapter: TrackAdapter
    private var currentTracks: List<TrackUi> = emptyList()
    private var currentCoverPath: String? = null
    private var isPeekHeightSet = false
    private var isMenuHeightPrepared = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backButton)
        coverImageView = view.findViewById(R.id.coverImageView)
        playlistNameTextView = view.findViewById(R.id.playlistNameTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        trackCountTextView = view.findViewById(R.id.trackCountTextView)
        durationTextView = view.findViewById(R.id.durationTextView)
        infoLayout = view.findViewById(R.id.infoLayout)
        shareButton = view.findViewById(R.id.shareButton)
        menuButton = view.findViewById(R.id.menuButton)
        buttonsLayout = view.findViewById(R.id.buttonsLayout)
        tracksRecyclerView = view.findViewById(R.id.tracksRecyclerView)
        emptyTracksLayout = view.findViewById(R.id.emptyTracksLayout)
        bottomSheetContainer = view.findViewById(R.id.bottomSheetContainer)
        overlay = view.findViewById(R.id.overlay)
        menuOverlay = view.findViewById(R.id.menuOverlay)
        menuBottomSheetContainer = view.findViewById(R.id.menuBottomSheetContainer)

        overlay.visibility = View.GONE
        overlay.alpha = 0f
        menuOverlay.visibility = View.GONE
        menuOverlay.alpha = 0f

        setupBottomSheet()
        setupMenuBottomSheet()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        val playlistId = arguments?.getLong("playlistId") ?: 0
        if (playlistId > 0) {
            viewModel.loadPlaylist(playlistId)
        }

        view?.post {
            prepareMenuHeight()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            isDraggable = true
            peekHeight = 200
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isAdded) return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                        overlay.alpha = 0f
                        isPeekHeightSet = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 1f
                        if (!isPeekHeightSet) {
                            view?.post {
                                calculateAndSetPeekHeight()
                                isPeekHeightSet = true
                            }
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 1f
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isAdded) return

                val normalized = (slideOffset + 1) / 2
                overlay.alpha = normalized.coerceIn(0f, 1f)
                if (normalized > 0.01f) {
                    overlay.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun calculateAndSetPeekHeight() {
        if (!isAdded) return

        val shareLocation = IntArray(2)
        shareButton.getLocationOnScreen(shareLocation)
        val shareBottomY = shareLocation[1] + shareButton.height

        val screenHeight = resources.displayMetrics.heightPixels
        val spaceBelowShare = screenHeight - shareBottomY

        var targetHeight = spaceBelowShare - dpToPx(24)
        val internalPadding = dpToPx(36)
        targetHeight = targetHeight + internalPadding

        val finalHeight = targetHeight.coerceAtLeast(dpToPx(150))
        bottomSheetBehavior.peekHeight = finalHeight
    }

    private fun prepareMenuHeight() {
        if (!isAdded) return

        val titleLocation = IntArray(2)
        playlistNameTextView.getLocationOnScreen(titleLocation)
        val titleBottomY = titleLocation[1] + playlistNameTextView.height

        val screenHeight = resources.displayMetrics.heightPixels
        val spaceBelowTitle = screenHeight - titleBottomY
        val menuTopPadding = dpToPx(4 + 12 + 8)
        val targetHeight = spaceBelowTitle + menuTopPadding
        val finalHeight = targetHeight.coerceAtLeast(dpToPx(200))

        val params = menuBottomSheetContainer.layoutParams
        params.height = finalHeight
        menuBottomSheetContainer.layoutParams = params
        menuBottomSheetContainer.requestLayout()

        menuBottomSheetBehavior.peekHeight = finalHeight
        isMenuHeightPrepared = true
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            isDraggable = true
            peekHeight = 400
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isAdded) return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        menuOverlay.visibility = View.GONE
                        menuOverlay.alpha = 0f
                        isMenuHeightPrepared = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED -> {
                        menuOverlay.visibility = View.VISIBLE
                        menuOverlay.alpha = 1f
                        if (!isMenuHeightPrepared) {
                            view?.post {
                                prepareMenuHeight()
                            }
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isAdded) return

                val normalized = (slideOffset + 1) / 2
                menuOverlay.alpha = normalized.coerceIn(0f, 1f)
                if (normalized > 0.01f) {
                    menuOverlay.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { trackUi ->
            val bundle = Bundle().apply {
                putParcelable("track", trackUi)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, bundle)
        }

        trackAdapter.setOnLongClickListener { trackUi ->
            showDeleteConfirmationDialog(trackUi)
        }

        tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tracksRecyclerView.adapter = trackAdapter
    }

    private fun showDeleteConfirmationDialog(trackUi: TrackUi) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrackFromPlaylist(trackUi.trackId)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        shareButton.setOnClickListener {
            viewModel.onShareClick()
        }

        menuButton.setOnClickListener {
            if (menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                if (!isMenuHeightPrepared) {
                    prepareMenuHeight()
                }
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        val menuShareItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuShareItem)
        val menuEditItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuEditItem)
        val menuDeleteItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuDeleteItem)

        menuShareItem.setOnClickListener {
            viewModel.onShareClick()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuEditItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val playlistId = arguments?.getLong("playlistId") ?: 0
            val trackIds = currentTracks.map { it.trackId }.toLongArray()
            val trackCount = currentTracks.size

            val bundle = Bundle().apply {
                putLong("playlistId", playlistId)
                putString("playlistName", playlistNameTextView.text.toString())
                putString("playlistDescription", descriptionTextView.text.toString())
                putString("playlistCoverPath", currentCoverPath)
                putLongArray("trackIds", trackIds)
                putInt("trackCount", trackCount)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, bundle)
        }

        menuDeleteItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.deletePlaylist()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Loading -> {}
                is PlaylistState.Content -> {
                    displayPlaylist(state)
                    updateMenuInfo(state)
                    currentCoverPath = state.playlist.coverPath
                }
                is PlaylistState.Error -> {}
            }
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is PlaylistEvent.SharePlaylist -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.text)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)))
                }
                is PlaylistEvent.ShowEmptyShareToast -> {
                    Toast.makeText(requireContext(), getString(R.string.no_tracks_to_share), Toast.LENGTH_SHORT).show()
                }
                is PlaylistEvent.ShowDeleteConfirmation -> {
                    showDeletePlaylistDialog()
                }
                is PlaylistEvent.PlaylistDeleted -> {
                    findNavController().popBackStack()
                }
                is PlaylistEvent.NavigateBack -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateMenuInfo(state: PlaylistState.Content) {
        val menuPlaylistName = menuBottomSheetContainer.findViewById<TextView>(R.id.menuPlaylistName)
        val menuTrackCount = menuBottomSheetContainer.findViewById<TextView>(R.id.menuTrackCount)
        val menuCoverImageView = menuBottomSheetContainer.findViewById<ImageView>(R.id.menuCoverImageView)

        menuPlaylistName.text = state.playlist.name
        val trackCount = state.tracks.size
        menuTrackCount.text = resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)

        val coverPath = state.playlist.coverPath
        if (!coverPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(coverPath)
                .placeholder(R.drawable.placeholder_cover_312)
                .error(R.drawable.placeholder_cover_312)
                .into(menuCoverImageView)
        } else {
            Glide.with(this)
                .load(R.drawable.placeholder_cover_312)
                .into(menuCoverImageView)
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_message))
            .setPositiveButton(getString(R.string.delete_playlist_yes)) { _, _ ->
                viewModel.confirmDeletePlaylist()
            }
            .setNegativeButton(getString(R.string.delete_playlist_no)) { dialog, _ ->
                dialog.dismiss()
                viewModel.cancelDeletePlaylist()
            }
            .show()
    }

    private fun displayPlaylist(state: PlaylistState.Content) {
        val playlist = state.playlist

        playlistNameTextView.text = playlist.name

        if (!playlist.description.isNullOrEmpty()) {
            descriptionTextView.text = playlist.description
            descriptionTextView.isVisible = true
        } else {
            descriptionTextView.isVisible = false
        }

        val durationSum = state.totalDuration
        val durationFormat = SimpleDateFormat("mm", Locale.getDefault())
        val minutes = durationFormat.format(durationSum).toIntOrNull() ?: 0
        val durationText = if (minutes > 0) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (hours > 0) {
                if (remainingMinutes > 0) {
                    "$hours ч $remainingMinutes мин"
                } else {
                    "$hours ч"
                }
            } else {
                "$minutes мин"
            }
        } else {
            "0 мин"
        }
        durationTextView.text = durationText

        val trackCount = state.tracks.size
        trackCountTextView.text = resources.getQuantityString(
            R.plurals.tracks_count,
            trackCount,
            trackCount
        )

        val coverPath = playlist.coverPath
        if (!coverPath.isNullOrEmpty()) {
            currentCoverPath = coverPath
            Glide.with(this)
                .load(coverPath)
                .placeholder(R.drawable.placeholder_cover_312)
                .error(R.drawable.placeholder_cover_312)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                .into(coverImageView)
        } else {
            currentCoverPath = null
            Glide.with(this)
                .load(R.drawable.placeholder_cover_312)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                .into(coverImageView)
        }

        val trackUis = state.tracks.map { TrackMapper.mapToUi(it) }
        currentTracks = trackUis
        trackAdapter.updateTracks(trackUis)

        if (state.tracks.isEmpty()) {
            emptyTracksLayout.isVisible = true
            tracksRecyclerView.isVisible = false
        } else {
            emptyTracksLayout.isVisible = false
            tracksRecyclerView.isVisible = true
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        overlay.visibility = View.VISIBLE
        overlay.alpha = 1f

        view?.post {
            calculateAndSetPeekHeight()
            isPeekHeightSet = true
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    companion object {
        fun newInstance(playlistId: Long): PlaylistFragment {
            val fragment = PlaylistFragment()
            val args = Bundle()
            args.putLong("playlistId", playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}