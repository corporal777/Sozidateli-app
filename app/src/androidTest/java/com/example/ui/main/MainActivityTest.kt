package com.example.ui.main


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private var mLogin = "t_3@houseofapps.ru"
    private var mPassword = "Lera1801"

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {


//        val appCompatButton = onView(
//            allOf(
//
//                withId(R.id.ibLogin), withText("�����"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.llLogin),
//                        childAtPosition(
//                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
//                            3
//                        )
//                    ),
//                    1
//                )
//            )
//        )
//        appCompatButton.check(matches(isDisplayed()))
//        appCompatButton.perform(scrollTo(), click())


                onView(withId(R.id.etLogin))
            .perform(typeText(mLogin))

        onView(withId(R.id.etPassword))
            .perform(typeText(mPassword))

        onView(withId(R.id.ibLogin)).perform(click())

//        val login = onView(
//            allOf(
//                withId(R.id.etLogin), withText("�����"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.content),
//                        childAtPosition(
//                            withClassName(`is`("android.widget.ScrollView")),
//                            0
//                        )
//                    ),
//                    2
//                )
//            )
//        )
//        login.perform(typeText(mLogin))
//
//        val password = onView(
//            allOf(
//                withId(R.id.etPassword), withText("�����"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.content),
//                        childAtPosition(
//                            withClassName(`is`("android.widget.ScrollView")),
//                            0
//                        )
//                    ),
//                    3
//                )
//            )
//        )
//        password.perform(typeText(mPassword))
//
//        val appCompatButton2 = onView(
//            allOf(
//                withId(R.id.ibLogin), withText("�����"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.content),
//                        childAtPosition(
//                            withClassName(`is`("android.widget.ScrollView")),
//                            0
//                        )
//                    ),
//                    5
//                )
//            )
//        )
//        appCompatButton2.perform(scrollTo(), click())
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
