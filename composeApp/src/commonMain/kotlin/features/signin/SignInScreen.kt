package features.signin

import GoogleSignInButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import authentication.rememberGoogleAuthentication
import navigation.LocalNavigator
import navigation.NavTarget

@Composable
fun SignInScreen() {
    val googleAuthentication = rememberGoogleAuthentication()
    val isSignIn by googleAuthentication.isSignedIn

    val navigator = LocalNavigator.current

    LaunchedEffect(isSignIn) {
        if (isSignIn) {
            navigator.newRoot(NavTarget.FeatureMaps)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        Text(
            text = "Welcome to Application",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(0.3f))

        GoogleSignInButton {
            googleAuthentication.signIn()
        }

        Spacer(Modifier.weight(1f))
    }

}