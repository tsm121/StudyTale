package programvareutvikling.studytale_skeleton;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import me.itangqi.waveloadingview.WaveLoadingView;

public class GetTimetable extends AppCompatActivity {

    //Data for calculating time restrictions
    private Date datemaker = new Date();
    private int eventMonth;
    private int eventHour;
    private int eventMin;
    private int eventDay;
    private int eventYear = 1900 + datemaker.getYear();


    private String lectureUsername; //Usernname for 1024
    private String filename; // Filename on ISC-calendar file which gets downloaded

    private ICalendar ical; // iCal object

    // Mappen hvor timeplanene blir lagret
    private final String LECTURE_FILE_PATH = "/lecture";

    // Liste over alle eventene
    private List<VEvent> events;
    private ArrayList<String> lectureMonday = new ArrayList<>();
    private ArrayList<String> lectureTuesday = new ArrayList<>();
    private ArrayList<String> lectureWednesday = new ArrayList<>();
    private ArrayList<String> lectureThursday = new ArrayList<>();
    private ArrayList<String> lectureFriday = new ArrayList<>();

    private int current_week_number = -1;

    //Date to check week number
    //Format date
    private SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_timetable);
        setLectureUsername();

        WaveLoadingView loadingView = (WaveLoadingView) findViewById(R.id.loader);
        loadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        loadingView.setCenterTitle("Getting your timetable");
        loadingView.setBottomTitle("Username: " + getLectureUsername());
        loadingView.setAnimDuration(3000);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                final String FORMAT = "MM/dd/yyyy";
                 df = new SimpleDateFormat(FORMAT);

                generateFileName(); //Generate unique filename
                downloadLecturePlan(); // Download lecture plan from 1024
                createIcalenderObject(); // Make iCal objecto from downloaded .ics file
                createListOfEvents(); // Generate a list of all iCal events
                createWeekLists(); // Generate timeplan for current week
                removeDuplicates();

                Intent intent = getIntent();
                intent.setClass(GetTimetable.this, SelectLecturesActivity.class);

                intent.putExtra("monday", lectureMonday);
                intent.putExtra("tuesday", lectureTuesday);
                intent.putExtra("wednesday", lectureWednesday);
                intent.putExtra("thursday", lectureThursday);
                intent.putExtra("friday", lectureFriday);

                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

            }
        }, 2000); //Delay showing "GetStory" activity //use 2000
    }

    //Removing duplicates from lectures
    private void removeDuplicates() {

        this.lectureMonday = new ArrayList<>(new LinkedHashSet<>(lectureMonday));

        this.lectureTuesday = new ArrayList<>(new LinkedHashSet<>(lectureTuesday));

        this.lectureWednesday = new ArrayList<>(new LinkedHashSet<>(lectureWednesday));

        this.lectureThursday = new ArrayList<>(new LinkedHashSet<>(lectureThursday));

        this.lectureFriday = new ArrayList<>(new LinkedHashSet<>(lectureFriday));


    }

    // Laster ned timeplanen
    public void downloadLecturePlan(){
        // Laster ned filen fra urlen - isc fil
        fileDownload("https://ntnu.1024.no/2017/var/" + getLectureUsername() + "/ical/forelesninger/");
    }

    //Download file from url
    public void fileDownload(String URL) {

        //Check if directory excists
        File direct = new File(Environment.getExternalStorageDirectory()
                + LECTURE_FILE_PATH);

        if (!direct.exists()) {
            direct.mkdirs();
        }

        //Create a DownloadManager for downloading file
        DownloadManager mgr = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);

        //Parse URL to DownloadManager
        Uri downloadUri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        //Set download settings
        request.setAllowedOverRoaming(false)
                .setVisibleInDownloadsUi(false)
                .setNotificationVisibility(2)
                .setDestinationInExternalFilesDir(this, LECTURE_FILE_PATH, getFilename());

        //Download
        mgr.enqueue(request);

        //Check download status
        while (true){
            //Get download status
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = mgr.query(query);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);

            //Do actions depending on status
            if (DownloadManager.STATUS_SUCCESSFUL == status){
                //CONTINUE
                break;
            }
            else if (DownloadManager.STATUS_FAILED == status){
                //TRY AGAIN
            }
            else if (DownloadManager.STATUS_PAUSED == status){
                //Get user to turn on wifi/cellular
            }

        }

    }

    // Lager et biweekly calender object
    private void createIcalenderObject(){

        try {
            File f = new File(this.getExternalFilesDir(LECTURE_FILE_PATH + "/" + getFilename()).toString());

            // Lager en inputstream av filen
            InputStream in_s = new FileInputStream(f);

            ical = (ICalendar) Biweekly.parse(in_s).first(); // parser inputstremen over i et ical object.
            in_s.close(); // lukker inputstreamen
        } catch (IOException e) {e.printStackTrace();}
    }

    //Lager en liste over alle eventene i ical objectet og sorterer dem etter dato.
    private void createListOfEvents(){
        events = new ArrayList<VEvent>(ical.getEvents());
        Collections.sort(events, new Comparator<VEvent>() {
            @Override
            public int compare(VEvent event1, VEvent event2) {
                return event1.getDateStart().getValue().compareTo(event2.getDateStart().getValue());
            }
        });
    }

    //Checking evens and putting them in correct list
    public void createWeekLists(){
        for(int i = 0; i < events.size(); i++){
            setTimeTableFromEvent(i);
            if(checkWeekNumber(getEventMonth() + "/" + getEventDay() + "/" + getEventYear())){
                String format = String.valueOf(getEventHour()) + ":" + String.valueOf(getEventMin()) + " " + String.valueOf(getEventDay()) + "-" + String.valueOf(getEventMonth()) + "-"  + String.valueOf(getEventYear());

                //("HH:mm dd-MM-yyyy"
                if(isDay(i, "Mon")){
                    lectureMonday.add(format);
                }
                else if (isDay(i,"Tue")){
                    lectureTuesday.add(format);
                }
                else if (isDay(i,"Wed")){
                    lectureWednesday.add(format);
                }
                else if (isDay(i,"Thu")){
                    lectureThursday.add(format);
                }
                else if (isDay(i,"Fri")){
                    lectureFriday.add(format);
                }
            }
        }

        //If there is no lectures on a given day, add "No lectures"
        if(lectureMonday.size() == 0) lectureMonday.add("No lectures");
        if(lectureTuesday.size() == 0) lectureTuesday.add("No lectures");
        if(lectureWednesday.size() == 0) lectureWednesday.add("No lectures");
        if(lectureThursday.size() == 0) lectureThursday.add("No lectures");
        if(lectureFriday.size() == 0) lectureFriday.add("No lectures");
    }

    //Check if todays weekdumber matches given, return true if there is a match
    private boolean checkWeekNumber(String check_date) {

        //Date to check week number
        //Format date
        try {
            //Get week number from date
            Date date = df.parse(check_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            //Get current week number if it has not been set
            if (current_week_number == -1) {
                Calendar calender = Calendar.getInstance();


                this.current_week_number = calender.get(Calendar.WEEK_OF_YEAR);

            }

            //TODO: For demo use
            current_week_number = 14;

            //Return true if current week equals date week number
            if (cal.get(Calendar.WEEK_OF_YEAR) == this.current_week_number) return true;
            else return false;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("COULD NOT COMPARE WEEK NUMBER");
            return false;
        }
    }

    //Henter ut informasjon om eventet i kalenderen
    public void setTimeTableFromEvent(int i){
        String eventDate = convertStringMonthToIntMonth(i);

        setEventMonth(Integer.valueOf(eventDate.substring(0,2)));
        setEventDay(Integer.valueOf(eventDate.substring(3,5)));
        setEventHour(Integer.valueOf(eventDate.substring(6,8)));
        setEventMin(Integer.valueOf(eventDate.substring(9,11)));
    }

    private boolean isDay(int i, String day){
        return events.get(i).getDateStart().getValue().toString().substring(0,3).equals(day);
    }

    //Endrer formatet til stringen fra kalender filen, den gir ut dato som String og ikke Int
    public String convertStringMonthToIntMonth(int i){
        return events.get(i).getDateStart().getValue().toString().substring(4, 19)
                .replace("Jan", "01").replace("Feb", "02").replace("Mar", "03")
                .replace("Apr", "04").replace("Mai", "05").replace("Jun", "06")
                .replace("Jul", "07").replace("Aug", "08").replace("Sep", "09")
                .replace("Oct", "10").replace("Nov", "11").replace("Des", "12");
    }

    //--------Getters and setters--------//

    public int getEventHour() {
        return eventHour;
    }

    public void setEventHour(int eventHour) {
        this.eventHour = eventHour;
    }

    public int getEventMin() {
        return eventMin;
    }

    public void setEventMin(int eventMin) {
        this.eventMin = eventMin;
    }

    public void setEventMonth(int eventMonth){
        this.eventMonth = eventMonth;
    }

    public int getEventMonth(){
        return this.eventMonth;
    }

    public void setEventDay(int eventDay){
        this.eventDay = eventDay;
    }

    public int getEventDay(){
        return this.eventDay;
    }

    public String getFilename() {
        return filename;
    }

    public void generateFileName() {
        this.filename =  "lecture_" + getLectureUsername() + ".ics";
    }

    public String getLectureUsername() {
        return lectureUsername;
    }

    private void setLectureUsername(){
        this.lectureUsername = getIntent().getStringExtra("1024username");
    }

    public int getEventYear() {
        return eventYear;
    }
}
