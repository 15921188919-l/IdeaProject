package com.tensquare.spit.dao;

import com.tensquare.spit.pojo.Spit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 吐槽的dao
 */
public interface SpitDao extends MongoRepository<Spit,String>{

    /**
     * 根据上级ID查询吐槽评论
     */
    public Page<Spit> findByParentidOrderByPublishtime(String parentid, Pageable pageable);
}
