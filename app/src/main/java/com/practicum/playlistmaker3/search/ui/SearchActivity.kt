package com.practicum.playlistmaker3.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var placeholdersContainer: LinearLayout
    private lateinit var placeholderError: View
    private lateinit var placeholderEmpty: View
    private lateinit var refreshButton: Button
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var historyContainer: ScrollView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: TrackAdapter

    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupListeners()
        setupSearchTextWatcher()
        setupFocusChangeListener()
        setupRecyclerView()
        setupHistoryRecyclerView()
        observeViewModel()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracksList)
        progressBar = findViewById(R.id.progressBar)
        placeholdersContainer = findViewById(R.id.placeholdersContainer)
        placeholderError = findViewById(R.id.placeholderError)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        refreshButton = placeholderError.findViewById(R.id.refreshButton)

        historyContainer = findViewById(R.id.historyScrollView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
    }

    private fun setupFocusChangeListener() {
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (searchEditText.text.isNullOrEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            hideKeyboard()
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            clearSearchResults()
        }

        refreshButton.setOnClickListener {
            viewModel.retryLastSearch()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun setupSearchTextWatcher() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchText = s?.toString() ?: ""
                if (!searchText.isNullOrEmpty()) {
                    clearSearchResults()
                }
                viewModel.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearSearchResults()
                    viewModel.loadHistory()
                }
            }
        })
    }

    private fun clearSearchResults() {
        trackAdapter.updateTracks(emptyList())
        showPlaceholders(isError = false, isEmpty = false)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { trackUi ->
            viewModel.addToHistory(trackUi)
            openPlayerActivityWithDebounce(trackUi)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = TrackAdapter(emptyList()) { trackUi ->
            viewModel.addToHistory(trackUi)
            openPlayerActivityWithDebounce(trackUi)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private var lastClickTime = 0L
    private fun openPlayerActivityWithDebounce(trackUi: TrackUi) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.TRACK_EXTRA, trackUi)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is SearchUiState.Loading -> showLoading()
                is SearchUiState.Content -> showContent(state.tracks)
                is SearchUiState.History -> showHistory(state.tracks)
                is SearchUiState.Empty -> showEmpty()
                is SearchUiState.Error -> showError()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholdersContainer.visibility = View.GONE
    }

    private fun showContent(tracks: List<TrackUi>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        historyContainer.visibility = View.GONE
        placeholdersContainer.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
    }

    private fun showHistory(tracks: List<TrackUi>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.VISIBLE
        placeholdersContainer.visibility = View.GONE
        historyAdapter.updateTracks(tracks)
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
        placeholderEmpty.visibility = View.VISIBLE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = View.VISIBLE
        placeholderEmpty.visibility = View.GONE
    }

    private fun showPlaceholders(isError: Boolean, isEmpty: Boolean) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = if (isError) View.VISIBLE else View.GONE
        placeholderEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        searchEditText.setText(savedText)
    }
}