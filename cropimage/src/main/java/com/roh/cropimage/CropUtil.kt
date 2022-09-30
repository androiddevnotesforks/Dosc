package com.roh.cropimage

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CropUtil constructor(bitmap: Bitmap) {

    private var mBitmap: Bitmap

    var canvasWidth: Int by mutableStateOf(0)

    var canvasHeight: Int by mutableStateOf(0)

    var circleOne: Offset by mutableStateOf(Offset(0.0f, 0.0f))
        private set

    var circleTwo: Offset by mutableStateOf(Offset(0.0f, 0.0f))
        private set

    var circleThree: Offset by mutableStateOf(Offset(0.0f, 0.0f))
        private set

    var circleFour: Offset by mutableStateOf(Offset(0.0f, 0.0f))
        private set

    var guideLineOne: GuideLine by mutableStateOf(GuideLine())
        private set

    var guideLineTwo: GuideLine by mutableStateOf(GuideLine())
        private set

    var guideLineThree: GuideLine by mutableStateOf(GuideLine())
        private set

    var guideLineFour: GuideLine by mutableStateOf(GuideLine())
        private set


    init {
        mBitmap = bitmap
        canvasWidth = bitmap.width
        canvasHeight = bitmap.height

        resetCropView()

    }

    fun updateBitmapSizeChange(width: Int, height: Int) {
        this.canvasWidth = width
        this.canvasHeight = height
        resetCropView()

    }

    fun cropImage(mWidth: Int, mHeight:Int): Bitmap {

        val rect = getRectFromPoints()

        val bitmap: Bitmap = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, true)

        var imgLef = if (rect.left.toInt() < 0) 0 else rect.left.toInt()
        var imgTop = if (rect.top.toInt() < 0) 0 else rect.top.toInt()

        val imgWidth = if (rect.width.toInt() > mWidth) mWidth else rect.width.toInt()
        val imgHeight =
            if (rect.height.toInt() > mHeight) mHeight else rect.height.toInt()

        if (imgLef + imgWidth > mWidth) {
            imgLef = 0
        }
        if (imgTop + imgHeight > mHeight) {
            imgTop = abs(mHeight - imgHeight)
        }

        val cropBitmap =
            Bitmap.createBitmap(
                bitmap,
                imgLef,
                imgTop,
                imgWidth,
                imgHeight
            )


        return Bitmap.createScaledBitmap(cropBitmap, mWidth, mHeight, true)

    }



    fun resetCropView() {
        updateCircleOne(Offset(0f, 0f))
        updateCircleTwo(Offset(canvasWidth.toFloat(), 0f))
        updateCircleThree(Offset(0f, canvasHeight.toFloat()))
        updateCircleFour(Offset(canvasWidth.toFloat(), canvasHeight.toFloat()))

    }
    fun updateCropEdges(
        circleOne: Offset,
        circleTwo: Offset,
        circleThree: Offset,
        circleFour: Offset
    ) {
        this.circleOne = circleOne
        this.circleTwo = circleTwo
        this.circleThree = circleThree
        this.circleFour = circleFour

        updateGuideLineOne()
        updateGuideLineTwo()
        updateGuideLineThree()
        updateGuideLineFour()

    }

    fun updateCircleOne(offset: Offset) {

        this.circleOne = offset
        this.circleTwo = Offset(circleTwo.x, offset.y)
        this.circleThree = Offset(offset.x, circleThree.y)

        updateGuideLineOne()
        updateGuideLineTwo()
        updateGuideLineThree()
        updateGuideLineFour()
    }

    fun updateCircleTwo(offset: Offset) {
        this.circleTwo = offset
        this.circleOne = Offset(circleOne.x, offset.y)
        this.circleFour = Offset(offset.x, circleFour.y)
        updateGuideLineOne()
        updateGuideLineTwo()
        updateGuideLineThree()
        updateGuideLineFour()
    }

    fun updateCircleThree(offset: Offset) {
        this.circleThree = offset
        this.circleOne = Offset(offset.x, circleOne.y)
        this.circleFour = Offset(circleFour.x, offset.y)
        updateGuideLineOne()
        updateGuideLineTwo()
        updateGuideLineThree()
        updateGuideLineFour()

    }

    fun updateCircleFour(offset: Offset) {
        this.circleFour = offset
        this.circleTwo = Offset(offset.x, circleTwo.y)
        this.circleThree = Offset(circleThree.x, offset.y)
        updateGuideLineOne()
        updateGuideLineTwo()
        updateGuideLineThree()
        updateGuideLineFour()
    }

    private fun updateGuideLineOne() {
        val diffY =
            sqrt((circleOne.x - circleThree.x).pow(2) + (circleOne.y - circleThree.y).pow(2)) / 3
        this.guideLineOne = GuideLine(
            start = Offset(circleOne.x, circleThree.y - (diffY * 2)),
            end = Offset(circleTwo.x, circleTwo.y + diffY)
        )
    }

    private fun updateGuideLineTwo() {
        val diffY =
            sqrt((circleOne.x - circleThree.x).pow(2) + (circleOne.y - circleThree.y).pow(2)) / 3
        this.guideLineTwo = GuideLine(
            start = Offset(circleOne.x, circleOne.y + (diffY * 2)),
            end = Offset(circleTwo.x, circleTwo.y + (diffY * 2))
        )
    }

    private fun updateGuideLineThree() {
        val diffX =
            sqrt((circleOne.x - circleTwo.x).pow(2) + (circleOne.y - circleTwo.y).pow(2)) / 3
        this.guideLineThree = GuideLine(
            start = Offset(circleOne.x + diffX, circleOne.y),
            end = Offset(circleThree.x + diffX, circleThree.y)
        )
    }

    private fun updateGuideLineFour() {
        val diffX =
            sqrt((circleOne.x - circleTwo.x).pow(2) + (circleOne.y - circleTwo.y).pow(2)) / 3
        this.guideLineFour = GuideLine(
            start = Offset(circleOne.x + (diffX + diffX), circleOne.y),
            end = Offset(circleThree.x + (diffX + diffX), circleThree.y)
        )
    }

    //           x
    //left/top  #----------------#
    //       y  |                |
    //          |                |
    //          |                |
    //          |                |  height
    //          #----------------#  right/bottom
    //                      width
    //

    private fun getRectFromPoints(): Rect = Rect(
        circleOne.x,
        circleOne.y,
        circleFour.x,
        circleFour.y,
    )

}