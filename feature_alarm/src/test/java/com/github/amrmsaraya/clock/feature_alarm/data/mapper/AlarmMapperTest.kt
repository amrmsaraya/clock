package com.github.amrmsaraya.clock.feature_alarm.data.mapper

import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AlarmMapperTest {

    @Test
    fun `toAlarm() with AlarmDTO then should return a Alarm`() {

        // Given
        val alarmDTO = AlarmDTO(
            id = 1,
            title = "title",
            hour = 1,
            minute = 1,
            amPm = 1,
            color = 1,
            repeatOn = emptyList(),
            ringtone = "ringtone",
            enabled = true,
            ringTime = 1,
            snooze = 1
        )

        // When
        val result = alarmDTO.toAlarm()

        // Then
        assertThat(result).isEqualTo(
            Alarm(
                id = 1,
                title = "title",
                hour = 1,
                minute = 1,
                amPm = 1,
                color = 1,
                repeatOn = emptyList(),
                ringtone = "ringtone",
                enabled = true,
                ringTime = 1,
                snooze = 1
            )
        )
    }

    @Test
    fun `toAlarmDTO() with Alarm then should return a AlarmDTO`() {

        // Given
        val alarm = Alarm(
            id = 1,
            title = "title",
            hour = 1,
            minute = 1,
            amPm = 1,
            color = 1,
            repeatOn = emptyList(),
            ringtone = "ringtone",
            enabled = true,
            ringTime = 1,
            snooze = 1
        )

        // When
        val result = alarm.toAlarmDTO()

        // Then
        assertThat(result).isEqualTo(
            AlarmDTO(
                id = 1,
                title = "title",
                hour = 1,
                minute = 1,
                amPm = 1,
                color = 1,
                repeatOn = emptyList(),
                ringtone = "ringtone",
                enabled = true,
                ringTime = 1,
                snooze = 1
            )
        )
    }
}