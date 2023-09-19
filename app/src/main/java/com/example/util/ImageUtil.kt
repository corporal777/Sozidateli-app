package com.example.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import io.reactivex.Maybe

class ImageUtil {

    companion object {
        fun getBitmapFromUri(ctx: Context, imageUri: Uri?, reqHeight: Int, reqWidth: Int): Bitmap? {
            if (imageUri == null) return null
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val imagePath = getMediaAbsolutePath(ctx, imageUri)
            var bitmap: Bitmap = BitmapFactory.decodeFile(imagePath, options)
            val height: Int = options.outHeight
            val width: Int = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                inSampleSize =
                    if (width > height) Math.round(height.toFloat() / reqHeight.toFloat())
                    else Math.round(width.toFloat() / reqWidth.toFloat())

            }
            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            bitmap = BitmapFactory.decodeFile(imagePath, options)
            return bitmap
        }


        fun loadBitmapFromUrl(
            url: String?,
            transformations: List<Transformation>? = null,
            onResult: (bitmap: Bitmap?) -> Unit
        ) {
            if (url.isNullOrBlank()) {
                onResult(null)
                return
            }

            Picasso.get().load(url)
                .apply { if (transformations != null) transform(transformations) }
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) =
                        onResult(null)

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) =
                        onResult(bitmap)
                })
        }

        fun getGalleryImages(context: Context): MutableList<Uri> {
            val fileList = mutableListOf<Uri>()
            val projection = arrayOf(MediaStore.Files.FileColumns._ID)
            val sortOrder = MediaStore.Images.Media._ID + " DESC"

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )

                    fileList.add(contentUri)
                }
            }
            return fileList
        }
    }
}

fun String?.loadBitmap(
    transformations: List<Transformation>? = null,
    onResult: (bitmap: Bitmap?) -> Unit
) {
    ImageUtil.loadBitmapFromUrl(this, transformations, onResult)
}

fun String?.loadBitmap(transformations: List<Transformation>? = null): Maybe<Optional<Bitmap>> {
    return Maybe.create { emitter ->
        loadBitmap(transformations) { emitter.onSuccess(it.asOptional()) }
    }
}


fun String?.loadBitmapNew(context: Context): Bitmap? {
    return if (this.isNullOrEmpty()) null
    else Glide.with(context).asBitmap().load(this).submit().get()
}

private fun getMediaAbsolutePath(ctx: Context, uri: Uri?): String? {
    val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
    if (uri == null) return null
    val cursor: Cursor? = ctx.contentResolver.query(uri, filePathColumn, null, null, null)
    if (cursor == null) return null
    cursor.moveToFirst()
    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
    val picturePath: String = cursor.getString(columnIndex)
    cursor.close()
    return picturePath
}


