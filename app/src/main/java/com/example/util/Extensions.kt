package com.example.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.ColorInt
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
import androidx.core.view.isInvisible
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.BuildConfig
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.extensions.calendar
import com.example.extensions.dp
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import okhttp3.OkHttpClient
import okhttp3.Request
import onTextChanged
import java.io.*
import java.util.*


fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else this
}

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


fun FragmentManager.showDatePicker(
    currentDate: String?,
    onDateSelected: (date: Long) -> Unit
) {
    var selection = if (!currentDate.isNullOrBlank())
        serverDateToMilliseconds(currentDate, DATE_FORMAT_SHORT_MONTH_FULL_YEAR)
    else {
        val current = Calendar.getInstance()
        current.add(Calendar.YEAR, -14)
        current.timeInMillis
    }
    val timezone = TimeZone.getDefault()
    selection += timezone.getOffset(selection)
    val endDate = Calendar.getInstance()
    endDate.add(Calendar.YEAR, -14)

    val picker = MaterialDatePicker
        .Builder
        .datePicker()
        .setTitleText(R.string.profile_birthday)
        .setTheme(R.style.DatePickerStyle)
        .setSelection(selection)
        .setCalendarConstraints(
            CalendarConstraints.Builder()
                .setEnd(endDate.timeInMillis)
                .setOpenAt(selection)
                .setValidator(WeekDayValidator()).build()
        )
        .build()
    picker.addOnPositiveButtonClickListener {
        onDateSelected.invoke(it)
    }
    picker.show(this, "")
}

fun PopupWindow.settings() {
    isOutsideTouchable = true
    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
}

fun String?.phoneToServer() = this?.replace("-", "")?.replace(" ", "")


@TargetApi(21)
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
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
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
    error: Int? = R.drawable.avatar_placeholder
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
}

fun getMonthName(calendar: Calendar?): String {
    var month = ""
    val monthNames = arrayOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )

    return if (calendar == null) ""
    else {
        month =
            if (getCurrentYear() == calendar.get(Calendar.YEAR)) monthNames[calendar.get(Calendar.MONTH)]
            else monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR)

        month
    }
}

fun getDeviceName(): String {
    val manufacturer: String = Build.MANUFACTURER
    val model: String = Build.MODEL
    return if (model.startsWith(manufacturer)) capitalize(model)
    else capitalize(manufacturer) + " " + model
}

fun getAppVersion(): String {
    return BuildConfig.VERSION_NAME
}

fun getAppVersionCode(): String {
    return BuildConfig.VERSION_CODE.toString()
}

private fun capitalize(str: String): String {
    if (TextUtils.isEmpty(str)) {
        return str
    }
    val arr = str.toCharArray()
    var capitalizeNext = true
    var phrase = ""
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase += Character.toUpperCase(c)
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase += c
    }
    return phrase
}

fun removeFirstAndLastSpaces(str: String?): String {
    val reg = "[\\s]+$".toRegex()
    val regLast = "^[\\s]+".toRegex()
    val value = str?.replace(regLast, "")
    return value?.replace(reg, "") ?: ""
}

fun String.removeAllDoubleSpaces(): String {
    val newStr = this.trim().replace("[\\s]+".toRegex(), " ")
    val sb = StringBuilder(newStr)
    val currentChar = ' '
    var counter = 0
    sb.forEach {
        if (it == currentChar) counter++
    }
    run loop@{
        sb.forEachIndexed { index, c ->
            if (currentChar == c && counter > 1) {
                sb.deleteCharAt(index)
                return@loop
            }
        }
    }
    return sb.toString()
}

fun markWon(context: Context): Markwon {
    return Markwon.builder(context)
        .usePlugins(
            listOf(
                SoftBreakAddsNewLinePlugin.create(),
                LinkifyPlugin.create(),
                HtmlPlugin.create(),
                MarkwonInlineParserPlugin.create()
            )
        )
        .build();
}


fun getCurrentYear(): Int = System.currentTimeMillis().calendar().get(Calendar.YEAR)
fun getCurrentMonth(): Int = System.currentTimeMillis().calendar().get(Calendar.MONTH)
fun getCurrentDay(): Int = System.currentTimeMillis().calendar().get(Calendar.DAY_OF_MONTH)

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

//                override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
//                    return super.calculateDxToMakeVisible(view, snapPreference) - dp2px(height.toFloat())
//                }
//
//                override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
//                    return super.calculateDyToMakeVisible(view, snapPreference) - dp2px(mBinding.eventsList.scaleX)
//                }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 20f / displayMetrics.densityDpi
            }
        }
    }
    mSmoothScroller.targetPosition = 0
    this.startSmoothScroll(mSmoothScroller)
}

fun showCustomTabsBrowser(context: Context, url: String) {
    try {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        Toast.makeText(context, R.string.link_open_error, Toast.LENGTH_LONG).show()
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


fun pdfToBitmap(url: String, context: Context, index: Int): Bitmap? {
    val client = OkHttpClient()
    val request = Request.Builder().url(url)
        .addHeader("Content-Type", "application/json")
        .build()

    val response = client.newCall(request).execute()
    val inputStream: InputStream? = response.body?.byteStream()
    val bytes = inputStream?.readBytes()

    val imagesFolder = File(context.cacheDir, "pdf")
    try {
        imagesFolder.mkdirs()
        val pdfFile = File(imagesFolder, "pdf_image-$index.png")
        bytes?.let { pdfFile.writeBytes(it) }

        val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)

        val page = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_4444)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close()
        renderer.close()
        return bitmap
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun pdfToUri(url: String, context: Context, index: Int): Uri? {
    val client = OkHttpClient()
    val request = Request.Builder().url(url)
        .addHeader("Content-Type", "application/json")
        .build()

    val response = client.newCall(request).execute()
    val inputStream: InputStream? = response.body?.byteStream()
    val bytes = inputStream?.readBytes()

    val imagesFolder = File(context.cacheDir, "pdf")
    try {
        imagesFolder.mkdirs()
        val pdfFile = File(imagesFolder, "pdf_image-$index.png")
        bytes?.let { pdfFile.writeBytes(it) }

        val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)

        val page = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_4444)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close()
        renderer.close()

        val stream = FileOutputStream(pdfFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            pdfFile
        )
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun copyTextToBuffer(context: Context, link: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("sozidateli_app_text", link)
    clipboardManager.setPrimaryClip(clip)
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

