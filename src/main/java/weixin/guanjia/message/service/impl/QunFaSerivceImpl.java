package weixin.guanjia.message.service.impl;

import java.io.Serializable;

import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weixin.guanjia.account.service.WeixinAccountServiceI;
import weixin.guanjia.message.entity.QunFa;
import weixin.guanjia.message.service.QunFaServiceI;

/**
 * @author huwei
 * @date 2015年12月4日
 *
 */
@Service("qunFaService")
@Transactional
public class QunFaSerivceImpl  extends CommonServiceImpl implements QunFaServiceI{
	@Autowired
	private WeixinAccountServiceI weixinAccountService;
	@Override
 	public <T> void delete(T entity) {
 		super.delete(entity);
 	}
 	@Override
 	public <T> Serializable save(T entity) {
 		((QunFa)entity).setAccountId(ResourceUtil.getWeiXinAccountId());
 		Serializable t = super.save(entity);
 		return t;
 	}
 	@Override
 	public <T> void saveOrUpdate(T entity) {
 		super.saveOrUpdate(entity);
 	}
}
