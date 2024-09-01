package authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
class IosGoogleAuthentication(
    private val uiViewController: UIViewController
) : GoogleAuthentication {

    private val _isSignedIn = mutableStateOf(false)
    override val isSignedIn: State<Boolean>
        get() = _isSignedIn

    private val _user = mutableStateOf(getUser())
    override val user: State<User?>
        get() = _user

    override fun checkIsSignIn() {
        _isSignedIn.value = getUser() != null
    }

    init {
        val clientId = FIRApp.defaultApp()?.options()?.clientID
        if (clientId != null) {
            val config = GIDConfiguration(clientId)
            GIDSignIn.sharedInstance.configuration = config
        }
    }

    override fun signIn() {
        val user = FIRAuth.auth().currentUser
        if (user == null) {
            GIDSignIn.sharedInstance.signInWithPresentingViewController(uiViewController) { result, error ->
                if (result != null) {
                    val googleUser = result.user
                    val idToken = googleUser.idToken?.tokenString

                    if (idToken != null) {
                        val accessToken = googleUser.accessToken.tokenString
                        val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                            IDToken = idToken,
                            accessToken = accessToken
                        )

                        FIRAuth.auth().signInWithCredential(credential) { firResult, firError ->
                            _isSignedIn.value = firResult != null && firError == null
                        }

                    } else {
                        _isSignedIn.value = false
                    }

                } else {
                    _isSignedIn.value = false
                }
            }
        } else {
            _isSignedIn.value = true
        }
    }

    override fun signOut() {
        FIRAuth.auth().signOut(null)
        _isSignedIn.value = false
    }

    private fun getUser(): authentication.User? {
        val firebaseUser = FIRAuth.auth().currentUser
        return if (firebaseUser != null) {
            authentication.User(
                name = firebaseUser.displayName.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                photoUrl = firebaseUser.photoURL?.absoluteString
            )
        } else {
            null
        }
    }
}

@Composable
actual fun rememberGoogleAuthentication(): GoogleAuthentication {
    val uiViewController = LocalUIViewController.current
    return remember { IosGoogleAuthentication(uiViewController) }
}