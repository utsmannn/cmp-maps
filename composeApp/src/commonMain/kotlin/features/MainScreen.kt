package features

import GoogleSignInButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import authentication.rememberGoogleAuthentication
import navigation.LocalNavigator
import navigation.NavTarget

@Composable
fun MainScreen() {

    val navigator = LocalNavigator.current

    val googleAuthentication = rememberGoogleAuthentication()
    val result by googleAuthentication.isSignedIn

    var counter by remember { mutableStateOf(0) }

    LaunchedEffect(counter) {
        //

        if (counter == 9) {

        }
    }

    DisposableEffect(Unit) {
        onDispose {

        }
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {
                navigator.navigate(NavTarget.SearchLocation)
            }
        ) {
            Text("Search location")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                navigator.navigate(NavTarget.ReverseLocation)
            }
        ) {
            Text("Reversed location")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                navigator.navigate(NavTarget.GoogleMaps)
            }
        ) {
            Text("Google Maps")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                navigator.navigate(NavTarget.FeatureMaps)
            }
        ) {
            Text("Maps Feature")
        }

        Spacer(Modifier.height(12.dp))

        GoogleSignInButton(
            onClick = {
                googleAuthentication.signIn()
            }
        )

        Button(
            onClick = {
                googleAuthentication.signOut()
            }
        ) {
            Text("Google Sign Out")
        }

    }

}