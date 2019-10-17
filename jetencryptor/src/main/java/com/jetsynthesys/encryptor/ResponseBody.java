package com.jetsynthesys.encryptor;



public class ResponseBody {


    /**
     * code : 200
     * status : Success
     * message : Success
     * data : {"jwt":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1NTg2ODk0ODUsImp0aSI6IkVlYWp6ZWl6NkdRMjJRYk1DZjJjSnNWYTlHZUJ1Qmh4cDNMYTFRSmxWb1E9IiwiaXNzIjoidml0dW5lLnB1YmxpY2FtLmluIiwibmJmIjoxNTU4Njg5NDg1LCJleHAiOjE1NTg3NzU4ODUsImRhdGEiOnsiZGV2aWNlSWQiOiI2ZTE1NzU3ZWE3MTNmYmNkIiwicGFja2FnZU5hbWUiOiJpbi5wdWJsaWNhbS52aXR1bmUifX0.0rbk3yesloryWg-hQ1OgQlAhjBVK33Fc3vs0K3G5SXsHryEe7NuVbae2rOctrsiNA93eS_GXS7QFwiOk5o2_wQ","key":"-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3LneTCQW9xYIfvOqpMoF\nj21vNrkkPCf8Nfuutpe0mcC1wdE4VC20VWCY8T1PjqTTM8rPBYrkHmETqL829A9F\n6tsCVr3hejxcsukkOnV2zcCVvXZjUJQHUWqu1hcpaQSoEizPGejoy6isaV4dme2F\nWCZISiZUQ+/71FpPJHDSB9+bGqqnofbf4YgAdoEIJCv4R8uyyj/Vd837a3SQZzjz\nUcN7m9HgOBZPVT/NaEPRDOVGRHQPjm8Bb0wyZFbM+FqD3aTJG4QOXkTJL5aM2uFg\n3/NpjSPIxDv01W8EeV886HCZ3KdUoO1ttXF0RmZ4XzIjuxdeNYpK6zV8abZxDCXg\npQIDAQAB\n-----END PUBLIC KEY-----\n","cert":"sha256/qst6Yvf9dkLZK2tsLP/QW3SbdhrXPUHHLS8wMli7vzk="}
     */

    private int code;
    private String status;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * jwt : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1NTg2ODk0ODUsImp0aSI6IkVlYWp6ZWl6NkdRMjJRYk1DZjJjSnNWYTlHZUJ1Qmh4cDNMYTFRSmxWb1E9IiwiaXNzIjoidml0dW5lLnB1YmxpY2FtLmluIiwibmJmIjoxNTU4Njg5NDg1LCJleHAiOjE1NTg3NzU4ODUsImRhdGEiOnsiZGV2aWNlSWQiOiI2ZTE1NzU3ZWE3MTNmYmNkIiwicGFja2FnZU5hbWUiOiJpbi5wdWJsaWNhbS52aXR1bmUifX0.0rbk3yesloryWg-hQ1OgQlAhjBVK33Fc3vs0K3G5SXsHryEe7NuVbae2rOctrsiNA93eS_GXS7QFwiOk5o2_wQ
         * key : -----BEGIN PUBLIC KEY-----
         MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3LneTCQW9xYIfvOqpMoF
         j21vNrkkPCf8Nfuutpe0mcC1wdE4VC20VWCY8T1PjqTTM8rPBYrkHmETqL829A9F
         6tsCVr3hejxcsukkOnV2zcCVvXZjUJQHUWqu1hcpaQSoEizPGejoy6isaV4dme2F
         WCZISiZUQ+/71FpPJHDSB9+bGqqnofbf4YgAdoEIJCv4R8uyyj/Vd837a3SQZzjz
         UcN7m9HgOBZPVT/NaEPRDOVGRHQPjm8Bb0wyZFbM+FqD3aTJG4QOXkTJL5aM2uFg
         3/NpjSPIxDv01W8EeV886HCZ3KdUoO1ttXF0RmZ4XzIjuxdeNYpK6zV8abZxDCXg
         pQIDAQAB
         -----END PUBLIC KEY-----

         * cert : sha256/qst6Yvf9dkLZK2tsLP/QW3SbdhrXPUHHLS8wMli7vzk=
         */

        private String jwt;
        private String key;
        private String cert;

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getCert() {
            return cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }
    }
}
