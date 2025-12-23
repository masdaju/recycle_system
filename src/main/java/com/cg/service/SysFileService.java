package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件表 服务类
 * </p>
 */
public interface SysFileService extends IService<SysFile> {

    String upload(MultipartFile file);


    boolean delFromDisk(String realPath);
}
