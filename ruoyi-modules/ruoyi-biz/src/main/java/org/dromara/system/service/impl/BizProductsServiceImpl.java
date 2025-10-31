package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.system.domain.bo.BizProductImagesBo;
import org.dromara.system.domain.vo.BizProductImagesVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.IBizProductImagesService;
import org.dromara.system.service.ISysOssService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.BizProductsBo;
import org.dromara.system.domain.vo.BizProductsVo;
import org.dromara.system.domain.BizProducts;
import org.dromara.system.mapper.BizProductsMapper;
import org.dromara.system.service.IBizProductsService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 商品信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-02-26
 */
@RequiredArgsConstructor
@Service
public class BizProductsServiceImpl implements IBizProductsService {

    private final IBizProductImagesService bizProductImagesServiceImpl;

    private final BizProductsMapper baseMapper;

    /**
     * 查询商品信息
     *
     * @param id 主键
     * @return 商品信息
     */
    @Override
    public BizProductsVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商品信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品信息分页列表
     */
    @Override
    public TableDataInfo<BizProductsVo> queryPageList(BizProductsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizProducts> lqw = buildQueryWrapper(bo);
        lqw.eq(BizProducts::getTargetLanguage, "CHS");
        Page<BizProductsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(t -> {
            setBizProductsVo(t);
        });
        return TableDataInfo.build(result);
    }

    private void setBizProductsVo(BizProductsVo bizProductsVo) {
        if (bizProductsVo.getParentId() <= 0) {
            BizProductsBo bo = new BizProductsBo();
            bo.setParentId(bizProductsVo.getId());
            LambdaQueryWrapper<BizProducts> lqw = buildQueryWrapper(bo);
            List<BizProductsVo> s = baseMapper.selectVoList(lqw);
            List<String> s2 = s.stream().map(t -> t.getTargetLanguage()).toList();
            bizProductsVo.setGenedLang(s2);
        }
    }

    /**
     * 查询符合条件的商品信息列表
     *
     * @param bo 查询条件
     * @return 商品信息列表
     */
    @Override
    public List<BizProductsVo> queryList(BizProductsBo bo) {
        LambdaQueryWrapper<BizProducts> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizProducts> buildQueryWrapper(BizProductsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizProducts> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizProducts::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), BizProducts::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BizProducts::getDescription, bo.getDescription());
        lqw.eq(bo.getPriceMin() != null, BizProducts::getPriceMin, bo.getPriceMin());
        lqw.eq(bo.getPriceMax() != null, BizProducts::getPriceMax, bo.getPriceMax());
        lqw.eq(bo.getDiscountPrice() != null, BizProducts::getDiscountPrice, bo.getDiscountPrice());
        lqw.eq(bo.getStock() != null, BizProducts::getStock, bo.getStock());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), BizProducts::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getSeller()), BizProducts::getSeller, bo.getSeller());
        lqw.eq(bo.getCreatedAt() != null, BizProducts::getCreatedAt, bo.getCreatedAt());
        lqw.eq(bo.getUpdatedAt() != null, BizProducts::getUpdatedAt, bo.getUpdatedAt());
        lqw.eq(bo.getZip() != null, BizProducts::getZip, bo.getZip());
        lqw.eq(bo.getParentId() != null, BizProducts::getParentId, bo.getParentId());
        return lqw;
    }

    /**
     * 新增商品信息
     *
     * @param bo 商品信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizProductsBo bo) {
        BizProducts add = MapstructUtils.convert(bo, BizProducts.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品信息
     *
     * @param bo 商品信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizProductsBo bo) {
        BizProducts update = MapstructUtils.convert(bo, BizProducts.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizProducts entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商品信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public Boolean copyById(Long id,String imageType, String targetLanguage) {
        BizProductsVo bizProductsVo = this.queryById(id);
        bizProductsVo.setId(null);
        BizProducts add = MapstructUtils.convert(bizProductsVo, BizProducts.class);
        add.setCreatedAt(new Date());
        add.setCreateTime(new Date());
        add.setSourceLanguage(bizProductsVo.getTargetLanguage());
        add.setTargetLanguage(targetLanguage);
        add.setParentId(id);

        try {
            if (baseMapper.insert(add) > 0) {
                bizProductImagesServiceImpl.copyImagesByProductId(id,imageType, add);
            }
            baseMapper.updateById(add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 从 OSS 下载 ZIP 文件并处理
     */
    @Override
    public Boolean handlerZip(Long productId) throws Exception {
        BizProductsVo bizProductsVo = this.queryById(productId);
        SysOssVo sysOssVo = sysOssServiceImpl.getById(bizProductsVo.getZip());
        OssClient ossClient = OssFactory.instance(sysOssVo.getService());

        InputStream zipInputStream = ossClient.getObjectContent(sysOssVo.getFileName());
        ZipInputStream zipStream = new ZipInputStream(zipInputStream);
        ZipEntry entry;
        //定义中文
        String productImagePath = "image/"+productId+"/CHS";

        while ((entry = zipStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                // 提取目录层级中的标识（如 480）
                String entryName = entry.getName(); // 例如 "480/1.jpg"
                String[] parts = entryName.split("/"); // 分割路径
                String[] su = entryName.split("\\."); // 分割路径
                String suffix = su[su.length - 1];

                if(!suffix.equals("jpg")){
                    continue;
                }
                if (parts.length >= 2) {
                    String identifier = parts[0]; // 提取标识（如 480）

                    // 读取文件内容到内存
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    long totalLength = 0; // 文件总长度
                    while ((bytesRead = zipStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalLength += bytesRead; // 累加每次读取的字节数
                    }

                    String contentType = "image/jpeg"; // 根据文件类型设置
                    SysOssVo sysOssVo1 = sysOssServiceImpl.upload(new ByteArrayInputStream(outputStream.toByteArray()), suffix, productImagePath+"/"+entryName, totalLength, contentType);

                    BizProductImagesBo bizProductImagesBo = new BizProductImagesBo();
                    bizProductImagesBo.setProductId(productId);
                    bizProductImagesBo.setImageUrl(sysOssVo1.getOssId());
                    bizProductImagesBo.setType(identifier);
                    bizProductImagesBo.setSouceLanguage("CHS");
                    bizProductImagesBo.setTargetLanguage("CHS");

                    bizProductImagesServiceImpl.insertByBo(bizProductImagesBo);

                    System.out.println("已上传并录入: " + entryName + ", 标识: " + identifier);
                }
            }
        }

        return true;
    }

    private final ISysOssService sysOssServiceImpl;

}
