package com.lxy.wifistore.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * 类描述：专题信息实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:10:21
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class TopicEntity {
	@SerializedName ("topicInfoObjs")
	public List<TopicInfo> topicList;
	@SerializedName ("resultObj")
	public ResultEntity    result;
	@SerializedName ("tn")
	public int             totalTopicCount; //总专题数量
	@SerializedName ("tp")
	public int             totalPages;     //总页数
	@SerializedName ("ttn")
	public int             curTopicCount;  //当前结果中专题数量
	                                        
	public TopicEntity() {
	}
	
	public static class TopicInfo implements Serializable {
		private static final long serialVersionUID = -7466774134358240385L;
		@SerializedName ("id")
		public int                id;
		@SerializedName ("type")
		public int                type;
		@SerializedName ("logo")
		public String             icon;
		@SerializedName ("name")
		public String             name;
		@SerializedName ("intro")
		public String             summary;
		@SerializedName ("pubTime")
		public String             time;
		@SerializedName ("appCount")
		public int                count;
		@SerializedName ("appBaseInfoObjs")
		public ArrayList<AppEntity>    appInfos;
		
		public TopicInfo() {
		}
		
		@Override
		public String toString() {
			return "TopicInfo [id=" + id + ", type=" + type + ", icon=" + icon + ", name=" + name + ", summary=" + summary + ", time=" + time + ", count=" + count + ", appInfos=" + appInfos + "]";
		}
		
	}
	
	@Override
	public String toString() {
		return "TopicEntity [topicList=" + topicList + ", result=" + result + ", totalTopicCount=" + totalTopicCount + ", totalPages=" + totalPages + ", curTopicCount=" + curTopicCount + "]";
	}
}
