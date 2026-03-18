package com.practicum.playlistmaker3

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.data.repository.TrackRepository
import com.practicum.playlistmaker3.di.NetworkModule
import com.practicum.playlistmaker3.presentation.SearchState
import com.practicum.playlistmaker3.presentation.SearchViewModel
import com.practicum.playlistmaker3.presentation.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

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

    private lateinit var viewModel: SearchViewModel
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupViewModel()
        setupListeners()
        setupSearchTextWatcher()
        setupEditorActionListener()
        setupRecyclerView()
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
    }

    private fun setupViewModel() {
        val repository = TrackRepository(NetworkModule.itunesApiService)
        val factory = SearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
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
    }

    private fun setupEditorActionListener() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }
    }

    private fun setupSearchTextWatcher() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearSearchResults()
                }
            }
        })
    }

    private fun performSearch() {
        hideKeyboard()
        viewModel.searchTracks(searchText)
    }

    private fun clearSearchResults() {
        trackAdapter.updateTracks(emptyList())
        showPlaceholders(isError = false, isEmpty = false)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.tracks)
                is SearchState.Empty -> showEmpty()
                is SearchState.Error -> showError()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholdersContainer.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        placeholdersContainer.visibility = View.GONE

        trackAdapter.updateTracks(tracks)
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
        placeholderEmpty.visibility = View.VISIBLE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = View.VISIBLE
        placeholderEmpty.visibility = View.GONE
    }

    private fun showPlaceholders(isError: Boolean, isEmpty: Boolean) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholdersContainer.visibility = View.VISIBLE
        placeholderError.visibility = if (isError) View.VISIBLE else View.GONE
        placeholderEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
}