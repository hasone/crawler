package com.zxsoft.crawler.entity;

// Generated 2014-9-19 17:19:57 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Website generated by hbm2java
 */
@Entity
@Table(name = "website", catalog = "crawler")
public class Website implements java.io.Serializable {

        /**
	 * 
	 */
        private static final long serialVersionUID = -5354652608097154552L;
        private Integer id;
        private String site;
        private SiteType siteType;
        private String comment;
        private Integer region;
        private String status;
        private Integer provinceId;
        private Integer cityId;
        private Integer areaId;
        private Set<Section> sections = new HashSet<Section>(0);
//        private Set<Auth> auths = new HashSet<Auth>(0);

        public Website() {
        }

        public Website(String site, SiteType siteType, String comment) {
                this.site = site;
                this.siteType = siteType;
                this.comment = comment;
        }

        public Website(String site, SiteType siteType, String comment, Integer region, Set<Section> sections) {
                this.site = site;
                this.siteType = siteType;
                this.comment = comment;
                this.region = region;
                this.sections = sections;
        }

        @Id
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        @Column(name = "id", unique = true, nullable = false)
        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        @Column(name = "site", unique = true, nullable = false, length = 150)
        public String getSite() {
                return this.site;
        }

        public void setSite(String site) {
                this.site = site;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "type", nullable = false)
        public SiteType getSiteType() {
                return this.siteType;
        }

        public void setSiteType(SiteType siteType) {
                this.siteType = siteType;
        }

        @Column(name = "comment", nullable = false, length = 100)
        public String getComment() {
                return this.comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }

        @Column(name = "region", length = 45)
        public Integer getRegion() {
                return this.region;
        }

        public void setRegion(Integer region) {
                this.region = region;
        }

        @Column(name = "status", length = 45)
        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        @Column(name = "provinceId")
        public Integer getProvinceId() {
                return provinceId;
        }

        public void setProvinceId(Integer provinceId) {
                this.provinceId = provinceId;
        }

        @Column(name = "cityId")
        public Integer getCityId() {
                return cityId;
        }

        public void setCityId(Integer cityId) {
                this.cityId = cityId;
        }

        @Column(name = "areaId")
        public Integer getAreaId() {
                return areaId;
        }

        public void setAreaId(Integer areaId) {
                this.areaId = areaId;
        }

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "website")
        public Set<Section> getSections() {
                return this.sections;
        }

        public void setSections(Set<Section> sections) {
                this.sections = sections;
        }

//        @OneToMany(fetch = FetchType.LAZY, mappedBy = "website")
//        public Set<Auth> getAuths() {
//                return auths;
//        }
//
//        public void setAuths(Set<Auth> auths) {
//                this.auths = auths;
//        }

}
