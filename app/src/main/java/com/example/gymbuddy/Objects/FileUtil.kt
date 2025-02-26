package com.example.gymbuddy.Objects

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {
    fun from(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = createTemporalFileFrom(inputStream)
        inputStream?.close()
        return file
    }

    private fun createTemporalFileFrom(inputStream: InputStream?): File {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = File.createTempFile("tempImage", ".jpg")
            val outputStream = FileOutputStream(targetFile)
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            outputStream.close()
        }
        return targetFile!!
    }
}