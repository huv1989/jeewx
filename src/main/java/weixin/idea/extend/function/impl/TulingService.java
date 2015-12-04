package weixin.idea.extend.function.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import weixin.guanjia.core.entity.message.resp.Article;
import weixin.guanjia.core.entity.message.resp.NewsMessageResp;
import weixin.guanjia.core.entity.message.resp.TextMessageResp;
import weixin.guanjia.core.service.impl.BaiduTranslateService;
import weixin.guanjia.core.util.MessageUtil;
import weixin.idea.extend.function.KeyServiceI;

/**
 * @author huwei
 * @date 2015年12月3日
 *
 */
public class TulingService implements KeyServiceI{

	/* (non-Javadoc)
	 * @see weixin.idea.extend.function.KeyServiceI#getKey()
	 */
	@Override
	public String getKey() {
		return "图灵,机器人";
	}

	/* (non-Javadoc)
	 * @see weixin.idea.extend.function.KeyServiceI#excute(java.lang.String, weixin.guanjia.core.entity.message.resp.TextMessageResp, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String excute(String content, TextMessageResp defaultMessage,HttpServletRequest request) {
		String respMessage = null;
		if(content.contains("天气")){
			String json =  BaiduTranslateService.httpRequest("http://www.tuling123.com/openapi/api?key=71370c63f4cd4db53c35771428279aab&info="+content);
			Map map = (Map) JSON.parse(json);
			String text = map.get("text").toString();
			if( text.split(":").length > 1){
			String location = text.split(":")[0];
			String [] days = text.split(":")[1].split(";");
			
			List<Article> articleList = new ArrayList<Article>();
			// 创建图文消息
			NewsMessageResp newsMessage = new NewsMessageResp();
			newsMessage.setToUserName(defaultMessage.getToUserName());
			newsMessage.setFromUserName(defaultMessage.getFromUserName());
			newsMessage.setCreateTime(new Date().getTime());
			newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
			Article article1 = new Article();
			article1.setTitle(location);
			article1.setDescription("天气预报");
			article1.setPicUrl("http://unidust.cn/images/wechat_weather.jpg");
			article1.setUrl("http://m.sm.cn/s?q="+content);
			articleList.add(article1);
			for(int i=0; i<3;i++){
				Article article = new Article();
				article.setTitle(days[i]);
				article.setDescription("天气预报");
				article.setPicUrl(image(days[i]));
				article.setUrl("http://m.sm.cn/s?q="+content);
				articleList.add(article);
			}
			newsMessage.setArticleCount(articleList.size());
			newsMessage.setArticles(articleList);
			respMessage = MessageUtil.newsMessageToXml(newsMessage);
			}else{
				defaultMessage.setContent("查询出错!");
				respMessage = MessageUtil.textMessageToXml(defaultMessage);
			}
			}else{
				String json =  BaiduTranslateService.httpRequest("http://www.tuling123.com/openapi/api?key=71370c63f4cd4db53c35771428279aab&info="+content);
				Map map = (Map) JSON.parse(json);
				// 创建图文消息
				List<Article> articleList = new ArrayList<Article>();				
				NewsMessageResp newsMessage = new NewsMessageResp();
				newsMessage.setToUserName(defaultMessage.getToUserName());
				newsMessage.setFromUserName(defaultMessage.getFromUserName());
				newsMessage.setCreateTime(new Date().getTime());
				newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
				
				int code = Integer.parseInt(map.get("code").toString());
				switch (code) {
				//文字类
				case  100000:
					defaultMessage.setContent(map.get("text") .toString());
					respMessage = MessageUtil.textMessageToXml(defaultMessage);
					break;
				//链接类
				case 200000:
					defaultMessage.setContent(map.get("text") .toString()+":"+map.get("url") .toString());
					respMessage = MessageUtil.textMessageToXml(defaultMessage);
					break;
				//新闻
				case 302000:
					List newsList = (List) JSON.parse(map.get("list").toString());
					for(int i = 0; i < 4; i++){
						Map news = (Map) JSON.parse(newsList.get(i).toString());
						Article article = new Article();
						article.setTitle(news.get("article") .toString());
						article.setDescription(news.get("source") .toString());
						article.setPicUrl("http://unidust.cn/images/weixin-xinwen.png");
						article.setUrl(news.get("detailurl") .toString());
						articleList.add(article);
					}		
					newsMessage.setArticleCount(articleList.size());
					newsMessage.setArticles(articleList);
					respMessage = MessageUtil.newsMessageToXml(newsMessage);
					break;
				//菜谱
				case 308000:				
					List newsList2 = (List) JSON.parse(map.get("list").toString());
					for(int i = 0; i < 4; i++){
						Map news = (Map) JSON.parse(newsList2.get(i).toString());
						Article article = new Article();
						article.setTitle(news.get("info") .toString());
						article.setDescription(news.get("icon") .toString());
						article.setPicUrl(news.get("icon") .toString());
						article.setUrl(news.get("detailurl") .toString());
						articleList.add(article);
					}			
					newsMessage.setArticleCount(articleList.size());
					newsMessage.setArticles(articleList);
					respMessage = MessageUtil.newsMessageToXml(newsMessage);
					break;
				default:
					defaultMessage.setContent("R U Kidding Me？");
					respMessage = MessageUtil.textMessageToXml(defaultMessage);
					break;	
				}
			}
		return respMessage;
	}
	//获取天气图标
	   protected static String image(String weather){
		   String w= "";
		   if(weather.contains("晴")){
			   w = "http://s1.rr.itc.cn/weather/0828/m/Sunny_d.png";
		   }
		   if(weather.contains("云")){
			   w = "http://s1.rr.itc.cn/weather/0828/m/Cloudy_d_s.png";
		   }
		   if(weather.contains("云") && weather.contains("晴")){
			   w = "http://s1.rr.itc.cn/weather/0828/m/SunnyCloudy_d.png";
		   }
			if(weather.contains("雪")){
				w = "http://s1.rr.itc.cn/weather/0828/m/Snow_d.png";
			}
			if(weather.contains("阴")){
				w = "http://s1.rr.itc.cn/weather/0828/m/Overcast_d_s.png";
			}
			if(weather.contains("雨")){
				w = "http://s1.rr.itc.cn/weather/0828/m/LightRain_d_s.png";
			}
		   return w;
	   }
}
