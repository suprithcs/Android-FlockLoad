package com.flockload.flockload;

import java.io.Serializable;

public class DownloadParams implements Serializable{
	private Integer startByteRange;
	private Integer endByteRange;
	private String flockURL;
	private Integer part;
	private String actualFilename;
	private String flockedFilesFolder;
	private String groupOwnerAddress;
	private String groupOwnerPort;
	private String fileExtension;
	private String contentSize;
	
	
	DownloadParams(Integer startByteRange, Integer endByteRange,String flockURL, Integer part, String actualFilename, String flockedFilesFolder, String groupOwnerAddress, String groupOwnerPort, String fileExtension, String contentSize) {
        this.startByteRange = startByteRange;
        this.endByteRange = endByteRange;
        this.flockURL = flockURL;
        this.part = part;
        this.actualFilename = actualFilename;
        this.flockedFilesFolder = flockedFilesFolder;
        this.groupOwnerAddress = groupOwnerAddress;
        this.groupOwnerPort = groupOwnerPort;
        this.fileExtension = fileExtension;
        this.setContentSize(contentSize);
    }
	
	public Integer getStartByteRange() {
		return startByteRange;
	}

	public void setStartByteRange(Integer startByteRange) {
		this.startByteRange = startByteRange;
	}

	public Integer getEndByteRange() {
		return endByteRange;
	}

	public void setEndByteRange(Integer endByteRange) {
		this.endByteRange = endByteRange;
	}

	public String getFlockURL() {
		return flockURL;
	}

	public void setFlockURL(String flockURL) {
		this.flockURL = flockURL;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public String getActualFilename() {
		return actualFilename;
	}

	public void setActualFilename(String actualFilename) {
		this.actualFilename = actualFilename;
	}

	public String getFlockedFilesFolder() {
		return flockedFilesFolder;
	}

	public void setFlockedFilesFolder(String flockedFilesFolder) {
		this.flockedFilesFolder = flockedFilesFolder;
	}

	public String getGroupOwnerAddress() {
		return groupOwnerAddress;
	}

	public void setGroupOwnerAddress(String groupOwnerAddress) {
		this.groupOwnerAddress = groupOwnerAddress;
	}

	public String getGroupOwnerPort() {
		return groupOwnerPort;
	}

	public void setGroupOwnerPort(String groupOwnerPort) {
		this.groupOwnerPort = groupOwnerPort;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getContentSize() {
		return contentSize;
	}

	public void setContentSize(String contentSize) {
		this.contentSize = contentSize;
	}

	
}
