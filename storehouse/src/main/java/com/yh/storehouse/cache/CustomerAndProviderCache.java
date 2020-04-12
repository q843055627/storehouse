package com.yh.storehouse.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yh.storehouse.domain.Customer;
import com.yh.storehouse.domain.Goods;
import com.yh.storehouse.domain.Provider;
import com.yh.storehouse.vo.CustomerVo;
import com.yh.storehouse.vo.ProviderVo;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;


@Aspect
@Component
@EnableAspectJAutoProxy
public class CustomerAndProviderCache {

    private Logger logger = Logger.getLogger(CustomerAndProviderCache.class);

    //声明容器
    private static Map<String ,Object> CACHE_CONTAINER = new HashMap<>();

    public static Map<String ,Object> getCACHECONTAINER(){
        return CACHE_CONTAINER;
    }

    //声明切面表达式
    private static final String POINTCUT_CUSTOMER_ADD = "execution(* com.yh.storehouse.serviceImpl.CustomerServiceImpl.save(..))";
    private static final String POINTCUT_CUSTOMER_GET = "execution(* com.yh.storehouse.serviceImpl.CustomerServiceImpl.getById(..))";
    private static final String POINTCUT_CUSTOMER_UPDATE = "execution(* com.yh.storehouse.serviceImpl.CustomerServiceImpl.UpdateById(..))";
    private static final String POINTCUT_CUSTOMER_DELETE= "execution(* com.yh.storehouse.serviceImpl.CustomerServiceImpl.removeById(..))";
    private static final String POINTCUT_CUSTOMER_BATCHDELETE= "execution(* com.yh.storehouse.serviceImpl.CustomerServiceImpl.removeByIds(..))";

    private static final String CACHE_CUSTOMER_PROFIX = "customer";

