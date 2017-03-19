package com.ruenzuo.sabisukatto.sabisukattokit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ruenzuo on 19/03/2017.
 */

public interface MediaDownloadService {

    @GET
    Call<ResponseBody> downloadMedia(@Url String fileUrl);

}
