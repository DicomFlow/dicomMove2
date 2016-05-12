package br.ufpb.dicomflow.bean;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
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

@Entity
@Table(name="series")
public class Series extends AbstractPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6640652465765376582L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="study_fk")
	private Study study;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="series_fk")
	private Set<Instance> instances;
	
	@Column(name="series_iuid")
	private String seriesIuid;
	
	@Column(name="series_no")
	private String seriesNumber;
	
	@Column(name="modality")
	private String modality;
	
	@Column(name="body_part")
	private String bodyPartExamined;
	
	@Column(name="pps_start")
	private Date ppsStartDate;
	
	@Column(name="pps_iuid")
	private String ppsIuid;
	
	@Column(name="num_instances")
	private Integer numberOfSeriesRelatedInstances;
	
	@Column(name="src_aet")
	private String sourceAET;
	
	@Column(name="retrieve_aets")
	private String retrieveAETs;
	
	@Column(name="availability")
    private Integer availability;
	
	@Column(name="series_status")
    private Integer seriesStatus;
	
	@Column(name="updated_time")
    private Date updatedTime;
	
	@Column(name="created_time")
    private Date createdTime;
	
	@Column(name="series_attrs")
    private byte[] encodedAttributes;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public String getSeriesIuid() {
		return seriesIuid;
	}

	public void setSeriesIuid(String seriesIuid) {
		this.seriesIuid = seriesIuid;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getBodyPartExamined() {
		return bodyPartExamined;
	}

	public void setBodyPartExamined(String bodyPartExamined) {
		this.bodyPartExamined = bodyPartExamined;
	}

	public Date getPpsStartDate() {
		return ppsStartDate;
	}

	public void setPpsStartDate(Date ppsStartDate) {
		this.ppsStartDate = ppsStartDate;
	}

	public String getPpsIuid() {
		return ppsIuid;
	}

	public void setPpsIuid(String ppsIuid) {
		this.ppsIuid = ppsIuid;
	}

	public Integer getNumberOfSeriesRelatedInstances() {
		return numberOfSeriesRelatedInstances;
	}

	public void setNumberOfSeriesRelatedInstances(
			Integer numberOfSeriesRelatedInstances) {
		this.numberOfSeriesRelatedInstances = numberOfSeriesRelatedInstances;
	}

	public String getSourceAET() {
		return sourceAET;
	}

	public void setSourceAET(String sourceAET) {
		this.sourceAET = sourceAET;
	}

	public String getRetrieveAETs() {
		return retrieveAETs;
	}

	public void setRetrieveAETs(String retrieveAETs) {
		this.retrieveAETs = retrieveAETs;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getSeriesStatus() {
		return seriesStatus;
	}

	public void setSeriesStatus(Integer seriesStatus) {
		this.seriesStatus = seriesStatus;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public byte[] getEncodedAttributes() {
		return encodedAttributes;
	}

	public void setEncodedAttributes(byte[] encodedAttributes) {
		this.encodedAttributes = encodedAttributes;
	}

	public Set<Instance> getInstances() {
		return instances;
	}

	public void setInstances(Set<Instance> instances) {
		this.instances = instances;
	}
	
	
	
	

}
