package com.winning.mars_consumer.monitor.bean;

public class UpdateInfo {

    private String code;
    private String message;
    private UpdateBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdateBean getData() {
        return data;
    }

    public void setData(UpdateBean data) {
        this.data = data;
    }

    public class UpdateBean{
        private String id;
        private String app_type;
        private String version_code;
        private String download_url;
        private String public_date;
        private Integer update_type;
        private String app_intro;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApp_type() {
            return app_type;
        }

        public void setApp_type(String app_type) {
            this.app_type = app_type;
        }

        public String getVersion_code() {
            return version_code;
        }

        public void setVersion_code(String version_code) {
            this.version_code = version_code;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public String getPublic_date() {
            return public_date;
        }

        public void setPublic_date(String public_date) {
            this.public_date = public_date;
        }

        public Integer getUpdate_type() {
            return update_type;
        }

        public void setUpdate_type(Integer update_type) {
            this.update_type = update_type;
        }

        public String getApp_intro() {
            return app_intro;
        }

        public void setApp_intro(String app_intro) {
            this.app_intro = app_intro;
        }
    }
}
