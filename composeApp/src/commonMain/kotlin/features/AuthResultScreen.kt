package features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import authentication.rememberGoogleAuthentication

@Composable
fun AuthResultScreen() {

    val googleAuthentication = rememberGoogleAuthentication()

    val user by googleAuthentication.user

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign in as: ${user?.email}")
    }
}