package com.example

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.screenshot.Screenshot
import com.example.TestUtils.waitFor
import com.example.TestUtils.waitId
import com.example.ui.main.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {


    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun loginTest() {
        Screenshot.capture().setName("Auth").setFormat(Bitmap.CompressFormat.JPEG).process();
        val appCompatButton = onView(
            Matchers.allOf(
                withId(R.id.ibLogin), ViewMatchers.withText("Войти"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.llLogin),
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    1
                )
            )
        )
        appCompatButton.perform(ViewActions.scrollTo(), ViewActions.click())

        Screenshot.capture().setName("Login-Screen").setFormat(Bitmap.CompressFormat.JPEG)
            .process();

        val textInputEtLogin = onView(
            Matchers.allOf(
                withId(R.id.etLogin),
                isDisplayed()
            )
        )
        textInputEtLogin.perform(replaceText("podxvat777@gmail.com"), closeSoftKeyboard())
        Screenshot.capture().setName("Email-typed").setFormat(Bitmap.CompressFormat.JPEG).process();

        val textInputEtPassword = onView(
            Matchers.allOf(
                withId(R.id.etPassword),
                isDisplayed()
            )
        )
        textInputEtPassword.perform(replaceText("Qwerty123"), closeSoftKeyboard())
        Screenshot.capture().setName("Password-typed").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButton2 = onView(
            Matchers.allOf(
                withId(R.id.btn_login), ViewMatchers.withText("Войти"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.content),
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    5
                )
            )
        )
        appCompatButton2.perform(click())
        Screenshot.capture().setName("Login-Email").setFormat(Bitmap.CompressFormat.JPEG).process();
        onView(isRoot()).perform(waitFor(5000))
        Screenshot.capture().setName("Welcome").setFormat(Bitmap.CompressFormat.JPEG).process();
//        val mButton = onView(withId(R.id.ibLogin))
//        mButton.check(matches(isDisplayed()))
//        mButton.perform(click())
        //mPresenter.getData(mLogin, mPassword)

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