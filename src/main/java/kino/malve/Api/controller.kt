package kino.malve.Api

import com.google.gson.GsonBuilder
import entities.cinemamode.Request
import entities.cinemamode.playlist.ItemParam
import kino.malve.Api.pojo.Config
import kino.malve.Api.pojo.Element
import kino.malve.Api.pojo.MovieElement
import kino.malve.Api.pojo.MyElement
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.*

@RestController
class controller : WebSocketListener() {

    private val gson = GsonBuilder().create()
    private val service = ApiUtils.soService
    //val basepath = "/media/A6BC83C0BC838A0F/pojos/"
    private val basepath = "D:/TestCinemamode/"
    private lateinit var currentPhase: Element
    val config = Stack<Element>()


    /**
     * Websocket connection to Kodi for receiving notifications
     */
    init {
        OkHttpClient().newWebSocket(okhttp3.Request.Builder().url("ws://localhost:9090/jsonrpc").build(), this)
    }


    @GetMapping("/getMovies")
    fun greeting(): String {
        val result = service.getMovies().execute().body()
        if (result != null) {
            return gson.toJson(result.result.movies)
        }
        return ""
    }

    @GetMapping("/startPreProgram")
    fun startCinemamode(@RequestParam(value = "movieId") movieId: Int) {
        //create standard config
        startNextPhase()
    }

    @GetMapping("/startPreProgramWithConfig")
    fun startCinemamode(@RequestParam(value = "config") config: String) {
        gson.fromJson(config, Config::class.java)
        startNextPhase()
    }

    @GetMapping("/startCinemamode")
    fun startCinemamode() {
        currentPhase.counter = 0
        clearPlaylist()
    }


    fun startNextPhase() {
        currentPhase = config.pop()
        clearPlaylist()
        setVolume(currentPhase.volume)

        val localCurrenPhase = currentPhase //local variable necessary for smart casting

        when (localCurrenPhase) {
            is MovieElement -> startMovie(localCurrenPhase.movieId)
            is MyElement -> {
                addAll(getRandomFiles(localCurrenPhase.itemsPath, localCurrenPhase.counter))
                startPlaylist()
            }
        }
    }

    /**
     * called when notification via websocket is received
     */
    override fun onMessage(webSocket: WebSocket?, text: String?) {
        val req = gson.fromJson(text, Notification::class.java)
        if (req.method == "Player.OnStop" && !config.empty()) {
            if (currentPhase.counter <= 1)
                startNextPhase()
            else
                currentPhase.counter--

        }
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



