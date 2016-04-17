package ro.androidiasi.codecamp.data.model;

/**
 * Created by andrei on 06/04/16.
 */
public class DataSession extends AbstractDataModel {

    private String mName;
    private String mDescription;

    private DataCodecamper mDataCodecamper;
    private DataRoom mDataRoom;
    private DataTimeFrame mDataTimeFrame;

    private boolean mFavorite;

    public DataSession() {
    }

    public DataSession(DataCodecamper pDataCodecamper, DataRoom pDataRoom, String pName, String pDescription, DataTimeFrame pDataTimeFrame) {
        mDataCodecamper = pDataCodecamper;
        mDataRoom = pDataRoom;
        mName = pName;
        mDescription = pDescription;
        mDataTimeFrame = pDataTimeFrame;
    }

    public DataCodecamper getDataCodecamper() {
        return mDataCodecamper;
    }

    public DataRoom getDataRoom() {
        return mDataRoom;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public DataTimeFrame getDataTimeFrame() {
        return mDataTimeFrame;
    }


    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean pFavorite) {
        mFavorite = pFavorite;
    }
}