package me.kdv.noadsradio.presentation.ui.station

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.MoreExecutors
import com.intas.metrolog.presentation.ui.events.adapter.StationGroupListAdapter
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
    private var stationGroupListAdapter: StationGroupListAdapter? = null

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

        setupStationRecyclerView()
        setupStationGroupRecyclerView()
        loadStationGroups()
        loadStations()

        /* Creating session token (links our UI with service and starts it) */
        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, MusicPlayerService::class.java))

        /* Instantiating our MediaController and linking it to the service using the session token */
        val mediacontrollerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        mediacontrollerFuture.addListener({
            player = mediacontrollerFuture.get()
            /*val uri = Uri.parse("https://listen2.myradio24.com/8226")

            loadMediaItem(uri)*/

        }, MoreExecutors.directExecutor())

        viewModel.getInfo()
        viewModel.setIsCurrent(1)
        binding.stationInfoMaterialCardView.visibility = View.GONE

        viewModel.resetAllStations()

        binding.stationControlImageView.setOnClickListener {
            currentStation?.let {
                if (it.state == StationState.PLAYING) {
                    stopPlaying()
                }
            }
        }
    }

    private fun loadStationGroups() {
        viewModel.stationGroups.observe(this, {
            stationGroupListAdapter?.submitList(null)
            stationGroupListAdapter?.submitList(it)
        })
    }

    private fun loadStations() {
        viewModel.stations.observe(this, {
            stationListAdapter?.submitList(it)
            currentStationList = it.toMutableList()
        })
    }

    fun setupStationRecyclerView() {

        val stationRecyclerView = binding.stationRecyclerView
        stationListAdapter = StationListAdapter()
        stationRecyclerView?.let {
            with(it) {
                adapter = stationListAdapter
                itemAnimator = null
                recycledViewPool.setMaxRecycledViews(0, StationListAdapter.MAX_POOL_SIZE)
            }
        }

        setupStationRVClickListener()
        setupStationRVScrollListener()
    }

    fun setupStationGroupRecyclerView() {

        val stationGroupRecyclerView = binding.stationGroupRecyclerView
        stationGroupListAdapter = StationGroupListAdapter()
        stationGroupRecyclerView?.let {
            with(it) {
                adapter = stationGroupListAdapter
                itemAnimator = null
                recycledViewPool.setMaxRecycledViews(0, StationGroupListAdapter.MAX_POOL_SIZE)
            }
        }
        setupStationGroupRVClickListener()
    }

    private fun setupStationGroupRVClickListener() {
        stationGroupListAdapter?.onStationGroupClickListener = { stationGroup, position ->

            viewModel.setIsCurrent(stationGroup.id)

            currentStationList?.let { csl ->
                for ((index, value) in csl.withIndex()) {
                    if (value.groupId == stationGroup.id) {
                        binding.stationRecyclerView.smoothScrollToPosition(index)
                        break
                    }
                }
            }
        }
    }

    private fun setupStationRVScrollListener() {
        binding.stationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val position = recyclerView.getCurrentPosition()

                currentStationList?.let {
                    val station = it[position]
                    viewModel.setIsCurrent(station.groupId)
                }

            }
        })
    }

    fun RecyclerView?.getCurrentPosition(): Int {
        return (this?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    }

    private fun setupStationRVClickListener() {

        stationListAdapter?.onStationClickListener = { station, position ->

            station.position = position
            prevStation = currentStation

            when (station.state) {
                StationState.PLAYING -> {
                    setStationState(currentStation, StationState.STOPPED)
                    currentStation = null
                    stopPlaying()
                }
                StationState.LOADED -> {
                }
                StationState.STOPPED -> {
                    setStationState(currentStation, StationState.PLAYING)
                    setStationState(prevStation, StationState.STOPPED)

                    currentStation = station
                    loadMediaItem(Uri.parse(station.url))
                }
            }
        }
    }

    fun loadMediaItem(uri: Uri) {
        player?.let { player ->
            /* We use setMediaId as a unique identifier for the media (which is needed for mediasession and we do NOT use setUri because we're gonna do
               something like setUri(mediaItem.mediaId) when we need to load the media like we did above in the MusicPlayerService and more precisely when we were building the session */
            val newItem =
                MediaItem.Builder().setMediaId("$uri") /* setMediaId and NOT setUri */.build()

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

                    binding.stationInfoMaterialCardView.visibility = View.VISIBLE
                    binding.stationTitleTextView.text = currentStation?.name
                    binding.songTitleTextView.text = mediaMetadata.title
                    Glide.with(applicationContext).load(R.drawable.outline_stop_circle_48).into(binding.stationControlImageView)
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
                            setStationState(currentStation, StationState.PLAYING)
                        }
                        Player.STATE_ENDED -> {
                            //your logic
                            Log.d("NoAdsPlayerPlaybackState", "STATE_ENDED")
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_BUFFERING")
                            //showProgressBar()
                            setStationState(currentStation, StationState.LOADED)
                        }
                        Player.STATE_IDLE -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_IDLE")
                            //hideProgressBar()
                            stopPlaying()
                            setStationState(currentStation, StationState.STOPPED)

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
        Glide.with(applicationContext).load(R.drawable.baseline_play_circle_outline_48).into(binding.stationControlImageView)
        binding.stationInfoMaterialCardView.visibility = View.GONE
    }

    private fun setStationState(station: Station?, state: StationState) {
        station?.let {
            viewModel.changeStationState(it, state)
        }
    }

    private fun resetAllStations() {
        viewModel.resetAllStations()
    }
}