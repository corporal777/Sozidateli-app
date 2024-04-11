package com.example.ui.auth


import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
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
@RunWith(androidx.test.runner.AndroidJUnit4::class)
class RegisterScreenTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun registerScreenTest() {
        TestUtils.doScreenShot("Register-Account")

        AuthorizationScreenTest().authorizationWithRegistration()

        onView(isRoot()).perform(TestUtils.waitFor(2000))

        val editTextLastName = onView(TestUtils.findCustomEditText(R.id.etLastName))
        editTextLastName.perform(replaceText("Иванов"))

        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val editTextFirstName = onView(TestUtils.findCustomEditText(R.id.etFirstName))
        editTextFirstName.perform(replaceText("Иван"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val editTextMiddleName = onView(TestUtils.findCustomEditText(R.id.etMiddleName))
        editTextMiddleName.perform(replaceText("Иванович"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val editTextLogin = onView(TestUtils.findCustomEditText(R.id.etLogin))
        editTextLogin.perform(replaceText("testmail@gmail.com"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val birthdayView = onView(TestUtils.findCustomMenuText(R.id.etBirthday))
        birthdayView.perform(replaceText("14.10.1996"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewPasswordOne = onView(allOf(withId(R.id.etPasswordOne), isDisplayed()))
        viewPasswordOne.perform(replaceText("Qwerty123"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewPasswordTwo = onView(allOf(withId(R.id.etPasswordTwo), isDisplayed()))
        viewPasswordTwo.perform(replaceText("Qwerty123"))
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val viewCheckBox = onView(allOf(withId(R.id.sc_mobile_phone)))
        viewCheckBox.perform(scrollTo())
        viewCheckBox.perform(click())
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        TestUtils.doScreenShot("Register-Data-Full")

        val viewButtonSave = onView(allOf(withId(R.id.btn_save)))
        viewButtonSave.perform(scrollTo())
        onView(isRoot()).perform(TestUtils.waitFor(1000))
        viewButtonSave.perform(click())

        onView(isRoot()).perform(TestUtils.waitFor(5000))
        TestUtils.doScreenShot("Register-Data-Saved")


        val codeView = onView(allOf(withId(R.id.email_code_view)))
        codeView.perform(setConfirmationCode("998877"))
        onView(isRoot()).perform(TestUtils.waitFor(5000))

        TestUtils.doScreenShot("Confirmation-Code-Entered")

        val viewButtonConfirm = onView(allOf(withId(R.id.btnConfirm)))
        viewButtonConfirm.perform(click())

        TestUtils.doScreenShot("Registration-is-successfully")
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

    private fun setConfirmationCode(code: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(
                    withClassName(Matchers.`is`("com.example.ui.views.codeView.CodeConfirmationView")),
                    isDisplayed()
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
}
