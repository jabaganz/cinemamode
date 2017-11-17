package kino.malve.Api

import com.google.gson.GsonBuilder
import entities.cinemamode.Request
import entities.cinemamode.playlist.ItemParam
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
open class controller : WebSocketListener() {

    private val gson = GsonBuilder().create()
    private val service = ApiUtils.soService
    //val basepath = "/media/A6BC83C0BC838A0F/pojos/"
    private val basepath = "D:/TestCinemamode/"
    private lateinit var currentPhase: Element
    private var movieId = 0
    val config: Stack<Element>


    init {
        OkHttpClient().newWebSocket(okhttp3.Request.Builder().url("ws://localhost:9090/jsonrpc").build(), this)

        config = Stack()
        config.addAll(listOf(
                MyElement("movie", 99, "", 80),
                MyElement("dolby", 1, "dolby", 100),
                MyElement("trailer ", 2, "trailer", 80),
                MyElement("advertising", 2, "advertising", 80),
                MyElement("music", 99, "music", 70)))
    }


    @GetMapping("/getMovies")
    open fun greeting(): String {
        val result = service.getMovies().execute().body()
        if (result != null) {
            return gson.toJson(result.result.movies)
        }
        return ""
    }

    @GetMapping("/startPreProgram")
    fun startCinemamode(@RequestParam(value = "movieId") movieId: Int) {
        this.movieId = movieId
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

        val localCurrenPhase = currentPhase

        when (localCurrenPhase) {
            is MovieElement -> startMovie(localCurrenPhase.movieId)
            is MyElement -> {
                addAll(getRandomFiles(localCurrenPhase.itemsPath, localCurrenPhase.counter))
                startPlaylist()
            }
        }
    }


    private fun clearPlaylist() {
        service.clear().execute()
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        val req = GsonBuilder().create().fromJson(text, Notification::class.java)
        println(currentPhase)
        if (req.method == "Player.OnStop" && !config.empty()) {
            if (currentPhase.counter <= 1) {
                startNextPhase()
            } else {
                currentPhase.counter -= 1
            }

        }
    }


    private fun setVolume(percentage: Int) {
        service.jsonRPC(gson.toJson(Request(method = "Application.SetVolume", params = VolumeParam(percentage)))).execute()
    }

    private fun startPlaylist() {
        service.jsonRPC(gson.toJson(Request(method = "Player.Open", params = ItemParam(PlaylistItem(1))))).execute()
    }

    fun addAll(list: List<String>) {
        list.map {
            service.jsonRPC(gson.toJson(Request(params = PlayListParam(item = FileItem(it))))).execute()¡
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



