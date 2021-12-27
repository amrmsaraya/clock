package com.github.amrmsaraya.clock.presentation.alarm_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.domain.usecase.AlarmCRUDUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmActivityViewModel @Inject constructor(
    private val alarmCRUDUseCase: AlarmCRUDUseCase
) : ViewModel() {

    fun insertAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmCRUDUseCase.insert(alarm)
    }
}