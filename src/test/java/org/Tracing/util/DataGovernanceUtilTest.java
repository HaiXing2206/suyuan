package org.Tracing.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataGovernanceUtilTest {

    @Test
    void shouldMaskPhone() {
        String masked = DataGovernanceUtil.maskValue("phone", "13812345678");
        Assertions.assertEquals("138****5678", masked);
    }

    @Test
    void shouldValidateEmailFormat() {
        Assertions.assertTrue(DataGovernanceUtil.isFormatValid("email", "user@example.com"));
        Assertions.assertFalse(DataGovernanceUtil.isFormatValid("email", "invalid-email"));
    }
}
