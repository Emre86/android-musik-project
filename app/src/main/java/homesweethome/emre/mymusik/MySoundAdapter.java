package homesweethome.emre.mymusik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by emre on 03/10/16.
 */
public class MySoundAdapter extends ArrayAdapter<Sound> {

    public MySoundAdapter(Context context, ArrayList<Sound> sounds) {
        super(context, 0, sounds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Sound sound = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mysound, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvDuree = (TextView) convertView.findViewById(R.id.tvDuree);
        // Populate the data into the template view using the data object
        tvTitle.setText(sound.getTitre());
        tvDuree.setText(sound.getDuree());
        // Return the completed view to render on screen
        return convertView;
    }




}
