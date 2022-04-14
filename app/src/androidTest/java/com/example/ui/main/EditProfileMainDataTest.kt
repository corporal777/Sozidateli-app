package com.example.ui.main


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.R
import com.example.ui.userprofile.edit.UserEditFragment
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditProfileMainDataTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        var textInputEtBirthday = Espresso.onView(
            Matchers.allOf(
                withId(R.id.etBirthday), isDisplayed()
            )
        )
        textInputEtBirthday.perform(replaceText("14.10.1996"))

        var textInputTvGender = Espresso.onView(
            Matchers.allOf(
                withId(R.id.tvGender),
                isDisplayed()
            )
        )
        textInputTvGender.perform(replaceText("Мужской"))



        var textInputTvCity = Espresso.onView(
            Matchers.allOf(
                withId(R.id.etCity),
                isDisplayed()
            )
        )
        textInputTvCity.perform(replaceText("г. Москва"))


        var textInputNotes = Espresso.onView(
            Matchers.allOf(
                withId(R.id.etNotes),
                isDisplayed()
            )
        )
        textInputNotes.perform(replaceText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."), closeSoftKeyboard())

        val mButton = onView(withId(R.id.btnSave))
        mButton.check(matches(isDisplayed()))
        mButton.perform(click())

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
