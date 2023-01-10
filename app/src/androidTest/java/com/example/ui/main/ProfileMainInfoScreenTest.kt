package com.example.ui.main


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
class ProfileMainInfoScreenTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun profileMainInfoScreenTest() {
        val appCompatButton = onView(
allOf(withId(R.id.ibLogin), withText("Войти"),
childAtPosition(
allOf(withId(R.id.llLogin),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
3)),
1)))
        appCompatButton.perform(scrollTo(), click())
        
        val textInputEditText = onView(
allOf(withId(R.id.etLogin),
childAtPosition(
childAtPosition(
withId(R.id.tilLogin),
0),
0),
isDisplayed()))
        textInputEditText.perform(replaceText("+79263401234"), closeSoftKeyboard())
        
        val textInputEditText2 = onView(
allOf(withId(R.id.etPassword),
childAtPosition(
childAtPosition(
withId(R.id.tilPassword),
0),
0),
isDisplayed()))
        textInputEditText2.perform(replaceText("Qwerty123"), closeSoftKeyboard())
        
        val appCompatButton2 = onView(
allOf(withId(R.id.btn_login), withText("Войти"),
childAtPosition(
allOf(withId(R.id.content),
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0)),
5)))
        appCompatButton2.perform(scrollTo(), click())
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.profile), withContentDescription("Профиль"),
childAtPosition(
childAtPosition(
withId(R.id.main_nav_bar),
0),
4),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val appCompatButton3 = onView(
allOf(withId(R.id.btnEditProfile), withText("Редактировать профиль"),
childAtPosition(
childAtPosition(
withId(R.id.profileScrollView),
0),
2),
isDisplayed()))
        appCompatButton3.perform(click())
        
        val appCompatButton4 = onView(
allOf(withId(R.id.btnMainInfo), withText("Основная информация"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
1),
0),
isDisplayed()))
        appCompatButton4.perform(click())
        
        val appCompatButton5 = onView(
allOf(withId(R.id.btnEdit), withText("Редактировать"),
childAtPosition(
childAtPosition(
withId(R.id.navHostFragment),
0),
2),
isDisplayed()))
        appCompatButton5.perform(click())
        
        val checkableImageButton = onView(
allOf(withId(com.google.android.material.R.id.text_input_end_icon),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
1),
0),
isDisplayed()))
        checkableImageButton.perform(click())
        
        val materialButton = onView(
allOf(withId(android.R.id.button1), withText("ОК"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton.perform(scrollTo(), click())
        
        val appCompatAutoCompleteTextView = onView(
allOf(withId(R.id.tvGender),
childAtPosition(
childAtPosition(
withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
0),
1),
isDisplayed()))
        appCompatAutoCompleteTextView.perform(click())
        
        val materialTextView = onData(anything())
.inAdapterView(childAtPosition(
withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
0))
.atPosition(0)
        materialTextView.perform(click())
        
        val daDataAutoCompleteTextView = onView(
allOf(withId(R.id.etCity),
childAtPosition(
childAtPosition(
withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
0),
0),
isDisplayed()))
        daDataAutoCompleteTextView.perform(replaceText("мо"), closeSoftKeyboard())
        
        val constraintLayout = onData(anything())
.inAdapterView(childAtPosition(
withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
0))
.atPosition(0)
        constraintLayout.perform(click())
        
        val textInputEditText3 = onView(
allOf(withId(R.id.etNotes),
childAtPosition(
childAtPosition(
withClassName(`is`("com.example.ui.views.CustomTextInputLayout")),
0),
0),
isDisplayed()))
        textInputEditText3.perform(replaceText("Hello world"), closeSoftKeyboard())
        }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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
