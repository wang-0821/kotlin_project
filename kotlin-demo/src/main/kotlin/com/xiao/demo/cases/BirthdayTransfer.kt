package com.xiao.demo.cases

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader

/**
 *
 * @author lix wang
 */
object BirthdayTransfer {
    fun transfer(fileInputPath: String, fileOutputPath: String) {
        val reader = BufferedReader(InputStreamReader(FileInputStream(File(fileInputPath))))
        val outputFile = File(fileOutputPath)
        if (outputFile.exists()) {
            throw IllegalArgumentException("Already exists file with path: $fileOutputPath.")
        } else {
            outputFile.createNewFile()
        }
        val writer = BufferedWriter(FileWriter(outputFile))
        try {
            do {
                val line = reader.readLine()
                if (!line.isNullOrBlank()) {
                    doTransfer(line)
                        ?.also {
                            writer.write(it)
                        }
                }
            } while (!line.isNullOrBlank())
            writer.flush()
        } finally {
            reader.close()
            writer.close()
        }
    }

    private fun doTransfer(inputLine: String): String? {
        try {
            val separateList = inputLine.split("\t")
            return if (separateList.size < 2) {
                "$inputLine\r\n"
            } else {
                "${separateList[0]} ${separateList[1]} ${separateList[1].substring(10..13)}\r\n"
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Parse $inputLine failed.", e)
        }
    }
}

fun main() {
    BirthdayTransfer.transfer("dataset.txt", "result.txt")
}