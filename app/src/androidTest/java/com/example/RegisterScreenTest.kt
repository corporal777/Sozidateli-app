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
class RegisterScreenTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun registerScreenTest() {
        val appCompatButton = onView(
allOf(withId(R.id.ibEmail), withText("Зарегистрироваться"),
childAtPosition(
allOf(withId(R.id.llEmail),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
2)),
1)))
        appCompatButton.perform(scrollTo(), click())
        
        val textInputEditText = onView(
allOf(withId(R.id.et_last_name),
childAtPosition(
childAtPosition(
withId(R.id.til_last_name),
0),
0)))
        textInputEditText.perform(scrollTo(), replaceText("Белов"), closeSoftKeyboard())
        
        val textInputEditText2 = onView(
allOf(withId(R.id.et_first_name),
childAtPosition(
childAtPosition(
withId(R.id.til_first_name),
0),
0)))
        textInputEditText2.perform(scrollTo(), replaceText("Сергей"), closeSoftKeyboard())
        
        val textInputEditText3 = onView(
allOf(withId(R.id.et_first_name), withText("Сергей"),
childAtPosition(
childAtPosition(
withId(R.id.til_first_name),
0),
0)))
        textInputEditText3.perform(pressImeActionButton())
        
        val appCompatCheckBox = onView(
allOf(withId(R.id.sc_no_middle_name), withText("Нет отчества"),
childAtPosition(
allOf(withId(R.id.til_middle_name),
childAtPosition(
withId(R.id.ln_content),
5)),
1)))
        appCompatCheckBox.perform(scrollTo(), click())
        
        val textInputEditText4 = onView(
allOf(withId(R.id.et_email),
childAtPosition(
childAtPosition(
withId(R.id.til_email),
0),
0)))
        textInputEditText4.perform(scrollTo(), replaceText("+79263450000"), closeSoftKeyboard())
        
        val textInputEditText5 = onView(
allOf(withId(R.id.etPassword),
childAtPosition(
childAtPosition(
withId(R.id.tilPassword),
0),
0),
isDisplayed()))
        textInputEditText5.perform(replaceText("Qwerty123"), closeSoftKeyboard())
        
        val textInputEditText6 = onView(
allOf(withId(R.id.etPasswordConfirm),
childAtPosition(
childAtPosition(
withId(R.id.tilPasswordConfirm),
0),
0),
isDisplayed()))
        textInputEditText6.perform(replaceText("Qwerty123"), closeSoftKeyboard())
        
        val frameLayout = onView(
allOf(withId(R.id.flAgree),
childAtPosition(
allOf(withId(R.id.llAgree),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
8)),
0),
isDisplayed()))
        frameLayout.perform(click())
        
        val appCompatButton2 = onView(
allOf(withId(R.id.ib_register), withText("Зарегистрироваться"),
childAtPosition(
allOf(withId(R.id.ln_content),
childAtPosition(
withId(R.id.sv_content),
0)),
9)))
        appCompatButton2.perform(scrollTo(), click())
        
        val textInputEditText7 = onView(
allOf(withId(R.id.etCode),
childAtPosition(
childAtPosition(
withId(R.id.tilCode),
0),
0)))
        textInputEditText7.perform(scrollTo(), replaceText("998877"), closeSoftKeyboard())
        
        val textInputEditText8 = onView(
allOf(withId(R.id.etCode), withText("998877"),
childAtPosition(
childAtPosition(
withId(R.id.tilCode),
0),
0)))
        textInputEditText8.perform(pressImeActionButton())
        
        val appCompatButton3 = onView(
allOf(withId(R.id.ibRegister), withText("Зарегистрироваться"),
childAtPosition(
childAtPosition(
withId(R.id.svContent),
0),
11)))
        appCompatButton3.perform(scrollTo(), click())
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
