package com.dd3boh.outertune.playback

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.navigation.NavController
import com.dd3boh.outertune.constants.DiscordTokenKey
import com.dd3boh.outertune.constants.EnableDiscordRPCKey
import com.dd3boh.outertune.extensions.mediaItems
import com.dd3boh.outertune.extensions.metadata
import com.dd3boh.outertune.utils.dataStore
import com.dd3boh.outertune.utils.get
import com.dd3boh.outertune.utils.rememberPreference
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Timestamps
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
fun DiscordRPCInfoFetch(player: Player, ctx: Context){
    val discordToken = ctx.dataStore.get(DiscordTokenKey, "")
    val enableRPC = ctx.dataStore.get(EnableDiscordRPCKey, true)

    if (discordToken != "" || !enableRPC){
        val client = HttpClient()
        val clientDiscordCDN = HttpClient()

        val rpc = KizzyRPC(discordToken)

        var mediaID = player.currentMediaItem?.mediaId
        var title = player.currentMediaItem?.mediaMetadata?.title.toString()
        var album = if (title == player.currentMediaItem?.mediaMetadata?.albumTitle.toString()) "Single" else player.currentMediaItem?.mediaMetadata?.albumTitle.toString()
        var artist = player.currentMediaItem?.mediaMetadata?.artist.toString()
        var artwork = player.currentMediaItem?.mediaMetadata?.artworkUri.toString()


//    Log.e("kizzy song info", album.toString() + artist.toString() + artwork.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: HttpResponse = clientDiscordCDN.get("https://kizzyapi-1-z9614716.deta.app/image?url=" + artwork) {
                }
                val responseBody: String = response.bodyAsText()

                // Parse the JSON response
                val json = JSONObject(responseBody)
                var thumbCDN = json.getString("id")

                rpc.setActivity(
                        activity = Activity(
                                name = title,
                                details = title,
                                state = artist,
                                type = 2,
                                assets = Assets(
                                        largeImage = thumbCDN,
//                                    smallImage = "mp:external/_jGArMHI-5rpJu4qVDuiBARu8iEXnHeT0SZS6tZnZug/https/i.imgur.com/zDxXZKk.png",
                                        smallImage = null,
                                        largeText = if (album != "Single") "Album:" + album else album,
//                                    smallText = artist,
                                ),

                                ),
                )
            } catch (e: Exception) {
                println("Error: ${e.message}")
            } finally {
                clientDiscordCDN.close()
            }
        }


//    CoroutineScope(Dispatchers.IO).launch {
//            try {
//
//                val response: HttpResponse = client.get("https://pipedapi.r4fo.com/streams/" + mediaID) {
//                }
//                val responseBody: String = response.bodyAsText()
//
//                var thumb = ""
//                var title = ""
//                var artist = ""
//                var artistThumb = ""
//
//                var thumbCDN = ""
//                var artistThumbCDN = ""
//
//                // Parse the JSON response
//                val json = JSONObject(responseBody)
//                thumb = json.getString("thumbnailUrl")
//                title = json.getString("title")
//                artist = json.getString("uploader").split(" - Topic")[0]
//                artistThumb = json.getString("uploaderAvatar")
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        val response: HttpResponse = clientDiscordCDN.get("https://kizzyapi-1-z9614716.deta.app/image?url=" + artistThumb) {
//                        }
//                        val responseBody: String = response.bodyAsText()
//
//                        // Parse the JSON response
//                        val json = JSONObject(responseBody)
//                        artistThumbCDN = json.getString("id")
//                    } catch (e: Exception) {
//                        println("Error: ${e.message}")
//                    } finally {
//                        clientDiscordCDN.close()
//                    }
//                }
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        val response: HttpResponse = clientDiscordCDN.get("https://kizzyapi-1-z9614716.deta.app/image?url=" + thumb) {
//                        }
//                        val responseBody: String = response.bodyAsText()
//
//                        // Parse the JSON response
//                        val json = JSONObject(responseBody)
//                        thumbCDN = json.getString("id")
//                        Thread.sleep(500)
//                        if (artistThumbCDN == ""){
//                            artistThumbCDN = "mp:external/_jGArMHI-5rpJu4qVDuiBARu8iEXnHeT0SZS6tZnZug/https/i.imgur.com/zDxXZKk.png"
//                        }
//                        rpc.setActivity(
//                                activity = Activity(
//                                        name = title,
//                                        details = title,
//                                        state = artist,
//                                        type = 2,
//                                        assets = Assets(
//                                                largeImage = thumbCDN,
//                                                smallImage = artistThumbCDN,
//                                                largeText = title,
//                                                smallText = artist,
//                                        ),
//
//                                        ),
//                        )
//
//                    } catch (e: Exception) {
//                        println("Error: ${e.message}")
//                    } finally {
//                        clientDiscordCDN.close()
//                    }
//                }
//
//
//            } catch (e: Exception) {
//                println("Error: ${e.message}")
//            } finally {
//                client.close()
//
//            }
//        }
            }
}