import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleMaps.GMSServices
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.netfox.ENFXGesture
import cocoapods.netfox.ENFXGestureShake
import cocoapods.netfox.NFX
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.darwin.NSObject
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "AppDelegateAdapter")
class AppDelegateAdapter {

    @OptIn(ExperimentalForeignApi::class)
    fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        println("first launch from kotlin")

        NFX.sharedInstance().start()

        GMSServices.provideAPIKey("AIzaSyA0m6uScEWSqH83f1qNjTDOExTyoa7TNdw")

        FIRAuth.initialize()
        FIRApp.configure()
        return true
    }

    @OptIn(ExperimentalForeignApi::class)
    fun application(app: UIApplication, openURL: NSURL, options: Map<Any?, *>): Boolean {
        return GIDSignIn.sharedInstance.handleURL(openURL)
    }


}