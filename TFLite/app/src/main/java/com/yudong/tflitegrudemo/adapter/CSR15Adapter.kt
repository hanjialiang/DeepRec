package com.yudong.tflitegrudemo.adapter

class CSR15Adapter(
) : Adapter(
    featsName,
    targetsName,
    masksName,
    modelName,
    batchNum,
    batchSize,
    innerUnits,
    trainItems
) {
    companion object {
        const val featsName = "feats_bs512.txt"
        const val targetsName = "targets_bs512.txt"
        const val masksName = "masks_bs512.txt"
        const val modelName = "saved_model.tflite"
        const val batchSize = 512
        const val batchNum = 46138
        const val innerUnits = 100
        const val trainItems = 37484
    }
}