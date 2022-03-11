package com.raywenderlich.android.trippey.files

import java.io.File
import java.io.FileOutputStream

class FilesHelperImpl(
    private val directory: File
) : FilesHelper {
    override fun getData(): List<File> {
        TODO("Not yet implemented")
    }

    override fun saveData(fileName: String, data: String) {
        val file = buildFile(fileName)
        val fileOutputStream = buildOutputStream(file)

        fileOutputStream.use {
            it.runCatching {
                write(data.toByteArray())
            }
        }
    }

    override fun deleteData(fileName: String) {
        TODO("Not yet implemented")
    }

    private fun buildFile(fileName: String): File {
        return File(directory, fileName)
    }

    private fun buildOutputStream(file: File): FileOutputStream {
        return FileOutputStream(file)
    }
}