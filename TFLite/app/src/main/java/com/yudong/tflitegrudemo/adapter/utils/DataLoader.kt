package com.yudong.tflitegrudemo.adapter.utils

import android.content.Context
import java.io.FileInputStream
import java.io.IOException
import java.util.*

internal class DataLoader(
        private val featsName: String,
        private val targetsName: String,
        private val masksName: String,
        val batchNum: Int,
        val batchSize: Int,
        val loadTargets: Boolean = true
    ) {
    private var feats: Array<IntArray>? = null
    private var targets: Array<IntArray>? = null
    private var masks: Array<IntArray>? = null
    var isLoad = false
        private set

    @Throws(IOException::class)
    fun load(context: Context) {
        val assets = context.assets
        feats = Utils.read2DArray(assets.open(featsName), batchNum, batchSize)
        if (loadTargets) {
            targets = Utils.read2DArray(assets.open(targetsName), batchNum, batchSize)
        }
        masks = Utils.read2DArray(assets.open(masksName), batchNum, batchSize)
        isLoad = true
    }

    @Throws(IOException::class)
    fun loadAsPath() {
        feats = Utils.read2DArray(FileInputStream(featsName), batchNum, batchSize)
        if (loadTargets) {
            targets = Utils.read2DArray(FileInputStream(targetsName), batchNum, batchSize)
        }
        masks = Utils.read2DArray(FileInputStream(masksName), batchNum, batchSize)
        isLoad = true
    }

    fun getBatch(num: Int): Map<String, IntArray> {
        assert(isLoad)
        val result: MutableMap<String, IntArray> = HashMap()
        result["feat"] = feats!![num];
        if (loadTargets) {
            result["target"] = targets!![num]
        }
        result["mask"] = masks!![num]
        return result
    }
}