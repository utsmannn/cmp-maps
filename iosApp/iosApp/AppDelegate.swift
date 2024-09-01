//
//  AppDelegate.swift
//  iosApp
//
//  Created by Muhammad Utsman on 16/06/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import ComposeApp

class AppDelegate : NSObject, UIApplicationDelegate {
    
    private let appDelegateAdapter = AppDelegateAdapter()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        return appDelegateAdapter.application(application: application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    
    func application(_ app: UIApplication,
                     open url: URL,
                     options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        return appDelegateAdapter.application(app: app, openURL: url, options: options)
    }
}
