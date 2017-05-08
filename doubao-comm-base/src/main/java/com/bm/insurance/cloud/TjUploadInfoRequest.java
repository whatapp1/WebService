package com.bm.insurance.cloud;

import com.bm.insurance.cloud.bean.PageRequest;

import java.util.Date;

/**
 * Created by WFY
 * DATE: 2017/4/23
 * TIME：11:32
 */
public class TjUploadInfoRequest extends PageRequest {
    private static final long serialVersionUID = -2322479136233959403L;
    private String tjName;//体检人姓名
    private String uid;
    private String tjOrderId;//体检订单号
    private String tjOrderCompanyId;//体检公司
    private Date startDownloadTime;//解析日期
    private Date endDownloadTime;//解析日期
    private Integer tjUploadStatus;//体检解析状态
    private String tjPdfPath;//PDF文件地址
    private Date startOrderTime;//预约体检时间
    private Date endOrderTime;//预约体检时间
    private Date endCheckTime;//下载日期
    private Date startCheckTime;//下载日期

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public Date getStartOrderTime() {
        return startOrderTime;
    }

    public void setStartOrderTime(Date startOrderTime) {
        this.startOrderTime = startOrderTime;
    }

    public Date getEndOrderTime() {
        return endOrderTime;
    }

    public void setEndOrderTime(Date endOrderTime) {
        this.endOrderTime = endOrderTime;
    }


    public Date getStartCheckTime() {
        return startCheckTime;
    }

    public void setStartCheckTime(Date startCheckTime) {
        this.startCheckTime = startCheckTime;
    }


    public Date getEndCheckTime() {
        return endCheckTime;
    }

    public void setEndCheckTime(Date endCheckTime) {
        this.endCheckTime = endCheckTime;
    }


    public Date getStartDownloadTime() {
        return startDownloadTime;
    }

    public void setStartDownloadTime(Date startDownloadTime) {
        this.startDownloadTime = startDownloadTime;
    }


    public Date getEndDownloadTime() {
        return endDownloadTime;
    }

    public void setEndDownloadTime(Date endDownloadTime) {
        this.endDownloadTime = endDownloadTime;
    }


    public String getTjName() {
        return tjName;
    }

    public void setTjName(String tjName) {
        this.tjName = tjName;
    }

    public String getTjOrderId() {
        return tjOrderId;
    }

    public void setTjOrderId(String tjOrderId) {
        this.tjOrderId = tjOrderId;
    }

    public String getTjOrderCompanyId() {
        return tjOrderCompanyId;
    }

    public void setTjOrderCompanyId(String tjOrderCompanyId) {
        this.tjOrderCompanyId = tjOrderCompanyId;
    }

    public String getTjPdfPath() {
        return tjPdfPath;
    }

    public void setTjPdfPath(String tjPdfPath) {
        this.tjPdfPath = tjPdfPath;
    }


    public Integer getTjUploadStatus() {
        return tjUploadStatus;
    }

    public void settjUploadStatus(Integer tjUploadStatus) {
        this.tjUploadStatus = tjUploadStatus;
    }


}
