package ro.androidiasi.codecamp.data.source.remote;

import android.support.annotation.CallSuper;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.androidiasi.codecamp.data.crawler.CodecampNew;
import ro.androidiasi.codecamp.data.crawler.Conference;
import ro.androidiasi.codecamp.data.model.DataCodecamp;
import ro.androidiasi.codecamp.data.model.DataCodecamper;
import ro.androidiasi.codecamp.data.model.DataRoom;
import ro.androidiasi.codecamp.data.model.DataSession;
import ro.androidiasi.codecamp.data.model.DataSponsor;
import ro.androidiasi.codecamp.data.model.DataTimeFrame;
import ro.androidiasi.codecamp.data.source.DataConference;
import ro.androidiasi.codecamp.data.source.IAgendaDataSource;
import ro.androidiasi.codecamp.data.source.ILoadCallback;
import ro.androidiasi.codecamp.data.source.remote.exception.DataUnavailable;

/**
 * Created by andrei on 21/04/16.
 */
@EBean
public abstract class BaseRemoteDataSource implements IRemoteClient, IAgendaDataSource<Long> {

    private static final String TAG = "BaseRemoteDataSource";
    protected DataConference mConference;

    private ObjectMapper mObjectMapper;
    private ILoadCallback<List<DataRoom>> mDataRoomListCallback;
    private ILoadCallback<List<DataSession>> mDataSessionListCallback;
    private ILoadCallback<List<DataTimeFrame>> mDataTimeFrameListCallback;
    private ILoadCallback<List<DataCodecamper>> mDataCodecamperListCallback;
    private ILoadCallback<List<DataSponsor>> mDataSponsorsListCallback;
    private ILoadCallback<List<DataConference>> mDataConferencesListCallback;

    @AfterInject public void afterMembersInject() {
        this.mObjectMapper = new ObjectMapper();
        this.afterInject();
    }

    public void afterInject() {};

    @CallSuper
    @Override public void startCodecampJsonRequest() throws DataUnavailable {
        if (mConference == null) {
            throw new NullPointerException("DataConference is NULL! Please set DataConference first!");
        }
    }

    @Override public void getRoomsList(boolean pForced, final ILoadCallback<List<DataRoom>> pLoadCallback) {
        this.mDataRoomListCallback = pLoadCallback;
        this.requestData(pLoadCallback);
    }

    @Override public void getSessionsList(boolean pForced, final ILoadCallback<List<DataSession>> pLoadCallback) {
        this.mDataSessionListCallback = pLoadCallback;
        this.requestData(pLoadCallback);
    }

    @Override public void getFavoriteSessionsList(boolean pFroced, final ILoadCallback<List<DataSession>> pLoadCallback) {
        //not storing favorites on the web
    }

    @Override public void getTimeFramesList(boolean pForced, ILoadCallback<List<DataTimeFrame>> pLoadCallback) {
        this.mDataTimeFrameListCallback = pLoadCallback;
        this.requestData(pLoadCallback);
    }

    @Override public void getCodecampersList(boolean pForced, ILoadCallback<List<DataCodecamper>> pLoadCallback) {
        this.mDataCodecamperListCallback = pLoadCallback;
        this.requestData(pLoadCallback);
    }

    @Override
    public void getSponsorsList(boolean pForced, ILoadCallback<List<DataSponsor>> pLoadCallback) {
        this.mDataSponsorsListCallback = pLoadCallback;
        this.requestData(pLoadCallback);
    }

    @Override
    public void getConferencesList(boolean pForced, ILoadCallback<List<DataConference>> pLoadCallback) {
        this.mDataConferencesListCallback = pLoadCallback;
        try {
            this.startConferencesRequest(new ILoadCallback<List<DataConference>>() {
                @Override public void onSuccess(List<DataConference> pObject) {
                    mDataConferencesListCallback.onSuccess(pObject);
                }

                @Override public void onFailure(Exception pException) {
                    mDataConferencesListCallback.onFailure(pException);
                }
            });
        } catch (DataUnavailable pDataUnavailable) {
            Log.e(TAG, "getConferencesList: ", pDataUnavailable);
        }
    }

    @Override public void getRoomsList(ILoadCallback<List<DataRoom>> pLoadCallback) {
        this.getRoomsList(false, pLoadCallback);
    }

    @Override public void getSessionsList(ILoadCallback<List<DataSession>> pLoadCallback) {
        this.getSessionsList(false, pLoadCallback);
    }

    @Override public void getFavoriteSessionsList(ILoadCallback<List<DataSession>> pLoadCallback) {
        this.getFavoriteSessionsList(false, pLoadCallback);
    }

    @Override public void getTimeFramesList(ILoadCallback<List<DataTimeFrame>> pLoadCallback) {
        this.getTimeFramesList(false, pLoadCallback);
    }

    @Override public void getCodecampersList(ILoadCallback<List<DataCodecamper>> pLoadCallback) {
        this.getCodecampersList(false, pLoadCallback);
    }

