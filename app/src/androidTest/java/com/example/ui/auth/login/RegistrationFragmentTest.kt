package com.example.ui.auth.login


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.R
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
class RegistrationFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    private val mName = "Сергей"
    private val mLastName = "Иванов"
    private val mPhone = "+79991117788"
    private val mPassword = "Qwerty123"


    @Test
    fun registrationFragmentTest() {


        val textInputFirstName = onView(
            allOf(
                withId(R.id.etFirstName),
                isDisplayed()
            )
        )
        textInputFirstName.perform(replaceText(mName), closeSoftKeyboard())

        var textInputLastName = onView(
            allOf(
                withId(R.id.etLastName),
                isDisplayed()
            )
        )
        textInputLastName.perform(replaceText(mLastName), closeSoftKeyboard())

        textInputLastName = onView(
            allOf(
                withId(R.id.etLastName), withText(mLastName),
                isDisplayed()
            )
        )
        textInputLastName.perform(pressImeActionButton())


        var textInputMidName = onView(
            allOf(
                withId(R.id.etMiddleName),
                isDisplayed()
            )
        )
        textInputMidName.perform(replaceText("Иванович"), closeSoftKeyboard())

        textInputMidName = onView(
            allOf(
                withId(R.id.etMiddleName), withText("Иванович"),
                isDisplayed()
            )
        )
        textInputMidName.perform(pressImeActionButton())



        val textInputEmail = onView(
            allOf(
                withId(R.id.etEmail),
                isDisplayed()
            )
        )
        textInputEmail.perform(replaceText(mPhone), closeSoftKeyboard())

        val textInputPassword= onView(
            allOf(
                withId(R.id.etPassword),
                isDisplayed()
            )
        )
        textInputPassword.perform(replaceText(mPassword), closeSoftKeyboard())

        var textInputPasswordConfirm = onView(
            allOf(
                withId(R.id.etPasswordConfirm),
                isDisplayed()
            )
        )
        textInputPasswordConfirm.perform(replaceText(mPassword), closeSoftKeyboard())

        textInputPasswordConfirm = onView(
            allOf(
                withId(R.id.etPasswordConfirm), withText(mPassword),
                isDisplayed()
            )
        )
        textInputPasswordConfirm.perform(pressImeActionButton())

        val frameLayout = onView(
            allOf(
                withId(R.id.flAgree),
                isDisplayed()
            )
        )
        frameLayout.perform(click())

        val mButtonReg = onView(withId(R.id.ibRegister))
        mButtonReg.check(ViewAssertions.matches(isDisplayed()))
        mButtonReg.perform(click())


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
