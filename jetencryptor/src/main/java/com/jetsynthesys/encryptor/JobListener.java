package com.jetsynthesys.encryptor;
/*
 * Copyright (c) 2018 - 08 - 10. JetSynthesys Pvt. Ltd.
 * @Rahul Pawar
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
public interface JobListener {

    void workStarted(int workid);
    void workFinished(int workid);
    void workResult(String Result);
    void onworkError(String error);
}
