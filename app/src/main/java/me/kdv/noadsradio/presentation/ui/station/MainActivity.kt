package me.kdv.noadsradio.presentation.ui.station

import android.annotation.SuppressLint
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
import dagger.hilt.android.AndroidEntryPoint
import me.kdv.noadsradio.R
import me.kdv.noadsradio.databinding.ActivityMainBinding
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.model.StationState
import me.kdv.noadsradio.presentation.MusicPlayerService
import me.kdv.noadsradio.presentation.ui.station.adapter.station.StationListAdapter
import me.kdv.noadsradio.presentation.ui.station.adapter.station_group.StationGroupListAdapter


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

    private var currentStation: Station? = null
    private var prevStation: Station? = null

    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadStationList()

        getStationGroups()
        getStations()
        resetAllStations()

        initStationGroupRecyclerView()
        initStationRecyclerView()

        initMediaPlayer()

        initClickListeners()

        initObservers()
    }

    private fun initMediaPlayer() {
        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, MusicPlayerService::class.java))

        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()

            player?.currentMediaItem?.mediaId?.let {
                viewModel.setCurrentMediaId(it)
            }
        }, MoreExecutors.directExecutor())
    }

    private fun getStationGroups() {
        viewModel.stationGroups.observe(this, {
            stationGroupListAdapter?.submitList(null)
            stationGroupListAdapter?.submitList(it)
        })
    }

    private fun getStations() {
        viewModel.stations.observe(this, {
            stationListAdapter?.submitList(it)
            currentStationList = it.toMutableList()
        })

        viewModel.currentMediaId.observe(this, {
            setStationIsPlayingByMediaId(it)
        })
    }

    private fun initStationRecyclerView() {

        val stationRecyclerView = binding.stationRecyclerView
        stationListAdapter = StationListAdapter()
        stationRecyclerView.let {
            with(it) {
                adapter = stationListAdapter
                itemAnimator = null
                recycledViewPool.setMaxRecycledViews(0, StationListAdapter.MAX_POOL_SIZE)
            }
        }

        initStationRVClickListener()
        initStationRVScrollListener()
    }

    private fun initStationGroupRecyclerView() {

        val stationGroupRecyclerView = binding.stationGroupRecyclerView
        stationGroupListAdapter = StationGroupListAdapter()
        stationGroupRecyclerView.let {
            with(it) {
                adapter = stationGroupListAdapter
                itemAnimator = null
                recycledViewPool.setMaxRecycledViews(0, StationGroupListAdapter.MAX_POOL_SIZE)
            }
        }
        initStationGroupRVClickListener()
    }

    private fun initStationGroupRVClickListener() {
        stationGroupListAdapter?.onStationGroupClickListener = { stationGroup, position ->

            viewModel.setIsCurrentStationGroup(stationGroup.id)

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

    private fun initStationRVScrollListener() {
        binding.stationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val position = recyclerView.getCurrentPosition(dy)

                currentStationList?.let {
                    val station = it[position]
                    viewModel.setIsCurrentStationGroup(station.groupId)
                }
            }
        })
    }

    fun RecyclerView.getCurrentPosition(dy: Int): Int {
        val manager = this.layoutManager as LinearLayoutManager
        return if (dy > 0) {
            manager.findLastVisibleItemPosition()
        } else {
            manager.findFirstVisibleItemPosition()
        }
    }

    private fun initStationRVClickListener() {

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

                    setStationState(prevStation, StationState.STOPPED)

                    currentStation = station

                    setStationState(station, StationState.LOADED)
                    playStation(station)
                }
            }
        }
    }

    private fun playStation(station: Station) {

        player?.let { player ->

            val uri = Uri.parse(station.url)

            val newItem = MediaItem.Builder().setMediaId("$uri").build()

            player.setMediaItem(newItem)
            player.prepare()
            player.play()

            player.addListener(object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)

                    binding.stationInfoMaterialCardView.visibility = View.VISIBLE
                    binding.stationTitleTextView.text = currentStation?.name
                    binding.songTitleTextView.text = mediaMetadata.title
                    Glide.with(applicationContext).load(R.drawable.baseline_stop_48)
                        .into(binding.stationControlImageView)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) { // check player play back state
                        Player.STATE_READY -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_READY")
                            setStationState(currentStation, StationState.PLAYING)
                        }
                        Player.STATE_ENDED -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_ENDED")
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_BUFFERING")
                            setStationState(currentStation, StationState.LOADED)
                        }
                        Player.STATE_IDLE -> {
                            Log.d("NoAdsPlayerPlaybackState", "STATE_IDLE")
                            stopPlaying()
                        }
                    }
                }
            })
        }
    }

    private fun stopPlaying() {
        player?.let { player ->
            player.playWhenReady = false
            player.stop()
        }

        binding.stationInfoMaterialCardView.visibility = View.GONE

        setStationState(currentStation, StationState.STOPPED)
    }

    private fun setStationState(station: Station?, state: StationState) {
        station?.let {
            viewModel.changeStationState(it, state)
        }
    }

    private fun resetAllStations() {
        viewModel.resetAllStations()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setStationIsPlayingByMediaId(mediaId: String) {

        currentStationList?.forEach {

            if (it.url.equals(mediaId)) {
                setStationState(it, StationState.PLAYING)
                currentStation = it
            }
        }
        stationListAdapter?.notifyDataSetChanged()
    }

    private fun loadStationList() {
        viewModel.loadStationList()
    }

    private fun initClickListeners() {
        binding.stationControlImageView.setOnClickListener {
            currentStation?.let {
                if (it.state == StationState.PLAYING) {
                    stopPlaying()
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.currentMediaId.observe(this, {
            viewModel.getCurrentPlayingStation(it).observe(this, {
                runOnUiThread {
                    binding.stationInfoMaterialCardView.visibility = View.VISIBLE
                    binding.stationTitleTextView.text = it.name
                    Glide.with(applicationContext).load(R.drawable.baseline_stop_48)
                        .into(binding.stationControlImageView)
                }
            })
        })
    }
}