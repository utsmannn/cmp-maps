package navigation

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

sealed class NavTarget : Parcelable {

    @Parcelize
    data object Main : NavTarget()

    @Parcelize
    data object SearchLocation : NavTarget()

    @Parcelize
    data object ReverseLocation : NavTarget()

    @Parcelize
    data object GoogleMaps : NavTarget()

    @Parcelize
    data object FeatureMaps : NavTarget()

    @Parcelize
    data object SignInResult : NavTarget()

    @Parcelize
    data object Splash : NavTarget()

    @Parcelize
    data object SignIn : NavTarget()
}