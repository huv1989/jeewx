package weixin.guanjia.message.entity.qunfa;

/**
 * @author huwei
 * @date 2015年12月4日
 *
 */
public class TextMessage {
	private Filter filter;
	private Text text;
	private String msgtype;
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	
}
