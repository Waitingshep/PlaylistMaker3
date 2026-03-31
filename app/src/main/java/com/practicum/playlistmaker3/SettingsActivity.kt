package com.practicum.playlistmaker3

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        backButton.setOnClickListener {
            finish()
        }

        // Устанавливаем состояние переключателя в соответствии с текущей темой
        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        // Обработчик переключения темы
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        val shareTextView = findViewById<TextView>(R.id.share_app)

        shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_url))
            }

            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_via))
            startActivity(chooser)
        }

        val supportTextView = findViewById<TextView>(R.id.message_support)

        supportTextView.setOnClickListener {
            val message = getString(R.string.support_message)
            val subject = getString(R.string.support_subject)

            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(supportIntent)
        }

        val agreementTextView = findViewById<TextView>(R.id.agreement)

        agreementTextView.setOnClickListener {
            val agreementUrl = getString(R.string.agreement_url)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(browserIntent)
        }
    }
}