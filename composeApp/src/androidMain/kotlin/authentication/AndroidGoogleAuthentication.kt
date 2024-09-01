package authentication

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.utsman.cmpbasic.R
import org.utsman.cmpbasic.SecretConfig

class AndroidGoogleAuthentication(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : GoogleAuthentication {

    private val _isSignIn = mutableStateOf(false)
    override val isSignedIn: State<Boolean>
        get() = _isSignIn

    private val _user = mutableStateOf(getUser())
    override val user: State<User?>
        get() = _user

    override fun checkIsSignIn() {
        _isSignIn.value = getUser() != null
    }

    private val credentialManager = CredentialManager.create(context)

    override fun signIn() {
        if (getUser() == null) {
            coroutineScope.launch {
                signInWithIdentity()
            }
        } else {
            _isSignIn.value = true
        }
    }

    override fun signOut() {
        coroutineScope.launch {
            val clearCredentialStateRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearCredentialStateRequest)
            Firebase.auth.signOut()
            _isSignIn.value = false
        }
    }

    private fun signInWithCredential(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result.user
                    _isSignIn.value = firebaseUser != null
                    _user.value = getUser()
                } else {
                    _isSignIn.value = false
                }
            }
    }

    private fun getUser(): authentication.User? {
        val firebaseUser = Firebase.auth.currentUser
        return if (firebaseUser != null) {
            authentication.User(
                name = firebaseUser.displayName.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                photoUrl = firebaseUser.photoUrl.toString()
            )
        } else {
            null
        }
    }

    private suspend fun signInWithIdentity() {
        val webClientId = SecretConfig.WEB_CLIENT_ID

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val resultCredential = result.credential

            val googleCredentialType = GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            if (resultCredential is CustomCredential && resultCredential.type == googleCredentialType) {
                try {
                    val tokenCredential = GoogleIdTokenCredential
                        .createFrom(resultCredential.data)

                    val idToken = tokenCredential.idToken
                    signInWithCredential(idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    _isSignIn.value = false
                }
            }
        } catch (e: GetCredentialException) {
            _isSignIn.value = false
        }
    }

}

@Composable
actual fun rememberGoogleAuthentication(): GoogleAuthentication {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val androidGoogleAuthentication = remember {
        AndroidGoogleAuthentication(context, coroutineScope)
    }

    return androidGoogleAuthentication
}