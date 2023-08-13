package com.atguigu.gulimall.product.config;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.exception.GLException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class FeignErrorDecoderConfig implements ErrorDecoder {

    @Override
    public Exception decode(final String methodKey, final Response response) {
        try {
            String message = Util.toString(response.body().asReader());
            JSONObject jsonObject = JSONObject.parseObject(message);
            // 包装成自己自定义的异常，这里建议根据自己的代码改
            String msg = jsonObject.getString("message");
            return new GLException(BizCodeEnum.getCodeByMsg(msg), msg);
        } catch (IOException e) {
            log.error("FeignErrorDecoderConfig msg={}", e.getMessage());
            return new GLException(BizCodeEnum.UNKNOW_EXCEPTION);
        }
    }
}

