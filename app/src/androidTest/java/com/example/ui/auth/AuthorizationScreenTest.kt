package com.example.ui.auth


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.R
import com.example.TestUtils
import com.example.ui.main.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthorizationScreenTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun authorizationWithLogin() {
        val viewInteraction = onView(
            allOf(withId(R.id.btnLogin), isDisplayed())
        )
        onView(isRoot()).perform(TestUtils.waitFor(2000))
        viewInteraction.perform(click())
    }

    @Test
    fun authorizationWithRegistration() {
        val viewInteraction = onView(allOf(withId(R.id.btnRegister), isDisplayed()))
        onView(isRoot()).perform(TestUtils.waitFor(2000))
        viewInteraction.perform(click())
    }
}
