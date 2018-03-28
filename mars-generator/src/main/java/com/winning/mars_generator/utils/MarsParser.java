package com.winning.mars_generator.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.exception.MarsParseConfigurationException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse configuration file
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsParser {
    //for xml parser
    private static final String NODE_DEBUG = "debug";
    private static final String NODE_CPU = "cpu";
    private static final String INTERVAL_MILLIS = "intervalMillis";
    private static final String SAMPLE_MILLIS = "sampleMillis";
    private static final String LEVEL = "level";

    private static MarsParser parser;
    public static void parseMarsConfiguration(Context context) {
        if (parser == null) {
            parser = new MarsParser();
        }
        parser.usePullParse(context);
    }

    private void usePullParse(Context context) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(getConfigInputStream(context), "UTF-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG: {
                        if (NODE_CPU.equalsIgnoreCase(nodeName)){
                            MarsConfig.CPU cpuConfig = new MarsConfig.CPU();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            String sampleMillis = xmlPullParser.getAttributeValue("", SAMPLE_MILLIS);
                            cpuConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            cpuConfig.setSampleMillis(Long.parseLong(sampleMillis));
                            MarsConfig.setCpu(cpuConfig);
                        }else if(NODE_DEBUG.equalsIgnoreCase(nodeName)){
                            String level = xmlPullParser.getAttributeValue("", LEVEL);
                            MarsConfig.setDebug(Integer.parseInt(level));
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            throw new MarsParseConfigurationException(
                    MarsParseConfigurationException.FILE_FORMAT_IS_NOT_CORRECT);
        }catch (IOException e){
            throw new MarsParseConfigurationException(MarsParseConfigurationException.IO_EXCEPTION);
        }
    }

    private InputStream getConfigInputStream(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] fileNames = assetManager.list("");
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                if (Const.CONFIGURATION_FILE_NAME.equalsIgnoreCase(fileName)) {
                    return assetManager.open(fileName, AssetManager.ACCESS_BUFFER);
                }
            }
        }
        throw new MarsParseConfigurationException(
                MarsParseConfigurationException.CAN_NOT_FIND_MARS_FILE);
    }
}
