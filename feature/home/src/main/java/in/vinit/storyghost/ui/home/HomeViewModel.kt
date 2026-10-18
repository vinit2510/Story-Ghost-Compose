package `in`.vinit.storyghost.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.network.ApiException
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.core.network.makeApiCall
import `in`.vinit.storyghost.data.billing.BillingRepository
import `in`.vinit.storyghost.data.model.StoryListResponse.Data.User.FeedReelsTray.EdgeReelsTrayToReel.Edge
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.data.user.UserRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.toUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
const val SEARCH_THRESHOLD = 3

/**
 * ViewModel governing the primary Home screen feed and user search experience.
 *
 * Responsibilities:
 * - Initiates subscription verification on startup via [BillingRepository].
 * - Automatically loads and paginates stories from the user's following list via [StoryRepository].
 * - Implements dual-mode search:
 *   - Local instant filtering for short queries or when browsing cached followings.
 *   - Remote query execution with 500ms debounce when search query length >= [SEARCH_THRESHOLD].
 * - Supports toggling between Story and Highlight viewing modes ([isHighlightMode]).
 * - Manages session authentication state and initiates logout.
 */
@OptIn(FlowPreview::class)
class HomeViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    private val localStorage: LocalStorage,
    billingRepository: BillingRepository,
    private val logger: Logger
) : ViewModel() {

    private val _storyList = MutableStateFlow<UiState<List<Edge>>>(UiState.Loading)

    private val _searchResults = MutableStateFlow<UiState<List<Edge>>>(UiState.Success(emptyList()))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isHighlightMode = MutableStateFlow(false)
    val isHighlightMode = _isHighlightMode.asStateFlow()

    private val _showLoginDialogTrigger = MutableStateFlow(false)
    val showLoginDialogTrigger = _showLoginDialogTrigger.asStateFlow()

    private var nextMaxId: String? = null
    private var hasMoreFollowings: Boolean = true
    private var isLoadingFollowing: Boolean = false
    private val _isPaginating = MutableStateFlow(false)
    val isPaginating = _isPaginating.asStateFlow()
    private val storyDataset = mutableListOf<Edge>()

    fun onLoginDialogTriggerHandled() {
        _showLoginDialogTrigger.update { false }
    }

    val profilePicUrl = localStorage.getUserData()
        .map { json ->
            if (json.isNullOrBlank()) null
            else {
                try {
                    Gson().fromJson(
                        json,
                        UserInfoResponse::class.java
                    )?.data?.userContainer?.reel?.user?.profilePicUrl
                } catch (_: Exception) {
                    null
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn = localStorage.isLoggedIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isPremium = localStorage.isPremium()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isRemoteSearchActive = MutableStateFlow(false)

    val displayList =
        combine(
            _searchQuery,
            _storyList,
            _searchResults,
            _isRemoteSearchActive
        ) { query, storiesState, results, remoteActive ->
            if (remoteActive) {
                results
            } else {
                if (storiesState is UiState.Success) {
                    val filtered = storiesState.data.filter { edge ->
                        edge.node?.user?.username?.lowercase()?.contains(query.lowercase()) == true
                    }
                    UiState.Success(filtered)
                } else {
                    storiesState
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        viewModelScope.launch {
            localStorage.isLoggedIn()
                .distinctUntilChanged()
                .drop(1)
                .filter { it }
                .collect {
                    reloadAfterLogin()
                }
        }

        billingRepository.checkSubscription()
        loadStoryList()
        fetchUserDataIfMissing()
    }

    fun loadStoryList(isRefresh: Boolean = false) {
        viewModelScope.launch {
            val userId = localStorage.getUserId().first()
            if (userId.isNullOrBlank()) {
                _showLoginDialogTrigger.update { true }
                _storyList.update { UiState.Success(emptyList()) }
                return@launch
            }

            if (isRefresh) {
                nextMaxId = null
                hasMoreFollowings = true
                storyDataset.clear()
                _storyList.update { UiState.Loading }
            }

            if (isLoadingFollowing || (!hasMoreFollowings && !isRefresh)) return@launch
            isLoadingFollowing = true

            if (storyDataset.isEmpty() && !isRefresh) {
                _storyList.update { UiState.Loading }
            }

            val result = makeApiCall { storyRepository.getFollowing(userId, nextMaxId) }
            isLoadingFollowing = false

            result.fold(
                onSuccess = { response ->
                    nextMaxId = response.nextMaxId
                    hasMoreFollowings = response.hasMore == true && !nextMaxId.isNullOrEmpty()

                    val newActiveStories = response.users
                        ?.filter { (it.latestReelMedia ?: 0L) != 0L }
                        ?.map { user ->
                            Edge(
                                node = Edge.Node(
                                    id = user.id ?: user.pkId?.toString(),
                                    user = Edge.Node.ReelUser(
                                        id = user.id ?: user.pkId?.toString(),
                                        username = user.username,
                                        profilePicUrl = user.profilePicUrl
                                    )
                                )
                            )
                        } ?: emptyList()

                    storyDataset.addAll(newActiveStories)
                    _storyList.update { UiState.Success(storyDataset.toList()) }

                    if (storyDataset.size < 6 && hasMoreFollowings) {
                        loadStoryList(isRefresh = false)
                    }
                },
                onFailure = { ex ->
                    if (ex is ApiException && (ex.httpCode == 401 || ex.httpCode == 302)) {
                        _showLoginDialogTrigger.update { true }
                    }
                    logger.e(TAG, "loadStoryList: Failed to load following story list", ex)
                    if (storyDataset.isEmpty()) {
                        _storyList.update { result.map { emptyList<Edge>() }.toUiState() }
                    }
                }
            )
        }
    }

    fun loadNextPage() {
        val currentQuery = _searchQuery.value.trim()
        if (currentQuery.isNotEmpty() || _isRemoteSearchActive.value || isLoadingFollowing || !hasMoreFollowings) {
            return
        }

        viewModelScope.launch {
            val userId = localStorage.getUserId().first() ?: return@launch

            isLoadingFollowing = true
            _isPaginating.update { true }

            val result = makeApiCall { storyRepository.getFollowing(userId, nextMaxId) }
            isLoadingFollowing = false
            _isPaginating.update { false }

            result.fold(
                onSuccess = { response ->
                    nextMaxId = response.nextMaxId
                    hasMoreFollowings = response.hasMore == true && !nextMaxId.isNullOrEmpty()

                    val newActiveStories = response.users
                        ?.filter { (it.latestReelMedia ?: 0L) != 0L }
                        ?.map { user ->
                            Edge(
                                node = Edge.Node(
                                    id = user.id ?: user.pkId?.toString(),
                                    user = Edge.Node.ReelUser(
                                        id = user.id ?: user.pkId?.toString(),
                                        username = user.username,
                                        profilePicUrl = user.profilePicUrl
                                    )
                                )
                            )
                        } ?: emptyList()

                    storyDataset.addAll(newActiveStories)
                    _storyList.update { UiState.Success(storyDataset.toList()) }
                },
                onFailure = { ex ->
                    logger.e(TAG, "loadNextPage: Failed to load next page", ex)
                }
            )
        }
    }

    fun retry() {
        loadStoryList(isRefresh = true)
    }

    private fun fetchUserDataIfMissing() {
        viewModelScope.launch {
            val userJson = localStorage.getUserData().first()
            if (userJson.isNullOrBlank()) {
                val userId = localStorage.getUserId().first()
                if (!userId.isNullOrBlank()) {
                    val result = makeApiCall { userRepository.getUserInfoByUserId(userId) }
                    result.fold(
                        onSuccess = { data ->
                            val json = Gson().toJson(data)
                            localStorage.saveUserData(json)
                        },
                        onFailure = { ex ->
                            logger.e(
                                TAG,
                                "fetchUserDataIfMissing: Failed to fetch user data for missing user ID: $userId",
                                ex
                            )
                        }
                    )
                }
            }
        }
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        if (query.length >= SEARCH_THRESHOLD) {
            _isRemoteSearchActive.update { true }
            performRemoteSearch(query)
        }
    }

    private fun performRemoteSearch(query: String) {
        viewModelScope.launch {
            _searchResults.update { UiState.Loading }
            val result = makeApiCall {
                val response = userRepository.searchUsersByQuery(query)
                response.allUsers.mapNotNull { data ->
                    val user = data.user ?: return@mapNotNull null
                    val userId = user.userId ?: return@mapNotNull null
                    Edge(
                        node = Edge.Node(
                            id = userId,
                            user = Edge.Node.ReelUser(
                                id = userId,
                                username = user.username,
                                profilePicUrl = user.profilePicUrl
                            )
                        )
                    )
                }
            }
            if (result.isFailure) {
                logger.e(
                    TAG,
                    "performRemoteSearch: Failed to perform remote search for query: $query",
                    result.exceptionOrNull()
                )
            }
            _searchResults.update { result.toUiState() }
        }
    }

    fun onSearchQueryChange(query: String) {
        if (_searchQuery.value == query) return
        _searchQuery.update { query }
        _isRemoteSearchActive.update { false }
        _searchResults.update { UiState.Success(emptyList()) }
    }

    fun toggleHighlightMode() {
        _isHighlightMode.update { !it }
    }

    fun logout() {
        viewModelScope.launch {
            localStorage.clearAuthentication()
            nextMaxId = null
            hasMoreFollowings = true
            storyDataset.clear()
            _searchQuery.update { "" }
            _isRemoteSearchActive.update { false }
            _searchResults.update { UiState.Success(emptyList()) }
            _storyList.update { UiState.Success(emptyList()) }
        }
    }

    fun reloadAfterLogin() {
        fetchUserDataIfMissing()
        loadStoryList(isRefresh = true)
    }
}