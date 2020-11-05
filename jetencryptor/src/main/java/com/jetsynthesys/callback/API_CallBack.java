/*
 * Copyright (c) 2018, 5. JetSynthesys Pvt. Ltd.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 *
 */

package com.jetsynthesys.callback;


import com.jetsynthesys.encryptor.RequestModel;
import com.jetsynthesys.encryptor.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface API_CallBack {

    @POST("auth/token")
    Call<ResponseBody> getEncryptorData(@Header("Content-Type") String content_type,
                                        @Header("APP-SIGNATURE") String app_signature,
                                        @Header("DEVICE-ID") String device_id,
                                        @Header("PACKAGE-NAME") String package_name,
                                        @Body RequestModel requestModel);

    @POST("auth/token")
    Call<String> getEncryptorDataWakau(@Header("Content-Type") String content_type,
                                        @Header("PARAM_3") String app_signature,
                                        @Header("PARAM_1") String device_id,
                                        @Header("PARAM_2") String package_name,
                                        @Body RequestModel requestModel);

    @POST("api/v1/Auth/get")
    Call<ResponseBody> getEncryptorDataJetEngage(@Header("Content-Type") String content_type,
                                                 @Header("APP-SIGNATURE") String app_signature,
                                                 @Header("DEVICE-ID") String device_id,
                                                 @Header("PACKAGE-NAME") String package_name,
                                                 @Body RequestModel requestModel);

}
