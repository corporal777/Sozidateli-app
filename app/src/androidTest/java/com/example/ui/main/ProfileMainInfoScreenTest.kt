package com.example.ui.main


import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.runner.screenshot.Screenshot
import com.example.R
import com.example.TestUtils
import com.example.TestUtils.waitUntilVisible
import com.google.android.material.internal.ContextUtils.getActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProfileMainInfoScreenTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun profileMainInfoScreenTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.btnLogin), withText("Войти"),
                childAtPosition(
                    allOf(
                        withId(R.id.content),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    1
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val textInputLogin = onView(allOf(withId(R.id.etLogin), isDisplayed()))
        textInputLogin.perform(replaceText("+79263401234"), closeSoftKeyboard())

        val textInputPassword = onView(allOf(withId(R.id.etPassword), isDisplayed()))
        textInputPassword.perform(replaceText("Qwerty123"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btn_login), withText("Войти"),
                childAtPosition(
                    allOf(
                        withId(R.id.content),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    5
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        onView(isRoot()).perform(TestUtils.waitFor(10000))
        Screenshot.capture().setName("Root").setFormat(Bitmap.CompressFormat.JPEG).process();

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.profile), withContentDescription("Профиль"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_nav_bar),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())
        Screenshot.capture().setName("Profile").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.btnEditProfile), withText("Редактировать профиль"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.profileScrollView),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())
        Screenshot.capture().setName("Edit-Profile").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.btnMainInfo), withText("Основная информация"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())
        Screenshot.capture().setName("Profile-Main").setFormat(Bitmap.CompressFormat.JPEG).process();

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.btnEdit), withText("Редактировать"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navHostFragment),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())
        Screenshot.capture().setName("Profile-Main-Edit").setFormat(Bitmap.CompressFormat.JPEG).process();
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        //main data edit test
        //birthday
        val textInputBirthday = onView(allOf(withId(R.id.etBirthday), isDisplayed()))
        textInputBirthday.perform(click())

        val materialButton = onView(
            allOf(
                withId(android.R.id.button1), withText("ОК"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton.perform(scrollTo(), click())
        Screenshot.capture().setName("Profile-Birthday").setFormat(Bitmap.CompressFormat.JPEG).process();
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        //gender
        val genderTextView = onView(
            allOf(
                withId(R.id.tvGender),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        genderTextView.perform(click())

        val genderItemTextView = onData(anything())
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0)
        genderItemTextView.perform(click())
        onView(isRoot()).perform(TestUtils.waitFor(1000))
        Screenshot.capture().setName("Profile-Gender").setFormat(Bitmap.CompressFormat.JPEG).process();

        //city
        val cityTextView = onView(
            allOf(
                withId(R.id.etCode),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        cityTextView.perform(replaceText("г. Москва"), closeSoftKeyboard())
        onView(isRoot()).perform(TestUtils.waitFor(1000))
        Screenshot.capture().setName("Profile-City").setFormat(Bitmap.CompressFormat.JPEG).process();

//        val decorView = withDecorView(not(TestUtils.currentActivity(mActivityScenarioRule)?.window?.decorView))
//        val cityItemTextView = onData(anything())
//            .inRoot(RootMatchers.isPlatformPopup())
//            .onChildView(withId(android.R.id.text1))
//            .atPosition(0)
//        cityItemTextView
//            //.waitUntilVisible(3000)
//            .perform(click())

        val textInputNotes = onView(
            allOf(
                withId(R.id.etNotes),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputNotes.perform(scrollTo())
        textInputNotes.perform(replaceText("Hello world"), closeSoftKeyboard())
        Screenshot.capture().setName("Profile-Notes").setFormat(Bitmap.CompressFormat.JPEG).process();
        onView(isRoot()).perform(TestUtils.waitFor(1000))

        val appCompatButtonSave = onView(
            allOf(
                withId(R.id.btnSave), withText("Сохранить"),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    1
                )
            )
        )
        appCompatButtonSave.perform(click())
        onView(isRoot()).perform(TestUtils.waitFor(5000))
        Screenshot.capture().setName("Profile-Data-Saved").setFormat(Bitmap.CompressFormat.JPEG).process();
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
