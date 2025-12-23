package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.Waste;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 废品表 服务类
 * </p>
 */
public interface WasteService extends IService<Waste> {

    boolean saveWaste(Waste waste, MultipartFile file);

    void updateWaste(Waste waste, MultipartFile file);
}
