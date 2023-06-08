package com.example.data.models

import android.graphics.Bitmap

data class ProfileUserData(
    var user: UserDetail,
    var avatar: Bitmap?,
    var interests: Map<InterestNew, List<InterestNew>>?
) {

    fun setUserShortAddress(address: SearchAddressModel){
        if (address.data?.isNotEmpty() == true){
            user.address?.shortAddres = address.data[0].region
        }
    }

}