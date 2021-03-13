package com.tensquare.spit.controller;

import com.tensquare.spit.pojo.Spit;
import com.tensquare.spit.service.SpitService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/spit")
public class SpitController {

    @Autowired
    private SpitService spitService;

    /**
     * 查询所有
     */
    @RequestMapping(method = RequestMethod.GET )
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findAll());
    }

    /**
     * 查询一个
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable String id){
        return new Result(true,StatusCode.OK,"查询成功",spitService.findById(id));
    }

    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Spit spit){
        spitService.add(spit);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public Result update(@RequestBody Spit spit,@PathVariable String id){
        //设置id
        spit.setId(id);
        spitService.update(spit);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String id){
        spitService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 根据上级ID查询吐槽数据(分页)
     */
    @RequestMapping(value = "/comment/{parentid}/{page}/{size}",method = RequestMethod.GET)
    public Result findByParentid(@PathVariable String parentid,@PathVariable int page,@PathVariable int size){
        Page<Spit> list = spitService.findByParentid(parentid,page,size);
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(list.getTotalElements(),list.getContent()));
    }


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 吐槽点赞
     */
    @RequestMapping(value ="/thumbup/{id}",method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String id){

        //模拟当前登录用户
        String userid = "1";

        //1.从redis查询用户是否已经点赞过
        String flag = (String) redisTemplate.opsForValue().get("spit_"+userid+"_"+id);

        if(flag!=null){
            //点赞过该吐槽
            return new Result(false,StatusCode.REPEATE_ERROR,"该用户已经点赞过");
        }

        spitService.thumbup(id);

        //2.把数据存入redis
        redisTemplate.opsForValue().set("spit_"+userid+"_"+id,"1");
        return new Result(true,StatusCode.OK,"点赞成功");
    }



}
