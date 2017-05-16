package com.lxy.wifistore.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;


/**
 * 类描述：详情信息实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:08:48
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DetailEntity {
	@SerializedName ("appBaseInfoObj")
	public BaseInfo                baseInfo;
	@SerializedName ("appResourceInfoObjs")
	public ArrayList<ResourceInfo> resourceInfos;
	@SerializedName ("commentSummaryInfoObj")
	public CommentInfo             commentInfo;
	@SerializedName ("cpInfoObjs")
	public ArrayList<CPInfo>       cpInfo;
	@SerializedName ("relatedRecommendedInfoObjs")
	public ArrayList<RelatedInfo>  relatedInfos;
	
	public DetailEntity() {
	}
	
	public static class BaseInfo implements Serializable {
		private static final long serialVersionUID = 8099187095197749541L;
		public int                appId;
		@SerializedName ("downloads")
		public long               downloads;
		@SerializedName ("intro")
		public String             summary;
		@SerializedName ("logo")
		public String             icon;
		@SerializedName ("name")
		public String             name;
		@SerializedName ("pkgSize")
		public String             size;
		@SerializedName ("price")
		public int                price;
		@SerializedName ("star")
		public int                star;
		@SerializedName ("update")
		public String             time;
		@SerializedName ("lv")
		public String             miniSdk;
		@SerializedName ("cpName")
		public String             cpName;
		@SerializedName ("pckName")
		public String             pckName;
		@SerializedName ("downPath")
		public String             downPath;
		
		@Override
		public String toString() {
			return "BaseInfo [downloads=" + downloads + ", summary=" + summary + ", icon=" + icon + ", name=" + name + ", size=" + size + ", price=" + price + ", star=" + star + ", time=" + time + ", miniSdk=" + miniSdk + ", cpName=" + cpName + "]";
		}
	}
	
	public static class ResourceInfo implements Serializable {
		private static final long serialVersionUID = 5158634889869198995L;
		@SerializedName ("capSeq")
		public int                order;
		@SerializedName ("resType")
		public int                resType;
		@SerializedName ("url")
		public String             url;
		
		@Override
		public String toString() {
			return "ResourceInfo [order=" + order + ", resType=" + resType + ", url=" + url + "]";
		}
	}
	
	public static class CommentInfo implements Serializable {
		private static final long serialVersionUID = 1414217335695845711L;
		@SerializedName ("inGeneral")
		public int                soso;
		@SerializedName ("notLike")
		public int                hate;
		@SerializedName ("veryLike")
		public int                love;
		
		@Override
		public String toString() {
			return "CommentInfo [soso=" + soso + ", hate=" + hate + ", love=" + love + "]";
		}
	}
	
	public static class CPInfo implements Serializable {
		private static final long serialVersionUID = -8193257991882763846L;
		@SerializedName ("cpName")
		public String             cpName;
		@SerializedName ("defaultDetail")
		public boolean            defaultDetail;
		@SerializedName ("detailsId")
		public int                detailId;
		@SerializedName ("email")
		public String             email;
		
		@Override
		public String toString() {
			return "CPInfo [cpName=" + cpName + ", defaultDetail=" + defaultDetail + ", detailId=" + detailId + ", email=" + email + "]";
		}
	}
	
	public static class RelatedInfo implements Serializable {
		private static final long serialVersionUID = 4306733126877957457L;
		@SerializedName ("appId")
		public int                id;
		@SerializedName ("appName")
		public String             appName;
		@SerializedName ("cpName")
		public String             cpName;
		@SerializedName ("logo")
		public String             icon;
		@SerializedName ("price")
		public int                price;
		@SerializedName ("star")
		public int                star;
		
		@Override
		public String toString() {
			return "RelatedInfo [appName=" + appName + ", cpName=" + cpName + ", icon=" + icon + ", price=" + price + ", star=" + star + "]";
		}
	}
	
	@Override
	public String toString() {
		return "DetailEntity [baseInfo=" + baseInfo + ", resourceInfos=" + resourceInfos + ", commentInfo=" + commentInfo + ", cpInfo=" + cpInfo + ", relatedInfos=" + relatedInfos + "]";
	}
}
