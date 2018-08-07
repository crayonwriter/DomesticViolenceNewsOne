import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.domesticviolencenewsone.R;

import java.util.List;

public class DVArticlesAdapter extends ArrayAdapter<DVArticles>{
   //Constructor
    public DVArticlesAdapter(Context context, List<DVArticles> dvArticles) {
        super(context, 0, dvArticles);
    }

    //Get the item for the listview by overriding the getView method

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
    }
}
