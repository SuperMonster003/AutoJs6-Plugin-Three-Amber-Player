package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ItemVideoQueueBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.VideoThemePalette
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.VideoThemePaletteGenerator

internal data class VideoQueueRow(
    val index: Int,
    val title: String,
    val durationLabel: String,
    val current: Boolean,
)

internal class VideoQueueAdapter(
    private val palette: VideoThemePalette,
    private val onSelect: (Int) -> Unit,
) : ListAdapter<VideoQueueRow, VideoQueueAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemVideoQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemVideoQueueBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: VideoQueueRow) {
            val background = if (row.current) palette.primaryContainer else palette.surfaceContainerLow
            val foreground = if (row.current) palette.onPrimaryContainer else palette.onSurface
            binding.root.background = RippleDrawable(
                ColorStateList.valueOf(
                    VideoThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
                ),
                background.toDrawable(),
                null,
            )
            binding.currentIndicator.isVisible = row.current
            ImageViewCompat.setImageTintList(
                binding.currentIndicator,
                ColorStateList.valueOf(foreground),
            )
            binding.videoTitle.text = row.title
            binding.videoTitle.setTextColor(foreground)
            binding.videoDetails.text = binding.root.context.getString(
                R.string.queue_item_details,
                row.index + 1,
                row.durationLabel,
            )
            binding.videoDetails.setTextColor(
                if (row.current) {
                    VideoThemePaletteGenerator.withAlpha(foreground, SECONDARY_TEXT_ALPHA)
                } else {
                    palette.onSurfaceVariant
                },
            )
            binding.root.isActivated = row.current
            binding.root.contentDescription = if (row.current) {
                binding.root.context.getString(R.string.queue_current_item_description, row.title)
            } else {
                row.title
            }
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let(onSelect)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<VideoQueueRow>() {
        override fun areItemsTheSame(oldItem: VideoQueueRow, newItem: VideoQueueRow): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: VideoQueueRow, newItem: VideoQueueRow): Boolean =
            oldItem == newItem
    }

    private companion object {
        const val RIPPLE_ALPHA = 0x24
        const val SECONDARY_TEXT_ALPHA = 0xCC
    }
}
