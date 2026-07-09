package com.practicum.playlistmaker3.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
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
    private var lastClickTime: Long = 0

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        setupSearchTextWatcher()
        setupFocusChangeListener()
        setupRecyclerView()
        setupHistoryRecyclerView()
        observeViewModel()

        val restoredText = savedInstanceState?.getString(SEARCH_TEXT_KEY)
        if (!restoredText.isNullOrEmpty()) {
            searchEditText.removeTextChangedListener(textWatcher)
            searchEditText.setText(restoredText)
            searchEditText.addTextChangedListener(textWatcher)
            viewModel.restoreState(restoredText)
        } else {
            viewModel.loadHistory()
        }
    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.search_edit_text)
        clearButton = view.findViewById(R.id.clear_button)
        recyclerView = view.findViewById(R.id.tracksList)
        progressBar = view.findViewById(R.id.progressBar)
        placeholdersContainer = view.findViewById(R.id.placeholdersContainer)
        placeholderError = view.findViewById(R.id.placeholderError)
        placeholderEmpty = view.findViewById(R.id.placeholderEmpty)
        refreshButton = placeholderError.findViewById(R.id.refreshButton)

        historyContainer = view.findViewById(R.id.historyScrollView)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)
    }

    private fun setupFocusChangeListener() {
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (searchEditText.text.isNullOrEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    private fun setupListeners() {
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

    private val textWatcher = object : TextWatcher {
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
    }

    private fun setupSearchTextWatcher() {
        searchEditText.addTextChangedListener(textWatcher)
    }

    private fun clearSearchResults() {
        trackAdapter.updateTracks(emptyList())
        showPlaceholders(isError = false, isEmpty = false)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { trackUi ->
            viewModel.addToHistory(trackUi)
            openPlayerFragmentWithDebounce(trackUi)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = trackAdapter
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = TrackAdapter(emptyList()) { trackUi ->
            viewModel.addToHistory(trackUi)
            openPlayerFragmentWithDebounce(trackUi)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter
    }

    private fun openPlayerFragmentWithDebounce(trackUi: TrackUi) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            val bundle = Bundle().apply {
                putParcelable("track", trackUi)
            }
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
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
        if (tracks.isEmpty()) {
            historyContainer.visibility = View.GONE
            placeholdersContainer.visibility = View.GONE
            return
        }
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
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }
}