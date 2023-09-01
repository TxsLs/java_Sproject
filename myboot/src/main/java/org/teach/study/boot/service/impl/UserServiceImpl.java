package org.teach.study.boot.service.impl;

import java.util.Map;

import org.quincy.rock.core.dao.MybatisSQLProvider;
import org.quincy.rock.core.dao.sql.Sort;
import org.quincy.rock.core.vo.PageSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teach.study.boot.AppUtils;
import org.teach.study.boot.BaseService;
import org.teach.study.boot.dao.UserDao;
import org.teach.study.boot.entity.Photo;
import org.teach.study.boot.entity.User;
import org.teach.study.boot.service.UserService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class UserServiceImpl extends BaseService<User, UserDao> implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;	

	@Override
	public PageSet<User> queryPageByCondition(Map<String, Object> condition, Sort sort, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		PageHelper.orderBy(sort.toString(MybatisSQLProvider.getFieldNameConverter()));
		Page<User> p = this.operate().findPageByCondition(condition);
		return AppUtils.toPageSet(p);
	}

	@Override
	public User findByCode(String code) {
		return operate().findByCode(code);
	}
	
	@Override
	@Transactional
	public int changeSelfPassword(String code, String oldPassword, String newPassword) {
		User vo = operate().findByCode(code);
		if (vo == null || !passwordEncoder.matches(oldPassword, vo.getPassword()))
			return 0;
		else {
			String encodedPassword = passwordEncoder.encode(newPassword);
			return operate().changePassword(code, encodedPassword);
		}
	}

	@Override
	@Transactional
	public int changePassword(String code, String newPassword) {
		String encodedPassword = passwordEncoder.encode(newPassword);
		return operate().changePassword(code, encodedPassword);
	}

	@Override
	public User checkPassword(String code, String password) {
		User vo = operate().findByCode(code);
		if (vo == null || !passwordEncoder.matches(password, vo.getPassword()))
			return null;
		else {
			return vo;
		}
	}

	@Override
	public Photo getPhoto(long id) {
		return this.operate().getPhoto(id);
	}
	
	@Override
	@Transactional
	public int updatePhoto(Photo photo) {
		return operate().updatePhoto(photo);
	}

	@Override
	@Transactional
	public int updateSelfInfo(User vo) {
		return operate().updateSelfInfo(vo);
	}

	/** 
	 * insert。
	 * @see org.teach.study.boot.BaseService#insert(org.teach.study.boot.Entity, boolean)
	 */
	@Override
	@Transactional
	public int insert(User entity, boolean ignoreNullValue) {
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		return super.insert(entity, ignoreNullValue);
	}
}
