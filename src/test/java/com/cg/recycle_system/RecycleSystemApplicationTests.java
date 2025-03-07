package com.cg.recycle_system;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

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
