package com.example


import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.runner.screenshot.Screenshot
import com.example.TestUtils.waitUntilVisible
import com.example.ui.main.MainActivity
import junit.framework.AssertionFailedError
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun registerScreenTest() {
        Screenshot.capture().setName("Register").setFormat(Bitmap.CompressFormat.JPEG).process();
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

        val textInputLastName = onView(allOf(withId(R.id.et_last_name), isDisplayed()))
        textInputLastName.perform(scrollTo(), replaceText("Белов"), pressImeActionButton())
        Screenshot.capture().setName("Last-Name").setFormat(Bitmap.CompressFormat.JPEG).process();

        val textInputName = onView(allOf(withId(R.id.et_first_name), isDisplayed()))
        textInputName.perform(scrollTo(), replaceText("Сергей"), pressImeActionButton())
        Screenshot.capture().setName("First-Name").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatCheckBox =
            onView(allOf(withId(R.id.sc_no_middle_name), withText("Нет отчества"), isDisplayed()))
        appCompatCheckBox.perform(scrollTo(), click())
        Screenshot.capture().setName("Middle-Name").setFormat(Bitmap.CompressFormat.JPEG).process();

        val textInputEmail = onView(allOf(withId(R.id.et_email), isDisplayed()))
        textInputEmail.perform(replaceText("+79263401234"), pressImeActionButton())
        textInputEmail.perform(scrollTo())
        Screenshot.capture().setName("Email").setFormat(Bitmap.CompressFormat.JPEG).process();

        //test typing password
        val textInputPassword = onView(allOf(withId(R.id.etPassword), isDisplayed()))
        textInputPassword.perform(replaceText("Qwerty123"), closeSoftKeyboard())

        val textInputPasswordConfirm = onView(allOf(withId(R.id.etPasswordConfirm), isDisplayed()))
        textInputPasswordConfirm.perform(replaceText("Qwerty123"), closeSoftKeyboard())
        Screenshot.capture().setName("Password").setFormat(Bitmap.CompressFormat.JPEG).process();



        //test agree
        val checkBoxAgree = onView(allOf(withId(R.id.flAgree), isDisplayed()))
        checkBoxAgree.perform(scrollTo())
        checkBoxAgree.waitUntilVisible(3000).perform(click())
        Screenshot.capture().setName("Agree").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.ib_register), withText("Зарегистрироваться"),
                childAtPosition(
                    allOf(
                        withId(R.id.ln_content),
                        childAtPosition(
                            withId(R.id.sv_content),
                            0
                        )
                    ),
                    9
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())
        Screenshot.capture().setName("Click-Register").setFormat(Bitmap.CompressFormat.JPEG)
            .process();

        onView(isRoot()).perform(TestUtils.waitFor(10000))

        //enter code test
        val textInputCode = onView(allOf(withId(R.id.etCode), isDisplayed()))
        textInputCode.perform(scrollTo(), replaceText("998877"), pressImeActionButton())

        Screenshot.capture().setName("Enter-Code").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButtonRegister = onView(
            allOf(
                withId(R.id.ibRegister), withText("Зарегистрироваться"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.svContent),
                        0
                    ),
                    11
                )
            )
        )
        appCompatButtonRegister.perform(scrollTo(), click())
        onView(isRoot()).perform(TestUtils.waitFor(5000))
        Screenshot.capture().setName("Welcome").setFormat(Bitmap.CompressFormat.JPEG).process();
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
