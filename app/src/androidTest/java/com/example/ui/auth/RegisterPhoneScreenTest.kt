package com.example.ui.auth

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.R
import com.example.TestUtils
import com.example.ui.main.MainActivity
import com.example.ui.views.codeView.CodeConfirmationView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(androidx.test.runner.AndroidJUnit4::class)
class RegisterPhoneScreenTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun registerPhoneScreenTest() {
        TestUtils.doScreenShot("Register-Account")

        AuthorizationScreenTest().authorizationWithRegistration()

        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(2000))

        val editTextLastName = Espresso.onView(TestUtils.findCustomEditText(R.id.etLastName))
        editTextLastName.perform(ViewActions.replaceText("Иванов"))

        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val editTextFirstName = Espresso.onView(TestUtils.findCustomEditText(R.id.etFirstName))
        editTextFirstName.perform(ViewActions.replaceText("Иван"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val editTextMiddleName = Espresso.onView(TestUtils.findCustomEditText(R.id.etMiddleName))
        editTextMiddleName.perform(ViewActions.replaceText("Иванович"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val editTextLogin = Espresso.onView(TestUtils.findCustomEditText(R.id.etLogin))
        editTextLogin.perform(ViewActions.replaceText("+79998882211"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val birthdayView = Espresso.onView(TestUtils.findCustomMenuText(R.id.etBirthday))
        birthdayView.perform(ViewActions.replaceText("14.10.1996"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val viewPasswordOne = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.etPasswordOne),
                ViewMatchers.isDisplayed()
            )
        )
        viewPasswordOne.perform(ViewActions.replaceText("Qwerty123"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val viewPasswordTwo = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.etPasswordTwo),
                ViewMatchers.isDisplayed()
            )
        )
        viewPasswordTwo.perform(ViewActions.replaceText("Qwerty123"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        val viewCheckBox =
            Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.sc_mobile_phone)))
        viewCheckBox.perform(ViewActions.scrollTo())
        viewCheckBox.perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))

        TestUtils.doScreenShot("Register-Data-Full")

        val viewButtonSave = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.btn_save)))
        viewButtonSave.perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(1000))
        viewButtonSave.perform(ViewActions.click())

        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(5000))
        TestUtils.doScreenShot("Register-Data-Saved")


        val codeView = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.phone_code_view)))
        codeView.perform(setConfirmationCode("9988"))
        Espresso.onView(ViewMatchers.isRoot()).perform(TestUtils.waitFor(5000))

        TestUtils.doScreenShot("Confirmation-Code-Entered")

        val viewButtonConfirm =
            Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.btnConfirm)))
        viewButtonConfirm.perform(ViewActions.click())

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
}