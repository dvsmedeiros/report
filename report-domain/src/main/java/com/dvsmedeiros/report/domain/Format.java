package com.dvsmedeiros.report.domain;

public enum Format {

    PDF ( ".pdf" ) , HTML ( ".html" ), JASPER(".jasper");

    private Format ( String extension ) {
        this.extension = extension;
    }

    private String extension;

    public String getExtension () {
        return extension;
    }

    public void setExtension ( String extension ) {
        this.extension = extension;
    }

}
