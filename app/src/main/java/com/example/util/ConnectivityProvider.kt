package com.example.util

import android.content.Context
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.Single

class ConnectivityProvider(private val context: Context) {
    fun observeNetworkConnectivity(): Observable<Boolean> {
        return ReactiveNetwork.observeNetworkConnectivity(context)
                .map { it.state() == NetworkInfo.State.CONNECTED }
    }

    fun checkInternetConnectivity(): Single<Boolean> {
        return ReactiveNetwork.checkInternetConnectivity()
    }
}