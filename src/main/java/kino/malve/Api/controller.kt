package kino.malve.Api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import entities.cinemamode.Request
import entities.cinemamode.playlist.ItemParam
import kino.malve.Api.pojo.Config
import kino.malve.Api.pojo.Element
import kino.malve.Api.pojo.MovieElement
import kino.malve.Api.pojo.PreProgramElement
import kino.malve.kodiAPI.ApiUtils
import kino.malve.kodiAPI.pojos.Notification.Notification
import kino.malve.kodiAPI.pojos.item.FileItem
import kino.malve.kodiAPI.pojos.item.MovieItem
import kino.malve.kodiAPI.pojos.item.PlaylistItem
import kino.malve.kodiAPI.pojos.param.PlayListParam
import kino.malve.kodiAPI.pojos.param.VolumeParam
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
class controller : WebSocketListener() {


    private val gson: Gson
    private val service = ApiUtils.soService
    //private val basepath = "D:/TestCinemamode/"
    private var currentPhase: Element? = null
    private val config = Stack<Element>()


    /**
     * Spring properties
     */
    @Value("\${base.path}")
    lateinit var basepath: String
    @Value("\${remote.houseCode}")
    lateinit var remoteHouseCode: String
    @Value("\${remote.pathToPowerswitch}")
    lateinit var pathToPowerswitch: String
    @Value("\${remote.address.light}")
    lateinit var remoteAddressLight: String


    /**
     * Websocket connection to Kodi for receiving notifications
     */
    init {
        OkHttpClient()
                .newWebSocket(okhttp3.Request.Builder()
                        .url("ws://localhost:9090/jsonrpc")
                        .build(), this)
        gson = GsonBuilder()
                .registerTypeAdapterFactory(
                        RuntimeTypeAdapterFactory
                                .of(Element::class.java, "javaType")
                                .registerSubtype(MovieElement::class.java, "movie")
                                .registerSubtype(PreProgramElement::class.java, "element"))
                .create()
    }

    /**
     * Abfrage aller verfügbaren Filme
     */
    @CrossOrigin
    @GetMapping("/getMovies")
    fun greeting(): String {
        service.getMovies().execute().body()?.let {
            return gson.toJson(it.result.movies)
        }
        return ""
    }

    /**
     * Starten des Vorprogramms (in den meisten Fällen Musik)
     */
    @GetMapping("/startPreProgram")
    fun startCinemamode(@RequestParam(value = "movieId") movieId: Int) {
        //ToDo:create standard config
        startNextPhase()
    }

    /**
     * Starten des Vorprogramms mit eigener Konfiguration
     */
    @PostMapping("/startPreProgramWithConfig")
    fun startCinemamode(@RequestBody config: String) {
        this.config.clear()
        this.currentPhase = null
        gson.fromJson(config, Config::class.java)
                ?.elements
                ?.reversed()
                ?.stream()
                ?.forEach { this.config.push(it) }
        startNextPhase()
    }

    @GetMapping("/startCinemamode")
    fun startCinemamode() {
        this.currentPhase?.counter = 0
        clearPlaylist()
    }

    private fun startNextPhase() {
        clearPlaylist()
        this.currentPhase = config.pop()
        this.currentPhase?.let {
            setVolume(it.volume)
            when (it) {
                is MovieElement -> startMovie(it.movieId)
                is PreProgramElement -> {
                    addAll(getRandomFiles(it.itemsPath, it.counter))
                    startPlaylist()
                }
            }
            determineAmbientLight(it.lightStatus)
        }
    }

    /**
     * called when notification via websocket is received
     */
    override fun onMessage(webSocket: WebSocket?, text: String?) {
        val req = gson.fromJson(text, Notification::class.java)
        if (req.method == "Player.OnStop" && !config.empty()) {
            this.currentPhase?.let {
                if (it.counter <= 1)
                    startNextPhase()
                else
                    it.counter -= 1
            }
        }
    }

    fun determineAmbientLight(lightStatus: Boolean) {
        Thread {
            Runtime.getRuntime()
                    .exec("$pathToPowerswitch $remoteHouseCode $remoteAddressLight ${if (lightStatus) 1 else 0}")
                    .waitFor(2, TimeUnit.SECONDS)
        }.start()
    }


    private fun clearPlaylist() {
        service.clear().execute()
    }

    private fun setVolume(percentage: Int) {
        service.jsonRPC(gson.toJson(Request(method = "Application.SetVolume", params = VolumeParam(percentage)))).execute()
    }

    private fun startPlaylist() {
        service.jsonRPC(gson.toJson(Request(method = "Player.Open", params = ItemParam(PlaylistItem(1))))).execute()
    }

    fun addAll(list: List<String>) {
        list.map {
            service.jsonRPC(gson.toJson(Request(params = PlayListParam(item = FileItem(it))))).execute()
        }
    }

    fun startMovie(movieId: Int) {
        service.jsonRPC(gson.toJson(Request(method = "Player.Open", params = ItemParam(item = MovieItem(movieId))))).execute()
    }

    fun getRandomFiles(path: String, amount: Int): List<String> {
        val files = File(basepath + path).listFiles().map { it.absolutePath }
        Collections.shuffle(files, Random(System.currentTimeMillis()))
        return files.take(amount)
    }
}



