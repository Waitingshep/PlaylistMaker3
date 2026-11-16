package com.practicum.playlistmaker3.playlist.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.NavController
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
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistId: Long,
    isDark: Boolean,
    viewModel: PlaylistViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val observerRef = remember { arrayOfNulls<Observer<PlaylistState>>(1) }
    val rootViewRef = remember { arrayOfNulls<View>(1) }

    LaunchedEffect(playlistId) {
        if (playlistId > 0) viewModel.loadPlaylist(playlistId)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { e ->
            when (e) {
                is PlaylistEvent.SharePlaylist -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, e.text)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)))
                }
                is PlaylistEvent.ShowEmptyShareToast ->
                    Toast.makeText(context, context.getString(R.string.no_tracks_to_share), Toast.LENGTH_SHORT).show()
                is PlaylistEvent.ShowDeleteConfirmation -> showDeletePlaylistDialog(context, viewModel)
                is PlaylistEvent.PlaylistDeleted -> navController.popBackStack()
                is PlaylistEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    LaunchedEffect(isDark) {
        var attempts = 0
        while (rootViewRef[0] == null && attempts < 10) {
            kotlinx.coroutines.delay(10)
            attempts++
        }
        val view = rootViewRef[0] ?: return@LaunchedEffect
        val sheetColor = if (isDark) 0xFF1A1B22.toInt() else 0xFFFFFFFF.toInt()
        val bottomSheetContainer = view.findViewById<View>(R.id.bottomSheetContainer)
        val menuBottomSheetContainer = view.findViewById<View>(R.id.menuBottomSheetContainer)
        bottomSheetContainer?.setBackgroundColor(sheetColor)
        menuBottomSheetContainer?.setBackgroundColor(sheetColor)
        (bottomSheetContainer as? ViewGroup)?.getChildAt(0)?.setBackgroundColor(sheetColor)
        (menuBottomSheetContainer as? ViewGroup)?.getChildAt(0)?.setBackgroundColor(sheetColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView(
            factory = { ctx ->
                val parent = FrameLayout(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val view = LayoutInflater.from(ctx).inflate(R.layout.fragment_playlist, parent, false)
                parent.addView(view)
                rootViewRef[0] = view
                view.setBackgroundColor(0xFFFFFFFF.toInt())

                val backButton = view.findViewById<ImageButton>(R.id.backButton)
                backButton.setColorFilter(0xFF1A1B22.toInt())

                val shareButton = view.findViewById<ImageButton>(R.id.shareButton)
                val menuButton = view.findViewById<ImageButton>(R.id.menuButton)
                val tracksRecyclerView = view.findViewById<RecyclerView>(R.id.tracksRecyclerView)
                val bottomSheetContainer = view.findViewById<View>(R.id.bottomSheetContainer)
                val overlay = view.findViewById<View>(R.id.overlay)
                val menuOverlay = view.findViewById<View>(R.id.menuOverlay)
                val menuBottomSheetContainer = view.findViewById<View>(R.id.menuBottomSheetContainer)
                val emptyTracksLayout = view.findViewById<View>(R.id.emptyTracksLayout)

                val sheetColor = if (isDark) 0xFF1A1B22.toInt() else 0xFFFFFFFF.toInt()
                val menuTextColor = if (isDark) 0xFFFFFFFF.toInt() else 0xFF1A1B22.toInt()

                bottomSheetContainer.setBackgroundColor(sheetColor)
                menuBottomSheetContainer.setBackgroundColor(sheetColor)
                (bottomSheetContainer as? ViewGroup)?.getChildAt(0)?.setBackgroundColor(sheetColor)
                (menuBottomSheetContainer as? ViewGroup)?.getChildAt(0)?.setBackgroundColor(sheetColor)

                overlay.visibility = View.GONE
                overlay.alpha = 0f
                menuOverlay.visibility = View.GONE
                menuOverlay.alpha = 0f

                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    isHideable = false
                    isDraggable = true
                    peekHeight = 200
                }

                bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                overlay.visibility = View.GONE
                                overlay.alpha = 0f
                            }
                            BottomSheetBehavior.STATE_COLLAPSED -> {
                                overlay.visibility = View.VISIBLE
                                overlay.alpha = 1f
                                view.post { calculateAndSetPeekHeight(shareButton, bottomSheetBehavior, context) }
                            }
                            BottomSheetBehavior.STATE_EXPANDED -> {
                                overlay.visibility = View.VISIBLE
                                overlay.alpha = 1f
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        val normalized = (slideOffset + 1) / 2
                        overlay.alpha = normalized.coerceIn(0f, 1f)
                        if (normalized > 0.01f) overlay.visibility = View.VISIBLE
                    }
                })

                val menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                    isHideable = true
                    isDraggable = true
                    peekHeight = 400
                }

                menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                menuOverlay.visibility = View.GONE
                                menuOverlay.alpha = 0f
                                bottomSheetBehavior.isDraggable = true
                            }
                            BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED -> {
                                menuOverlay.visibility = View.VISIBLE
                                menuOverlay.alpha = 1f
                                bottomSheetBehavior.isDraggable = false
                                view.post {
                                    prepareMenuHeight(
                                        view.findViewById(R.id.playlistNameTextView),
                                        menuBottomSheetContainer,
                                        menuBottomSheetBehavior,
                                        context
                                    )
                                }
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        val normalized = (slideOffset + 1) / 2
                        menuOverlay.alpha = normalized.coerceIn(0f, 1f)
                        if (normalized > 0.01f) menuOverlay.visibility = View.VISIBLE
                    }
                })

                val trackAdapter = TrackAdapter(emptyList()) {  }
                trackAdapter.setOnLongClickListener { showDeleteTrackDialog(context, viewModel, it) }
                tracksRecyclerView.layoutManager = LinearLayoutManager(context)
                tracksRecyclerView.adapter = trackAdapter

                backButton.setOnClickListener { navController.popBackStack() }
                shareButton.setOnClickListener { viewModel.onShareClick() }
                menuButton.setOnClickListener {
                    if (menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                        prepareMenuHeight(
                            view.findViewById(R.id.playlistNameTextView),
                            menuBottomSheetContainer,
                            menuBottomSheetBehavior,
                            context
                        )
                        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

                val menuShareItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuShareItem)
                val menuEditItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuEditItem)
                val menuDeleteItem = menuBottomSheetContainer.findViewById<TextView>(R.id.menuDeleteItem)
                val menuPlaylistName = menuBottomSheetContainer.findViewById<TextView>(R.id.menuPlaylistName)
                val menuTrackCount = menuBottomSheetContainer.findViewById<TextView>(R.id.menuTrackCount)

                menuShareItem.setTextColor(menuTextColor)
                menuEditItem.setTextColor(menuTextColor)
                menuDeleteItem.setTextColor(menuTextColor)
                menuPlaylistName.setTextColor(menuTextColor)
                menuTrackCount.setTextColor(0xFFAEAFB4.toInt())

                menuShareItem.setOnClickListener {
                    viewModel.onShareClick()
                    menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                menuEditItem.setOnClickListener {
                    menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    navController.navigate("${com.practicum.playlistmaker3.ui.Routes.EDIT_PLAYLIST}/$playlistId")
                }
                menuDeleteItem.setOnClickListener {
                    menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.deletePlaylist()
                }

                val observer = Observer<PlaylistState> { state ->
                    if (state is PlaylistState.Content) {
                        displayPlaylist(
                            context = context,
                            view = view,
                            state = state,
                            trackAdapter = trackAdapter,
                            bottomSheetBehavior = bottomSheetBehavior,
                            overlay = overlay,
                            emptyTracksLayout = emptyTracksLayout,
                            tracksRecyclerView = tracksRecyclerView
                        )
                        updateMenuInfo(context, menuBottomSheetContainer, state)
                    }
                }
                observerRef[0] = observer
                viewModel.state.observeForever(observer)

                parent
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            observerRef[0]?.let { viewModel.state.removeObserver(it) }
        }
    }
}

