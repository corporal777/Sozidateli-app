package com.example.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.CalendarContract
import android.provider.MediaStore
import android.text.InputFilter
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
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
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.example.app.BuildConfig
import com.example.app.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.onTextChanged
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


fun AppCompatCheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
    isChecked = checked
    setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
}

fun EditText.initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}

fun <T> AppCompatAutoCompleteTextView.initDropDownAdapter(list: MutableList<T>) {
    keyListener = null
    setAdapter(
        NoFilterArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            list
        )
    )
}


fun PopupWindow.settings() {
    isOutsideTouchable = true
    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
}

fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
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

    fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
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

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun View.getDrawable(res: Int): Drawable? {
    return ContextCompat.getDrawable(context, res)
}

fun View.getColor(res: Int): Int {
    return ContextCompat.getColor(context, res)
}

fun View.getColorStateList(res: Int): ColorStateList? {
    return ContextCompat.getColorStateList(context, res)
}

fun TextView.setLeftDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0)
}

fun TextView.setRightDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
}

fun ImageView.setImagePicasso(url: String?, placeholder: Any? = null, error: Any? = null) {
    Picasso.get()
        .load(url)
        .let {
            when (placeholder) {
                null -> it
                is Int -> it.placeholder(placeholder)
                else -> it.placeholder(placeholder as Drawable)
            }
        }
        .let {
            when (error) {
                null -> it
                is Int -> it.error(error)
                else -> it.error(error as Drawable)
            }
        }
        .into(this)
}

fun ImageView.setImage(
    image: Any?, crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = null,
    transformations: List<Transformation>? = null
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }

        is Uri -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }

        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossfad, placeholder, error, transformations)
                }
            else
                load(File(resImage)) {
                    setParams(crossfad, placeholder, error, transformations)
                }

        is Drawable ->
            load(resImage) {
                setParams(crossfad, placeholder, error, transformations)
            }

        is Bitmap -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
    }
}

fun ImageView.setCircleAvatar(
    image: Any?, crossFad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.avatar_placeholder_circle
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
        }

        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }
            else
                load(File(resImage)) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }

        is Drawable ->
            load(resImage) {
                setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
            }

        is Bitmap -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
        }
    }
}


fun ImageRequest.Builder.setParams(
    crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.background_image_placeholder,
    transformations: List<Transformation>? = null
) {
    if (crossfad != null) crossfade(crossfad)
    if (placeholder != null) placeholder(placeholder)
    if (error != null) error(error)
    if (!transformations.isNullOrEmpty()) transformations(transformations)
    scale(Scale.FILL)
    diskCachePolicy(CachePolicy.ENABLED)
    listener(
        onStart = {},
        onCancel = {},
        onError = { _, _ ->

        }
    )
}




fun LinearLayoutManager.smoothScrollToFirstItem(
    context: Context,
    appBar: AppBarLayout?,
    jumpToPosition: Int
) {
    val mSmoothScroller by lazy {
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_END
            }

            override fun updateActionForInterimTarget(action: Action?) {
                action?.jumpTo(jumpToPosition)
            }

            override fun onStop() {
                super.onStop()
                appBar?.setExpanded(true)
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 20f / displayMetrics.densityDpi
            }
        }
    }
    mSmoothScroller.targetPosition = 0
    this.startSmoothScroll(mSmoothScroller)
}



fun openDeviceCalendarApp(
    context: Context,
    dateFrom: String?,
    dateTo: String?,
    name: String?,
    desc: String?,
    address: String?
) {
    try {
        val startCal = defaultServerDateFormatter.parse(dateFrom ?: "").calendar()
        val endCal = defaultServerDateFormatter.parse(dateTo ?: "").calendar()

        val intent: Intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
            putExtra(CalendarContract.Events.TITLE, name)
            putExtra(CalendarContract.Events.DESCRIPTION, desc)
            putExtra(CalendarContract.Events.EVENT_LOCATION, address)
            putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
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

fun TextView.changeTitleTextColor(show: Boolean){
    if (show) setTextColor(getColor(R.color.title_text_error_red))
    else setTextColor(getColor(R.color.chat_list_date))
}

