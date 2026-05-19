package com.practicum.playlistmaker3.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker3.App
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.presentation.common.Creator

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private val getThemeUseCase by lazy { Creator.provideGetThemeUseCase() }
    private val setThemeUseCase by lazy { Creator.provideSetThemeUseCase() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        backButton.setOnClickListener {
            finish()
        }

        val currentTheme = getThemeUseCase()
        themeSwitcher.isChecked = currentTheme == ThemeMode.DARK

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
            setThemeUseCase(newMode)
            (applicationContext as App).switchTheme(isChecked)
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