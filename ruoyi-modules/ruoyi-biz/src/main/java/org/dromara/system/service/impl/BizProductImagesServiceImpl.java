package org.dromara.system.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.system.domain.BizProductImages;
import org.dromara.system.domain.BizProducts;
import org.dromara.system.domain.bo.BizProductImagesBo;
import org.dromara.system.domain.vo.BizProductImagesVo;
import org.dromara.system.domain.vo.SysOssConfigVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.mapper.BizProductImagesMapper;
import org.dromara.system.service.IBizProductImagesService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品图片Service业务层处理
 *
 * @author Lion Li
 * @date 2025-02-26
 */
@RequiredArgsConstructor
@Service
public class BizProductImagesServiceImpl implements IBizProductImagesService {

    private final BizProductImagesMapper baseMapper;

    private final SysOssServiceImpl sysOssServiceImpl;


    /**
     * 查询商品图片
     *
     * @param id 主键
     * @return 商品图片
     */
    @Override
    public BizProductImagesVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商品图片列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品图片分页列表
     */
    @Override
    public TableDataInfo<BizProductImagesVo> queryPageList(BizProductImagesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizProductImages> lqw = buildQueryWrapper(bo);
        Page<BizProductImagesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);

        List<BizProductImagesVo> filterResult = StreamUtils.toList(result.getRecords(), this::matchingUrl);
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    private BizProductImagesVo matchingUrl(BizProductImagesVo bizProductImagesVo){
//        List<SysOssVo> sysOssVos = SysOssServiceImpl.listByIds(Arrays.asList(bizProductImagesVo.getImageUrl()));
//        if(sysOssVos.size() > 0){
//            bizProductImagesVo.setImageUrl(sysOssVos.get(0).getUrl());
//        }
        return bizProductImagesVo;
    }

    /**
     * 查询符合条件的商品图片列表
     *
     * @param bo 查询条件
     * @return 商品图片列表
     */
    @Override
    public List<BizProductImagesVo> queryList(BizProductImagesBo bo) {
        LambdaQueryWrapper<BizProductImages> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizProductImages> buildQueryWrapper(BizProductImagesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizProductImages> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizProductImages::getId);
        lqw.eq(bo.getProductId() != null, BizProductImages::getProductId, bo.getProductId());
        lqw.eq(bo.getImageUrl() != null, BizProductImages::getImageUrl, bo.getImageUrl());
        lqw.eq(bo.getSortOrder() != null, BizProductImages::getSortOrder, bo.getSortOrder());
        lqw.eq(bo.getCreatedAt() != null, BizProductImages::getCreatedAt, bo.getCreatedAt());
        lqw.eq(StringUtils.isNotBlank(bo.getTargetLanguage()), BizProductImages::getTargetLanguage, bo.getTargetLanguage());
        lqw.eq(StringUtils.isNotBlank(bo.getSouceLanguage()), BizProductImages::getSouceLanguage, bo.getSouceLanguage());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), BizProductImages::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getUrlPublic()), BizProductImages::getUrlPublic, bo.getUrlPublic());
        lqw.eq(StringUtils.isNotBlank(bo.getFileNam()), BizProductImages::getFileNam, bo.getFileNam());
        return lqw;
    }

    /**
     * 新增商品图片
     *
     * @param bo 商品图片
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizProductImagesBo bo) {
        BizProductImages add = MapstructUtils.convert(bo, BizProductImages.class);
        validEntityBeforeSave(add);
//        SysOssVo sysOssVo = sysOssServiceImpl.getById(add.getImageUrl());
//        add.setUrlPublic(sysOssVo.getUrl());
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品图片
     *
     * @param bo 商品图片
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizProductImagesBo bo) {
        BizProductImages update = MapstructUtils.convert(bo, BizProductImages.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizProductImages entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商品图片信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    private final SysOssConfigServiceImpl sysOssConfigService;

    @Override
    public Boolean copyImagesByProductId(Long sourceProductId,String imageType, BizProducts targetProducts) {
        BizProductImagesBo bizProductImagesBo = new BizProductImagesBo();
        bizProductImagesBo.setProductId(sourceProductId);
        bizProductImagesBo.setType(imageType);
        List<BizProductImagesVo> bizProductImagesVos = this.queryList(bizProductImagesBo);

        OssClient ossClient = null;
        SysOssConfigVo sysOssConfigVo = null;

        for (BizProductImagesVo bizProductImagesVo: bizProductImagesVos){
            try {
                SysOssVo sysOssVo = sysOssServiceImpl.getById(bizProductImagesVo.getImageUrl());
                String[] pathArr = sysOssVo.getFileName().split("/CHS/");
                String imageUrl = pathArr[0]+"/"+targetProducts.getTargetLanguage()+"/"+pathArr[1];
                if(ossClient == null){
                    ossClient = OssFactory.instance(sysOssVo.getService());
                    sysOssConfigVo = sysOssConfigService.queryByConfigKey(ossClient.getConfigKey());
                }

                ossClient.copy(sysOssConfigVo.getBucketName(),sysOssVo.getFileName(),sysOssConfigVo.getBucketName(),imageUrl);
//                sysOssServiceImpl.upload()
//                String sourceImageUrl = basePath+"/"+sysOssVo.getFileName();
//                String targetImage = UUID.randomUUID() + sysOssVo.getFileSuffix();
//                String targetImageUrl = basePath+"/"+targetImage;

                //先复制1次
//                FileUtil.copy(sourceImageUrl,targetImageUrl,true);

                SysOssVo sysOssVoCreate = new SysOssVo();
                sysOssVoCreate.setOriginalName(sysOssVo.getOriginalName());
                sysOssVoCreate.setFileName(imageUrl);
                sysOssVoCreate.setUrl("http://"+sysOssConfigVo.getEndpoint()+"/"+sysOssConfigVo.getBucketName()+"/"+imageUrl);
                sysOssVoCreate.setService(sysOssVo.getService());
                sysOssVoCreate.setFileSuffix(sysOssVo.getFileSuffix());
                sysOssServiceImpl.insert(sysOssVoCreate);

//                sysOssServiceImpl.up

                //判断是否需要翻译
                HttpRequest httpRequest = HttpRequest.get("http://127.0.0.1:8887/check-text?image="+sysOssVoCreate.getUrl());
                HttpResponse httpResponse = httpRequest.execute();
                String result = httpResponse.body();

                //需要翻译
                if(result.equals("1")) {
                    int a = 3;
//                    String res = ImageTranslator.translateImageUrl(host + "/" + sysOssVo.getFileName(), targetProducts.getSourceLanguage(), targetProducts.getTargetLanguage());
//                    JSONObject jsonObject = JSONUtil.parseObj(res);
//                    String url = jsonObject.getJSONObject("Data").getStr("Url");
//                    HttpUtil.downloadFileFromUrl(url, targetImageUrl);
                }

                bizProductImagesVo.setId(null);
                bizProductImagesVo.setProductId(targetProducts.getId());
                BizProductImages bizProductImages = MapstructUtils.convert(bizProductImagesVo,BizProductImages.class);
                bizProductImages.setCreatedAt(new Date());
                bizProductImages.setCreateTime(new Date());
                bizProductImages.setImageUrl(sysOssVoCreate.getOssId());
                bizProductImages.setSouceLanguage(bizProductImagesVo.getTargetLanguage());
                bizProductImages.setTargetLanguage(targetProducts.getTargetLanguage());

                baseMapper.insert(bizProductImages);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

}
