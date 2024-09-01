package features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import authentication.rememberGoogleAuthentication
import kotlincomposemultiplatform1.composeapp.generated.resources.Res
import kotlincomposemultiplatform1.composeapp.generated.resources.compose_multiplatform
import kotlincomposemultiplatform1.composeapp.generated.resources.ic_firebase_auth
import kotlincomposemultiplatform1.composeapp.generated.resources.ic_google_maps
import kotlinx.coroutines.delay
import navigation.LocalNavigator
import navigation.NavTarget
import navigation.Navigator
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen() {
    val googleAuthentication = rememberGoogleAuthentication()
    val isSignIn by googleAuthentication.isSignedIn

    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        delay(3000)
        googleAuthentication.checkIsSignIn()
    }

    LaunchedEffect(isSignIn) {
        delay(3000)
        if (isSignIn) {
            navigator.newRoot(NavTarget.FeatureMaps)
        } else {
            navigator.newRoot(NavTarget.SignIn)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(0.2f))

        Row(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.width(12.dp))

            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Image(
                painter = painterResource(Res.drawable.ic_google_maps),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Image(
                painter = painterResource(Res.drawable.ic_firebase_auth),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

        }

        Spacer(Modifier.weight(1f))

        CircularProgressIndicator()

        Spacer(Modifier.weight(0.2f))

    }
}