private fun calculateAndSetPeekHeight(
    shareButton: View,
    bottomSheetBehavior: BottomSheetBehavior<View>,
    context: android.content.Context
) {
    val shareLocation = IntArray(2)
    shareButton.getLocationOnScreen(shareLocation)
    val shareBottomY = shareLocation[1] + shareButton.height
    val screenHeight = context.resources.displayMetrics.heightPixels
    val spaceBelowShare = screenHeight - shareBottomY

    var targetHeight = spaceBelowShare - dpToPx(context, 24)
    val internalPadding = dpToPx(context, 36)
    targetHeight += internalPadding

    val finalHeight = targetHeight.coerceAtLeast(dpToPx(context, 150))
    bottomSheetBehavior.peekHeight = finalHeight
}

private fun prepareMenuHeight(
    playlistNameTextView: TextView,
    menuBottomSheetContainer: View,
    menuBottomSheetBehavior: BottomSheetBehavior<View>,
    context: android.content.Context
) {
    val titleLocation = IntArray(2)
    playlistNameTextView.getLocationOnScreen(titleLocation)
    val titleBottomY = titleLocation[1] + playlistNameTextView.height
    val screenHeight = context.resources.displayMetrics.heightPixels
    val spaceBelowTitle = screenHeight - titleBottomY
    val menuTopPadding = dpToPx(context, 4 + 12 + 8)
    val targetHeight = spaceBelowTitle + menuTopPadding
    val finalHeight = targetHeight.coerceAtLeast(dpToPx(context, 200))

    val params = menuBottomSheetContainer.layoutParams
    params.height = finalHeight
    menuBottomSheetContainer.layoutParams = params
    menuBottomSheetContainer.requestLayout()

    menuBottomSheetBehavior.peekHeight = finalHeight
}

