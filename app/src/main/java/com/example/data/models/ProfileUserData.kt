package com.example.data.models

import android.graphics.Bitmap

data class ProfileUserData(
    var user: UserDetail,
    var interests: Map<InterestNew, List<InterestNew>>? = emptyMap()
) {


    fun setUserShortAddress(address: SearchAddressModel){
        if (address.data?.isNotEmpty() == true){
            user.address?.shortAddres = address.data[0].region
        }
    }

    fun setUserInterests(list : List<InterestNew>?){
        interests = if (user.isHasInterests() && !list.isNullOrEmpty()) {
            mutableMapOf<InterestNew, MutableList<InterestNew>>().apply {
                list.filter { it.parent == 0 }.forEach {
                    val parent = list.filter { parent -> parent.parent == it.id }
                    parent.let { it1 ->
                        user.interests?.forEach { usIn ->
                            val isUserInterest = it1.find { it2 -> it2.id == usIn }
                            if (isUserInterest != null)
                                getOrPut(it) { mutableListOf() }.add(isUserInterest)
                        }
                    }
                }
            }
        } else mutableMapOf()
    }

}