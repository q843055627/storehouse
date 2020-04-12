package com.yh.storehouse.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yh.storehouse.domain.Dept;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.vo.DeptVo;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
* 类上面标注的 @Aspect 注解，这表明该类是一个 Aspect（其实就是 Advisor）。
* 该类无需实现任何的接口，只需定义一个方法（方法叫什么名字都无所谓），只需在方法上标注 @Around 注解，
* 在注解中使用了 AspectJ 切点表达式。方法的参数中包括一个 ProceedingJoinPoint 对象，它在 AOP 中称为 Joinpoint（连接点），
* 可以通过该对象获取方法的任何信息，例如：方法名、参数等。
* */
@Aspect         //需要引入aop   作用是把当前类标识为一个切面供容器读取，即标注Aspect切面类
@Component          //把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>
@EnableAspectJAutoProxy     //开启AOP功能  用注解方式开启 AOP 功能，需要在对应配置类中添加该注解
public class DeptAndUserCache {

    private static Logger log = Logger.getLogger(Aspect.class);
    //声明缓存容器
    private  Map<String , Object> CACHE_CONTAINER = new HashMap<>();

    /*  eg :  execution(* aop.demo.GreetingImpl.*(..))
    * execution()：表示拦截方法，括号中可定义需要匹配的规则。
        第一个“*”：表示方法的返回值是任意的。
        第二个“*”：表示匹配该类中所有的方法。
        (..)：表示方法的参数是任意的。
    */
    //声明切面表达式       AspectJ 切点表达式
    private static final String POINTCUT_DEPT_UPDATE = "execution(* com.yh.storehouse.serviceImpl.DeptServiceImpl.updateById(..))";
    private static final String POINTCUT_DEPT_GET = "execution(* com.yh.storehouse.serviceImpl.DeptServiceImpl.getById(..))";
    private static final String POINTCUT_DEPT_DELETE = "execution(* com.yh.storehouse.serviceImpl.DeptServiceImpl.removeById(..))";
    private static final String POINTCUT_DEPT_ADD= "execution(* com.yh.storehouse.serviceImpl.DeptServiceImpl.save(..))";

    //前缀
    private static final String CACHE_DEPT_PROFIX = "dept:";

    //查询切入          环绕增强，即在拦截的方法（getOne 方法）“开始”和“结束”时都执行一遍 cacheDeptGet 方法
    @Around(value = POINTCUT_DEPT_GET)
    public Object cacheDeptGet(ProceedingJoinPoint joinPoint) throws Throwable{     //Joinpoint（连接点）参数
        //取出第一个数据
        Integer obj = (Integer) joinPoint.getArgs()[0];     //joinPoint.getArgs()[0]获取的是传给被拦截方法的参数
        //从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + obj);
        if(null != res1){
            log.info("已从缓存里找到部门对象" + CACHE_DEPT_PROFIX + obj);
            return res1;        //缓存里有值，直接返回即可，不用查库
        }
        else {
            Dept res2 = (Dept) joinPoint.proceed();         //放行,执行 getOne 方法，从库里读取数据
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + res2.getId(),res2);  //放入缓存容器中
            log.info("未从缓存中找到，去数据库中找到并放到缓存中" + CACHE_DEPT_PROFIX+res2.getId());
            return res2;
        }
    }
    @Around(value = POINTCUT_DEPT_UPDATE)
    //更新切入
    public Object cacheDeptUpdate(ProceedingJoinPoint joinPoint) throws Throwable{
        //获取数据---更新的deptvo信息
        DeptVo deptVo = (DeptVo) joinPoint.getArgs()[0];
        //放行--更新数据库信息    执行 updateById 方法
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if(isSuccess){      //数据库更新成功，则更新缓存
            //判断当前缓存是否已有这个更新的数据
            Dept dept = (Dept) CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + deptVo.getId());
            if(null == dept){
                //更新缓存
                dept = new Dept();
                BeanUtils.copyProperties(deptVo,dept);      //复制 deptVo 信息给 dept
                CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + dept.getId(),dept);
                log.info("部门对象缓存已更新" + CACHE_DEPT_PROFIX + deptVo.getId());
            }
        }
        return isSuccess;
    }

    @Around(value = POINTCUT_DEPT_DELETE)
    public Object cacheDeptDelete(ProceedingJoinPoint joinPoint) throws Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if(isSuccess){
            CACHE_CONTAINER.remove(CACHE_DEPT_PROFIX + id);
            log.info("部门对象缓存已删除"+ CACHE_DEPT_PROFIX+id);
        }
        return isSuccess;
    }

    @Around(value = POINTCUT_DEPT_ADD)
    public Object cacheDeptAdd(ProceedingJoinPoint joinPoint) throws Throwable{
        Dept obj = (Dept) joinPoint.getArgs()[0];
        Boolean  res = (Boolean) joinPoint.proceed();
        if(res){
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + obj.getId(),obj);
            log.info("已新增部门对象缓存" + CACHE_DEPT_PROFIX + obj.getId());
        }
        return res;
    }

    //用户对象缓存

    private static final String POINTCUT_USER_UPDATE = "execution(* com.yh.storehouse.serviceImpl.UserServiceImpl.updateById(..))";
    private static final String POINTCUT_USER_ADD = "execution(* com.yh.storehouse.serviceImpl.UserServiceImpl.save(..))";
    private static final String POINTCUT_USER_DELETE = "execution(* com.yh.storehouse.serviceImpl.UserServiceImpl.removeById(..))";
    private static final String POINTCUT_USER_GET = "execution(* com.yh.storehouse.serviceImpl.UserServiceImpl.getById(..))";

    private static final String CACHE_USER_PROFIX = "user";

    /**
     * 用户添加切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_USER_ADD)
    public Object cacheUserAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        User object = (User) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + object.getId(), object);
            log.info("已新增用户对象缓存" + CACHE_USER_PROFIX + object.getId());
        }
        return res;
    }

    @Around(value = POINTCUT_USER_GET)
    public Object cacheUserGet(ProceedingJoinPoint joinPoint)throws Throwable{
        Integer id = (Integer) joinPoint.getArgs()[0];
        Object res1 = CACHE_CONTAINER.get(CACHE_USER_PROFIX + id);
        if(null != res1){
            log.info("已从缓存中找到用户对象" + CACHE_USER_PROFIX + id);
            return res1;
        }
        else{
            User res2 = (User) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + res2.getId(),res2);
            log.info("未从缓存中找到用户对象，从数据库中找到并放入缓存中" + CACHE_USER_PROFIX + res2.getId());
            return res2;
        }
    }
    @Around(value = POINTCUT_USER_UPDATE)
    public Object cacheUSerUpdate(ProceedingJoinPoint joinPoint) throws Throwable{
        User userVo = (User) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if(res){
            User user = (User) CACHE_CONTAINER.get(CACHE_USER_PROFIX + userVo.getId());
            if(null == user){
                user = new User();
            }
            BeanUtils.copyProperties(userVo,user);
            CACHE_CONTAINER.put(CACHE_USER_PROFIX+user.getId(),user);
            log.info("用户对象缓存已更新"+ CACHE_USER_PROFIX + user.getId());
        }
        return res;
    }


    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_USER_DELETE)
    public Object cacheUserDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_USER_PROFIX + id);
            log.info("用户对象缓存已删除" + CACHE_USER_PROFIX + id);
        }
        return isSuccess;
    }

}
