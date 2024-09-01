import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val isAndroid: Boolean

@Composable
expect fun BackPress(enable: Boolean, handler: () -> Unit)

expect fun quitApp()