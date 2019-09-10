package com.android.deskclock.addeditpage.selectaudio

import android.app.IntentService
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Runnable
import java.io.File
import java.io.FileFilter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors

class ScanAudioFileService : IntentService {

    constructor() : this("default")

    constructor(serviceName: String) : super(serviceName)

    private val packageNameFilter = FileFilter {
        !it.name.startsWith(".") && (it.isDirectory)
    }

    private val fileNameFilter = FileFilter {
        !it.name.startsWith(".") && !it.isDirectory && (it.name.endsWith(
            ".mp3",
            true
        ) || it.name.endsWith(".ogg", true)
                )
    }


    private val mWaitForScanDir = ConcurrentLinkedQueue<File>()
    private val fixedThreadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (fixedThreadPool.isShutdown) {
            if (mWaitForScanDir.size != 0) {
                mWaitForScanDir.clear()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        val path = intent?.getStringExtra("filePath")
        if (path != null && path != "") {
            Log.d("ScanAudioFileService", "path= $path")
            startScan(path)
        } else {
            throw IllegalArgumentException()
        }
    }


    private fun startScan(startPath: String) {
        val file = File(startPath)
        Log.d("ScanAudioFileService", "files = ${file.list()}")
        require(file.exists() || file.isDirectory) { "路径必须为文件夹" }
        val dirs = file.listFiles(packageNameFilter)
        val musicFiles = file.listFiles(fileNameFilter)

        var musicFileList: List<File>? = null
        if (musicFiles != null && musicFiles.isNotEmpty()) {
            musicFileList = ArrayList()
            (musicFileList as ArrayList).add(file)
            for (mf in musicFiles) {
                (musicFileList as ArrayList).add(mf)
            }
        }

        if (musicFileList != null) {
            sendResultToView(musicFileList)
        }

        if (dirs != null && dirs.isNotEmpty()) {
            startScanInternal(dirs)
        }
    }

    private fun startScanInternal(dirs: Array<File>) {
        val runnables = java.util.ArrayList<Runnable>()

        for (f in dirs) {
            mWaitForScanDir.offer(f)
            runnables.add(Runnable {
                scanFileInThread()
            })
        }

        for (r in runnables) {
            fixedThreadPool.submit(r)
        }

        fixedThreadPool.shutdown()
    }

    private fun scanFileInThread() {
        while (!mWaitForScanDir.isEmpty()) {
            val f = mWaitForScanDir.poll()
            if (f != null) {
                val dir = f.listFiles(packageNameFilter)
                val musicFiles = f.listFiles(fileNameFilter)

                for (innerFile in dir) {
                    mWaitForScanDir.offer(innerFile)
                }
                var musicFileList: List<File>? = null
                if (musicFiles.isNotEmpty()) {
                    musicFileList = ArrayList()
                    (musicFileList as ArrayList).add(f)
                    for (mf in musicFiles) {
                        (musicFileList as ArrayList).add(mf)
                    }
                }
                if (musicFileList != null) {
                    sendResultToView(musicFileList)
                }
            }
        }

    }

    private fun sendResultToView(resultList: List<File>) {
        val mainHandler = SelectAudioFragment.MyHandler(mainLooper)
        val m = mainHandler.obtainMessage()
        m.what = SelectAudioFragment.CODE_RECEIVED
        m.obj = resultList
        mainHandler.sendMessage(m)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!fixedThreadPool.isShutdown || !fixedThreadPool.isTerminated) {
            fixedThreadPool.shutdownNow()
        }
    }
}