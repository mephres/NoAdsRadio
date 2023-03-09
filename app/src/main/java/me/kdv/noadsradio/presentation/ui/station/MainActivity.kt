package me.kdv.noadsradio.presentation.ui.station

import android.content.ComponentName
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.common.util.concurrent.MoreExecutors
import com.intas.metrolog.presentation.ui.events.adapter.StationListAdapter
import dagger.hilt.android.AndroidEntryPoint
import me.kdv.noadsradio.R
import me.kdv.noadsradio.databinding.ActivityMainBinding
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.model.StationState
import me.kdv.noadsradio.presentation.MusicPlayerService


@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var stationListAdapter: StationListAdapter? = null
    private var currentStationList: MutableList<Station>? = null
    private val viewModel by viewModels<MainViewModel>()

    private var playWhenReady = true
    private var currentStation: Station? = null
    private var prevStation: Station? = null
    private var playbackPosition = 0L

    /* This is the global variable of the player
       (which is basically a media controller)
       you're going to use to control playback,
       you're not gonna need anything else other than this,
       which is created from the media controller */
    private var player: Player? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerView()
        loadStationGroups()
        loadStations()

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


        /*binding.btGetInfo.setOnClickListener({
            getMediaItem()
        })

        binding.btStop.setOnClickListener {
            player.playWhenReady = false
            player.stop()
        }

        binding.btNext.setOnClickListener {
            //val uri = Uri.parse("https://radio.sweetmelodies.tk:8000/stream1")
            val uri = Uri.parse("https://jfm1.hostingradio.ru:14536/rcstream.mp3")

            loadMediaItem(uri)
        }*/

        viewModel.getInfo()
    }

    private fun loadStationGroups() {
        viewModel.stationGroups.observe(this, { stationGroupList ->
            stationGroupList.forEach {
                addChip(this, binding.chipGroup, it.description, it.id)
            }
        })
    }

    private fun loadStations() {
        viewModel.stations.observe(this, {
            stationListAdapter?.submitList(it)
            currentStationList = it.toMutableList()
        })
    }

    fun setupRecyclerView() {

        val stationRecyclerView = binding.stationRecyclerView
        stationListAdapter = StationListAdapter()
        stationRecyclerView?.let {
            with(it) {
                adapter = stationListAdapter
                itemAnimator = null
                recycledViewPool.setMaxRecycledViews(0, StationListAdapter.MAX_POOL_SIZE)
            }
        }

        setupClickListener()
        //onScrollListener()
    }

    private fun setupClickListener() {

        stationListAdapter?.onStationClickListener = { station, position ->

            station.position = position
            prevStation = currentStation

            when(station.state) {
                StationState.PLAYING -> {
                    setStationState(currentStation,StationState.STOPPED)
                    //station.state = StationState.STOPPED

                    currentStation = null
                    stopPlaying()
                }
                StationState.LOADED -> {
                }
                StationState.STOPPED -> {
                    //station.state = StationState.PLAYING
                    setStationState(currentStation,StationState.PLAYING)
                    setStationState(prevStation,StationState.STOPPED)

                    //stopPrevStation()
                    currentStation = station
                    loadMediaItem(Uri.parse(station.url))
                }
            }
            /*
            if (station.state.equals(StationState.STOPPED)) {
                station.state = StationState.PLAYING

                stopPrevStation()
                currentStation = station
                loadMediaItem(Uri.parse(station.url))
            } else if (station.state.equals(StationState.PLAYING)) {
                station.state = StationState.STOPPED

                currentStation = null
                stopPlaying()
            }*/

            /*currentStationList?.set(position, station)
            stationListAdapter?.submitList(currentStationList)
            stationListAdapter?.notifyItemChanged(position)*/
        }
    }

    private fun stopPrevStation() {
        prevStation?.let {
            it.state = StationState.STOPPED
            currentStationList?.set(it.position, it)
            stationListAdapter?.notifyItemChanged(it.position)
        }

    }

    private fun addChip(
        context: Context,
        chipGroup: ChipGroup,
        chipText: String,
        chipTag: Int
    ): Chip {
        val chip = Chip(context)
        chip.isCheckable = false
        chip.text = chipText
        chip.tag = chipTag
        chip.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary))
        chip.setTextColor(ContextCompat.getColor(context, R.color.md_white))
        chip.checkedIconTint =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_white))
        chip.isChecked = false
        chip.chipCornerRadius = 4F
        chip.isClickable = true
        chip.chipStrokeWidth = 2F
        chip.chipStrokeColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_black))
        chipGroup.addView(chip)

        chip.setOnClickListener {
            val a = it.tag as Int
            val b = 1
        }

        return chip
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

            player.addListener(object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    Log.d("NoAdsPlayer", "mediaMetadata.title: ${mediaMetadata.title}")
                    Log.d("NoAdsPlayer", "mediaMetadata.artist: ${mediaMetadata.artist}")
                    Log.d("NoAdsPlayer", "mediaMetadata.albumArtist: ${mediaMetadata.albumArtist}")
                    Log.d("NoAdsPlayer", "mediaMetadata.albumTitle: ${mediaMetadata.albumTitle}")
                    Log.d("NoAdsPlayer", "mediaMetadata.genre: ${mediaMetadata.genre}")
                    Log.d("NoAdsPlayer", "mediaMetadata.station: ${mediaMetadata.station}")
                    Log.d("NoAdsPlayer", "mediaMetadata.subtitle: ${mediaMetadata.subtitle}")
                }

                override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onPlaylistMetadataChanged(mediaMetadata)
                    val a = 1
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) { // check player play back state
                        Player.STATE_READY -> {
                            //changeStationState(StationState.PLAYING)
                            Log.d("NoAdsPlayerPlaybackState", "STATE_READY")
                            //hideProgressBar()
                            setStationState(currentStation,StationState.PLAYING)
                        }
                        Player.STATE_ENDED -> {
                            //your logic
                            Log.d("NoAdsPlayerPlaybackState", "STATE_ENDED")
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_BUFFERING")
                            //showProgressBar()
                            setStationState(currentStation,StationState.LOADED)
                        }
                        Player.STATE_IDLE -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_IDLE")
                            //hideProgressBar()
                            stopPlaying()
                            setStationState(currentStation,StationState.STOPPED)

                            //setStationState(StationState.STOPPED)
                        }
                        else -> {
                            //playerView.hideController()
                        }
                    }
                }
            })
        }
    }

    private fun stopPlaying() {
        player?.let { exoPlayer ->
            exoPlayer.playWhenReady = false
            exoPlayer.stop()
        }
    }

    private fun showProgressBar() {
        currentStation?.let {
            it.state = StationState.LOADED
            currentStationList?.set(it.position, it)
            stationListAdapter?.submitList(currentStationList)
            stationListAdapter?.notifyItemChanged(it.position)
        }

    }

    private fun hideProgressBar() {
        currentStation?.let {
            it.state = StationState.PLAYING
            currentStationList?.set(it.position, it)
            stationListAdapter?.submitList(currentStationList)
            stationListAdapter?.notifyItemChanged(it.position)
        }
    }

    private fun setStationState(station: Station?, state: StationState) {
        station?.let {
            viewModel.changeStationState(it,state)
        }
    }
}