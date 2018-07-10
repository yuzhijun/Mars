package com.winning.mars_generator.core.modules.sm;

import android.support.annotation.StringDef;

import com.winning.mars_generator.core.BaseBean;
import com.winning.mars_generator.core.modules.sm.blockCanary.LongBlockBean;
import com.winning.mars_generator.core.modules.sm.blockCanary.ShortBlockBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class SmBean extends BaseBean{
    public LongBlockBean mLongBlockBean;
    public ShortBlockBean mShortBlockBean;
    public @BlockType String blockType;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({BlockType.LONG, BlockType.SHORT})
    public @interface BlockType {
        public static final String LONG = "LongBlock";
        public static final String SHORT = "ShortBlock";
    }

    public SmBean(LongBlockBean longBlockInfo) {
        this.mLongBlockBean = longBlockInfo;
        this.blockType = BlockType.LONG;
    }

    public SmBean(ShortBlockBean shortBlockInfo) {
        this.mShortBlockBean = shortBlockInfo;
        this.blockType = BlockType.SHORT;
    }

    @Override
    public String toString() {
        return "BlockInfo{" +
                "longBlockInfo=" + mLongBlockBean +
                ", shortBlockInfo=" + mShortBlockBean +
                ", blockType='" + blockType + '\'' +
                '}';
    }
}
