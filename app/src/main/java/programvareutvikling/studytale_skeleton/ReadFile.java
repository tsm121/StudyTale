package programvareutvikling.studytale_skeleton;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


public class ReadFile extends Activity{


    private HashMap<String, String> file_settings;
    private HashMap<String, String> sql_queries;
    private ArrayList<String> chapterlist;
    private HashMap<String, ArrayList<String>> chapters;
    private String filename;
    private Context context;
    private String filepath;

    public ReadFile(String filename, String url_path, Context context) {
        this.filename = filename;
        this.context = context;
        this.filepath = url_path;

        //Download file
        file_download(filepath, context);
        //Read file
        if(readFile()) System.out.println("FILE READ");
        else System.err.println("COULD NOT READ FILE");

    }

    //Download file from url
    public void file_download(String URL, Context that) {

        System.out.println("FINDING STORY FROM URL: " + URL.toString());

        //Check if directory excists
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/user_stories");

        //If directory don't excist, create
        if (!direct.exists()) {
            direct.mkdirs();
        }

        //Check if file already exists, if true delete old version
        File file = new File(this.context.getExternalFilesDir("/user_stories/" + this.filename).toString());
        if(file.exists()){
            file.delete();
            System.out.println(this.filename + " already exist, deleting old version");
        }

        //Create a DownloadManager for downloading fil  e
        DownloadManager mgr = (DownloadManager) that.getSystemService(that.DOWNLOAD_SERVICE);

        //Parse URL to DownloadManager
        Uri downloadUri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        //Set download settings
        request.setAllowedOverRoaming(false)
                .setVisibleInDownloadsUi(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDestinationInExternalFilesDir(that,"/user_stories", this.filename);

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
                System.out.println("Download SUCCESSFUL from server");
                break;
            }
            else if (DownloadManager.STATUS_FAILED == status){
                System.err.println("Download FAILED from server");
                //TRY AGAIN
            }
            else if (DownloadManager.STATUS_PAUSED == status){
                System.err.println("Download PAUSED");
                //Get user to turn on wifi/cellular
            }
        }
    }

    //Reading file from server
    private Boolean readFile(){

        //Initialize variables
        file_settings = new HashMap<>();
        sql_queries = new HashMap<>();
        chapterlist = new ArrayList<>();
        chapters = new HashMap<>();
        ArrayList<String>_chapterText = new ArrayList<>();
        String line = null;

        try {
            //Get story, read file
            File file = new File(this.context.getExternalFilesDir("/user_stories/" + this.filename).toString());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            //Counter to keep track of chapters
            int counter = 0;

            //Reading settings
            while ((line = reader.readLine()) != null){
                if(line.matches("SQL")) break;
                String[]_split_line = line.split("=");
                file_settings.put(_split_line[0], _split_line[1]);
            }

            //Reading SQL queries
            while ((line = reader.readLine()) != null){
                if(line.matches("END_SETTINGS")) break;
                String[]_split_line = line.split("=");
                sql_queries.put(_split_line[0], _split_line[1]);
            }

            //Reading chapters
            while ((line = reader.readLine()) != null){

                //Stop if end of file
                if(line.matches("END_FILE")) break;

                    //Add chapter to chapterlist
                else if(line.contains("Chapter")){
                    chapterlist.add(line);

                    while ((line = reader.readLine()) != null){
                        //If chapter is done, break
                        if(line.length() == 0) break;

                        //Add paragraph text
                        _chapterText.add(line);

                    }
                    //Save chapters: KEY=chapter, VALUE=chapter_text
                    chapters.put(chapterlist.get(counter), new ArrayList<>(_chapterText));
                    counter++;
                    _chapterText.clear();
                }
            }
            deleteSkeletonFile();
            return true;

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    //Deleting skeleton file downloaded from server
    private void deleteSkeletonFile(){

        try {
            File file = new File(this.context.getExternalFilesDir("/user_stories/" + this.filename).toString());
            if(file.exists()){
                file.delete();
                System.out.println("Deleting skeletonfile " + this.filename);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("COULD NOT DELETE SKELETONFILE");
        }


    }

    //----------GETTERS FOR STORY----------//

    public String getStoryTitle(){
        return this.file_settings.get("story_title");
    }

    public int getNumFriends(){
        return Integer.parseInt(this.file_settings.get("friends"));
    }

    public String getFriendsTitle(){
        return this.file_settings.get("friends_title");
    }

    public String getFriendsHint(){
        return this.file_settings.get("friends_hint");
    }

    public int getNumItems(){
        return Integer.parseInt(this.file_settings.get("item"));
    }

    public String getItemsTitle(){
        return this.file_settings.get("item_title");
    }

    public String getItemsHint(){
        return this.file_settings.get("item_hint");
    }

    public int getNumEnemies(){
        return Integer.parseInt(this.file_settings.get("enemies"));
    }

    public String getEnemiesTitle(){
        return this.file_settings.get("enemies_title");
    }

    public String getEnemiesHint(){
        return this.file_settings.get("enemies_hint");
    }

    public int getNumTextboxes(){
        return Integer.parseInt(this.file_settings.get("textbox"));
    }

    public String getTextboxesTitle(){
        return this.file_settings.get("textbox_title");
    }

    public String getTextboxesHint(){
        return this.file_settings.get("textbox_hint");
    }

    public HashMap<String, String> getSqlQueries() {
        return sql_queries;
    }

    public ArrayList<String> getChapterlist() {
        return chapterlist;
    }

    public HashMap<String, ArrayList<String>> getChapters() {
        return chapters;
    }

    public String getFilename() {
        return filename;
    }
}
