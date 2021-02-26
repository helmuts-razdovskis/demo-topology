package com.example.demo.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MacAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {"FEFEFEFEFEFE", "FE:FE:FE:FE:FE:FE", "FE-FE-FE-FE-FE-FE", "ABC.ABC.ABC.ABC", "000000000000"})
    void parseString_ShouldBeValid(String input) {
        assertDoesNotThrow(() -> MacAddress.parseString(input));

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"FEFEFEFEFE","FEFEFEFEFEFEFE", "āšūžīģōŗqw"})
    void parseString_ShouldBeInvalid(String input) {
       assertThrows(IllegalArgumentException.class, () -> MacAddress.parseString(input));

    }

}
