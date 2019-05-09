package ly.betime.shuriken.service;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistanceMatrixApi {

    @GET("distancematrix/json")
    Single<DistanceMatrixResponse> getDistance(@Query("origins") String origins,
                                               @Query("destinations") String destinations,
                                               @Query("key") String key);

}
