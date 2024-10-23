package com.dd3boh.outertune.ui.screens.settings

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dd3boh.outertune.LocalPlayerAwareWindowInsets
import com.dd3boh.outertune.R
import com.dd3boh.outertune.constants.AccountChannelHandleKey
import com.dd3boh.outertune.constants.AccountEmailKey
import com.dd3boh.outertune.constants.AccountNameKey
import com.dd3boh.outertune.constants.ContentCountryKey
import com.dd3boh.outertune.constants.ContentLanguageKey
import com.dd3boh.outertune.constants.CountryCodeToName
import com.dd3boh.outertune.constants.InnerTubeCookieKey
import com.dd3boh.outertune.constants.LanguageCodeToName
import com.dd3boh.outertune.constants.ProxyEnabledKey
import com.dd3boh.outertune.constants.ProxyTypeKey
import com.dd3boh.outertune.constants.ProxyUrlKey
import com.dd3boh.outertune.constants.SYSTEM_DEFAULT
import com.dd3boh.outertune.constants.YtmSyncKey
import com.dd3boh.outertune.ui.component.EditTextPreference
import com.dd3boh.outertune.ui.component.IconButton
import com.dd3boh.outertune.ui.component.ListPreference
import com.dd3boh.outertune.ui.component.PreferenceEntry
import com.dd3boh.outertune.ui.component.PreferenceGroupTitle
import com.dd3boh.outertune.ui.component.SwitchPreference
import com.dd3boh.outertune.ui.utils.backToMain
import com.dd3boh.outertune.utils.rememberEnumPreference
import com.dd3boh.outertune.utils.rememberPreference
import com.zionhuang.innertube.utils.parseCookieString
import java.net.Proxy
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val accountName by rememberPreference(AccountNameKey, "")
    val accountEmail by rememberPreference(AccountEmailKey, "")
    val accountChannelHandle by rememberPreference(AccountChannelHandleKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    val (ytmSync, onYtmSyncChange) = rememberPreference(YtmSyncKey, defaultValue = true)
    val (contentLanguage, onContentLanguageChange) = rememberPreference(key = ContentLanguageKey, defaultValue = "system")
    val (contentCountry, onContentCountryChange) = rememberPreference(key = ContentCountryKey, defaultValue = "system")

    val (proxyEnabled, onProxyEnabledChange) = rememberPreference(key = ProxyEnabledKey, defaultValue = false)
    val (proxyType, onProxyTypeChange) = rememberEnumPreference(key = ProxyTypeKey, defaultValue = Proxy.Type.HTTP)
    val (proxyUrl, onProxyUrlChange) = rememberPreference(key = ProxyUrlKey, defaultValue = "host:port")


    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)))
        PreferenceGroupTitle(
            title = "ACCOUNT"
        )
        PreferenceEntry(
            title = { Text(if (isLoggedIn) accountName else stringResource(R.string.login)) },
            description = if (isLoggedIn) {
                accountEmail.takeIf { it.isNotEmpty() }
                    ?: accountChannelHandle.takeIf { it.isNotEmpty() }
            } else null,
            icon = { Icon(Icons.Rounded.Person, null) },
            onClick = { navController.navigate("login") }
        )
        SwitchPreference(
            title = { Text(stringResource(R.string.ytm_sync)) },
            icon = { Icon(Icons.Rounded.Sync, null) },
            checked = ytmSync,
            onCheckedChange = onYtmSyncChange,
            isEnabled = isLoggedIn
        )

        PreferenceGroupTitle(
            title = "LOCALIZATION"
        )
        ListPreference(
            title = { Text(stringResource(R.string.content_language)) },
            icon = { Icon(Icons.Rounded.Language, null) },
            selectedValue = contentLanguage,
            values = listOf(SYSTEM_DEFAULT) + LanguageCodeToName.keys.toList(),
            valueText = {
                LanguageCodeToName.getOrElse(it) {
                    stringResource(R.string.system_default)
                }
            },
            onValueSelected = onContentLanguageChange
        )
        ListPreference(
            title = { Text(stringResource(R.string.content_country)) },
            icon = { Icon(Icons.Rounded.LocationOn, null) },
            selectedValue = contentCountry,
            values = listOf(SYSTEM_DEFAULT) + CountryCodeToName.keys.toList(),
            valueText = {
                CountryCodeToName.getOrElse(it) {
                    stringResource(R.string.system_default)
                }
            },
            onValueSelected = onContentCountryChange
        )

        PreferenceGroupTitle(
            title = stringResource(R.string.app_language),
        )
        LanguageSelector()

        PreferenceGroupTitle(
            title = "PROXY"
        )

        SwitchPreference(
            title = { Text(stringResource(R.string.enable_proxy)) },
            checked = proxyEnabled,
            onCheckedChange = onProxyEnabledChange
        )

        if (proxyEnabled) {
            ListPreference(
                title = { Text(stringResource(R.string.proxy_type)) },
                selectedValue = proxyType,
                values = listOf(Proxy.Type.HTTP, Proxy.Type.SOCKS),
                valueText = { it.name },
                onValueSelected = onProxyTypeChange
            )
            EditTextPreference(
                title = { Text(stringResource(R.string.proxy_url)) },
                value = proxyUrl,
                onValueChange = onProxyUrlChange
            )
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.content)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    // List of supported languages and their locale codes
    val languages = listOf(
        "Arabic" to "ar",
        "Belarusian" to "be",
        "Chinese Simplified" to "zh",
        "Czech" to "cs",
        "Dutch" to "nl",
        "English" to "en",
        "French" to "fr",
        "German" to "de",
        "Indonesian" to "id",
        "Italian" to "it",
        "Japanese" to "ja",
        "Korean" to "ko",
        "Portuguese, Brazilian" to "pt-BR",
        "Russian" to "ru",
        "Spanish" to "es",
        "Turkish" to "tr",
        "Ukrainian" to "uk",
        "Vietnamese" to "vi",
        "Bulgarian" to "bg",
        "Bengali" to "bn-rIN",
        "German" to "DE",
        "Greek" to "el-rGR",
        "Perdita" to "fa-rIR",
        "Finnish" to "fi-rFi",
        "Hungarian" to "hu",
        "Indonesian" to "id",
        "Malayalam" to "ml-rIN",
        "Punjabi" to "pa",
        "Polish" to "pl",
        "Swedish" to "sv-rSE"
    )

    // State to hold the currently selected language
    var selectedLanguage by remember { mutableStateOf(languages[0].second) }
    var expanded by remember { mutableStateOf(false) } // Dropdown expanded state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        ) {
        Column(modifier = Modifier.padding(16.dp)) {


            // Dropdown button
            FloatingActionButton(
                modifier = Modifier
                    .size(48.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { expanded = true },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Translate,
                    contentDescription = null
                )
            }


            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center

            )
            {


                // Dropdown menu for language selection
                DropdownMenu(

                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(text = language.first) },
                            onClick = {
                                selectedLanguage = language.second
                                expanded = false
                                updateLanguage(context, selectedLanguage)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}


fun updateLanguage(context: android.content.Context, languageCode: String) {
    val locale: Locale = if (languageCode.contains("-")) {
        // Handle languages with regions like pt-BR
        val parts = languageCode.split("-")
        Locale(parts[0], parts[1])
    } else {
        Locale(languageCode)
    }

    val config = Configuration(context.resources.configuration)
    config.setLocales(LocaleList(locale))

    // Update the configuration
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Optionally, recreate the activity to apply the language change throughout the app
    (context as? androidx.activity.ComponentActivity)?.recreate()
}
