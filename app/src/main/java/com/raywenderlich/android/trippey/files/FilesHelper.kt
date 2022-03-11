package com.raywenderlich.android.trippey.files

import java.io.File

interface FilesHelper {
    fun getData(): List<File>
    fun saveData(fileName: String, data: String)
    fun deleteData(fileName: String)
}