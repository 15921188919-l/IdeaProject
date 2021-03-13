package com.tensquare.recruit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.recruit.pojo.Recruit;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface RecruitDao extends JpaRepository<Recruit,String>,JpaSpecificationExecutor<Recruit>{

    /**
     * 根据state按照createtime倒序,取前面4条
     * @param state
     * @return
     */
    List<Recruit> findTop4ByStateOrderByCreatetimeDesc(String state);

    /**
     * 查询state不为0,按照createtime倒序,取前面12条
     * @param s
     * @return
     */
    List<Recruit> findTop12ByStateNotOrderByCreatetimeDesc(String s);
}
