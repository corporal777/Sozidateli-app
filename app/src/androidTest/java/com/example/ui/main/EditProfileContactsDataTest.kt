package com.example.ui.main

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.R
import com.example.util.AuthValidateUtil
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditProfileContactsDataTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        var textInputEtWorkPhone = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.etWorkPhone), ViewMatchers.isDisplayed()
            )
        )
        textInputEtWorkPhone.perform(ViewActions.replaceText("+79998887766"))

        assertTrue(AuthValidateUtil.isValidPhone("+79998887766"))

        var textInputEtAdditionalPhone = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.etAdditionalNumber),
                ViewMatchers.isDisplayed()
            )
        )
        textInputEtAdditionalPhone.perform(ViewActions.replaceText("+79998880000"))

        assertTrue(AuthValidateUtil.isValidPhone("+79998880000"))


        val mButton = Espresso.onView(ViewMatchers.withId(R.id.btnSave))
        mButton.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        mButton.perform(ViewActions.click())

    }
}