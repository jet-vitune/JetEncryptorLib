package com.jetsynthesys.jetencryptorjava;

import com.google.gson.annotations.SerializedName;

public class DataModel {
    @SerializedName("code")
    private final int code;

    @SerializedName("status")
    private final String status;

    @SerializedName("message")
    private final String message;

    @SerializedName("data")
    private final Data data;

    public DataModel(int code, String status, String message, Data data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("key")
        private final String key;

        @SerializedName("cert")
        private final String cert;

        public Data(String key, String cert) {
            this.key = key;
            this.cert = cert;
        }

        public String getKey() {
            return key;
        }

        public String getCert() {
            return cert;
        }
    }
}