    @Around(value = POINTCUT_CUSTOMER_ADD)
    public Object cacheCustomerAdd(ProceedingJoinPoint joinPoint) throws Throwable{
        CustomerVo customerVo = (CustomerVo) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if(isSuccess){
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + customerVo.getId(),customerVo);
            logger.info("客户对象缓存已新增" + CACHE_CUSTOMER_PROFIX + customerVo.getId());
        }
        return isSuccess;
    }

    @Around(value = POINTCUT_CUSTOMER_GET)
    public Object cacheCustomerGet(ProceedingJoinPoint joinPoint) throws Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Object res = CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + id);
        if(null != res){
            logger.info("已从缓存里面找到客户对象" + CACHE_CUSTOMER_PROFIX + id);
            return res;
        }
        else{
            Customer customer = (Customer) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + customer.getId() , customer);
            logger.info("未从缓存里面找到客户对象，去数据库查询并放到缓存" + CACHE_CUSTOMER_PROFIX + customer.getId());
            return customer;
        }
    }

    @Around(value = POINTCUT_CUSTOMER_UPDATE)
    public Object cacheCustomerUpdate(ProceedingJoinPoint joinPoint) throws Throwable{
        CustomerVo customerVo = (CustomerVo) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            Customer customer = (Customer) CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + customerVo.getId());
            if(null == customer){
                customer = new Customer();
            }
            BeanUtils.copyProperties(customerVo,customer);
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + customerVo.getId(),customer);
            logger.info("客户对象缓存已更新" + CACHE_CUSTOMER_PROFIX + customer.getId());
        }
        return res;
    }

    @Around(value = POINTCUT_CUSTOMER_DELETE)
    public Object cacheCustomerDelete(ProceedingJoinPoint joinPoint) throws  Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
            logger.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
        }
        return res;
    }

    @Around(value = POINTCUT_CUSTOMER_BATCHDELETE)
    public Object cacheCustomerBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable{
        List<Integer> ids = (List<Integer>) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            for(Integer id : ids){
                CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
                logger.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
            }
        }
        return res;
    }


    private static final String POINTCUT_PROVIDER_ADD = "execution(* com.yh.storehouse.serviceImpl.ProviderServiceImpl.save(..))";
    private static final String POINTCUT_PROVIDER_GET = "execution(* com.yh.storehouse.serviceImpl.ProviderServiceImpl.getById(..))";
    private static final String POINTCUT_PROVIDER_UPDATE = "execution(* com.yh.storehouse.serviceImpl.ProviderServiceImpl.UpdateById(..))";
    private static final String POINTCUT_PROVIDER_DELETE = "execution(* com.yh.storehouse.serviceImpl.ProviderServiceImpl.removeById(..))";
    private static final String POINTCUT_PROVIDER_BATCHDELETE = "execution(* com.yh.storehouse.serviceImpl.ProviderServiceImpl.removeByIds(..))";

    private static final String CACHE_PROVIDER_PROFIX = "provider";

    @Around(value = POINTCUT_PROVIDER_ADD)
    public Object cacheProviderAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        ProviderVo providerVo = (ProviderVo) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if(isSuccess){
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + providerVo.getId(),providerVo);
            logger.info("供应商对象缓存已新增" + CACHE_PROVIDER_PROFIX + providerVo.getId());
        }
        return isSuccess;
    }

    @Around(value = POINTCUT_PROVIDER_GET)
    public Object cacheProviderGet(ProceedingJoinPoint joinPoint) throws Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Object res = CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + id);
        if(null != res){
            logger.info("已从缓存里面找到供应商对象" + CACHE_PROVIDER_PROFIX + id);
            return res;
        }
        else{
            Provider provider = (Provider) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + provider.getId() , provider);
            logger.info("未从缓存里面找到供应商对象，去数据库查询并放到缓存" + CACHE_PROVIDER_PROFIX + provider.getId());
            return provider;
        }
    }

    @Around(value = POINTCUT_PROVIDER_UPDATE)
    public Object cacheProviderUpdate(ProceedingJoinPoint joinPoint) throws Throwable{
        ProviderVo providerVo = (ProviderVo) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            Provider provider = (Provider) CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + providerVo.getId());
            if(null == provider){
                provider = new Provider();
            }
            BeanUtils.copyProperties(providerVo,provider);
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + providerVo.getId(),provider);
            logger.info("供应商对象缓存已更新" + CACHE_PROVIDER_PROFIX + provider.getId());
        }
        return res;
    }

    @Around(value = POINTCUT_PROVIDER_DELETE)
    public Object cacheProviderDelete(ProceedingJoinPoint joinPoint) throws  Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
            logger.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
        }
        return res;
    }

    @Around(value = POINTCUT_PROVIDER_BATCHDELETE)
    public Object cacheProviderBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable{
        List<Integer> ids = (List<Integer>) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            for(Integer id : ids){
                CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
                logger.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
            }
        }
        return res;
    }


    //商品数据的缓存 声明切面表达式
    private static final String POINTCUT_GOODS_ADD = "execution(* com.yh.storehouse.serviceImpl.GoodsServiceImpl.save(..))";
    private static final String POINTCUT_GOODS_UPDATE = "execution(* com.yh.storehouse.serviceImpl.GoodsServiceImpl.updateById(..))";
    private static final String POINTCUT_GOODS_GET = "execution(* com.yh.storehouse.serviceImpl.GoodsServiceImpl.getById(..))";
    private static final String POINTCUT_GOODS_DELETE = "execution(* com.yh.storehouse.serviceImpl.GoodsServiceImpl.removeById(..))";

    private static final String CACHE_GOODS_PROFIX = "goods:";

    /**
     * 商品添加切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_ADD)
    public Object cacheGoodsAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods object = (Goods) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_GET)
    public Object cacheGoodsGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + object);
        if (res1 != null) {
            logger.info("已从缓存里面找到商品对象" + CACHE_GOODS_PROFIX + object);
            return res1;
        } else {
            Goods res2 = (Goods) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到商品对象，去数据库查询并放到缓存" + CACHE_GOODS_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_UPDATE)
    public Object cacheGoodsUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods deptVo = (Goods) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Goods dept = (Goods) CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + deptVo.getId());
            if (null == dept) {
                dept = new Goods();
            }
            BeanUtils.copyProperties(deptVo, dept);
            logger.info("商品对象缓存已更新" + CACHE_GOODS_PROFIX + deptVo.getId());
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + dept.getId(), dept);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_DELETE)
    public Object cacheGoodsDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_GOODS_PROFIX + id);
            logger.info("商品对象缓存已删除" + CACHE_GOODS_PROFIX + id);
        }
        return isSuccess;
    }
}
