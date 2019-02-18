package com.example.util.photohelper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import com.example.util.APP_DATA_DIR_NAME
import com.example.util.APP_IMAGE_DIR_NAME
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crop.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by stanl on 21.12.2017.
 */
class CropActivity : AppCompatActivity() {

    private lateinit var image: Uri
    private var rotation = 0
    private val compositeDisposable = CompositeDisposable()
    private val compressFormat = Bitmap.CompressFormat.PNG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        readFromBundle(intent?.extras!!)
        setupUi()

        compositeDisposable.add(cropView.load(image)
                .executeAsCompletable()
                .subscribe({
                    val orientation = Utils.getExifOrientation(this, image)
                    val rotation = this.rotation - orientation

                    if (rotation != 0) {
                        val rotate = when (rotation) {
                            90 -> CropImageView.RotateDegrees.ROTATE_90D
                            180 -> CropImageView.RotateDegrees.ROTATE_180D
                            270 -> CropImageView.RotateDegrees.ROTATE_270D
                            -90 -> CropImageView.RotateDegrees.ROTATE_M90D
                            -180 -> CropImageView.RotateDegrees.ROTATE_M180D
                            -270 -> CropImageView.RotateDegrees.ROTATE_M270D
                            else -> null
                        }
                        if (rotate != null) {
                            cropView.rotateImage(rotate, 0)
                        }
                    }
                }, {
                    showErrorToast()
                    it.printStackTrace()
                })
        )
    }

    private fun crop() {
        compositeDisposable.add(cropView.crop(image)
                .executeAsSingle()
                .flatMap { cropView.save(it).executeAsSingle(createNewUri()) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val bundle = Bundle()
                    bundle.putParcelable(ARG_IMAGE_URI, it)
                    val intent = Intent()
                    intent.putExtras(bundle)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, {
                    showErrorToast()
                    it.printStackTrace()
                })
        )
    }

    private fun showErrorToast() {
        Toast.makeText(this, getString(R.string.error_take_image), LENGTH_SHORT).show()
    }

    private fun readFromBundle(bundle: Bundle) {
        image = bundle.getParcelable(ARG_IMAGE_URI)!!
        rotation = bundle.getInt(ARG_IMAGE_ROTATION)
    }

    private fun setupUi() {
        cropView.setOutputHeight(500)
        cropView.setOutputWidth(500)
        cropView.setCompressQuality(90)
        cropView.setCompressFormat(compressFormat)
        cropView.setCropEnabled(true)

        btnSave.setOnClickListener { crop() }
        btnCancel.setOnClickListener { finish() }
        /*buttonRotateLeft.setOnClickListener { cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D) }
        buttonRotateRight.setOnClickListener { cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D) }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    @SuppressLint("SimpleDateFormat")
    private fun createNewUri(): Uri? {
        val currentTimeMillis = System.currentTimeMillis()
        val today = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val title = dateFormat.format(today)
        val dirPath = getDirPath()
        val fileName = "fredi" + title + "." + getMimeType(compressFormat)
        val path = "$dirPath/$fileName"
        val file = File(path)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(compressFormat))
        values.put(MediaStore.Images.Media.DATA, path)
        val time = currentTimeMillis / 1000
        values.put(MediaStore.MediaColumns.DATE_ADDED, time)
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time)
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length())
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun getMimeType(format: Bitmap.CompressFormat): String {
        return when (format) {
            Bitmap.CompressFormat.JPEG -> return "jpeg"
            Bitmap.CompressFormat.PNG -> return "png"
            else -> "png"
        }
    }

    private fun getDirPath(): String {
        var dirPath = ""
        var imageDir: File? = null
        val extStorageDir = Environment.getExternalStorageDirectory()
        if (extStorageDir.canWrite()) {
            imageDir = File(extStorageDir.path + "/" + APP_DATA_DIR_NAME + "/" + APP_IMAGE_DIR_NAME)
        }

        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.path
            }
        }

        return dirPath
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        private const val ARG_IMAGE_URI = "image_uri"
        private const val ARG_IMAGE_ROTATION = "image_rotation"

        fun getStartIntent(context: Context, uri: Uri, orientation: Int): Intent {
            val bundle = Bundle()
            bundle.putParcelable(ARG_IMAGE_URI, uri)
            bundle.putInt(ARG_IMAGE_ROTATION, orientation)
            val intent = Intent(context, CropActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }

        fun getUriFromResult(resultIntent: Intent?): Uri? = resultIntent?.extras?.getParcelable(ARG_IMAGE_URI)
    }
}