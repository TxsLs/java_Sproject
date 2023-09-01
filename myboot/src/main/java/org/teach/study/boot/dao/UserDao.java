package org.teach.study.boot.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.quincy.rock.core.dao.DaoUtil;
import org.quincy.rock.core.util.MapUtil;
import org.teach.study.boot.Dao;
import org.teach.study.boot.entity.Photo;
import org.teach.study.boot.entity.User;

import com.github.pagehelper.Page;

@Mapper
public interface UserDao extends Dao<User> {
	/**
	 * <b>组合条件查询。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param condition 组合条件Map
	 * @return 一页数据列表
	 */
	public Page<User> findPageByCondition(Map<String, Object> condition);

	/**
	 * <b>更新个人信息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 根据用户id更新用户信息，值对象的id必须有效。
	 * 可以修改自己少部分个人信息。
	 * @param vo　个人用户信息
	 * @return　更新数据条数
	 */
	public int updateSelfInfo(User vo);

	/**
	 * <b>获得照片。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 返回的照片数据存放在二进制数组里。
	 * @param id 主键id
	 * @return Photo
	 */
	public Photo getPhoto(long id);

	/**
	 * <b>更新照片。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param photo 照片实体对象
	 * @return 更新数据条数
	 */
	public int updatePhoto(Photo photo);

	/**
	 * <b>修改密码。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param code 用户登录名
	 * @param newPassword 加密后的新密码
	 * @return 更新数据条数
	 */
	default int changePassword(String code, String newPassword) {
		return updateMap(MapUtil.asMap("password", newPassword), DaoUtil.and().equal("code", code));
	}

	/**
	 * <b>查询用户信息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 返回信息包含加密的密码。
	 * @param code 工号
	 * @return User
	 */
	default User findByCode(String code) {
		return findByName("code", code);
	}
}
