package com.example.ui.auth

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.R
import com.example.TestUtils
import com.example.ui.main.MainActivity
import com.example.ui.views.codeView.CodeConfirmationView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RecoveryPasswordByPhoneScreenTest {


    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun recoveryPasswordPhoneTest() {
        TestUtils.doScreenShot("Auth-Account")

        AuthorizationScreenTest().authorizationWithLogin()

        TestUtils.doScreenShot("Login-Screen")

        onView(isRoot()).perform(TestUtils.waitFor(2000))

        val viewButtonForgot = onView(allOf(withId(R.id.btnForgotPassword)))
        viewButtonForgot.perform(click())

        TestUtils.doScreenShot("Recovery-Screen")

        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewEmail = onView(TestUtils.findCustomEditText(R.id.etLogin))
        viewEmail.perform(replaceText("+79998887700"))

        TestUtils.doScreenShot("Recovery-Screen-Phone-Typed")

        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewButtonRecover = onView(allOf(withId(R.id.btn_recovery)))
        viewButtonRecover.perform(click())
        onView(isRoot()).perform(TestUtils.waitFor(3000))

        val codeView = onView(allOf(withId(R.id.phone_code_view)))
        codeView.perform(setConfirmationCode("6646"))

        TestUtils.doScreenShot("Confirmation-Code-Entered")

        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewButtonConfirm = onView(allOf(withId(R.id.btnConfirm)))
        viewButtonConfirm.perform(click())

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

    private fun setConfirmationCode(code: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    ViewMatchers.withClassName(Matchers.`is`("com.example.ui.views.codeView.CodeConfirmationView")),
                    ViewMatchers.isDisplayed()
                )
            }

            override fun perform(uiController: UiController?, view: View) {
                if (view is CodeConfirmationView && !code.isNullOrEmpty())
                    view.enteredCode = code
            }

            override fun getDescription(): String {
                return "replace text"
            }
        }
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