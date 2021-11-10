package com.yudong.tflitegrudemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yudong.tflitegrudemo.adapter.Adapter
import com.yudong.tflitegrudemo.adapter.CSR15Adapter
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var lossTextView: TextView? = null
    private var progressTextView: TextView? = null
    private var debugInfo: EditText? = null
    private var progressBar: ProgressBar? = null
    private var trainNumText: EditText? = null
    private val adapter: Adapter = CSR15Adapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lossTextView = findViewById(R.id.lossTextView)
        progressTextView = findViewById(R.id.progressTextView)
        debugInfo = findViewById(R.id.debugInfoText)
        progressBar = findViewById(R.id.progressBar)
        trainNumText = findViewById(R.id.trainNumText)
        progressBar?.progress = 0

        val context = applicationContext
        try {
            adapter.load(context)
        } catch (e: IOException) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
        updateProgressTextView()
        updateLossTextView()
    }

    private fun updateProgressTextView() {
        progressBar!!.progress = adapter.currentBatch * 100 / adapter.totalBatch
        progressTextView!!.text = String.format("%d / %d", adapter.currentBatch, adapter.totalBatch)
    }

    private fun updateLossTextView() {
        lossTextView!!.text = String.format("%s: %.5f", adapter.metricName, adapter.metricValue)
    }

    fun trainButtonClick(view: View?) {
        val total = Integer.parseInt(trainNumText!!.text.toString())

        adapter.train(total)

        updateProgressTextView()
        updateLossTextView()
    }



    fun inferButtonClick(view: View?) {
        val prediction = adapter.infer()
        val builder = StringBuilder()
        prediction.forEach { i ->
            builder.append(i.toString()).append('\n')
        }
        debugInfo?.setText(builder.toString())
    }

    fun evalButtonClick(view: View?) {
        adapter.eval()

        updateProgressTextView()
        updateLossTextView()
    }

    fun saveButtonClick(view: View?) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 120)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 120 && resultCode == RESULT_OK) {
            val uri = data?.data
            val file = File(uri!!.path!!)
            debugInfo!!.setText(uri.path!!)
        }
    }
}