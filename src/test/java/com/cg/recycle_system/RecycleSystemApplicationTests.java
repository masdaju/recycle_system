package com.cg.recycle_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class RecycleSystemApplicationTests {
    @Test

    void contextLoads() {
        List<byte[]> list = new ArrayList<>();
        while (list.size() != 3000) {
            // 每次创建一个 1MB 的字节数组并添加到列表中
            list.add(new byte[1024 * 1024]);
        }
        System.out.println(list.size());
    }




}
