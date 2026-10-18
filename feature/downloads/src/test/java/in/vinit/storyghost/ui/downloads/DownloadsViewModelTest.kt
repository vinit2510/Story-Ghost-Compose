package `in`.vinit.storyghost.ui.downloads

import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.downloads.DownloadsRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadsViewModelTest {

    private val downloadsRepository: DownloadsRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DownloadsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads downloaded files successfully`() = runTest {
        val mockFiles = listOf(File("test_file_1.jpg"), File("test_file_2.mp4"))
        coEvery { downloadsRepository.getDownloadedFiles() } returns mockFiles

        viewModel = DownloadsViewModel(downloadsRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.fileList.value
        assertTrue(state is UiState.Success)
        assertEquals(mockFiles, (state as UiState.Success).data)
    }

    @Test
    fun `loadDownloads failure sets UiState Error and logs`() = runTest {
        coEvery { downloadsRepository.getDownloadedFiles() } throws RuntimeException("Storage error")

        viewModel = DownloadsViewModel(downloadsRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.fileList.value
        assertTrue(state is UiState.Error)
        coVerify(atLeast = 1) {
            logger.e(
                "DownloadsViewModel",
                "loadDownloads: Failed to load downloaded files",
                any()
            )
        }
    }

    @Test
    fun `deleteDownload success triggers callback and reloads files`() = runTest {
        val fileToDelete = File("delete_me.jpg")
        var callbackInvoked = false
        coEvery { downloadsRepository.getDownloadedFiles() } returns listOf(fileToDelete)
        coEvery { downloadsRepository.deleteFile(fileToDelete) } returns true

        viewModel = DownloadsViewModel(downloadsRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteDownload(fileToDelete) {
            callbackInvoked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(callbackInvoked)
        coVerify(exactly = 1) { downloadsRepository.deleteFile(fileToDelete) }
        coVerify(exactly = 2) { downloadsRepository.getDownloadedFiles() }
    }

    @Test
    fun `deleteDownload failure logs warning and does not invoke callback`() = runTest {
        val fileToDelete = File("delete_me.jpg")
        var callbackInvoked = false
        coEvery { downloadsRepository.getDownloadedFiles() } returns listOf(fileToDelete)
        coEvery { downloadsRepository.deleteFile(fileToDelete) } returns false

        viewModel = DownloadsViewModel(downloadsRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteDownload(fileToDelete) {
            callbackInvoked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, callbackInvoked)
        coVerify(exactly = 1) {
            logger.w(
                "DownloadsViewModel",
                "deleteDownload: Failed to delete file ${fileToDelete.name}"
            )
        }
    }
}
