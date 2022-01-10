package com.cherrydev.airsend.app.settings;

public class LicenseItem {
    private int id;
    private String title;
    private String websiteLink;
    private String licenseLink;
    private String licenseText;

    public LicenseItem(int id, String title, String websiteLink, String licenseLink, String licenseText) {
        this.id = id;
        this.title = title;
        this.websiteLink = websiteLink;
        this.licenseLink = licenseLink;
        this.licenseText = licenseText;
    }

    public LicenseItem(int id, String title, String websiteLink, String licenseLink) {
        this.id = id;
        this.title = title;
        this.websiteLink = websiteLink;
        this.licenseLink = licenseLink;
    }

    public LicenseItem(String title, String websiteLink, String licenseLink) {
        this.id = id;
        this.title = title;
        this.websiteLink = websiteLink;
        this.licenseLink = licenseLink;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public String getLicenseLink() {
        return licenseLink;
    }

    public String getLicenseText() {
        return licenseText;
    }
}
