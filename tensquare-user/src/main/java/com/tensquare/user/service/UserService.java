package com.tensquare.user.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import entity.Result;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import util.IdWorker;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<User> findAll() {
		return userDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<User> findSearch(Map whereMap, int page, int size) {
		Specification<User> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<User> findSearch(Map whereMap) {
		Specification<User> specification = createSpecification(whereMap);
		return userDao.findAll(specification);
	}


	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		return userDao.findById(id).get();
	}

	@Autowired
	private BCryptPasswordEncoder cryptPasswordEncoder;
	/**
	 * 增加
	 * @param user
	 */
	public void add(User user) {
		user.setId( idWorker.nextId()+"" );

		user.setPassword(cryptPasswordEncoder.encode(user.getPassword()));

		userDao.save(user);
	}

	/**
	 * 修改
	 * @param user
	 */
	public void update(User user) {
		userDao.save(user);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		userDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<User> createSpecification(Map searchMap) {

		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 手机号码
                if (searchMap.get("mobile")!=null && !"".equals(searchMap.get("mobile"))) {
                	predicateList.add(cb.like(root.get("mobile").as(String.class), "%"+(String)searchMap.get("mobile")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 性别
                if (searchMap.get("sex")!=null && !"".equals(searchMap.get("sex"))) {
                	predicateList.add(cb.like(root.get("sex").as(String.class), "%"+(String)searchMap.get("sex")+"%"));
                }
                // 头像
                if (searchMap.get("avatar")!=null && !"".equals(searchMap.get("avatar"))) {
                	predicateList.add(cb.like(root.get("avatar").as(String.class), "%"+(String)searchMap.get("avatar")+"%"));
                }
                // E-Mail
                if (searchMap.get("email")!=null && !"".equals(searchMap.get("email"))) {
                	predicateList.add(cb.like(root.get("email").as(String.class), "%"+(String)searchMap.get("email")+"%"));
                }
                // 兴趣
                if (searchMap.get("interest")!=null && !"".equals(searchMap.get("interest"))) {
                	predicateList.add(cb.like(root.get("interest").as(String.class), "%"+(String)searchMap.get("interest")+"%"));
                }
                // 个性
                if (searchMap.get("personality")!=null && !"".equals(searchMap.get("personality"))) {
                	predicateList.add(cb.like(root.get("personality").as(String.class), "%"+(String)searchMap.get("personality")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RabbitMessagingTemplate rabbitMessagingTemplate;
	/**
	 * 发送手机验证码
	 * @param mobile
	 */
	public void sendsms(String mobile) {

		//1.随机生成6位数字字符串
		String code = RandomStringUtils.randomNumeric(6);

		//2.存入redis
		//永久保存
		//redisTemplate.opsForValue().set("sms_"+mobile,code);

		//设置过期时间(5分钟后过期)
		redisTemplate.opsForValue().set("sms_"+mobile,code,5, TimeUnit.MINUTES);

		//3.让RabbitMQ发送消息
		Map<String,String> map = new HashMap<String,String>();
		map.put("mobile",mobile);
		map.put("code",code);

		rabbitMessagingTemplate.convertAndSend("itcast",map);
	}


	/**
	 * 用户注册
	 * @param user
	 * @param code
	 */
	public void register(User user, String code) {

		//1.从redis取出当初发给这个用户的验证码
		String redisCode = (String) redisTemplate.opsForValue().get("sms_" + user.getMobile());

		if(redisCode==null){
			throw new RuntimeException("请重新发送验证码");
		}

		if(!redisCode.equals(code)){
			throw new RuntimeException("验证码输入有误 ");
		}

		//插入用户
		user.setId(idWorker.nextId()+"");
		userDao.save(user);
	}

	/**
	 * 用户登录
	 * @param map
	 * @return
	 */
	public User login(Map<String, String> map) {

		//1.判断账户是否存在
		User user = userDao.findByMobile(map.get("mobile"));

		//2.判断密码是否正确
		if(user!=null && cryptPasswordEncoder.matches(map.get("password"),user.getPassword())){
			//成功
			return user;
		}else {
			//失败
			return null;
		}
	}

	/**
	 * 更新关注数
	 * @param userid
	 * @param x
	 */
	@Transactional
    public void updateFollowcount(String userid, int x) {
    	userDao.updateFollowcount(userid,x);
    }

	/**
	 * 更新粉丝数
	 * @param userid
	 * @param x
	 */
	@Transactional
	public void updateFanscount(String userid, int x) {
		userDao.updateFanscount(userid,x);
	}
}
