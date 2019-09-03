package com.android.deskclock.util

import android.content.res.Resources
import java.io.File
import java.io.FileOutputStream

object CopyRawToData {
    fun copyRawFile2Data(
        rawId: Int,
        dataDir: File,
        fileName: String,
        resources: Resources
    ) {
        Thread {
            val file = File("${dataDir.path}/$fileName")
            if (!file.exists()) {
                try {
                    val inputStream = resources.openRawResource(rawId)
                    val fileOutputStream = FileOutputStream(file)
                    val buf = ByteArray(1024)
                    var length = inputStream.read(buf)
                    while (length != -1) {
                        fileOutputStream.write(buf, 0, length)
                        length = inputStream.read(buf)
                    }
                    fileOutputStream.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}