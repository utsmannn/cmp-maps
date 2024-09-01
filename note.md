# Welcome to Kotlin Compose Multiplatform for Android and IOS by Utsman
Networking, Google Maps and Firebase Authentication from basic to advanced level

- Why networking?
- Why Google Maps?
- Why firebase authentication?

Goal project:
Google Maps Location Picker

# basic
- Download android studio
- Download xcode
- Plugin kotlin multiplatform
- starter project --> https://kmp.jetbrains.com/

## How it works
- Expect-actual concept
- Swift class interoperability in kotlin

### Ios side
- Kotlin code generated to swift code
- Swift class interopable to kotlin code
- Xcode runner run gradle script

### Android side
- Android Compose as usual

## Coding
### Phase 1

#### Build Config
Create own build config generator:
    - Works with `secret.properties`
    - Created task for generate properties from `secret.properties`
    - Run generate task depend on existing task when sync process

#### Networking
- Ktor: https://ktor.io/docs/client-create-multiplatform-application.html
- Kotlin Serializer plugin: https://github.com/Kotlin/kotlinx.serialization
- Try with reqres api: https://reqres.in/api/users?page=2

### Networking: Content negotiation and loggi
- Parsing response
- check response as log

#### Networking: State and handle error
- State -> condition data status flow
    - Idle
    - Loading
    - Success -> data
    - Failure -> exception

#### Networking: State compose handler
- State Handler
    - onIdle
    - onLoading
    - onSuccess(data)
    - onFailure(exception)


### Phase 2
#### Architecture: Uni-directional Data Flow
- Uni-directional component
    - Store states
    - Reducer
    - View
    - Action
- Uni-directional and Compose UI
    - Declarative
    - Reactive
    - Isolation / self-contained
    - integrate with state management (live data, stateflow, viewmodel)

#### Architecture: MVI (Model - View - Intent)
- MVI Component
    - Model (state)
    - View (rendered)
    - Intent (action)
- MVI: ViewModel

#### Architecture: ViewModel
- Library
- Model implementation
- Abstraction viewmodel

#### Architecture: Repository
- Abstraction repository
- Mapper model

### Phase 3
#### Navigation
- Composition Provider
- Manual navigation
- Navigation libraries
    - Jetbrain navigation -> jetpack navigation adopt
    - Voyager:
        pros: 
            - easy to use
            - flexible configuration
        cons: 
            - limited gesture support: ios swipe back
    - Decompose:
        pros: 
            - focus on development multiplatform
            - environment full support
            - lifecycle management
        cons:
            - limited gesture support: ios swipe back
            - learning curve
            - complexity
    - PreCompose
        pros:
            - lifecycle management
            - easy to use -> like jetpack navigation
        cons: 
            - limited gesture support: ios swipe back
    - Appyx
        pros:
            - gesture support: ios swipe back
            - lifecycle management
        cons:
            - complexity usability
- Side effect

#### Navigation: Appyx
- Setup appyx -> RootNode
- Setup interface navigator
- Setup ios
- Setup android
    Troubleshot: 
        - implement plugin parcelize
        - add custom parameter in `freeCompilerArgs`:
            `- P plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.bumble.appyx.utils.multiplatform.Parcelize`

### Phase 4
#### Kotlin Cinterop
- Bidirectional objective-c interopable
- Kotlin module compiled into a framework
- Swift library exported to objc support
- Pure swift library not supported

#### CocoaPod
- Setup CocoaPod
- Using netfox in kotlin side
    - issue task embedAndSign -> need kotlin downgrade from 2.0 to 1.9.23

### Phase 5
#### Search Location API
- Search Place API -> get location from query
  - https://discover.search.hereapi.com/v1/discover?at=-6.361380449431958,106.8334773180715&limit=2&q=stasiun&apiKey=BQkAEa32DStxlj60z8z2bQpqV9aqjfNVInsSrclBmMY
- Reversed Place API -> get place from coordinate
  - https://revgeocode.search.hereapi.com/v1/revgeocode?at=-6.360501654958923,106.83179711447815&limit=3&lang=en-US&apiKey=BQkAEa32DStxlj60z8z2bQpqV9aqjfNVInsSrclBmMY

#### Search Location
- Simple List
- Render item in list with column

#### Reversed Location
- Input Field lat long
- Render item in list column

### Phase 6
#### Google Maps Compose Render
- Google Maps API Key
  - Android: AIzaSyBLdpISj1DAZJZ3nDri1oVn2wNvJjJQLls
  - Ios: AIzaSyA0m6uScEWSqH83f1qNjTDOExTyoa7TNdw
- Google Maps Compose
  - Android Google Maps Compose
    - composeCompiler
  - Ios Google Maps UIKit
    - CocoaPod

#### Google Maps Compose Basic Function
- Google Maps State
- Camera
  - Initial camera
  - Animated camera
  - Camera Listener
  - Zoom
    - Zoom out
    - Zoom in
- Google Maps State Saver
  - Android issue fix with parcelable
- Settings
  - myLocationEnable
  - myLocationButtonEnabled
  - compassEnabled

#### Location Service
- Permission
  - ios:
    - Privacy - Location When In Use Usage Description
- Get Location

#### Gesture Detector
- Not started
- Start started
- Stop started (coordinate)
- Location picker feature

### Phase 7
#### Feature Google Maps
- Search location
  - Display all item in search content
  - Move camera and display marker for selected location
  - Display marker for all result location

### Phase 8
#### Feature Firebase Google Authentication
- Sign In With Firebase and Google
  - Firebase is under layer of Google Sign In
  - For android, Sign In API deprecated
  - For android, migration into Jetpack Identity is recommended

### Phase 9
#### Completing apps
- Add google sign in button
- Display profile dialog and logout button
- Add splash screen for check the credential
- Sign In screen
- Sign in into maps
