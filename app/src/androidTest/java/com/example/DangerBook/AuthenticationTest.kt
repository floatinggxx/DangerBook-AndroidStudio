package com.example.DangerBook

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.DangerBook.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AuthenticationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fromHomeScreen_navigateToLogin_and_assertIsDisplayed() {
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onNodeWithText("Iniciar Sesión").assertExists()
    }
}