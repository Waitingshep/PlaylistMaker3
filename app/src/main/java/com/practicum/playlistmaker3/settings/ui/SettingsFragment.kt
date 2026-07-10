package com.practicum.playlistmaker3.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeSwitcher = view.findViewById(R.id.themeSwitcher)

        viewModel.state.observe(viewLifecycleOwner) { state ->
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

        view.findViewById<TextView>(R.id.share_app).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_url))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        }

        view.findViewById<TextView>(R.id.message_support).setOnClickListener {
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

        view.findViewById<TextView>(R.id.agreement).setOnClickListener {
            val agreementUrl = getString(R.string.agreement_url)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)))
        }
    }
}