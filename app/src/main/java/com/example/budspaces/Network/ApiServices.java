package com.example.budspaces.Network;

import com.example.budspaces.Models.Count;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.Message;
import com.example.budspaces.Models.Room;
import com.example.budspaces.Models.SearchResult;
import com.example.budspaces.Models.SearchSettings;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.Setting;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.Models.User;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {
    /* Start Authentification Requests */
    @POST("auth/register")
    Call<ServerResponse> registerUser(@Body RequestBody body);

    @POST("auth/google/register")
    Call<ServerResponse> registerWithGoogle(@Query("token") String token, @Body RequestBody body);

    @POST("auth/facebook/register")
    Call<ServerResponse> registerWithFacebook(@Query("access_token") String token, @Body RequestBody body);

    @POST("auth/login")
    Call<ServerResponse> login(@Body RequestBody body);

    @POST("auth/logout")
    Call<ServerResponse> logout(@Header("auth-token") String token);

    @POST("auth/google")
    Call<ServerResponse> loginWithGoogle(@Query("token") String token);

    @POST("auth/facebook")
    Call<ServerResponse> loginWithFacebook(@Query("access_token") String token);

    @GET("auth/logged")
    Call<ServerResponse> isLoggedIn(@Header("auth-token") String token);

    @POST("auth/recover")
    Call<ServerResponse> recoverPassword(@Query("email") String email);

    @POST("auth/verify")
    Call<ServerResponse> verifyAccount(@Header("auth-token") String token);

    @POST("auth/checkCode")
    Call<ServerResponse> checkCode(@Header("auth-token") String token, @Query("code") String code);
    /* End Authentification Requests */


    /* Start User Requests */
    @POST("user/update/interests")
    Call<ServerResponse> updateInterests(@Header("auth-token") String token, @Body RequestBody body);

    @GET("user/{userId}")
    Call<User> getUserProfile(@Header("auth-token") String token, @Path("userId") String userId);

    @POST("user/update/details")
    Call<ServerResponse> updateProfile(@Header("auth-token") String token, @Body RequestBody body);

    @POST("user/update/picture")
    Call<ServerResponse> updateProfilePicture(@Header("auth-token") String token, @Body RequestBody body);

    @POST("user/update/password")
    Call<ServerResponse> updateProfilePassword(@Header("auth-token") String token, @Query("oldPass") String oldPass, @Query("newPass") String newPAss);

    @GET("user/chat/{userId}")
    Call<Member> getChatUser(@Path("userId") String userId);
    /* End User Requests */


    /* Start Themes Requests */
    @GET("theme/count")
    Call<Count> getThemesCount();

    @GET("theme")
    Call<List<Theme>> getThemes(@Query("page") int page, @Query("limit") int limit);
    /* End Themes Requests */


    /* Start Group Requests */
    @POST("group/add")
    Call<ServerResponse> createGroup(@Header("auth-token") String token, @Body RequestBody body);

    @POST("group/update/interests")
    Call<ServerResponse> updateGroupInterests(@Header("auth-token") String token, @Body RequestBody body);

    @POST("group/update/picture")
    Call<ServerResponse> updateGroupPicture(@Header("auth-token") String token, @Body RequestBody body);

    @POST("group/update/details")
    Call<ServerResponse> updateGroupDetails(@Header("auth-token") String token, @Body RequestBody body);
    @GET("group/host")
    Call<List<SimplifiedGroup>> getHostGroups(@Header("auth-token") String token, @Query("id") String userID);

    @GET("group/member")
    Call<List<SimplifiedGroup>> getMemberGroups(@Header("auth-token") String token, @Query("id") String userID);

    @GET("group/member")
    Call<List<SimplifiedGroup>> getMemberGroups(@Header("auth-token") String token);

    @GET("group/{groupId}")
    Call<Group> getGroup(@Header("auth-token") String token, @Path("groupId") String groupId);

    @PUT("group/leave/{groupId}")
    Call<ServerResponse> leaveGroup(@Header("auth-token") String token, @Path("groupId") String groupId);

    @DELETE("group/{groupId}")
    Call<ServerResponse> deleteGroup(@Header("auth-token") String token, @Path("groupId") String groupId);

//    @POST("group/members/list/{groupId}")
//    Call<List<Member>> getGroupFrontMembers(@Header("auth-token") String token, @Path("groupId") String groupId, @Body RequestBody body);
    @GET("group/{groupId}/members")
    Call<List<Member>> getGroupMembers(@Path("groupId") String groupId, @Query("page") int page, @Query("limit") int limit, @Query("shuffle") boolean shuffle);

    @PUT("group/join/{groupId}")
    Call<ServerResponse> joinGroup(@Header("auth-token") String token, @Path("groupId") String groupId);
    /* End Group Requests */

    /* Start Event Requests */
    @POST("event/add")
    Call<ServerResponse> createEvent(@Header("auth-token") String token, @Body RequestBody body);

    @POST("event/group/{groupId}")
    Call<List<Event>> getGroupEvents(@Header("auth-token") String token, @Path("groupId") String groupId);

    @GET("event/{eventID}")
    Call<Event> getEvent(@Header("auth-token") String token, @Path("eventID") String eventId);

    @POST("event/update/{eventID}")
    Call<ServerResponse> updateEvent(@Header("auth-token") String token, @Path("eventID") String eventId,@Body RequestBody body);

    @GET("event/{eventID}/members")
    Call<List<Member>> getEventMembers(@Path("eventID") String eventId, @Query("page") int page, @Query("limit") int limit, @Query("shuffle") boolean shuffle);

    @PUT("event/join/{eventID}")
    Call<ServerResponse> joinEvent(@Header("auth-token") String token, @Path("eventID") String eventID);

    @PUT("event/leave/{eventID}")
    Call<ServerResponse> leaveEvent(@Header("auth-token") String token, @Path("eventID") String eventID);

    @DELETE("event/{eventID}")
    Call<ServerResponse> deleteEvent(@Header("auth-token") String token, @Path("eventID") String eventID);

    @GET("event/month/group/{groupId}")
    Call<List<Event>> getGroupEventsByMonth(@Path("groupId") String groupId, @Query("page") int page, @Query("limit") int limit);

    @GET("event/past/group/{groupId}")
    Call<List<Event>> getGroupPastEvents(@Path("groupId") String groupId, @Query("page") int page, @Query("limit") int limit);

    @GET("event/name/group/{groupId}")
    Call<List<Event>> getGroupEventsByName(@Path("groupId") String groupId, @Query("page") int page, @Query("limit") int limit, @Query("name") String name);

    @GET("event/month/events")
    Call<List<Event>> getEventsByMonth(@Header("auth-token") String token, @Query("page") int page, @Query("limit") int limit);

    @GET("event/past/events")
    Call<List<Event>> getPastEvents(@Header("auth-token") String token, @Query("page") int page, @Query("limit") int limit);

    @GET("event/name/{name}")
    Call<List<Event>> getEventsByName(@Header("auth-token") String token, @Path("name") String name, @Query("page") int page, @Query("limit") int limit);
    /* End Event Requests */


    /* Start Timeline Requests */
    @GET("timeline")
    Call<List<HomeModel>> getHomeTimeline(@Header("auth-token") String token, @Query("page") Integer page, @Query("limit") Integer limit);

    @GET("timeline/day")
    Call<List<Event>> getSelectedDayTimeline(@Header("auth-token") String token, @Query("date") String date);

    @GET("timeline/group/{groupId}")
    Call<List<HomeModel>> getGroupTimeline(@Header("auth-token") String token, @Path("groupId") String groupId, @Query("page") Integer page, @Query("limit") Integer limit);

    @GET("timeline/countByDay")
    Call<List<Integer>> getEventsCountByDay(@Header("auth-token") String token, @Query("firstDay") String firstDay, @Query("daysNb") Integer daysNb);

    @POST("timeline/add/announcement")
    Call<ServerResponse> addAnnouncement(@Header("auth-token") String token, @Body RequestBody body);

    @GET("timeline/last/announcement/{groupId}")
    Call<HomeModel> getAnnouncement(@Header("auth-token") String token, @Path("groupId") String groupId);
    /* End Timeline Requests */


    /* Start Search Requests */
    @POST("/search")
    Call<SearchResult>Search(@Header("auth-token") String token, @Body RequestBody body);

    @POST("/search/filter")
    Call<ServerResponse>AddSearchFilter(@Header("auth-token") String token, @Body RequestBody body);

    @GET("/search/filter")
    Call<SearchSettings>GetSearchFilter(@Header("auth-token") String token);
    /* End Search Requests */

    /* Start Chat Requests */
    @GET("room")
    Call<List<Room>> getChatRooms(@Header("auth-token") String token, @Query("page") Integer page, @Query("limit") Integer limit);

    @GET("room/{roomId}")
    Call<List<Message>> getChatMessages(@Header("auth-token") String token, @Path("roomId") String roomId, @Query("page") Integer page, @Query("limit") Integer limit);
    /* End Chat Requests */

    /* Start Settings Requests */
    @POST("/settings")
    Call<ServerResponse> setPreferences(@Header("auth-token") String token, @Body RequestBody body);

    @GET("/settings")
    Call<Setting> getPreferences(@Header("auth-token") String token);
    /* End Settings Requests */

    @GET("/notifications/topics")
    Call<List<String>> getTopics(@Header("auth-token") String token);
}