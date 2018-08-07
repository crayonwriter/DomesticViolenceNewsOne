import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

//DVArticleLoader loads a list of domestic violence articles.
// AsyncTask is used to perform the network request to the URL

public class DVArticleLoader extends AsyncTaskLoader<List<DVArticles>> {

    /** Tag for log messages */
    private static final String LOG_TAG = DVArticleLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link DVArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public DVArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading called");
        forceLoad();
    }

    //This runs in the background thread
    @Override
    public List<DVArticles> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<DVArticles> dvArticlesList = QueryUtils.fetchDVArticlesData(mUrl);
        return  dvArticlesList;
    }
}
