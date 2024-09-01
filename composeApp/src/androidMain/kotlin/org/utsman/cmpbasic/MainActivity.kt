package org.utsman.cmpbasic

import App
import FinishDelegate
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import navigation.RootNode

class MainActivity : NodeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FinishDelegate.onFinish = {
            finish()
        }

        setContent {
            enableEdgeToEdge()
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                val lifecycleOwner = LocalLifecycleOwner.current
                NodeHost(
                    lifecycle = AndroidLifecycle(lifecycleOwner.lifecycle),
                    integrationPoint = appyxIntegrationPoint
                ) {
                    RootNode(it)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}