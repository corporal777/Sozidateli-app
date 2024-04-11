package com.example.ui.auth

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.R
import com.example.TestUtils
import com.example.ui.main.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class RecoveryPasswordScreenTest {


    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun recoveryPasswordEmailTest() {
        TestUtils.doScreenShot("Auth-Account")

        AuthorizationScreenTest().authorizationWithLogin()

        TestUtils.doScreenShot("Login-Screen")

        onView(isRoot()).perform(TestUtils.waitFor(2000))

        val viewButtonForgot = onView(allOf(withId(R.id.btnForgotPassword)))
        viewButtonForgot.perform(click())

        TestUtils.doScreenShot("Recovery-Screen")

        val viewEmail = onView(TestUtils.findCustomEditText(R.id.etLogin))
        viewEmail.perform(replaceText("flayshaw@gmail.com"))

        TestUtils.doScreenShot("Recovery-Screen-Email-Typed")

        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewButtonRecover = onView(allOf(withId(R.id.btn_recovery)))
        viewButtonRecover.perform(click())
        onView(isRoot()).perform(TestUtils.waitFor(3000))

        sendIntent()

        onView(isRoot()).perform(TestUtils.waitFor(3000))

        val viewPasswordOne = onView(allOf(withId(R.id.etPasswordOne), isDisplayed()))
        viewPasswordOne.perform(replaceText("Qwerty123"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewPasswordTwo = onView(allOf(withId(R.id.etPasswordTwo), isDisplayed()))
        viewPasswordTwo.perform(replaceText("Qwerty123"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        TestUtils.doScreenShot("Reset-Password-Typed")

        val viewButtonReset = onView(allOf(withId(R.id.btn_reset), isDisplayed()))
        viewButtonReset.perform(click())

        onView(isRoot()).perform(TestUtils.waitFor(5000))

        TestUtils.doScreenShot("Reset-Password-Changed")
    }

    private fun sendIntent() {
        Intents.init()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("https://alfa.sozidateli.ru/auth/21951/password-recovery?code=4f428e0a81cc53e93bbd0dc636072b69"))
        mActivityTestRule.finishActivity()
        mActivityTestRule.launchActivity(intent)
        Intents.release()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}