    @Override public void getSponsorsList(ILoadCallback<List<DataSponsor>> pLoadCallback) {
       getSponsorsList(false,  pLoadCallback);
    }

    @Override public void getConferencesList(ILoadCallback<List<DataConference>> pLoadCallback) {
        this.getConferencesList(false, pLoadCallback);
    }

    @Override public void getRoom(Long pLong, ILoadCallback<DataRoom> pLoadCallback) {
    }


    @Override public void getSession(Long pLong, ILoadCallback<DataSession> pLoadCallback) {

    }

    @Override public void getTimeFrame(Long pLong, ILoadCallback<DataTimeFrame> pLoadCallback) {

    }

    @Override public void getCodecamper(Long pLong, ILoadCallback<DataCodecamper> pLoadCallback) {

    }

    @Override public void isSessionFavorite(Long pLong, ILoadCallback<Boolean> pLoadCallback) {

    }

    @Override public void setSessionFavorite(Long pLong, boolean pFavorite, ILoadCallback<Boolean> pLoadCallback) {

    }

    private DataCodecamp getDataCodecampFromJson(String pDataJson) throws IOException {
        CodecampNew codecamp = this.mObjectMapper.readValue(pDataJson, CodecampNew.class);
        return DataCodecamp.fromCrawlerCodecamp(codecamp);
    }

    public List<DataConference> getDataConferencesFromJson(String pDataJson) throws IOException {
        Conference[] conferences = this.mObjectMapper.readValue(pDataJson, Conference[].class);
        List<DataConference> result = new ArrayList<>();
        for (Conference conference : conferences) {
            result.add(DataConference.fromCrawlerConference(conference));
        }
        return result;
    }

    private<Model> void requestData(ILoadCallback<Model> pLoadCallback) {//this does not look good :)
        try {
            this.startCodecampJsonRequest();
        } catch (DataUnavailable pDataUnavailable) {
            Log.e(TAG, "requestData: ", pDataUnavailable);
        }
    }

    /**
     * Webview triggers this method with the returned json;
     * Due to the onData() webview call...I'm losing track of the
     * callback I need to do.
     * I came up with this solution, keep a reference to the callback,
     * each time a request is done and remove it afer the json is returned.
     *
     * To implement more requests we need to specifically identify the callbacks, so
     * we need to add more ILoadCallback references.
     *
     * Or we could use an API... :))
     * @param pObject The Json returned by the webview
     */
    @Override public void onSuccess(String pObject) {
        DataCodecamp dataCodecamp = null;
        try {
            dataCodecamp = getDataCodecampFromJson(pObject);
        } catch (IOException pE) {
            this.onFailure(pE);
        }
        if (dataCodecamp != null) {
            if (mDataSessionListCallback != null) {
                mDataSessionListCallback.onSuccess(dataCodecamp.getDataSessions());
                this.mDataSessionListCallback = null;
            }
            if (mDataRoomListCallback != null) {
                this.mDataRoomListCallback.onSuccess(dataCodecamp.getDataRooms());
                this.mDataRoomListCallback = null;
            }
            if (mDataTimeFrameListCallback != null) {
                this.mDataTimeFrameListCallback.onSuccess(dataCodecamp.getTimeFrames());
                this.mDataTimeFrameListCallback = null;
            }
            if (mDataCodecamperListCallback != null) {
                this.mDataCodecamperListCallback.onSuccess(dataCodecamp.getDataCodecampers());
                this.mDataCodecamperListCallback = null;
            }
            if (mDataSponsorsListCallback != null) {
                this.mDataSponsorsListCallback.onSuccess(dataCodecamp.getDataSponsors());
                this.mDataSponsorsListCallback = null;
            }
        }
    }

    @Override public void onFailure(Exception pException) {
        Log.e(TAG, "onFailure: ", pException);
        if (mDataSessionListCallback != null) {
            mDataSessionListCallback.onFailure(pException);
            this.mDataSessionListCallback = null;
        }
        if (mDataRoomListCallback != null) {
            this.mDataRoomListCallback.onFailure(pException);
            this.mDataRoomListCallback = null;
        }
        if (mDataTimeFrameListCallback != null) {
            this.mDataTimeFrameListCallback.onFailure(pException);
            this.mDataTimeFrameListCallback = null;
        }
        if (mDataCodecamperListCallback != null) {
            this.mDataCodecamperListCallback.onFailure(pException);
            this.mDataCodecamperListCallback = null;
        }
        if (mDataSponsorsListCallback != null) {
            this.mDataSponsorsListCallback.onFailure(pException);
            this.mDataSponsorsListCallback = null;
        }
    }

    @Override public void invalidate() {
        //nothing to invalidate on remote
    }

    @Override public DataConference getConference() {
        return mConference;
    }

    public void setConference(DataConference pConference) {
        this.mConference = pConference;
    }
}
