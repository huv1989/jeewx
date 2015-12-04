package weixin.guanjia.message.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.LogUtil;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import weixin.guanjia.account.entity.WeixinAccountEntity;
import weixin.guanjia.account.service.WeixinAccountServiceI;
import weixin.guanjia.core.util.WeixinUtil;
import weixin.guanjia.message.dao.TextTemplateDao;
import weixin.guanjia.message.entity.QunFa;
import weixin.guanjia.message.entity.qunfa.Filter;
import weixin.guanjia.message.entity.qunfa.Text;
import weixin.guanjia.message.entity.qunfa.TextMessage;
import weixin.guanjia.message.service.QunFaServiceI;
import weixin.guanjia.message.service.TextTemplateServiceI;
import weixin.util.WeiXinConstants;

/**
 * 文本消息
 * 
 */
@Controller
@RequestMapping("/qunFaController")
public class QunFaController {
	
	@Autowired
	private TextTemplateDao textTemplateDao;
	@Autowired
	private TextTemplateServiceI textTemplateService;
	@Autowired
	private QunFaServiceI qunFaService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private WeixinAccountServiceI weixinAccountService;
	private String message;
	
    /**
     * 转向消息自动回复模板
     * @return
     */
	@RequestMapping(params = "list")
	public ModelAndView list() {
		return new ModelAndView("weixin/guanjia/qunfa/qunFaList");
	}

	/**
	 * 查询信息列表
	 * @param textTemplate
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "datagrid")
	@ResponseBody
	public void datagrid(QunFa qunfa, HttpServletRequest request,
			HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(QunFa.class, dataGrid);
		cq.eq("accountId", ResourceUtil.getWeiXinAccountId());
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq,
				qunfa);

		this.textTemplateService.getDataGridReturn(cq, true);

		TagUtil.datagrid(response, dataGrid);
	}

	@RequestMapping(params = "send")
	@ResponseBody
	public AjaxJson send(QunFa qunFa, HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		qunFa = this.qunFaService.getEntity(QunFa.class,qunFa.getId());
		TextMessage textM = new TextMessage();
		textM.setMsgtype(qunFa.getType());
		textM.setFilter(new Filter(false,"0"));
		textM.setText(new Text(qunFa.getContent()));
		JSONObject json= JSONObject.fromObject(textM);
		String accessToken = weixinAccountService.getAccessToken();
		String url = WeixinUtil.qunfa_url.replace("ACCESS_TOKEN",accessToken);
		JSONObject jsonObject= new JSONObject();
		System.out.println(json.toString());
		try {
			jsonObject = WeixinUtil.httpRequest(url, "POST", json.toString());
			LogUtil.info(jsonObject);
			if(jsonObject!=null){
				if (0 == jsonObject.getInt("errcode")) {
						message = "群发成功！";
				}
				else {
					message = "群发失败！错误码为："+jsonObject.getInt("errcode")+"错误信息为："+jsonObject.getString("errmsg");
				}
			}else{
				message = "群发失败！群发URL地址不正确。";
			}
		} catch (Exception e) {
			message = "群发失败！";
		}finally{
			systemService.addLog(message, Globals.Log_Type_DEL,Globals.Log_Leavel_INFO);		
		}
		j.setMsg(this.message);
		return j;
	}
	/**
	 * 删除信息
	 * @param textTemplate
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(QunFa textTemplate,
			HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		textTemplate = this.qunFaService.getEntity(QunFa.class,
				textTemplate.getId());

		this.qunFaService.delete(textTemplate);

		message = "删除信息数据成功！";
		systemService.addLog(message, Globals.Log_Type_DEL,
				Globals.Log_Leavel_INFO);
		j.setMsg(this.message);
		return j;
	}
	
	/**
	 * 批量删除文本消息
	 * 
	 * @return
	 */
	@RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		message = "删除信息数据成功";
		int succeed = 0;
		int error = 0;
		try {
			for (String id : ids.split(",")) {
				QunFa textTemplate = this.qunFaService.getEntity(QunFa.class,id);
				this.qunFaService.delete(textTemplate);
				succeed += 1;
				systemService.addLog(message, Globals.Log_Type_DEL,
						Globals.Log_Leavel_INFO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error += 1;
			message = "删除信息数据失败";
			throw new BusinessException(e.getMessage());
		}
		message="删除信息数据成功"+succeed+"条，失败"+error+"条";
		j.setMsg(message);
		return j;
	}

	/**
	 * 添加或修改消息页面跳转
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "addorUpdate")
	public ModelAndView addorUpdate(HttpServletRequest req) {
		WeixinAccountEntity weixinAccount = (WeixinAccountEntity) req.getSession().getAttribute(WeiXinConstants.WEIXIN_ACCOUNT);
		
		String id = req.getParameter("id");
		req.setAttribute("id", id);
		if (StringUtil.isNotEmpty(id)) {
			QunFa qunfa = this.textTemplateService.getEntity(QunFa.class, id);
			String name = qunfa.getName();
			String content = qunfa.getContent();
			req.setAttribute("accountId", ResourceUtil.getWeiXinAccountId());
			req.setAttribute("name", name);
			req.setAttribute("content", content);
		}
		return new ModelAndView("weixin/guanjia/qunfa/qunFaInfo");
	}

	/**
	 * 保存文本模板修改
	 * @param textTemplate
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "doSave")
	@ResponseBody
	public AjaxJson doSave(QunFa qunfa,
			HttpServletRequest req) {

		AjaxJson j = new AjaxJson();
		String id = qunfa.getId();
		if (StringUtil.isNotEmpty(id)) {
			QunFa qunfaResponse = this.textTemplateService.getEntity(
					QunFa.class, qunfa.getId());
			this.message = "修改关文本成功！";
			try {
				MyBeanUtils
						.copyBeanNotNull2Bean(qunfa, qunfaResponse);
				this.qunFaService.saveOrUpdate(qunfaResponse);
				systemService.addLog(message, Globals.Log_Type_UPDATE,
						Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			String accountId = ResourceUtil.getWeiXinAccountId();
			if (!"-1".equals(accountId)) {
				qunfa.setType("text");
				this.qunFaService.save(qunfa);
			} else {
				j.setSuccess(false);
				j.setMsg("请添加一个公众帐号。");
			}
		}
		return j;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}