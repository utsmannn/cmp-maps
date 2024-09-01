package authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

interface GoogleAuthentication {
    val isSignedIn: State<Boolean>
    val user: State<User?>

    fun checkIsSignIn()
    fun signIn()
    fun signOut()
}

@Composable
expect fun rememberGoogleAuthentication(): GoogleAuthentication