package `in`.vinit.storyghost.core.notification

import `in`.vinit.storyghost.core.common.Logger
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationFilterTest {

    private lateinit var constraintProvider: NotificationConstraintProvider
    private lateinit var logger: Logger
    private lateinit var filter: NotificationFilter

    @Before
    fun setUp() {
        constraintProvider = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        every { constraintProvider.currentVersionCode } returns 100
        coEvery { constraintProvider.isUserPremium() } returns false
        coEvery { constraintProvider.isUserLoggedIn() } returns false

        filter = NotificationFilter(constraintProvider, logger)
    }

    @Test
    fun `shouldDisplay returns true for empty constraints`() = runTest {
        val constraints = NotificationConstraints()
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay drops notification when app versionCode is less than minVersionCode`() = runTest {
        every { constraintProvider.currentVersionCode } returns 99
        val constraints = NotificationConstraints(minVersionCode = 100)
        assertFalse(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay permits notification when app versionCode satisfies minVersionCode`() = runTest {
        every { constraintProvider.currentVersionCode } returns 100
        val constraints = NotificationConstraints(minVersionCode = 100)
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay drops notification when app versionCode exceeds maxVersionCode`() = runTest {
        every { constraintProvider.currentVersionCode } returns 101
        val constraints = NotificationConstraints(maxVersionCode = 100)
        assertFalse(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay permits notification when app versionCode satisfies maxVersionCode`() = runTest {
        every { constraintProvider.currentVersionCode } returns 100
        val constraints = NotificationConstraints(maxVersionCode = 100)
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay evaluates PREMIUM_ONLY constraint correctly`() = runTest {
        val constraints = NotificationConstraints(targetTier = NotificationConstraints.TIER_PREMIUM_ONLY)

        coEvery { constraintProvider.isUserPremium() } returns false
        assertFalse(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserPremium() } returns true
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay evaluates FREE_ONLY constraint correctly`() = runTest {
        val constraints = NotificationConstraints(targetTier = NotificationConstraints.TIER_FREE_ONLY)

        coEvery { constraintProvider.isUserPremium() } returns true
        assertFalse(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserPremium() } returns false
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay permits all tiers when targetTier is ALL`() = runTest {
        val constraints = NotificationConstraints(targetTier = NotificationConstraints.TIER_ALL)

        coEvery { constraintProvider.isUserPremium() } returns false
        assertTrue(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserPremium() } returns true
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay evaluates LOGGED_IN_ONLY constraint correctly`() = runTest {
        val constraints = NotificationConstraints(targetAuth = NotificationConstraints.AUTH_LOGGED_IN_ONLY)

        coEvery { constraintProvider.isUserLoggedIn() } returns false
        assertFalse(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserLoggedIn() } returns true
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay evaluates LOGGED_OUT_ONLY constraint correctly`() = runTest {
        val constraints = NotificationConstraints(targetAuth = NotificationConstraints.AUTH_LOGGED_OUT_ONLY)

        coEvery { constraintProvider.isUserLoggedIn() } returns true
        assertFalse(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserLoggedIn() } returns false
        assertTrue(filter.shouldDisplay(constraints))
    }

    @Test
    fun `shouldDisplay permits all auth states when targetAuth is ANY`() = runTest {
        val constraints = NotificationConstraints(targetAuth = NotificationConstraints.AUTH_ANY)

        coEvery { constraintProvider.isUserLoggedIn() } returns false
        assertTrue(filter.shouldDisplay(constraints))

        coEvery { constraintProvider.isUserLoggedIn() } returns true
        assertTrue(filter.shouldDisplay(constraints))
    }
}
