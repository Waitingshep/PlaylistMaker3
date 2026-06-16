package com.practicum.playlistmaker3.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        backButton.setOnClickListener {
            finish()
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is SettingsState.ThemeLoaded -> {
                    themeSwitcher.isChecked = state.mode == ThemeMode.DARK
                }
                else -> {}
            }
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
            viewModel.setTheme(newMode)
        }


        val shareTextView = findViewById<TextView>(R.id.share_app)
        shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_url))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        }

        val supportTextView = findViewById<TextView>(R.id.message_support)
        supportTextView.setOnClickListener {
            val message = getString(R.string.support_message)
            val subject = getString(R.string.support_subject)
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            startActivity(supportIntent)
        }

        val agreementTextView = findViewById<TextView>(R.id.agreement)
        agreementTextView.setOnClickListener {
            val agreementUrl = getString(R.string.agreement_url)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)))
        }
    }
}