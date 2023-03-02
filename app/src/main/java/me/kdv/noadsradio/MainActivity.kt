package me.kdv.noadsradio

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import me.kdv.noadsradio.databinding.ActivityMainBinding


@UnstableApi
class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    /* This is the global variable of the player
       (which is basically a media controller)
       you're going to use to control playback,
       you're not gonna need anything else other than this,
       which is created from the media controller */
    lateinit var player: Player

    var page = 0

    // url of video which we are loading.
    var videoURL =
        "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*ContextCompat.startForegroundService(
            this,
            PlayerService.newService(this)
        )*/

        /*val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            MyWorker.makeRequest(page++)
        )*/

        /* Creating session token (links our UI with service and starts it) */
        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, MusicPlayerService::class.java))

        /* Instantiating our MediaController and linking it to the service using the session token */
        val mediacontrollerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        mediacontrollerFuture.addListener({
            player = mediacontrollerFuture.get()
            val uri = Uri.parse("https://listen2.myradio24.com/8226")

            loadMediaItem(uri)



        }, MoreExecutors.directExecutor())


        binding.btGetInfo.setOnClickListener({
            getMediaItem()
        })

        binding.btStop.setOnClickListener {
            player.playWhenReady = false
            player.stop()
        }

        binding.btNext.setOnClickListener {
            val uri = Uri.parse("https://radio.sweetmelodies.tk:8000/stream1")

            loadMediaItem(uri)
        }

    }

    fun getMediaItem() {
        try {
            player?.let {
                val currentMediaItem = it.currentMediaItem
                val currentTracks = it.currentTracks
                val audioAttributes = it.audioAttributes

                val title = it.mediaMetadata.title
                val station = it.mediaMetadata.station
                val a = 1
            }
        } catch (e: Exception) {

        }



    }

    fun loadMediaItem(uri: Uri) {
        player?.let { player ->
            /* We use setMediaId as a unique identifier for the media (which is needed for mediasession and we do NOT use setUri because we're gonna do
               something like setUri(mediaItem.mediaId) when we need to load the media like we did above in the MusicPlayerService and more precisely when we were building the session */
            val newItem = MediaItem.Builder()
                .setMediaId("$uri") /* setMediaId and NOT setUri */
                .build()

            /* Load it into our activity's MediaController */
            player.setMediaItem(newItem)
            player.prepare()
            player.play()
        }

    }

    /*override fun onStart() {
        super.onStart()
        //initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        /*hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }*/
    }

    public override fun onPause() {
        super.onPause()
        /*if (Util.SDK_INT <= 23) {
            releasePlayer()
        }*/
    }


    public override fun onStop() {
        super.onStop()
        /*if (Util.SDK_INT > 23) {
            releasePlayer()
        }*/
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this) // <- context
            .build()
            .also {
                binding.playerView.player = it
            }

        // create a media item.
        //val uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
        //val uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        val uri = Uri.parse("https://listen2.myradio24.com/8226")
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)

        player?.playWhenReady = playWhenReady
        player?.seekTo(currentItem, playbackPosition)
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }*/
}