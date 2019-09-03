package com.android.deskclock.addeditpage.selectaudio

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.R
import java.io.File

class AudioListAdapter(private val context: Context, private val selectedFile: File) :
    RecyclerView.Adapter<AudioListAdapter.AudioViewHolder>() {

    private val typeDir = 1
    private val typeMusic = 2

    private val fileList = ArrayList<File>()

    private val mediaPlayer = MediaPlayer()

    private lateinit var selectedHolder: AudioViewHolder

    private var onMusicFileSelectedListener: (File) -> Unit = {

    }

    init {
        val file = File("${context.dataDir}/马林巴琴.mp3")
        if (file.name != selectedFile.name) {
            fileList.add(file)
        }
        if (selectedFile.exists()) {
            fileList.add(selectedFile)
        }
    }

    fun setOnMusicFileSelectCallback(callback: (File) -> Unit) {
        onMusicFileSelectedListener = callback
    }

    fun addNewScannedFile(files: ArrayList<File>) {
        for (i in files) {
            if (i.name == selectedFile.name) {
                files.remove(i)
                break
            }
        }
        val startIndex = fileList.size
        fileList.addAll(files)
        notifyItemRangeInserted(startIndex, files.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        return if (viewType == typeDir) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_package, parent, false)
            AudioViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_file_name, parent, false)
            AudioViewHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (fileList[position].isDirectory) {
            typeDir
        } else {
            typeMusic
        }
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val file = fileList[position]
        if (file.isDirectory) {
            holder.setDirItemText(file.name)
        } else {
            holder.setMusicItemText(file.name)
            holder.itemView.setOnClickListener {
                if (holder != selectedHolder) {
                    onMusicFileSelectedListener(file)
                    holder.setItemSelected()
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                    try {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(holder.itemView.context, file.toUri())
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedHolder.setItemUnselected()
                    selectedHolder = holder
                }
            }
            if (file == selectedFile) {
                selectedHolder = holder
                selectedHolder.setItemSelected()
            }
        }
    }

    fun releaseMedia() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    class AudioViewHolder(audioView: View) : RecyclerView.ViewHolder(audioView) {
        fun setDirItemText(packageName: String) {
            itemView.findViewById<TextView>(R.id.audio_package_name).text = packageName
        }

        fun setMusicItemText(musicFileName: String) {
            itemView.findViewById<TextView>(R.id.audio_file_name).text = musicFileName
        }

        fun setItemSelected() {
            itemView.findViewById<ImageView>(R.id.audio_selected).visibility = View.VISIBLE
        }

        fun setItemUnselected() {
            itemView.findViewById<ImageView>(R.id.audio_selected).visibility = View.GONE
        }
    }
}