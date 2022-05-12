package com.example.ui.auth.login

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.R
import com.example.ui.main.MainActivity
import com.example.util.AuthValidateUtil
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val mLogin = "t_3@houseofapps.ru"
    private val mPassword = "Lera1801"
    //private val mPresenter = LoginPresenterTest()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        var textInputEtLogin = onView(
            Matchers.allOf(
                withId(R.id.etLogin),
                isDisplayed()
            )
        )
        textInputEtLogin.perform(replaceText("t_3@houseofapps.ru"), closeSoftKeyboard())

        textInputEtLogin = onView(
            Matchers.allOf(
                withId(R.id.etLogin), withText("t_3@houseofapps.ru"),
                isDisplayed()
            )
        )
        textInputEtLogin.perform(pressImeActionButton())

        var textInputEtPassword = onView(
            Matchers.allOf(
                withId(R.id.etPassword),
                isDisplayed()
            )
        )
        textInputEtPassword.perform(replaceText("Lera1801"), closeSoftKeyboard())

        textInputEtPassword = onView(
            Matchers.allOf(
                withId(R.id.etPassword), withText("Lera1801"),
                isDisplayed()
            )
        )
        textInputEtPassword.perform(pressImeActionButton())


        Assert.assertTrue(AuthValidateUtil.isValidEmail("t_3@houseofapps.ru"))
        Assert.assertTrue(AuthValidateUtil.isValidPassword("Lera1801"))

        val mButton = onView(withId(R.id.ibLogin))
        mButton.check(matches(isDisplayed()))
        mButton.perform(click())
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