private fun displayPlaylist(
    context: android.content.Context,
    view: View,
    state: PlaylistState.Content,
    trackAdapter: TrackAdapter,
    bottomSheetBehavior: BottomSheetBehavior<View>,
    overlay: View,
    emptyTracksLayout: View,
    tracksRecyclerView: RecyclerView
) {
    val playlist = state.playlist

    view.findViewById<TextView>(R.id.playlistNameTextView).apply {
        text = playlist.name
        setTextColor(0xFF1A1B22.toInt())
    }

    val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
    if (!playlist.description.isNullOrEmpty()) {
        descriptionTextView.text = playlist.description
        descriptionTextView.isVisible = true
        descriptionTextView.setTextColor(0xFF1A1B22.toInt())
    } else {
        descriptionTextView.isVisible = false
    }

    view.findViewById<TextView>(R.id.durationTextView).apply {
        setTextColor(0xFF1A1B22.toInt())
    }
    view.findViewById<TextView>(R.id.trackCountTextView).apply {
        setTextColor(0xFF1A1B22.toInt())
    }

    val durationSum = state.totalDuration
    val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum).toIntOrNull() ?: 0
    val durationText = if (minutes > 0) {
        val hours = minutes / 60
        val remaining = minutes % 60
        if (hours > 0) {
            if (remaining > 0) "$hours ч $remaining мин" else "$hours ч"
        } else "$minutes мин"
    } else "0 мин"
    view.findViewById<TextView>(R.id.durationTextView).text = durationText

    val trackCount = state.tracks.size
    view.findViewById<TextView>(R.id.trackCountTextView).text =
        context.resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)

    val coverPath = playlist.coverPath
    val coverImageView = view.findViewById<ImageView>(R.id.coverImageView)
    if (!coverPath.isNullOrEmpty()) {
        Glide.with(context)
            .load(coverPath)
            .placeholder(R.drawable.placeholder_cover_312)
            .error(R.drawable.placeholder_cover_312)
            .transform(CenterCrop(), RoundedCorners(dpToPx(context, 8)))
            .into(coverImageView)
    } else {
        Glide.with(context)
            .load(R.drawable.placeholder_cover_312)
            .transform(CenterCrop(), RoundedCorners(dpToPx(context, 8)))
            .into(coverImageView)
    }

    val trackUis = state.tracks.map { TrackMapper.mapToUi(it) }
    trackAdapter.updateTracks(trackUis)

    emptyTracksLayout.isVisible = state.tracks.isEmpty()
    tracksRecyclerView.isVisible = state.tracks.isNotEmpty()

    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    overlay.visibility = View.VISIBLE
    overlay.alpha = 1f

    view.post {
        calculateAndSetPeekHeight(
            view.findViewById(R.id.shareButton),
            bottomSheetBehavior,
            context
        )
    }
}

private fun updateMenuInfo(
    context: android.content.Context,
    menuBottomSheetContainer: View,
    state: PlaylistState.Content
) {
    val name = menuBottomSheetContainer.findViewById<TextView>(R.id.menuPlaylistName)
    val count = menuBottomSheetContainer.findViewById<TextView>(R.id.menuTrackCount)
    val cover = menuBottomSheetContainer.findViewById<ImageView>(R.id.menuCoverImageView)

    name.text = state.playlist.name
    val trackCount = state.tracks.size
    count.text = context.resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)

    val coverPath = state.playlist.coverPath
    if (!coverPath.isNullOrEmpty()) {
        Glide.with(context)
            .load(coverPath)
            .placeholder(R.drawable.placeholder_cover_312)
            .error(R.drawable.placeholder_cover_312)
            .into(cover)
    } else {
        Glide.with(context).load(R.drawable.placeholder_cover_312).into(cover)
    }
}

private fun showDeletePlaylistDialog(context: android.content.Context, viewModel: PlaylistViewModel) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.delete_playlist_title))
        .setMessage(context.getString(R.string.delete_playlist_message))
        .setPositiveButton(context.getString(R.string.delete_playlist_yes)) { _, _ -> viewModel.confirmDeletePlaylist() }
        .setNegativeButton(context.getString(R.string.delete_playlist_no)) { dialog, _ ->
            dialog.dismiss()
            viewModel.cancelDeletePlaylist()
        }
        .show()
}

private fun showDeleteTrackDialog(context: android.content.Context, viewModel: PlaylistViewModel, trackUi: TrackUi) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.delete_track_confirmation))
        .setPositiveButton(context.getString(R.string.yes)) { _, _ -> viewModel.deleteTrackFromPlaylist(trackUi.trackId) }
        .setNegativeButton(context.getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
        .show()
}

private fun dpToPx(context: android.content.Context, dp: Int): Int =
    (dp * context.resources.displayMetrics.density).toInt()