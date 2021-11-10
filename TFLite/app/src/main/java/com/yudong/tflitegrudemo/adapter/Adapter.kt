package com.yudong.tflitegrudemo.adapter

import android.content.Context
import com.yudong.tflitegrudemo.adapter.utils.DataLoader
import com.yudong.tflitegrudemo.adapter.utils.ModelLoader
import java.io.IOException

open class Adapter(
    featsName: String,
    targetsName: String,
    masksName: String,
    modelName: String,
    batchNum: Int,
    batchSize: Int,
    innerUnits: Int,
    trainItems: Int
) {
    private val modelLoader: ModelLoader = ModelLoader(modelName, batchSize, innerUnits, trainItems)
    private val dataLoader: DataLoader = DataLoader(featsName, targetsName, masksName, batchNum, batchSize)
    var totalBatch = 0
        private set
    var currentBatch = 0
        private set
    var metricValue = 0f
        private set
    var metricName = "N/A"
        private set
    var isLoad: Boolean = false
        private set

    @Throws(IOException::class)
    fun load(context: Context) {
        modelLoader.loadInterpreter(context)
        dataLoader.load(context)
        isLoad = true
    }

    private fun nextBatch() {
        currentBatch ++
        if (currentBatch == dataLoader.batchNum) currentBatch = 0
    }

    fun train(batchCount: Int = 1) {
        assert(batchCount > 0)
        metricName = "Loss"
        for (ii in 0 until batchCount) {
            val data = dataLoader.getBatch(currentBatch)
            val feat = data["feat"]
            val target = data["target"]
            val mask = data["mask"]

            metricValue = modelLoader.trainOnBatch(feat!!, target!!, mask!!)
            nextBatch()
        }
    }

    fun eval() {
        val data = dataLoader.getBatch(currentBatch)
        val feat = data["feat"]
        val target = data["target"]
        val mask = data["mask"]
        metricValue = modelLoader.evalOnBatch(feat!!, target!!, mask!!)
        metricName = "recall"
        nextBatch()
    }

    fun infer(): List<Int?> {
        val data = dataLoader.getBatch(currentBatch)
        val feat = data["feat"]
        val mask = data["mask"]
        val prediction = modelLoader.inferOnBatch(feat!!, mask!!)
        return prediction.map { x -> x.indices.maxByOrNull { x[it] } }
    }
}