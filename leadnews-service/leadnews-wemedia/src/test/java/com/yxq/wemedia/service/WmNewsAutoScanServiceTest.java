package com.yxq.wemedia.service;

import com.yxq.wemedia.WemediaApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/13
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
class WmNewsAutoScanServiceTest {

    @Resource
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    void autoScanWmNews() throws Exception {
        wmNewsAutoScanService.autoScanWmNews(6232);
    }
}