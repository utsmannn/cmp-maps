package navigation

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import features.AuthResultScreen
import features.MainScreen
import features.googlemaps.GoogleMapsScreen
import features.maps.MapsScreen
import features.reversed.ReversedLocationScreen
import features.search.SearchLocationScreen
import features.signin.SignInScreen
import features.splash.SplashScreen
import isAndroid
import quitApp

class RootNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Splash,
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = {
            if (isAndroid) {
                BackStackFader(it, defaultAnimationSpec = spring())
            } else {
                BackStackParallax(it)
            }
        },
        gestureFactory = {
            if (isAndroid) {
                GestureFactory.Noop()
            } else {
                BackStackParallax.Gestures(it)
            }
        }
    )
) : Node<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigator = remember {
            object : Navigator {
                override fun navigate(navTarget: NavTarget) {
                    backStack.push(navTarget)
                }

                override fun newRoot(navTarget: NavTarget) {
                    backStack.newRoot(navTarget)
                }

                override fun back() {
                    backStack.pop()
                }

                override fun quit() {
                    quitApp()
                }
            }
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator,
        ) {
            AppyxNavigationContainer(
                appyxComponent = backStack
            )
        }
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> {
        return when (navTarget) {
            is NavTarget.Main -> node(nodeContext) {
                BoxBackground {
                    MainScreen()
                }
            }

            is NavTarget.SearchLocation -> node(nodeContext) {
                BoxBackground {
                    SearchLocationScreen()
                }
            }

            is NavTarget.ReverseLocation -> node(nodeContext) {
                BoxBackground {
                    ReversedLocationScreen()
                }
            }

            is NavTarget.GoogleMaps -> node(nodeContext) {
                BoxBackground {
                    GoogleMapsScreen()
                }
            }

            is NavTarget.FeatureMaps -> node(nodeContext) {
                BoxBackground {
                    MapsScreen()
                }
            }

            is NavTarget.SignInResult -> node(nodeContext) {
                BoxBackground {
                    AuthResultScreen()
                }
            }
            is NavTarget.Splash -> node(nodeContext) {
                BoxBackground {
                    SplashScreen()
                }
            }
            is NavTarget.SignIn -> node(nodeContext) {
                BoxBackground {
                    SignInScreen()
                }
            }
        }
    }

    @Composable
    private fun BoxBackground(
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White)
        ) {
            content.invoke()
        }
    }
}