package com.cg.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件表 服务类
 * </p>
 *
 * @author 海カ布
 * @since 2024-10-22
 */
public interface SysFileService extends IService<SysFile> {

    String upload(MultipartFile file);


    boolean delFromDisk(String realPath);
}
