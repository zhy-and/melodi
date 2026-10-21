package com.extraordinary.weaves.tendency

internal object BackNavigationPolicy {
    enum class ExitAction {
        FIRST_AD,
        SECOND_AD,
        NONE,
    }

    private val firstAdExclusions = listOf(
        "HomeActivity",
        "AppStart",
        "AppGeDanDetail",
        "AppAlbumDetail",
        "AppArtDetail",
        "AppLyricView",
        "FeedView",
        "PrivacyPage",
        "PlayerView",
    )

    private val secondAdExclusions = listOf(
        "HomeActivity",
        "AppStart",
        "AppSearch",
        "AppMoreView",
        "ScanPage",
        "TrackList",
        "AppStyleList",
        "AppAlbumList",
        "AppArtistList",
        "AppGeDanList",
        "PlayerView",
        "AppPlayerList",
    )

    fun exitActionFor(className: String): ExitAction = when {
        firstAdExclusions.none(className::contains) -> ExitAction.FIRST_AD
        secondAdExclusions.none(className::contains) -> ExitAction.SECOND_AD
        else -> ExitAction.NONE
    }
}
