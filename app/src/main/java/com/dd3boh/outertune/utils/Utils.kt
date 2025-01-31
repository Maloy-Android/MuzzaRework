package com.dd3boh.outertune.utils

import com.dd3boh.outertune.db.entities.Artist
import com.dd3boh.outertune.ui.screens.settings.NavigationTab

fun reportException(throwable: Throwable) {
    throwable.printStackTrace()
}

/**
 * Converts the enable tabs list (string) to NavigationTab
 *
 * @param str Encoded string
 */
fun decodeTabString(str: String): List<NavigationTab> {
    return str.toCharArray().map {
        when (it) {
            'H' -> NavigationTab.HOME
            'S' -> NavigationTab.SONG
            'F' -> NavigationTab.FOLDERS
            'A' -> NavigationTab.ARTIST
            'B' -> NavigationTab.ALBUM
            'L' -> NavigationTab.PLAYLIST
            else -> {
                NavigationTab.NULL // this case should never happen. Just shut the compiler up
            }
        }
    }
}

/**
 * Converts the NavigationTab tabs list to string
 *
 * @param list Decoded NavigationTab list
 */
fun encodeTabString(list: List<NavigationTab>): String {
    var encoded = ""
    list.subList(0, list.indexOf(NavigationTab.NULL)).forEach {
        encoded += when (it) {
            NavigationTab.HOME -> "H"
            NavigationTab.SONG -> "S"
            NavigationTab.FOLDERS -> "F"
            NavigationTab.ARTIST -> "A"
            NavigationTab.ALBUM -> "B"
            NavigationTab.PLAYLIST -> "L"
            else -> { "" }
        }
    }

    return encoded
}

/**
 * Find the matching string, if not found the closest super string
 */
fun closestMatch(query: String, stringList: List<Artist>): Artist? {
    // Check for exact match first

    val exactMatch = stringList.find { query.lowercase() == it.artist.name.lowercase() }
    if (exactMatch != null) {
        return exactMatch
    }

    // Check for query as substring in any of the strings
    val substringMatches = stringList.filter { it.artist.name.contains(query) }
    if (substringMatches.isNotEmpty()) {
        return substringMatches.minByOrNull { it.artist.name.length }
    }

    return null
}