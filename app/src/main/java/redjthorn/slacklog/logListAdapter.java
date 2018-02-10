package redjthorn.slacklog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jaricthorning on 10/2/18.
 */
public class logListAdapter extends BaseAdapter {

    Context context;
    String[] data;
    String[] usernames;

    private static LayoutInflater inflater = null;

    public logListAdapter(Context context, String[] usernames, String[] dates) {
        // TODO Auto-generated constructor stub
        this.usernames = usernames;
        this.context = context;
        this.data = dates;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.log_list_item, null);
        TextView dateText = (TextView) vi.findViewById(R.id.logListDate);
        TextView username = (TextView) vi.findViewById(R.id.logListName);


        username.setText(usernames[position]);


        // convert seconds to milliseconds
        Date date = new Date(Integer.valueOf(data[position])*1000L);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0"));
        String formattedDate = sdf.format(date);


        dateText.setText(formattedDate);

        return vi;
    }
}
