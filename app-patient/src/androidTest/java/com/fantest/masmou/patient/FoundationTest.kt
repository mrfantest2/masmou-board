package com.fantest.masmou.patient

import android.content.res.Configuration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoundationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchShowsPatientShellInLandscape() {
        compose.onNodeWithText(compose.activity.getString(R.string.communication_area_title)).assertIsDisplayed()
        assertEquals(Configuration.ORIENTATION_LANDSCAPE, compose.activity.resources.configuration.orientation)
    }

    @Test
    fun arabicResourcesUseRightToLeftLayout() {
        val config = Configuration(compose.activity.resources.configuration)
        config.setLocale(Locale.forLanguageTag("ar"))
        val context = compose.activity.createConfigurationContext(config)
        assertEquals("مساحة التواصل", context.getString(R.string.communication_area_title))
        assertEquals(android.view.View.LAYOUT_DIRECTION_RTL, context.resources.configuration.layoutDirection)
    }

    @Test
    fun englishResourcesUseLeftToRightLayout() {
        val config = Configuration(compose.activity.resources.configuration)
        config.setLocale(Locale.ENGLISH)
        val context = compose.activity.createConfigurationContext(config)
        assertEquals("Communication area", context.getString(R.string.communication_area_title))
        assertEquals(android.view.View.LAYOUT_DIRECTION_LTR, context.resources.configuration.layoutDirection)
    }
}
