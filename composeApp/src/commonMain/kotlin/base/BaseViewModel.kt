package base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<MODEL, INTENT>(private val defaultModel: MODEL) : ViewModel() {

    private val mutableStateModel: MutableStateFlow<MODEL> = MutableStateFlow(defaultModel)
    val stateModel: StateFlow<MODEL> get() = mutableStateModel

    abstract fun handleIntent(appIntent: INTENT)

    fun updateModel(block: (MODEL) -> MODEL) {
        mutableStateModel.update(block)
    }

    fun restartModel() {
        mutableStateModel.value = defaultModel
    }

}