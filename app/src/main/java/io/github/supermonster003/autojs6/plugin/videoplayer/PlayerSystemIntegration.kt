package io.github.supermonster003.autojs6.plugin.videoplayer

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import java.util.concurrent.atomic.AtomicInteger

/** Activity-scoped MediaSession plus MediaStyle notification/system media controls. */
@androidx.annotation.OptIn(UnstableApi::class)
internal class PlayerSystemIntegration(
    private val activity: AppCompatActivity,
) {

    private val instanceId = nextInstanceId.getAndIncrement()
    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null

    fun attach(player: ExoPlayer) {
        detach()
        val sessionActivity = sessionActivityPendingIntent()
        val session = MediaSession.Builder(activity, player)
            .setId("$SESSION_ID_PREFIX$instanceId")
            .setSessionActivity(sessionActivity)
            .build()
        mediaSession = session

        val manager = notificationManager ?: createNotificationManager(sessionActivity).also {
            notificationManager = it
        }
        manager.setMediaSessionToken(session.platformToken)
        manager.setPlayer(player)
    }

    fun detach() {
        notificationManager?.setPlayer(null)
        mediaSession?.release()
        mediaSession = null
    }

    private fun createNotificationManager(
        sessionActivity: PendingIntent,
    ): PlayerNotificationManager = PlayerNotificationManager.Builder(
        activity.applicationContext,
        instanceId,
        NOTIFICATION_CHANNEL_ID,
    )
        .setChannelNameResourceId(R.string.media_notification_channel_name)
        .setChannelDescriptionResourceId(R.string.media_notification_channel_description)
        .setSmallIconResourceId(R.drawable.ic_notification_video)
        .setMediaDescriptionAdapter(
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence =
                    player.currentMediaItem?.mediaMetadata?.displayTitle
                        ?: player.currentMediaItem?.mediaMetadata?.title
                        ?: activity.getString(R.string.app_name)

                override fun createCurrentContentIntent(player: Player): PendingIntent = sessionActivity

                override fun getCurrentContentText(player: Player): CharSequence =
                    activity.getString(R.string.app_name)

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback,
                ): Bitmap? = null
            },
        )
        .build()
        .apply {
            setUsePreviousAction(true)
            setUseNextAction(true)
            setUseRewindAction(true)
            setUseFastForwardAction(true)
            setUseRewindActionInCompactView(true)
            setUseFastForwardActionInCompactView(true)
            setUseStopAction(false)
        }

    private fun sessionActivityPendingIntent(): PendingIntent {
        val target = Intent(activity.intent).apply {
            setClass(activity, VideoPlayerActivity::class.java)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        return PendingIntent.getActivity(
            activity,
            instanceId,
            target,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val SESSION_ID_PREFIX = "video-player-"
        const val NOTIFICATION_CHANNEL_ID = "video_playback"
        val nextInstanceId = AtomicInteger(6_003)
    }
}
