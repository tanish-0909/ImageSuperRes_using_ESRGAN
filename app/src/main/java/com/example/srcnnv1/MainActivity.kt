package com.example.srcnnv1

//import com.example.srcnnv1.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.srcnnv1.ml.Esrgan1
import org.tensorflow.lite.support.image.TensorImage

class MainActivity : AppCompatActivity() {

    private lateinit var captureBtn: Button
    private lateinit var improveBtn: Button
    private lateinit var myImagePreview: ImageView
    private lateinit var myOutputImage: ImageView
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        captureBtn = findViewById(R.id.captureBtn)
        improveBtn = findViewById(R.id.improveBtn)
        myImagePreview = findViewById(R.id.myImagePreview)
        myOutputImage = findViewById(R.id.myOutputImage)

        val myContext = this

        Log.d("debugging", "Main Activity created")
        //image processor

        captureBtn.setOnClickListener {
//            Log.d("debugging", "Capture button clicked")
            val intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        improveBtn.setOnClickListener {
//            Log.d("debugging", "Improve button clicked")
            val startTime = System.currentTimeMillis()
//            var tensorImage: TensorImage = TensorImage(DataType.UINT8)

//            if (::bitmap.isInitialized) {
            val model = Esrgan1.newInstance(myContext)

            // Creates inputs for reference.
            val originalImage = TensorImage.fromBitmap(bitmap)

            // Runs model inference and gets result.
            val outputs = model.process(originalImage)
            val enhancedImage = outputs.enhancedImageAsTensorImage
            val enhancedImageBitmap = enhancedImage.bitmap

// Releases model resources if no longer used.
            model.close()
            myOutputImage.setImageBitmap(enhancedImageBitmap)

//            } else {
//                Log.d("debugging", "Bitmap is not initialized")
//            }

            val endTime = System.currentTimeMillis()
            val maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024
            Log.d("debugging", "Max memory: $maxMemory MB")
            val diff = endTime - startTime
            Log.d("debugging", "timeTaken: $diff")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val uri = data?.data        //Uniform Resource Indicator: read the image from this location
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
            myImagePreview.setImageBitmap(bitmap)

            Log.d("debugging", "Bitmap successfully imported / captured")
        }
    }
}
