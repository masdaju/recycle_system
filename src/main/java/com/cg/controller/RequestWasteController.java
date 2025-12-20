package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cg.entity.RequestWaste;
import com.cg.entity.User;
import com.cg.entity.view.VWaste;
import com.cg.service.RequestWasteService;
import com.cg.service.UserService;
import com.cg.service.VWasteService;
import com.cg.service.WasteRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 每个申请Id对应的废品 前端控制器
 * 该控制器主要处理与每个申请ID对应的废品相关的HTTP请求，包括查询、创建、删除、更新废品申请以及检查废品数量并更新用户金额等操作。
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/request-waste")
public class RequestWasteController {

    @Autowired
    private RequestWasteService requestWasteService;

    @Autowired
    private VWasteService vWasteService;

    @Autowired
    private UserService userService;

    @Autowired
    private WasteRequestsService wasteRequestsService;

    /**
     * 根据申请ID查询对应的废品列表
     *
     * @param requestId 申请ID
     * @return 包含查询到的废品列表的 SaResult 对象
     */
    @GetMapping
    public SaResult list(@RequestParam Long requestId) {
        // 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<VWaste> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：根据申请ID进行查询
        wrapper.eq(VWaste::getRequestId, requestId);
        // 调用 vWasteService 的 list 方法，根据查询条件获取废品列表
        List<VWaste> list = vWasteService.list(wrapper);
        // 将查询到的废品列表封装到 SaResult 对象中返回
        return SaResult.data(list);
    }

    /**
     * 根据ID查询单个申请对应的废品信息
     *
     * @param id 申请对应的废品的ID
     * @return 包含查询到的废品信息的 SaResult 对象，若未找到则返回错误信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        // 调用 requestWasteService 的 getById 方法，根据ID获取申请对应的废品信息
        RequestWaste requestWaste = requestWasteService.getById(id);
        // 若查询到废品信息，则将其封装到 SaResult 对象中返回
        if (requestWaste != null) {
            return SaResult.data(requestWaste);
        } else {
            // 若未查询到废品信息，则返回错误信息
            return SaResult.error("未找到指定申请对应的废品");
        }

    }

    /**
     * 检查废品数量并更新用户金额
     *
     * @param map       包含废品ID和对应数量的映射
     * @param requestId 申请ID
     * @return 包含更新金额的 SaResult 对象，若更新失败则返回错误信息
     */
    @PostMapping("checkQuantity")
    public SaResult checkQuantity(@RequestBody Map<Long, BigDecimal> map, @RequestParam Long requestId) {

            // 调用 requestWasteService 的 checkQuantity 方法，根据废品数量计算应更新的金额
            BigDecimal amount = requestWasteService.checkQuantity(map, requestId);
            // 创建 LambdaUpdateWrapper 对象，用于构建更新条件
            LambdaUpdateWrapper<User> wrapper1 = new LambdaUpdateWrapper<>();
            // 添加更新条件：根据申请ID获取对应的用户ID
            wrapper1.eq(User::getId, (wasteRequestsService.getById(requestId)).getUserId())
                    // 设置更新的 SQL 语句，将用户的金额加上计算得到的金额
                    .setSql("amount = amount + " + amount);
            // 调用 userService 的 update 方法，根据更新条件更新用户的金额
            userService.update(wrapper1);
            // 将计算得到的金额封装到 SaResult 对象中返回
            return SaResult.data(amount);
    }

    /**
     * 创建新的废品申请
     *
     * @param params 包含废品申请信息的 RequestWaste 对象
     * @return 包含创建结果的 SaResult 对象，若创建成功则返回成功信息，若失败则返回错误信息
     */
    @PostMapping(value = "/create")
    public SaResult create(@RequestBody RequestWaste params) {
        try {
            // 调用 requestWasteService 的 save 方法，将废品申请信息保存到数据库
            requestWasteService.save(params);
            // 返回创建成功的信息
            return SaResult.ok("废品申请创建成功");
        } catch (Exception e) {
            // 若创建过程中出现异常，则返回错误信息
            return SaResult.error("废品申请创建失败: " + e.getMessage());
        }
    }


    @GetMapping("/getFromMyRequest")
    public SaResult getFromMyRequest(@RequestParam(required = false) Integer uid) {
        BigDecimal myAmount = requestWasteService.getFromMyRequest(uid);
        return SaResult.data(myAmount);
    }


    /**
     * 根据ID列表批量删除废品申请
     *
     * @param ids 要删除的废品申请的ID列表
     * @return 包含删除结果的 SaResult 对象，若删除成功则返回成功信息，若失败则返回错误信息
     */
    @PostMapping(value = "deleteByIds")
    public SaResult getById(@RequestBody List<Long> ids) {

        try {
            // 调用 requestWasteService 的 removeBatchByIds 方法，根据ID列表删除废品申请
            requestWasteService.removeBatchByIds(ids);
        } catch (Exception e) {
            // 若删除过程中出现异常，则返回错误信息
            return SaResult.error("删除失败: " + e.getMessage());
        }
        // 返回删除成功的信息
        return SaResult.ok("删除成功");
    }

    /**
     * 根据废品申请对象更新废品申请信息
     *
     * @param params 包含要更新信息的 RequestWaste 对象
     * @return 包含更新结果的 SaResult 对象，若更新成功则返回成功信息，若失败则返回错误信息
     */
    @PostMapping(value = "/update")
    public SaResult update(@RequestBody RequestWaste params) {
        try {
            // 调用 requestWasteService 的 updateById 方法，根据废品申请对象更新数据库中的记录
            requestWasteService.updateById(params);
            // 返回更新成功的信息
            return SaResult.ok("废品申请更新成功");
        } catch (Exception e) {
            // 若更新过程中出现异常，则返回错误信息
            return SaResult.error("废品申请更新失败: " + e.getMessage());
        }
    }
}