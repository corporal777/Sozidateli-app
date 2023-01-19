import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import com.example.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.ibEmail), withText("Зарегистрироваться"),
                childAtPosition(
                    allOf(
                        withId(R.id.llEmail),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            2
                        )
                    ),
                    1
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.et_last_name),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_last_name),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("Романов "), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.et_first_name),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_first_name),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("Роман "), closeSoftKeyboard())

        val appCompatCheckBox = onView(
            allOf(
                withId(R.id.sc_no_middle_name), withText("Нет отчества"),
                childAtPosition(
                    allOf(
                        withId(R.id.til_middle_name),
                        childAtPosition(
                            withId(R.id.ll_input),
                            2
                        )
                    ),
                    1
                )
            )
        )
        appCompatCheckBox.perform(scrollTo(), click())

        pressBack()

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.et_email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("+779268887766"), closeSoftKeyboard())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.et_email), withText("+779268887766"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.et_email), withText("+779268887766"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("+79268887766"))

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.et_email), withText("+79268887766"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText6.perform(closeSoftKeyboard())

        pressBack()

        val textInputEditText7 = onView(
            allOf(
                withId(R.id.etPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilPassword),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText7.perform(replaceText("Qwerty123"), closeSoftKeyboard())

        val textInputEditText8 = onView(
            allOf(
                withId(R.id.etPasswordConfirm),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilPasswordConfirm),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText8.perform(replaceText("Qwerty123"), closeSoftKeyboard())

        val textInputEditText9 = onView(
            allOf(
                withId(R.id.etPasswordConfirm), withText("Qwerty123"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilPasswordConfirm),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText9.perform(pressImeActionButton())

        val frameLayout = onView(
            allOf(
                withId(R.id.flAgree),
                childAtPosition(
                    allOf(
                        withId(R.id.llAgree),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        frameLayout.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.ib_register), withText("Зарегистрироваться"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.sv_content),
                        0
                    ),
                    4
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())
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
