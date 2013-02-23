package org.kdt;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 *
 */
public class TrueTest {

    @Test
    public void true_is_true() {
        assertThat(true, is(true));
        assertThat(true, is(not(false)));
    }
}
