package com.cg.service;

import com.cg.entity.Waste;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 废品表 服务类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface WasteService extends IService<Waste> {

    boolean saveWaste(Waste waste, MultipartFile file);

    void updateWaste(Waste waste, MultipartFile file);
}
