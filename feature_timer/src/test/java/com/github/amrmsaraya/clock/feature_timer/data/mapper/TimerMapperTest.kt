package com.github.amrmsaraya.clock.feature_timer.data.mapper

import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimerMapperTest {

    @Test
    fun `toTimer() with TimerDTO then should return a Timer`() {

        // Given
        val timerDTO = TimerDTO(
            id = 1,
            title = "title",
            timeMillis = 135
        )

        // When
        val result = timerDTO.toTimer()

        // Then
        assertThat(result).isEqualTo(
            Timer(
                id = 1,
                title = "title",
                timeMillis = 135
            )
        )
    }

    @Test
    fun `toTimerDTO() with Timer then should return a TimerDTO`() {

        // Given
        val timer = Timer(
            id = 1,
            title = "title",
            timeMillis = 135
        )

        // When
        val result = timer.toTimerDTO()

        // Then
        assertThat(result).isEqualTo(
            TimerDTO(
                id = 1,
                title = "title",
                timeMillis = 135
            )
        )
    }
}