package programvareutvikling.studytale_skeleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


public class StoryAdapter extends ArrayAdapter<String>{

    private Integer[] imageID;
    private ArrayList<String> timeLeft;
    private ArrayList<String> filenames;
    private SharedPreferences story_prefs;
    private SharedPreferences.Editor editor;

    //Constructor, save incomming data
    public StoryAdapter(@NonNull Context context, String[] stories, Integer[] imageID, ArrayList<String> timeLeft, ArrayList<String> filenames) {
        super(context, R.layout.story_list_layout, stories);
        this.imageID = imageID;
        this.timeLeft = timeLeft;
        this.filenames = filenames;
    }

    @NonNull
    @Override
    //Generate the inflatable view with given information
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Set holder to keep View ID's when reusing views when scrolling
        ViewHolder holder;

        //Set inflator
        LayoutInflater inflater = LayoutInflater.from(getContext());

        //Inflate view only if adding new view, othervise just update views
        //Add views to holder
        if(convertView == null) {

            convertView = inflater.inflate(R.layout.story_list_layout, parent, false);

            holder = new ViewHolder();

            //Set references to holder
            holder.story_name = (TextView) convertView.findViewById(R.id.story_name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.image = (ImageView) convertView.findViewById(R.id.story_image);

            convertView.setTag(holder);
            holder.time.setId(position + 69);

        } else {
            //Reuse views from holder
            holder = (ViewHolder) convertView.getTag();
        }

        //Set story title
        holder.story_name.setText(getItem(position));

        //Set image to done if story is finished
        if (this.timeLeft.get(position).matches("Finished")){
            holder.image.setImageResource(R.drawable.done_icon);
        }

        //Set category image
        else {
            holder.image.setImageResource(this.imageID[position]);
        }

        //Set time
        holder.time.setText(getTimeRemaining(getNextLecture(this.filenames.get(position))));

        return convertView;

    }

    //Get user lecture
    private String getNextLecture(String filename){

        story_prefs = getContext().getSharedPreferences(filename, MODE_PRIVATE);
        return story_prefs.getString("next_lecture", null);
    }

    //Set unlock value
    private void setUnlock(){
        editor = story_prefs.edit();
        editor.putBoolean("can_unlock",true);
        editor.commit();

    }

    //Get time remaining for given story
    private String getTimeRemaining(String next_lecture){

        String remaining_time = null;

        //for testing
        //Todo: For demo use
        next_lecture = "08:15 26-04-2017";

        //format: HH:mm dd-MM-yyyy
        SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");


        try {
            //Get todays date
            Date today = new Date();
            //Set format
            Date chapter_available = myFormat.parse(next_lecture);
            //Difference between two dates
            long diff = chapter_available.getTime() - today.getTime();
            //Calculate days, hours and minutes
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
            long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;


            //Printing format
            if(days < 0 || hours < 0 || minutes < 0){
                setUnlock();
                return "NEW CHAPTER!";
            }
            else if (days > 31){
                return "Over a month!";
            }
            //Remaining < 5 minutes
            else if (days == 0 && hours == 0 && minutes < 5){
                return "Less than\nfive minutes!";
            }
            //Minutes
            else if (days == 0 && hours == 0) {
                return minutes + " minutes";
            }

            //Hours and minutes
            else if (days == 0) {
                if(hours == 1 && minutes == 1) return hours + " hour,\n" + minutes + " minute";
                else if(hours == 1) return hours + " hour,\n" + minutes + " minutes";
                else if (minutes == 1) return hours + " hours,\n" + minutes + "minute";
                return hours + " hours,\n" + minutes + " minutes";
            }
            //Days and hours
            else {
                if (days == 1 && hours == 1) return days + " day,\n" + hours + " hour";
                else if (days == 1) return days + " day,\n" + hours % 24 + " hours";
                else if (hours == 1) return days + " days,\n" + hours  + " hour";
                else if (days > 0) return days + " days,\n" + hours % 24 + " hours" ;
                remaining_time = days + " days,\n" + hours % 24 + " hours";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remaining_time;
    }

    //Holder class to keep referenses
    private static class ViewHolder {
        public TextView story_name;
        public TextView time;
        public ImageView image;
    }
}