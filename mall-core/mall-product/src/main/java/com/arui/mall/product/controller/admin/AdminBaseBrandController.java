package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.product.service.BaseBrandService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品品牌列表")
@RestController
@RequestMapping("/product")
public class AdminBaseBrandController {

    @Resource
    private BaseBrandService baseBrandService;

    @Value(value = "${fastdfs.prefix}")
    private String prefix;

    /**
     * //http://127.0.0.1/product/brand/queryBrandByPage/1/10
     * @param currentPage
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "商品品牌列表分页")
    @GetMapping("/brand/queryBrandByPage/{currentPage}/{pageSize}")
    public R queryBrandByPage(
            @ApiParam(value = "当前页", required = true, example = "1")
            @PathVariable Long currentPage,

            @ApiParam(value = "每页多少条", required = true, example = "10")
            @PathVariable Long pageSize){
        Page<BaseBrand> baseBrandPage = new Page<>(currentPage, pageSize);
        baseBrandService.page(baseBrandPage, null);
        return R.ok(baseBrandPage);
    }

    /**http://127.0.0.1/product/brand
     * 添加商品品牌信息
     * @param baseBrand
     * @return
     */
    @ApiOperation(value = "添加商品品牌信息")
    @PostMapping("/brand")
    public R saveBrand(
            @ApiParam(value = "保存品牌信息的表单")
            @RequestBody BaseBrand baseBrand){
        baseBrandService.save(baseBrand);
        return R.ok();
    }

    @ApiOperation(value = "修改商品品牌信息")
    @PutMapping("/brand")
    public R updateBrand(
            @ApiParam(value = "保存品牌信息的表单")
            @RequestBody BaseBrand baseBrand){
        baseBrandService.updateById(baseBrand);
        return R.ok();
    }


    @ApiOperation(value = "删除商品品牌信息")
    @DeleteMapping("/brand/{id}")
    public R deleteBrand(
            @ApiParam(value = "品牌id")
            @PathVariable Long id){
        baseBrandService.removeById(id);
        return R.ok();
    }


    @ApiOperation(value = "上传文件到fastDFS")
    @PostMapping("/brand/fileUpload")
    public R fileUpload(
            @ApiParam(value = "上传的文件")
            MultipartFile file) throws IOException, MyException {
        // 获取fastDFS服务器信息文件
        String configFilePath = this.getClass().getResource("/tracker.conf").getFile();

        // 初始化
        ClientGlobal.init(configFilePath);

        // 创建trackerClient客户端
        TrackerClient trackerClient = new TrackerClient();

        // 获取tracker 连接
        TrackerServer connection = trackerClient.getConnection();

        // 创建StroageClient1
        StorageClient1 storageClient1 = new StorageClient1(connection, null);

        // 文件原始名称
        String originalFilename = file.getOriginalFilename();
        String extensionName = FilenameUtils.getExtension(originalFilename);

        // 实现上传
        String path = storageClient1.upload_appender_file1(file.getBytes(), extensionName, null);
        String imageUrl = prefix + path;
        return R.ok(imageUrl);
    }
}

