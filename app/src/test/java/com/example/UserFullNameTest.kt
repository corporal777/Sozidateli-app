package com.example

import com.example.data.models.user.User
import org.junit.Assert.assertTrue
import org.junit.Test

class UserFullNameTest {


    @Test
    fun correctNoMiddleNameTest() {
        val name = "Иван"
        val lastName = "Иванов"
        val middleName = null
        val user = User().apply {
            user_name = name
            user_last_name = lastName
            user_middle_name = middleName
        }
        assertTrue("$name $lastName" == user.fullName)
    }

    @Test
    fun correctEmptyMiddleNameTest() {
        val name = "Иван"
        val lastName = "Иванов"
        val middleName = ""
        val user = User().apply {
            user_name = name
            user_last_name = lastName
            user_middle_name = middleName
        }
        assertTrue("$name $lastName" == user.fullName)
    }

    @Test
    fun correctLineMiddleNameTest() {
        val name = "Иван"
        val lastName = "Иванов"
        val middleName = "-"
        val user = User().apply {
            user_name = name
            user_last_name = lastName
            user_middle_name = middleName
        }
        assertTrue("$name $lastName" == user.fullName)
    }

    @Test
    fun correctHasMiddleNameTest() {
        val name = "Иван"
        val lastName = "Иванов"
        val middleName = "Иванович"
        val user = User().apply {
            user_name = name
            user_last_name = lastName
            user_middle_name = middleName
        }
        assertTrue("$name $middleName $lastName" == user.fullName)
    }
}