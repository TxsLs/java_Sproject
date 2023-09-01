package org.teach.study.boot;

import org.quincy.rock.core.dao.CommonOperateProxy;
import org.quincy.rock.core.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>业务类抽象基类。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 业务实现类尽可能从该抽象基类继承。
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public abstract class BaseService<T extends Entity, D extends Dao<T>> extends CommonOperateProxy<T>
		implements Service<T> {
	@Autowired
	private D dao;

	@Autowired
	private IdentifierGenerator<String, Long> idGenerator;

	@Override
	protected D operate() {
		return dao;
	}

	@Override
	@Transactional
	public int insert(T entity, boolean ignoreNullValue) {
		String keyName = entity.getClass().getSimpleName();
		long id = idGenerator.generate(keyName);
		entity.setId(id);
		return dao.insert(entity, ignoreNullValue);
	}
}
