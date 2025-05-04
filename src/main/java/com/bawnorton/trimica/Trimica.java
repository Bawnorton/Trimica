package com.bawnorton.trimica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimica {
    public static final String MOD_ID = "trimica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        LOGGER.info("Trimica Initialized");
    }
}
