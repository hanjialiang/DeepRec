package com.yudong.tflitegrudemo.adapter.utils

import android.content.Context
import kotlin.Throws
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.HashMap

internal class ModelLoader(
    private var modelName: String,
    private val batchSize: Int,
    private val innerUnits: Int,
    private val trainItems: Int
) {
    private var interpreter: Interpreter? = null
    private var lastState = Array(batchSize) { FloatArray(innerUnits) }
    var isLoad: Boolean = false
        private set

    @Throws(IOException::class)
    fun loadInterpreter(context: Context): Int {
        val assets = context.assets
        val buffer: ByteBuffer = Utils.giveMeByteBuffer(assets.open(modelName))
        interpreter = Interpreter(buffer)
        isLoad = true
        return 0
    }

    @Throws(IOException::class)
    fun loadInterpreter(): Int {
        val buffer: ByteBuffer = Utils.giveMeByteBuffer(FileInputStream(modelName))
        interpreter = Interpreter(buffer)
        isLoad = true
        return 0
    }

    private fun trainOnBatch(
        feat: IntArray,
        target: IntArray,
        initialState: Array<FloatArray>
    ): Float {
        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: MutableMap<String, Any> = HashMap()
        inputs["feat"] = feat
        inputs["target"] = target
        inputs["initstate"] = initialState
        val loss = FloatBuffer.allocate(1)
        val state = Array(batchSize) { FloatArray(innerUnits) }
        outputs["loss"] = loss
        outputs["state"] = state
        interpreter?.runSignature(inputs, outputs, "train")
        lastState = state
        return loss.get(0)
    }

    private fun inferOnBatch(feat: IntArray, initialState: Array<FloatArray>): Array<FloatArray> {
        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: MutableMap<String, Any> = HashMap()
        inputs["feat"] = feat
        inputs["initstate"] = initialState
        val state = Array(batchSize) { FloatArray(innerUnits) }
        val predict = Array(batchSize) { FloatArray(trainItems) }
        outputs["state"] = state
        outputs["output"] = predict
        interpreter?.runSignature(inputs, outputs, "infer")
        lastState = state
        return predict
    }

    fun inferOnBatch(feat: IntArray, mask: IntArray): Array<FloatArray> {
        resetLastState(mask)
        return inferOnBatch(feat, lastState)
    }


    fun evalOnBatch(feat: IntArray, target: IntArray, mask: IntArray, recall_k: Int = 20): Float {
        val pred = inferOnBatch(feat, mask)
        var rec_sum = 0f
        for (row_idx in 0 until batchSize) {
            val gt = target[row_idx]
            val counter = pred[row_idx].count { x -> x > pred[row_idx][gt] }
            if (counter < recall_k) {
                rec_sum += 1
            }
        }

        return rec_sum / batchSize
    }

    private fun resetLastState(mask: IntArray) {
        for (i in 0 until batchSize) {
            if (mask[i] == 0) {
                for (j in 0 until innerUnits) {
                    this.lastState[i][j] = 0f
                }
            }
        }
    }

    fun trainOnBatch(feat: IntArray, target: IntArray, mask: IntArray): Float {
        resetLastState(mask)
        return trainOnBatch(feat, target, lastState)
    }

    fun saveWeights(path: String) {
        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: Map<String, Any> = HashMap()
        inputs["checkpoint_path"] = path
        this.interpreter!!.runSignature(inputs, outputs, "save")
    }

    companion object {
        const val defaultName = "saved_model.tflite"
    }
}