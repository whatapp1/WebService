package com.bm.insurance.cloud.orm.bean;

import com.bm.insurance.cloud.orm.entity.TjUploadInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by WFY
 * DATE: 2017/4/23
 * TIME：17:02
 */
public class PeUploadInfo extends TjUploadInfo implements Serializable {
    private static final long serialVersionUID = 7244505590103036300L;
    private String tjName;//体检人姓名
    private Long tjOrderId;//体检订单号
    private String tjOrderCompanyId;//体检公司
    private Date tjOrderTime;//预约体检时间
    private Date tjDownloadTime;//下载日期
    private Date tjUploadTime;//解析日期
    private int tjUploadStatus;//体检解析状态
    private String tjPdfPath;//PDF文件地址


    public String getTjName() {
        return tjName;
    }

    public void setTjName(String tjName) {
        this.tjName = tjName;
    }

    public Long getTjOrderId() {
        return tjOrderId;
    }

    public void setTjOrderId(Long tjOrderId) {
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

    public Date getTjOrderTime() {
        return tjOrderTime;
    }

    public void setTjOrderTime(Date tjOrderTime) {
        this.tjOrderTime = tjOrderTime;
    }

    public Date getTjDownloadTime() {
        return tjDownloadTime;
    }

    public void setTjDownloadTime(Date tjDownloadTime) {
        this.tjDownloadTime = tjDownloadTime;
    }

    public int getTjUploadStatus() {
        return tjUploadStatus;
    }

    public void settjUploadStatus(int tjUploadStatus) {
        this.tjUploadStatus = tjUploadStatus;
    }

    public Date getTjUploadTime() {
        return tjUploadTime;
    }

    public void setTjUploadTime(Date tjUploadTime) {
        this.tjUploadTime = tjUploadTime;
    }
}
