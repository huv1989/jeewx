package weixin.guanjia.message.service;

import java.io.Serializable;

import org.jeecgframework.core.common.service.CommonService;

/**
 * @author huwei
 * @date 2015年12月4日
 *
 */
public interface QunFaServiceI extends  CommonService{
	@Override
 	public <T> void delete(T entity);
 	@Override
 	public <T> Serializable save(T entity);
 	@Override
 	public <T> void saveOrUpdate(T entity);
}
