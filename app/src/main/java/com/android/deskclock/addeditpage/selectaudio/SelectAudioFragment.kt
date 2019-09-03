package com.android.deskclock.addeditpage.selectaudio

import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.R
import com.android.deskclock.customview.UselessToolbar
import java.io.File

class SelectAudioFragment(private val lastSelectedFile: File) : Fragment() {
    companion object {
        const val CODE_RECEIVED = 101
        private lateinit var onAudioFileSelectedCallback: (File) -> Unit
        private lateinit var instance: SelectAudioFragment
        private lateinit var hasSelectedFile: File
        private lateinit var mManager: FragmentManager

        fun setUpFragment(
            manager: FragmentManager, container: Int, selectedFile: File,
            callback: (File) -> Unit
        ) {
            mManager = manager
            hasSelectedFile = selectedFile
            onAudioFileSelectedCallback = callback
            instance = SelectAudioFragment(hasSelectedFile)
            manager.beginTransaction().add(container, instance).commit()
        }

        fun hideSelf(resultFile: File) {
            if (resultFile != hasSelectedFile) {
                onAudioFileSelectedCallback(resultFile)
            }
            mManager.beginTransaction().remove(instance).commit()
        }


    }

    private lateinit var audioAdapter: AudioListAdapter
    private var selectResult = lastSelectedFile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_select_alarm_audio, container, false)
        initViewAndListener(rootView)
        return rootView
    }

    private fun initViewAndListener(rootView: View) {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.audio_list)
        audioAdapter = AudioListAdapter(context!!,lastSelectedFile)

        audioAdapter.setOnMusicFileSelectCallback {
            selectResult = it
        }

        recyclerView.apply {
            adapter = audioAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }

        rootView.findViewById<UselessToolbar>(R.id.toolbar).setOnLeftItemClickListener {
            hideSelf(selectResult)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                context!!,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE"
                ),
                101
            )
        } else {
            val intent = Intent(context, ScanAudioFileService::class.java)
            intent.putExtra("filePath", Environment.getExternalStorageDirectory().absolutePath)
            activity?.startService(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        audioAdapter.releaseMedia()
    }

    override fun onDestroy() {
        val intent = Intent(context, ScanAudioFileService::class.java)
        context?.stopService(intent)
        super.onDestroy()
    }

    private fun addToList(list: ArrayList<File>) {
        audioAdapter.addNewScannedFile(list)
    }

    class MyHandler : Handler {

        constructor() : super()

        constructor(looper: Looper) : super(looper)

        override fun handleMessage(msg: Message?) {
            if (msg?.what == CODE_RECEIVED) {
                instance.addToList(msg.obj as ArrayList<File>)
            }
        }
    }
}