package org.teach.study.boot;

import org.quincy.rock.core.dao.CommonOperate;

/**
 * <b>Dao。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public interface Dao<T extends Entity> extends CommonOperate<T>, MetaDao<T> {

}
