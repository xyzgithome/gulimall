package com.atguigu.common.constant;

import org.apache.commons.lang.StringUtils;

public class ProductConstant {
    public enum AttrTypeEnum {
        ATTR_TYPE_SALE(0, "sale", "销售属性"),
        ATTR_TYPE_BASE(1, "base", "基础属性");
        private Integer code;
        private String type;
        private String msg;

        AttrTypeEnum(Integer code, String type, String msg) {
            this.code = code;
            this.type = type;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getType() {
            return type;
        }

        public String getMsg() {
            return msg;
        }

        public static Integer getCodeByType(String type) {
            for (AttrTypeEnum value : AttrTypeEnum.values()) {
                if (StringUtils.equalsIgnoreCase(value.getType(), type)) {
                    return value.getCode();
                }
            }
            return null;
        }
    }

}
