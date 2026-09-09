package com.practicum.playlistmaker3.playlist.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.utils.NavigationVisibilityListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {

    private val _viewModel: CreatePlaylistViewModel by viewModel()
    protected open val viewModel: CreatePlaylistViewModel get() = _viewModel

    private lateinit var backButton: ImageButton
    private lateinit var coverPlaceholder: FrameLayout
    private lateinit var coverIcon: ImageView
    private lateinit var coverImageView: ImageView

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEditText: EditText

    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var descriptionEditText: EditText

    private lateinit var createButton: Button

    private var coverUri: Uri? = null
    private var coverPath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            if (uri != null) {
                coverUri = uri
                coverPath = saveImageToPrivateStorage(uri)
                viewModel.setCoverPath(coverPath)
                viewModel.updateCover(uri)
                displayCover(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_create_playlist,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backButton)
        coverPlaceholder = view.findViewById(R.id.coverPlaceholder)
        coverIcon = view.findViewById(R.id.coverIcon)
        coverImageView = view.findViewById(R.id.coverImageView)

        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        nameEditText = view.findViewById(R.id.nameEditText)

        descriptionInputLayout = view.findViewById(R.id.descriptionInputLayout)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)

        createButton = view.findViewById(R.id.createButton)

        // Скрываем нижнюю навигацию через MainActivity
        (requireActivity() as? NavigationVisibilityListener)?.hideNavigation()

        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        nameEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        descriptionEditText.imeOptions = EditorInfo.IME_ACTION_DONE

        nameEditText.setPaintFlags(nameEditText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv())
        descriptionEditText.setPaintFlags(descriptionEditText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv())

        val args = arguments
        val playlistId = args?.getLong("playlistId") ?: 0L
        val playlistName = args?.getString("playlistName")
        val playlistDescription = args?.getString("playlistDescription")
        val playlistCoverPath = args?.getString("playlistCoverPath")

        if (playlistId > 0) {
            viewModel.setPlaylistId(playlistId)
        }

        if (!playlistName.isNullOrEmpty()) {
            nameEditText.setText(playlistName)
            viewModel.updateName(playlistName)
        }

        if (!playlistDescription.isNullOrEmpty()) {
            descriptionEditText.setText(playlistDescription)
            viewModel.updateDescription(playlistDescription)
        }

        if (!playlistCoverPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(playlistCoverPath)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                .into(coverImageView)
            coverIcon.visibility = View.GONE
            coverImageView.visibility = View.VISIBLE
        }

        updateHintColors(
            nameInputLayout,
            nameEditText.text.toString()
        )

        updateHintColors(
            descriptionInputLayout,
            descriptionEditText.text.toString()
        )

        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? NavigationVisibilityListener)?.showNavigation()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            handleBackPress()
        }

        coverPlaceholder.setOnClickListener {
            openImagePicker()
        }

        nameEditText.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s?.toString().orEmpty()
                    viewModel.updateName(text)
                    updateHintColors(nameInputLayout, text)
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            }
        )

        descriptionEditText.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s?.toString().orEmpty()
                    viewModel.updateDescription(text)
                    updateHintColors(descriptionInputLayout, text)
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            }
        )

        nameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                nameEditText.clearFocus()
                true
            } else {
                false
            }
        }

        descriptionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                descriptionEditText.clearFocus()
                true
            } else {
                false
            }
        }

        createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateCreateButtonState(state)
            if (state.creationResult != null && state.creationResult > 0) {
                val message = if (state.isEditMode) {
                    getString(R.string.playlist_updated, state.name)
                } else {
                    getString(R.string.playlist_created, state.name)
                }
                showSuccessSnackbar(message)
                viewModel.resetCreationResult()
                findNavController().popBackStack()
            }
            if (state.showDiscardDialog) {
                showDiscardDialog()
            }
        }
    }

    private fun updateCreateButtonState(state: CreatePlaylistState) {
        createButton.isEnabled = state.isCreateEnabled
        if (state.isCreateEnabled) {
            createButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            createButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.button_inactive)
        }
    }

    private fun showSuccessSnackbar(message: String) {
        val view = requireView()

        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val backgroundColor = if (isDarkTheme) {
            ContextCompat.getColor(requireContext(), R.color.white)
        } else {
            ContextCompat.getColor(requireContext(), R.color.media_button)
        }

        val textColor = if (isDarkTheme) {
            ContextCompat.getColor(requireContext(), R.color.black)
        } else {
            ContextCompat.getColor(requireContext(), R.color.white)
        }

        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        snackbar.setBackgroundTint(backgroundColor)
        snackbar.setTextColor(textColor)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            dpToPx(8),
            dpToPx(16),
            dpToPx(8),
            dpToPx(16)
        )
        snackbarView.layoutParams = params

        snackbar.show()
    }

    private fun updateHintColors(inputLayout: TextInputLayout, text: String) {
        val context = requireContext()
        val hasText = text.isNotEmpty()

        val isDarkTheme = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val strokeColor = if (hasText) {
            ContextCompat.getColor(context, R.color.blue)
        } else {
            if (isDarkTheme) {
                ContextCompat.getColor(context, R.color.white)
            } else {
                ContextCompat.getColor(context, R.color.search_text_hint)
            }
        }

        val hintColor = if (hasText) {
            ContextCompat.getColor(context, R.color.blue)
        } else {
            if (isDarkTheme) {
                ContextCompat.getColor(context, R.color.white)
            } else {
                ContextCompat.getColor(context, R.color.media_button)
            }
        }

        val strokeColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(-android.R.attr.state_focused)
            ),
            intArrayOf(strokeColor, strokeColor)
        )

        inputLayout.setBoxStrokeColorStateList(strokeColorStateList)

        val hintColorStateList = ColorStateList.valueOf(hintColor)

        inputLayout.setHintTextColor(hintColorStateList)
        inputLayout.defaultHintTextColor = hintColorStateList
    }

    private fun displayCover(uri: Uri) {
        coverIcon.visibility = View.GONE
        coverImageView.visibility = View.VISIBLE
        coverImageView.setImageDrawable(null)

        Glide.with(this)
            .load(uri)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(8))
            )
            .into(coverImageView)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            requireView().windowToken,
            0
        )
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun saveImageToPrivateStorage(sourceUri: Uri): String? {
        return try {
            val inputStream =
                requireContext()
                    .contentResolver
                    .openInputStream(sourceUri)
                    ?: return null

            val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"

            val file = File(
                requireContext().filesDir,
                fileName
            )

            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            file.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.discard_title))
            .setMessage(getString(R.string.discard_message))
            .setPositiveButton(getString(R.string.discard_positive)) { _, _ ->
                viewModel.onDiscardDialogConfirmed()
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.discard_negative)) { _, _ ->
                viewModel.onDiscardDialogCancelled()
            }
            .show()
    }

    protected open fun handleBackPress() {
        val shouldShowDialog = viewModel.onBackPressed()

        if (!shouldShowDialog) {
            findNavController().popBackStack()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetCreationResult()
    }
}