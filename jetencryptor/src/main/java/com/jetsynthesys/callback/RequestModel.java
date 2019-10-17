package com.jetsynthesys.callback;

public class RequestModel {


    /**
     * locale : {"country":"IN","platform":"android","version":"1.0","version_code":1,"language":"en","segment":"","device":"motorola","model":"payton"}
     */

    private LocaleBean locale;

    public LocaleBean getLocale() {
        return locale;
    }

    public void setLocale(LocaleBean locale) {
        this.locale = locale;
    }

    public static class LocaleBean {
        /**
         * country : IN
         * platform : android
         * version : 1.0
         * version_code : 1
         * language : en
         * segment :
         * device : motorola
         * model : payton
         */

        private String country;
        private String platform;
        private String version;
        private int version_code;
        private String language;
        private String segment;
        private String device;
        private String model;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getSegment() {
            return segment;
        }

        public void setSegment(String segment) {
            this.segment = segment;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
}
