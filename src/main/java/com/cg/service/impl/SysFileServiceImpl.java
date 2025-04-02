package com.cg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.SysFile;
import com.cg.mapper.SysFileMapper;
import com.cg.service.SysFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * <p>
 * 文件表 服务实现类
 * </p>
 *
 * @author 海カ布
 * @since 2024-10-22
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    //文件上传服务器路径
    @Value("${file.upload-path}")// /upload/
    private String uploadPath;
    //地址拼接
    @Value("${upload.path}") // /upload/
    private String path;
    // 文件预览的 URL 前缀，通常从配置文件中读取
    @Value("${preview.url}")
    private String previewUrl;

    /**
     * 文件上传方法
     *
     * @param file 要上传的文件
     * @return 上传结果封装对象 SaResult，包含成功上传的文件信息或错误信息
     */
    public String upload(MultipartFile file) {
        try {
            // 获取上传文件的原始文件名
            String originalFileName = file.getOriginalFilename();
            // 获取文件后缀名
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 使用 UUID 生成唯一的文件名
            String uniqueFileName = UUID.randomUUID().toString() + suffix;

            // 创建新的文件实体对象
            SysFile sysFile = new SysFile();
            // 设置文件名
            sysFile.setFileName(uniqueFileName);
            // 设置文件在服务器上的存储路径
            sysFile.setRealPath(path + uniqueFileName);
            // 设置文件的预览 URL
            sysFile.setFileUrl(previewUrl + uniqueFileName);

            // 创建文件对象，代表服务器上存储文件的位置
            File diskFile = new File(uploadPath + uniqueFileName);
            // 将上传的文件保存到服务器指定位置
            file.transferTo(diskFile);
            log.debug("文件上传成功================="+ uploadPath+uniqueFileName);
            // 将文件信息保存到数据库
            if (save(sysFile)) {
                return uniqueFileName;
            }else {
                throw new RuntimeException("文件上传失败");
            }

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delFromDisk(String realPath) {
        try {
            Path path = Paths.get(realPath);
            // 删除文件
            Files.delete(path);
            System.out.println("文件已成功删除！");
        } catch (NoSuchFileException e) {
            System.out.println("文件不存在: " + e);
        } catch (DirectoryNotEmptyException e) {
            System.out.println("目录不是空的: " + e);
        } catch (IOException e) {
            System.out.println("删除文件时发生错误: " + e);
        }
        return true;
    }

}
