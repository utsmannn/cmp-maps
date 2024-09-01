package navigation

import androidx.compose.runtime.staticCompositionLocalOf

interface Navigator {

    fun navigate(navTarget: NavTarget)
    fun newRoot(navTarget: NavTarget)
    fun back()
    fun quit()
}

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("navigator not provided") }