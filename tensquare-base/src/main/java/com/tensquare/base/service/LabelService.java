package com.tensquare.base.service;

import com.tensquare.base.dao.LabelDao;
import com.tensquare.base.pojo.Label;
import util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询所有
     */
    public List<Label> findAll(){
        return labelDao.findAll();
    }

    /**
     * 查询一个
     */
    public Label findById(String id){
        return labelDao.findById(id).get();
    }

    /**
     * 添加
     */
    public void add(Label label){
        //使用idWorker获取一个值
        label.setId(idWorker.nextId()+"");
        labelDao.save(label);
    }

    /**
     * 修改
     */
    public void update(Label label){
        labelDao.save(label);
    }

    /**
     * 删除
     */
    public void deleteById(String id){
        labelDao.deleteById(id);
    }

    /**
     * 创建Specification
     */
    private Specification<Label> createSpecification(Map searchMap){

        //提供匿名内部类
        return new Specification<Label>(){

            /**
             *
             * @param root 根对象,通过根对象获取需要查询的属性
             * @param criteriaQuery
             * @param criteriaBuilder 查询构造器.用于构造不同类型的查询
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //定义List集合,用于存储Predicate对象
                List<Predicate> preList = new ArrayList<>();

                //根据labelname进行模糊搜索
                if(searchMap.get("labelname")!=null && !searchMap.get("labelname").equals("")){
                    //把查询条件放入predicateList集合
                    preList.add(criteriaBuilder.like(root.get("labelname").as(String.class),"%"+searchMap.get("labelname")+"%"));
                }

                //根据state查询
                if(searchMap.get("state")!=null && !searchMap.get("state").equals("")){
                    //把查询条件放入predicateList集合
                    preList.add(criteriaBuilder.like(root.get("state").as(String.class),searchMap.get("state")+""));
                }

                //根据recommend查询
                if(searchMap.get("recommend")!=null && !searchMap.get("recommend").equals("")){
                    //把查询条件放入predicateList集合
                    preList.add(criteriaBuilder.like(root.get("recommend").as(String.class),searchMap.get("recommend")+""));
                }

                Predicate[] preArray = new Predicate[preList.size()];

                //preList.toArray(preArray):把preList集合中的每个元素逐一取出,放入preArray数组中
                return criteriaBuilder.and(preList.toArray(preArray));
            }
        };
    }

    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    public List<Label> findSearch(Map searchMap) {
        //创建Specification对象
        Specification<Label> spec = createSpecification(searchMap);
        List<Label> list = labelDao.findAll(spec);
        return list;
    }

    /**
     * 条件+分页
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public Page<Label> findSearch(Map searchMap, int page, int size) {
        //创建Specification对象
        Specification<Label> spec = createSpecification(searchMap);
        //注意：Spring Data JPA的page从0开始
        return labelDao.findAll(spec, PageRequest.of(page - 1, size));
    }
}
