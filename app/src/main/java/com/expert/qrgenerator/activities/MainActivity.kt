package com.expert.qrgenerator.activities

import android.R.attr.bitmap
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.expert.qrgenerator.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var context: Context
    private lateinit var toolbar: Toolbar
    private lateinit var inputBox: TextInputEditText
    private lateinit var generateBtn: MaterialButton
    private lateinit var qrGeneratedImage: AppCompatImageView
    private lateinit var shareBtn: MaterialButton
    private var qr_image:Bitmap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setUpToolbar()

    }

    private fun initViews() {
        context = this
        toolbar = findViewById(R.id.toolbar)
        inputBox = findViewById(R.id.input_text_box)
        generateBtn = findViewById(R.id.generate_btn)
        generateBtn.setOnClickListener(this)
        qrGeneratedImage = findViewById(R.id.qr_generated_img)
        shareBtn = findViewById(R.id.share_btn)
        shareBtn.setOnClickListener(this)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(getString(R.string.app_name))
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.generate_btn -> {
                if (!TextUtils.isEmpty(inputBox.text.toString())) {
                    val text = inputBox.text.toString()
                    qr_image = generateQRCode(text)
                    qrGeneratedImage.setImageBitmap(qr_image)
                    if (qr_image != null) {

                        try {
                            val cacheStoragePath = File(context.cacheDir, "images")
                            cacheStoragePath.mkdirs()
                            val stream = FileOutputStream("$cacheStoragePath/qr_generated_image.jpg")
                            qr_image!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            stream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    shareBtn.visibility = View.VISIBLE

                }
            }
            R.id.share_btn -> {
                shareImage()
            }
            else -> {

            }
        }
    }

    private fun shareImage(){
        //Save the image inside the APPLICTION folder
        val mediaStorageDir = File(externalCacheDir.toString() + "Image.png")

        try {
            val outputStream = FileOutputStream(mediaStorageDir.toString())
            qr_image!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val imageUri = FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", mediaStorageDir)
        if (imageUri != null) {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "image/*"
            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            startActivity(Intent.createChooser(waIntent, "Share with"))
        }

    }
}