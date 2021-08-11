package xiao.demo

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.IndicativeSentencesGeneration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 *
 * @author lix wang
 */
class DisplayNameGenerationTest {
    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
    class A_year_is_not_supported {
        @Test
        fun if_it_is_zero() {
        }

        @DisplayName("A negative value for year id not supported by the leap year computation.")
        @ParameterizedTest(name = "For example, year {0} is not supported.")
        @ValueSource(ints = [-1, -4])
        fun if_it_is_negative(year: Int) {
        }
    }

    @Nested
    @IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores::class)
    class A_year_id_a_leap_year {
        @Test
        fun if_it_is_divisible_by_4_but_not_by_100() {
        }

        @ParameterizedTest(name = "Year {0} is a leap year")
        @ValueSource(ints = [2016, 2020, 2048])
        fun if_it_is_one_of_the_following_years(year: Int) {
        }
    }
}