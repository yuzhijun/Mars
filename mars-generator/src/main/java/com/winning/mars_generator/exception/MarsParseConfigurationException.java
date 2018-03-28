package com.winning.mars_generator.exception;

/**
 * Created by yuzhijun on 2018/3/27.
 */

public class MarsParseConfigurationException extends RuntimeException {

    /**
     * can not find the mars.xml.xml file by the given id.
     * */
    public static final String CAN_NOT_FIND_MARS_FILE = "mars.xml.xml file is missing. Please ensure it under assets folder.";
    /**
     * can not parse the mars.xml.xml, check if it's in correct format.
     */
    public static final String FILE_FORMAT_IS_NOT_CORRECT = "can not parse the mars.xml.xml, check if it's in correct format";
    /**
     * parse configuration is failed.
     */
    public static final String PARSE_CONFIG_FAILED = "parse configuration is failed";
    /**
     * IO exception happened.
     */
    public static final String IO_EXCEPTION = "IO exception happened";

    public MarsParseConfigurationException(String errorMessage) {
        super(errorMessage);
    }
}
