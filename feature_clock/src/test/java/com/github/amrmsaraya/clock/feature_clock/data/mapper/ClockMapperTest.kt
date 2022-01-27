package com.github.amrmsaraya.clock.feature_clock.data.mapper

import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import com.github.amrmsaraya.clock.feature_clock.domain.entity.Clock
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClockMapperTest {

    @Test
    fun `toClock() with ClockDTO then should return a Clock`() {

        // Given
        val clockDTO = ClockDTO(
            id = "1",
            displayName = "1"
        )

        // When
        val result = clockDTO.toClock()

        // Then
        assertThat(result).isEqualTo(
            Clock(
                id = "1",
                displayName = "1"
            )
        )
    }

    @Test
    fun `toClockDTO() with Clock then should return a ClockDTO`() {

        // Given
        val clock = Clock(
            id = "1",
            displayName = "1"
        )

        // When
        val result = clock.toClockDTO()

        // Then
        assertThat(result).isEqualTo(
            ClockDTO(
                id = "1",
                displayName = "1"
            )
        )
    }
}