package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallThirdPartyApplicationTests {
    @Autowired
    private OSSClient ossClient;

    @Test
    public void demo2OSS() throws Exception {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\ossdemo6.png");

        ossClient.putObject("gulimall-xyzgit", "ossdemo6.png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传完成");
    }

}

