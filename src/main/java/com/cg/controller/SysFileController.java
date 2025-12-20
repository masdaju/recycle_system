package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.SysFile;
import com.cg.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 文件表 前端控制器
 * </p>
 *
 * @author 海カ布
 * @since 2024-10-22
 */
@RestController
@RequestMapping("/sys-file")
public class SysFileController {


    @Autowired
    private SysFileService sysFileService;

    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<SysFile> aPage = sysFileService.page(new Page<>(current, pageSize));
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        return SaResult.data(sysFileService.getById(id));
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody SysFile params) {
        sysFileService.save(params);
        return SaResult.ok("created successfully");
    }

    @PostMapping(value = "/delete/{id}")
    public SaResult delete(@PathVariable("id") String id) {
        sysFileService.removeById(id);
        return SaResult.ok("deleted successfully");
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody SysFile params) {
        sysFileService.updateById(params);
        return SaResult.ok("updated successfully");
    }

    //上传文件
//    @PostMapping("/upload")
//    public SaResult upload(MultipartFile file) {
//        return sysFileService.upload(file);
//    }

}
