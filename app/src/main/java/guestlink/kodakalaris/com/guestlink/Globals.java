package guestlink.kodakalaris.com.guestlink;


/**
 * Created by Donald Chapman1 on 6/25/2017.
 */

public class Globals {
    private static Globals instance;

    // Global variable
    private int data;
    private String photographer;
    private String location;
    private String subjects;
    private String guestIdLength = "0";
    private String deviceName;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setPhotographer(String d){
        this.photographer=d;
    }
    public String getPhotographer(){
        return this.photographer;
    }

    public void setLocation(String d){
        this.location = d;
    }
    public String getLocation(){
        return this.location;
    }

    public void setguestIdLength(String d){
        this.guestIdLength=d;
    }
    public String getguestIdLength(){
        return this.guestIdLength;
    }
    public void setdeviceName(String d){
        this.deviceName=d;
    }
    public String getdeviceName(){
        return this.deviceName;
    }
    public void setSubjects(String d){
        this.subjects=d;
    }
    public String getSubjects(){
        return this.subjects;
    }
    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
