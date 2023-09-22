package com.ej.tensorflowlitetest.tflite

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Classifier constructor(
    val context: Context
) {

    lateinit var interpreter : Interpreter

    var modelInputWidth : Int = 0
    var modelInputHeight : Int = 0
    var modelInputChannel : Int = 0
    var modelOutputClasses : Int = 0

    @Throws(IOException::class)
    fun init() {
        val model = FileUtil.loadMappedFile(context, MODEL_NAME)
        model.order(ByteOrder.nativeOrder())
        interpreter = Interpreter(model)
        initModelShape()
    }

    fun classify(image: Bitmap) : Pair<Int,Float>{
        val buffer = convertBitmapToGrayByteBuffer(resizeBitmap(image))
        val result = Array(1) {
            FloatArray(
                modelOutputClasses
            )
        }
        interpreter.run(buffer, result)
        return argmax(result[0])
    }

    private fun argmax(array: FloatArray): Pair<Int, Float> {
        var argmax = 0
        var max = array[0]
        for (i in 1 until array.size) {
            val f = array[i]
            if (f > max) {
                argmax = i
                max = f
            }
        }
        return Pair(argmax, max)
    }
    private fun initModelShape() {
        val inputTensor : Tensor = interpreter.getInputTensor(0)
        val inputShape = inputTensor.shape()
        val inputType = inputTensor.dataType()
        modelInputChannel = inputShape[0]
        modelInputWidth = inputShape[1]
        modelInputHeight = inputShape[2]

        val outputTensor = interpreter.getOutputTensor(0)
        val outputType = outputTensor.dataType()
        val outputShape = outputTensor.shape()
        modelOutputClasses =  outputShape[1]
        return
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false)
    }

    private fun convertBitmapToGrayByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteCount = bitmap.byteCount
        val byteByffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        byteByffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in pixels) {
            val r = pixel shr 16 and 0xFF
            val g = pixel shr 8 and 0xFF
            val b = pixel and 0xFF
            val avgPixelValue = (r + g + b) / 3.0f
            val normalizedPixelValue = avgPixelValue / 255.0f
            byteByffer.putFloat(normalizedPixelValue)
        }
        return byteByffer
    }

    fun finish() {
        interpreter.close()
    }
//    @Throws(IOException::class)
//    private fun loadModelFile(modelName: String): ByteBuffer {
//        //org.tensorflow.lite.support.common.FileUtil에 구현되어있음
//        //org.tensorflow.lite.support.common.FileUtil.loadMappedFile(context, modelName);
//        val am = context.assets
//        val afd = am.openFd(modelName)
//        val fis = FileInputStream(afd.fileDescriptor)
//        val fc: FileChannel = fis.channel
//        val startOffset = afd.startOffset
//        val declaredLength = afd.declaredLength
//
//        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }

    companion object {
        val MODEL_NAME = "keras_model_cnn.tflite"
    }
}