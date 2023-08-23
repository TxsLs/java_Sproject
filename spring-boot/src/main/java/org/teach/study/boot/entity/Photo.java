package org.teach.study.boot.entity;

import org.quincy.rock.core.util.StringUtil;
import org.teach.study.boot.Entity;

import lombok.Getter;
import lombok.Setter;

/**
 * <b>Photo。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Getter
@Setter
public class Photo extends Entity {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -7109419962019911868L;

	private byte[] photo;
	private String photoFile;

	public long length() {
		return isEmpty() ? 0 : photo.length;
	}

	public boolean isEmpty() {
		return StringUtil.isBlank(photoFile) || photo == null || photo.length == 0;
	}

	public static boolean isEmpty(Photo photo) {
		return photo == null || photo.isEmpty();
	}
}
