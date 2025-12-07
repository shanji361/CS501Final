package com.example.beautyapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.beautyapp.data.Product
import org.junit.Rule
import org.junit.Test

/**
 * StoreFinderScreenTest - Simplified UI tests for StoreFinderScreen
 *
 * Tests basic UI elements without complex mocking
 */
class StoreFinderScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Test 1: Back button displays
    @Test
    fun topBar_displaysBackButton() {
        // Arrange: Create a minimal product for testing
        val testProduct = createTestProduct()

        // Act
        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        // Assert: Back button exists
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    // Test 2: Title displays
    @Test
    fun topBar_displaysTitle() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Find Nearby Stores")
            .assertIsDisplayed()
    }

    // Test 3: Product name displays
    @Test
    fun initialScreen_displaysProductName() {
        val testProduct = createTestProduct(name = "Test Foundation")

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Test Foundation")
            .assertIsDisplayed()
    }

    // Test 4: "Show Nearby Stores" button exists
    @Test
    fun initialScreen_showsSearchButton() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Show Nearby Stores")
            .assertIsDisplayed()
    }

    // Test 5: Back button is clickable
    @Test
    fun backButton_isClickable() {
        val testProduct = createTestProduct()
        var backClicked = false

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { backClicked = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked) { "Back button was not clicked" }
    }

    // Test 6: "Searching for:" label displays
    @Test
    fun initialScreen_displaysSearchingForLabel() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Searching for:")
            .assertIsDisplayed()
    }

    // Test 7: Location icon displays
    @Test
    fun initialScreen_displaysLocationIcon() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Location")
            .assertIsDisplayed()
    }

    // Test 8: Descriptive text displays
    @Test
    fun initialScreen_displaysDescriptiveText() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("We'll show you nearby stores on a map")
            .assertIsDisplayed()
    }

    // Test 9: Alternative button displays
    @Test
    fun initialScreen_displaysAlternativeButton() {
        val testProduct = createTestProduct()

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Open in Google Maps without location")
            .assertIsDisplayed()
    }

    // Test 10: Product brand displays when available
    @Test
    fun initialScreen_displaysProductBrand() {
        val testProduct = createTestProduct(
            name = "Foundation",
            brand = "Maybelline"
        )

        composeTestRule.setContent {
            StoreFinderScreen(
                product = testProduct,
                onBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("Maybelline")
            .assertIsDisplayed()
    }

    // Helper function to create test product with minimal required fields
    private fun createTestProduct(
        name: String = "Test Product",
        brand: String = "Test Brand"
    ): Product {
        return Product(
            id = 1,
            brand = brand,
            name = name,
            price = "10.00",
            priceSign = "$",
            currency = "USD",
            imageLink = "",
            productLink = "",
            websiteLink = "",
            description = "",
            rating = null,
            category = "",
            productType = "",
            tagList = emptyList(),
            createdAt = "",
            updatedAt = "",
            productApiUrl = "",
            apiFeaturedImage = "",
            productColors = emptyList()
        )
    }
}