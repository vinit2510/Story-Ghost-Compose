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

    fun loadStoryList() {
        viewModelScope.launch {
            _storyList.update { UiState.Loading }
            val result = makeApiCall { storyRepository.getStoryFromFeed() }
            result.fold(
                onSuccess = { response ->
                    val edges = response.data?.user?.feedReelsTray?.edgeReelsTrayToReel?.edges
                        ?: emptyList()
                    _storyList.update { UiState.Success(edges) }
                },
                onFailure = { ex ->
                    if (ex is ApiException && ex.httpCode == 401) {
                        _showLoginDialogTrigger.update { true }
                    }
                    logger.e(TAG, "loadStoryList: Failed to load story list", ex)
                    _storyList.update { result.map { emptyList<Edge>() }.toUiState() }
                }
            )
        }
    }

    fun retry() {
        loadStoryList()
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
                response.users?.mapNotNull { data ->
                    val user = data.user ?: return@mapNotNull null
                    Edge(
                        node = Edge.Node(
                            id = user.id,
                            user = Edge.Node.ReelUser(
                                id = user.id,
                                username = user.username,
                                profilePicUrl = user.profilePicUrl
                            )
                        )
                    )
                } ?: emptyList()
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
            _searchQuery.update { "" }
            _isRemoteSearchActive.update { false }
            _searchResults.update { UiState.Success(emptyList()) }
            _storyList.update { UiState.Success(emptyList()) }
        }
    }

    fun reloadAfterLogin() {
        fetchUserDataIfMissing()
        loadStoryList()
    }
}