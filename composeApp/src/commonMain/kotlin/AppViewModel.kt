
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.State
import entity.data.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import repository.ReqresUserRepository


sealed class AppIntent {
    data object GetApi : AppIntent()
    data object UpdateCounter : AppIntent()
}

data class AppModel(
    val counter: Int = 0,
    val reqresResponseState: State<User> = State.Idle
)

class AppViewModel(
    private val reqresUserRepository: ReqresUserRepository = ReqresUserRepository()
) : BaseViewModel<AppModel, AppIntent>(AppModel()) {

    override fun handleIntent(appIntent: AppIntent) {
        when (appIntent) {
            is AppIntent.GetApi -> getApi()
            is AppIntent.UpdateCounter -> updateCounter()
        }
    }

    private fun getApi() = viewModelScope.launch {
        reqresUserRepository.getUser()
            .stateIn(this)
            .collectLatest { state ->
                updateModel { model ->
                    model.copy(
                        reqresResponseState = state
                    )
                }
            }
    }

    private fun updateCounter() {
        updateModel { model ->
            model.copy(
                counter = model.counter +1
            )
        }
    }

}