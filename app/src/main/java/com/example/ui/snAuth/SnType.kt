package com.example.ui.snAuth

import com.example.util.SN_FB
import com.example.util.SN_OK
import com.example.util.SN_VK

enum class SnType(val code: String) {
    VK(SN_VK), FB(SN_FB), OK(SN_OK)
}