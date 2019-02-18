package com.example.util.photohelper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.qingmei2.rximagepicker.core.RxImagePicker;
import com.qingmei2.rximagepicker.entity.Result;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MediaUtils implements LifecycleObserver {

    private static final int PERMISSION_CAMERA = 4;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 5;

    public static final int ACTION_CROP = 33;

    private Activity activity;
    private Fragment fragment;
    private Lifecycle lifecycle;
    private OnPhotoPathFoundListener onPhotoPathFoundListener;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MediaUtils(@NonNull AppCompatActivity activity) {
        this(activity, null);
    }

    public MediaUtils(@NonNull Fragment fragment) {
        this(fragment, null);
    }

    public MediaUtils(@NonNull AppCompatActivity activity, @Nullable OnPhotoPathFoundListener photoPathFoundListener) {
        this(activity, null, photoPathFoundListener);
    }

    public MediaUtils(@NonNull Fragment fragment, @Nullable OnPhotoPathFoundListener photoPathFoundListener) {
        this(null, fragment, photoPathFoundListener);
    }

    private MediaUtils(@Nullable AppCompatActivity activity, @Nullable Fragment fragment, @Nullable OnPhotoPathFoundListener photoPathFoundListener) {
        this.onPhotoPathFoundListener = photoPathFoundListener;
        if (activity != null) {
            this.activity = activity;
            lifecycle = activity.getLifecycle();
        } else if (fragment != null) {
            this.activity = fragment.getActivity();
            this.fragment = fragment;
            lifecycle = fragment.getLifecycle();
        } else {
            throw new NullPointerException("No activity or fragment activity provided");
        }

        lifecycle.addObserver(this);
    }

    public void setOnPhotoPathFoundListener(OnPhotoPathFoundListener onPhotoPathFoundListener) {
        this.onPhotoPathFoundListener = onPhotoPathFoundListener;
    }


    public void requestImageFromCamera() {
        if (checkCameraPermission()) {
            subscribeToRequest(RxImagePicker.INSTANCE.create(MyImagePicker.class)
                    .openCamera(activity));
        }
    }

    public void requestImageFromGallery() {
        if (checkStoragePermission()) {
            subscribeToRequest(RxImagePicker.INSTANCE.create(MyImagePicker.class)
                    .openGallery(activity));

        }
    }

    private void subscribeToRequest(Observable<Result> uriObservable) {
        if (uriObservable == null) return;
        compositeDisposable.add(uriObservable.subscribeOn(Schedulers.io())
                .map(getImageData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageRotationConsumer(), imageFindErrorConsumer()));
    }

    private Consumer<ImageRotation> imageRotationConsumer() {
        return new Consumer<ImageRotation>() {
            @Override
            public void accept(ImageRotation imageRotation) throws Exception {
                String key = getCallerClassName();
                if (lifecycle != null && lifecycle.getCurrentState() == Lifecycle.State.RESUMED) {
                    if (onPhotoPathFoundListener != null) {
                        onPhotoPathFoundListener.onPhotoFound(imageRotation.getPath(), imageRotation.getUri(), imageRotation.getRotation());
                    }
                    ImageSaver.getInstance().removeImageForKey(key);
                } else {
                    ImageSaver.getInstance().saveImage(key, imageRotation);
                }
            }
        };
    }

    private String getCallerClassName() {
        if (fragment != null) {
            return fragment.getClass().getName();
        } else {
            return activity.getClass().getName();
        }
    }

    private Consumer<Throwable> imageFindErrorConsumer() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (onPhotoPathFoundListener != null) {
                    onPhotoPathFoundListener.onPhotoFoundError(throwable);
                }
            }
        };
    }

    private Function<Result, ImageRotation> getImageData() {
        return new Function<Result, ImageRotation>() {
            @Override
            public ImageRotation apply(Result result) throws Exception {
                return new ImageRotation(getPathFromUri(result.getUri()), result.getUri(), findImageRotation(result.getUri()));
            }
        };
    }

    public String getPathFromUri(Uri uri) {
        String path = null;
        if (uri != null) {
            path = RealPathUtil.getPath(activity, uri);
            if (path == null) path = uri.getPath();
        }
        return path;
    }

    private int findImageRotation(Uri uri) {
        ContentResolver contentResolver = activity.getContentResolver();
        try (InputStream in = contentResolver.openInputStream(uri)) {
            ExifInterface exifInterface = new ExifInterface(in);

            int rotation = 0;
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }

            return rotation;
        } catch (IOException ignored) {
        }
        return 0;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        if (lifecycle != null) lifecycle.removeObserver(this);
        lifecycle = null;
        compositeDisposable.clear();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        String key = getCallerClassName();
        ImageRotation imageRotation = ImageSaver.getInstance().getImageForKey(key);
        if (imageRotation != null && onPhotoPathFoundListener != null) {
            if (onPhotoPathFoundListener.onPhotoFound(imageRotation.getPath(), imageRotation.getUri(), imageRotation.getRotation())) {
                ImageSaver.getInstance().removeImageForKey(key);
            }
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (fragment != null) {
                fragment.requestPermissions(permissions, PERMISSION_CAMERA);
            } else {
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            if (fragment != null) {
                fragment.requestPermissions(permissions, PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    public void handleOnPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_CAMERA) {
                requestImageFromCamera();
            } else if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
                requestImageFromGallery();
            }
        }
    }

    public interface OnPhotoPathFoundListener {

        boolean onPhotoFound(String path, Uri uri, int rotation);

        void onPhotoFoundError(Throwable throwable);
    }
}
