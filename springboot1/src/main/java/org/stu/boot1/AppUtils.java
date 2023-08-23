package org.stu.boot1;

import org.quincy.rock.core.vo.PageSet;

import com.github.pagehelper.Page;

/**
 * <b>AppUtils。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public abstract class AppUtils {
	
	/**
	 * <b>toPageSet。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param <E>
	 * @param page
	 * @return
	 */
	public static <E> PageSet<E> toPageSet(Page<E> page) {
		return PageSet.of(page.getResult(), page.getPageNum(), page.getPageSize(), page.getTotal());
	}
}
