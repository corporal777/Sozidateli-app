package com.example.util

import android.content.Context
import isConnectedToNetwork

class ConnnectivityHelper(private val context: Context){

    fun isConnectedToNetwork() = context.isConnectedToNetwork()

}