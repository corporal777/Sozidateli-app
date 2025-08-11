package com.example.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.example.app.BuildConfig
import com.example.app.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.common.dp
import com.example.common.parseColor
import com.example.extensions.onTextChanged
import com.example.extensions.textColor
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream






fun Activity.setWindowTransparency(listener: (Int) -> Unit) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
//    window.navigationBarColor = Color.TRANSPARENT
//    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.cancelWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.returnSystemInsets(window.decorView, listener)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.doEdgeDisplay(window.decorView, listener)
}

typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtil {

    fun doEdgeDisplay(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }

    fun removeSystemInsets(view: View, listener: (Int) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            listener.invoke(insets.systemWindowInsetTop)
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom,
                )
            )
        }
    }

}




fun saveImageToGallery(context: Context, bitmap: Bitmap, albumName: String) {
    val filename = "${System.currentTimeMillis()}.png"
    val write: (OutputStream) -> Boolean = {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DCIM}/$albumName"
            )
        }

        context.contentResolver.let {
            it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                it.openOutputStream(uri)?.let(write)
            }
        }
    } else {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + File.separator + albumName
        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }
        val image = File(imagesDir, filename)
        write(FileOutputStream(image))
    }
}

fun saveImageToCache(context: Context, image: Bitmap): Uri? {
    val imagesFolder = File(context.cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "shared_image.png")
        val stream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return uri
}

fun showCustomTabsBrowser(context: Context, url: String) {
    val pageUrl =
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            Uri.parse("https://$url")
        else Uri.parse(url)
    try {
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setStartAnimations(context, R.anim.browser_popup_enter, android.R.anim.fade_out)
            setExitAnimations(context, android.R.anim.fade_in, R.anim.browser_popup_exit)
        }.build()
        customTabsIntent.launchUrl(context, pageUrl)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Не удалось открыть страницу", Toast.LENGTH_SHORT).show()
    }
}

fun showFileBrowser(context: Context, url: String){
    val disposable = CompositeDisposable()
    disposable += FileUtils.savePdfToCache(url, context)
        .performOnBackgroundOutOnMain()
        .subscribeBy(
            onError = { it.printStackTrace() },
            onSuccess = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(it, "application/pdf")
                    context.startActivity(intent)
                    disposable.clear()
                } catch (e : Exception){
                    e.printStackTrace()
                    Toast.makeText(context, "Не удалось открыть страницу", Toast.LENGTH_SHORT).show()
                }
            }
        )

}



fun copyTextToBuffer(context: Context, link: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("sozidateli_app_text", link)
    clipboardManager.setPrimaryClip(clip)
}


fun Bitmap.toByArray(): ByteArray {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}

fun convertBitmapToFile(context: Context, fileName: String, bitmap: Bitmap): File {
    //create a file to write bitmap data
    val file = File(context.cacheDir, fileName)
    file.createNewFile()

    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitMapData = bos.toByteArray()

    //write the bytes in file
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos?.write(bitMapData)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

fun getSiteFilter(): Array<InputFilter> {
    return arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filterNot { it.isWhitespace() }
    })
}

fun getEmailFilter(): Array<InputFilter> {
    return arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it.isDigit() || it == '.' || it == '@' || it == '_' || it == '+'
        }
    })
}

fun getNameFilter(): Array<InputFilter> {
    return arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-' || it == ' '
        }
    })
}

fun getPhoneFilter(): Array<InputFilter> {
    return arrayOf(
        InputFilter { source, start, end, dest, dStart, dEnd ->
            source.toString().filterIndexed { index, it ->
                if (index == 0) it.isDigit() || it == '+'
                else it.isDigit()
            }
        })
}

fun imageCaptureCallback(
    onError: (t: Throwable) -> Unit,
    onSaved: (output: ImageCapture.OutputFileResults) -> Unit
): ImageCapture.OnImageSavedCallback {
    return object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
            onError.invoke(exc)
        }

        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            onSaved.invoke(output)
        }
    }
}

fun Fragment.getMakeSceneTransition(view: View): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(
        requireActivity(),
        Pair(view, view.transitionName)
    )
}


