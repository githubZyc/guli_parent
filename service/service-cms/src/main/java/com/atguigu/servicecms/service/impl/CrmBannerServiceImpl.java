package com.atguigu.servicecms.service.impl;

import com.atguigu.servicecms.entity.CrmBanner;
import com.atguigu.servicecms.mapper.CrmBannerMapper;
import com.atguigu.servicecms.service.CrmBannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-17
 */
@Service
public class CrmBannerServiceImpl extends ServiceImpl<CrmBannerMapper, CrmBanner> implements CrmBannerService {
    /**
     * 获取排序后的轮播图
     * 缓存@Cacheable
     * 根据方法对其返回结果进行缓存，下次请求时，如果缓存存在，则直接读取缓存数据返回；如果缓存不存在，则执行方法，并把返回的结果存入缓存中。一般用在查询方法上。
     *
     * 查看源码，属性值如下：
     * value：缓存名，必填，它指定了你的缓存存放在哪块命名空间
     * cacheNames：与 value 差不多，二选一即可
     * key：可选属性，可以使用 SpEL 标签自定义缓存的key
     *
     * @return
     */
    @Cacheable(value = "banner", key = "'selectIndexList'")
    @Override
    public List<CrmBanner> selectIndexList() {
        List<CrmBanner> list = baseMapper.selectList(new QueryWrapper<CrmBanner>().orderByDesc("sort"));
        return list;
    }
    @Override
    public void pageBanner(Page<CrmBanner> pageParam, Object o) {
        baseMapper.selectPage(pageParam,null);
    }
    @Override
    public CrmBanner getBannerById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 缓存@CachePut
     * 使用该注解标志的方法，每次都会执行，并将结果存入指定的缓存中。其他方法可以直接从响应的缓存中读取缓存数据，而不需要再去查询数据库。一般用在新增方法上。
     *
     * 查看源码，属性值如下：
     * value：缓存名，必填，它指定了你的缓存存放在哪块命名空间
     * cacheNames：与 value 差不多，二选一即可
     * key：可选属性，可以使用 SpEL 标签自定义缓存的key
     *
     * @param banner
     */
    @CachePut(value = "banner", key="saveBanner")
    @Override
    public void saveBanner(CrmBanner banner) {
        baseMapper.insert(banner);
    }

    /**
     * 缓存@CacheEvict
     * 使用该注解标志的方法，会清空指定的缓存。一般用在更新或者删除方法上
     *
     * 查看源码，属性值如下：
     * value：缓存名，必填，它指定了你的缓存存放在哪块命名空间
     * cacheNames：与 value 差不多，二选一即可
     * key：可选属性，可以使用 SpEL 标签自定义缓存的key
     * allEntries：是否清空所有缓存，默认为 false。如果指定为 true，则方法调用后将立即清空所有的缓存
     * beforeInvocation：是否在方法执行前就清空，默认为 false。如果指定为 true，则在方法执行前就会清空缓存
     *
     * @param banner
     */
    @CacheEvict(value = "banner", allEntries=true)
    @Override
    public void updateBannerById(CrmBanner banner) {
        baseMapper.updateById(banner);
    }

    @CacheEvict(value = "banner", allEntries=true)
    @Override
    public void removeBannerById(String id) {
        baseMapper.deleteById(id);
    }
}
