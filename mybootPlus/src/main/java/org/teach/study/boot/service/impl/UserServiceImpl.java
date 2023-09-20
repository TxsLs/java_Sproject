package org.teach.study.boot.service.impl;

import org.quincy.rock.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teach.study.boot.BaseService;
import org.teach.study.boot.dao.UserDao;
import org.teach.study.boot.entity.Photo;
import org.teach.study.boot.entity.User;
import org.teach.study.boot.service.UserService;

@Service
public class UserServiceImpl extends BaseService<User, UserDao> implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User findByCode(String code) {
		return this.dao().findByName("code", code);
	}

	@Override
	@Transactional
	public boolean changeSelfPassword(String code, String oldPassword, String newPassword) {
		User vo = this.findByCode(code);
		if (vo == null || !passwordEncoder.matches(oldPassword, vo.getPassword()))
			return false;
		else {
			String encodedPassword = passwordEncoder.encode(newPassword);
			return dao().changePassword(code, encodedPassword) > 0;
		}
	}

	@Override
	@Transactional
	public boolean changePassword(String code, String newPassword) {
		String encodedPassword = passwordEncoder.encode(newPassword);
		return dao().changePassword(code, encodedPassword) > 0;
	}

	@Override
	public User checkPassword(String code, String password) {
		User vo = this.findByCode(code);
		if (vo == null || !passwordEncoder.matches(password, vo.getPassword()))
			return null;
		else {
			return vo;
		}
	}

	@Override
	public Photo getPhoto(long id) {
		return this.dao().getPhoto(id);
	}

	@Override
	@Transactional
	public boolean updatePhoto(Photo photo) {
		return dao().updatePhoto(photo) > 0;
	}

	@Override
	@Transactional
	public boolean updateSelfInfo(User vo) {
		return dao().updateSelfInfo(vo) > 0;
	}

	@Override
	public boolean insert(User entity, boolean ignoreNullValue, String... excluded) {
		if (StringUtil.isEmpty(entity.getPassword())) {
			entity.setPassword("111111");
		}
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		return super.insert(entity, ignoreNullValue, excluded);
	}
}
