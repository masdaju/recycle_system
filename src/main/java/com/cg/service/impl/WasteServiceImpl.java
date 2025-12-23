package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.SysFile;
import com.cg.entity.Waste;
import com.cg.mapper.WasteMapper;
import com.cg.service.SysFileService;
import com.cg.service.WasteService;
import com.cg.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>
 * 废品表 服务实现类
 * 该类实现了WasteService接口，负责处理与废品表相关的业务逻辑，
 * 包括保存废品信息并上传图片，以及更新废品信息时更新图片等操作。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WasteServiceImpl extends ServiceImpl<WasteMapper, Waste> implements WasteService {
    // 注入SysFileService实例，用于处理文件相关业务
    private final SysFileService sysFileService;

    // 从配置文件中读取文件上传路径
    @Value("${upload.path}")
    private String uploadPath;
    // 从配置文件中读取图片预览URL前缀
    @Value("${preview.url}")
    private String previewUrl;

    /**
     * 保存废品信息并上传相关图片
     *
     * @param waste 废品实体对象
     * @param file  上传的图片文件
     * @return 保存成功返回true
     */
    @Override
    @Transactional
    public boolean saveWaste(Waste waste, MultipartFile file) {
        // 调用SysFileService的upload方法上传文件，并获取文件名
        String filename = sysFileService.upload(file);
        // 设置废品的图片URL，拼接预览URL前缀和文件名
        waste.setImgUrl(previewUrl + filename);
        // 设置废品的创建日期为当前日期
        waste.setCreateDate(new Date());
        // 打印废品信息，用于调试
        System.out.println(waste);
        // 调用父类的save方法保存废品信息到数据库
        save(waste);
        return true;
    }

    /**
     * 更新废品信息并更新相关图片
     *
     * @param waste 废品实体对象
     * @param file  上传的新图片文件
     */
    @Override
    @Transactional
    public void updateWaste(Waste waste, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            // 获取废品当前的图片URL
            String imgUrl = waste.getImgUrl();
            // 拼接出图片在磁盘上的真实路径
            String realPath = uploadPath + StringUtils.topath(imgUrl);
            // 调用SysFileService的delFromDisk方法从磁盘上删除旧图片
            sysFileService.delFromDisk(realPath);
            // 创建查询条件包装器，根据文件URL查询SysFile记录
            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysFile::getFileUrl, imgUrl);
            // 调用SysFileService的remove方法从数据库中删除旧图片记录
            sysFileService.remove(wrapper);
            // 调用SysFileService的upload方法上传新文件，并获取新文件名
            String filename = sysFileService.upload(file);
            // 设置废品的新图片URL，拼接预览URL前缀和新文件名
            waste.setImgUrl(previewUrl + filename);
            // 调用父类的updateById方法根据ID更新废品信息到数据库
        }
        updateById(waste);
    }
}