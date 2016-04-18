package ro.androidiasi.codecamp.sessions;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import ro.androidiasi.codecamp.BaseFragment;
import ro.androidiasi.codecamp.R;
import ro.androidiasi.codecamp.internal.model.Session;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by andrei on 08/04/16.
 */
@EFragment(R.layout.fragment_sessions_list)
public class SessionsFragment extends BaseFragment implements SessionsContract.View {

    @Bean SessionsPresenter mSessionsPresenter;
    @FragmentArg Boolean mShowOnlyFavorites;
    @ViewById(R.id.list_view) StickyListHeadersListView mListView;

    public static SessionsFragment newInstance(){
        return newInstance(false);
    }

    public static SessionsFragment newInstance(boolean pShowOnlyFavorites){
        return SessionsFragment_.builder()
                .mShowOnlyFavorites(pShowOnlyFavorites)
                .build();
    }

    @Override public void afterViews() {
        super.afterViews();
        this.mSessionsPresenter.setView(this);
        this.mSessionsPresenter.afterViews();
    }

    @Override public StickyListHeadersListView getListView() {
        return this.mListView;
    }

    @ItemClick(R.id.list_view) public void onSessionItemClicked(Session pSession){
        this.getNavigator().goToSessionDetails(pSession);
    }
}
