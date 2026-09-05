package com.fantest.masmou.patient

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PatientShellTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun patientShellShowsPrimaryRegions() {
        compose.onNodeWithText("Communication area").assertIsDisplayed()
        compose.onNodeWithText("Quick actions").assertIsDisplayed()
        compose.onNodeWithText("Tools").assertIsDisplayed()
    }
}
