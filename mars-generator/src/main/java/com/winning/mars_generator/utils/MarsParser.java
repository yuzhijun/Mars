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
    private static final String NODE_BATTERY = "battery";
    private static final String NODE_FPS = "fps";
    private static final String NODE_TRAFFIC = "traffic";
    private static final String NODE_SM = "sm";
    private static final String NODE_HEAP = "heap";
    private static final String NODE_RAM = "ram";
    private static final String NODE_PSS = "pss";

    private static final String INTERVAL_MILLIS = "intervalMillis";
    private static final String SAMPLE_MILLIS = "sampleMillis";
    private static final String LONG_BLOCK_THRESHOLD = "longBlockThreshold";
    private static final String SHORT_BLOCK_THRESHOLD = "shortBlockThreshold";
    private static final String DUMP_INTERVAL = "dumpInterval";
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
                        }else if(NODE_BATTERY.equalsIgnoreCase(nodeName)){
                            MarsConfig.Battery batteryConfig = new MarsConfig.Battery();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            batteryConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            MarsConfig.setBattery(batteryConfig);
                        }else if(NODE_FPS.equalsIgnoreCase(nodeName)){
                            MarsConfig.Fps fpsConfig = new MarsConfig.Fps();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            fpsConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            MarsConfig.setFps(fpsConfig);
                        }else if(NODE_TRAFFIC.equalsIgnoreCase(nodeName)){
                            MarsConfig.Traffic trafficConfig = new MarsConfig.Traffic();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            String sampleMillis = xmlPullParser.getAttributeValue("", SAMPLE_MILLIS);
                            trafficConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            trafficConfig.setSampleMillis(Long.parseLong(sampleMillis));
                            MarsConfig.setTraffic(trafficConfig);
                        }else if(NODE_SM.equalsIgnoreCase(nodeName)){
                            MarsConfig.Sm smConfig = new MarsConfig.Sm();
                            String longBlockThreshold = xmlPullParser.getAttributeValue("", LONG_BLOCK_THRESHOLD);
                            String shortBlockThreshold = xmlPullParser.getAttributeValue("", SHORT_BLOCK_THRESHOLD);
                            String dumpInterval = xmlPullParser.getAttributeValue("", DUMP_INTERVAL);
                            smConfig.setLongBlockThreshold(Long.parseLong(longBlockThreshold));
                            smConfig.setShortBlockThreshold(Long.parseLong(shortBlockThreshold));
                            smConfig.setDumpInterval(Long.parseLong(dumpInterval));
                            MarsConfig.setSm(smConfig);
                        }else if (NODE_HEAP.equalsIgnoreCase(nodeName)){
                            MarsConfig.Heap heapConfig = new MarsConfig.Heap();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            heapConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            MarsConfig.setHeap(heapConfig);
                        }else if(NODE_RAM.equalsIgnoreCase(nodeName)){
                            MarsConfig.Ram ramConfig = new MarsConfig.Ram();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            ramConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            MarsConfig.setRam(ramConfig);
                        }else if(NODE_PSS.equalsIgnoreCase(nodeName)){
                            MarsConfig.Pss pssConfig = new MarsConfig.Pss();
                            String intervalMillis = xmlPullParser.getAttributeValue("", INTERVAL_MILLIS);
                            pssConfig.setIntervalMillis(Long.parseLong(intervalMillis));
                            MarsConfig.setPss(pssConfig);
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
